package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.common.Formatting;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;
import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.empowerops.linqalike.CommonDelegates.identity;
import static com.empowerops.linqalike.Factories.empty;
import static com.empowerops.linqalike.Factories.range;
import static com.empowerops.linqalike.Linq.isEmpty;
import static com.empowerops.linqalike.common.Formatting.verticallyPrintMembers;
import static java.lang.Math.abs;

/**
 * A class that provides for the diffing of two group-of-groups for further analysis.
 *
 * <p>the main purpose of this class is to allow consumers to 'observe' one set-of-sets changing
 * and generate a reasonably minimal set of differences beween the changes. This is leveraged
 * heavily by PCP, which needs to observe the bands changing but, to keep our model unpolluted,
 * has no access to traditional events.
 *
 * <p>Consider (given that letters A, B,... are expensive points,
 * and Clusters 1 &amp; 11 have no relation inside java aside from their member similarities,
 * (IE they are not hte same instances))
 * <pre>{@code
 * time=0ms                 time=1ms
 *
 * Cluster1 {A, B, C, D}       Cluster11 {A, B, C, D}
 * Cluster2 {E, F}             Cluster12 {E, F, G}
 * Cluster3 {G, H, I}          Cluster13 {H, I, J}
 * Cluster4 {}                 Cluster14 {}
 * }</pre>
 *
 * <p>Some code (for example, UI code trying to draw these clusters as bands) would likely want
 * to create the objects, and apply the operations
 * <pre>{@code
 * Band1 {A, B, C, D}    No Change             Band1{A, B, C, D}
 * Band2 {E, F}          Add member 'G'        Band2{E, F, G}
 * Band3 {G, H, I}       Remove 'G', add 'J'   Band3{H, I, J}
 * Band4 {}              No Change             Band4{}
 * }</pre>
 *
 * <p> Thus, some piece of code must be responsible for analyzing the two cluster-sets
 * and generating a list of mappings that would look sometihng like:
 * <ol>
 *     <li>Map Cluster 1 to Cluster 11 via 'No change'</li>
 *     <li>Map Cluster 2 to Cluster 12 via 'Add member "G"'</li>
 *     <li>...</li>
 * </ol>
 *
 * <p><b>This is that.</b>
 *
 * @param <TGroup>
 * @param <TMember>
 */
public class GroupsChangeAnalyzer<TGroup, TMember>{

    public static final  boolean AlwaysValidateApplication;// = getEnvBool(GroupsChangeAnalyzer.class, "AlwaysValidateApplication").orElse(false);
    public static final  double  SetPercentageForMatch;//     = getEnvDouble(GroupsChangeAnalyzer.class, "SetPercentageForMatch").orElse(0.40);

    static{
        String AlwaysValidateEnv = System.getProperty("com.empowerops.linqalike.experimental.GroupsChangeAnalyzer.AlwaysValidateApplication");
        AlwaysValidateApplication = AlwaysValidateEnv != null && AlwaysValidateEnv.equalsIgnoreCase("true");

        String MatchPercentEnv = System.getProperty("com.empowerops.linqalike.experimental.GroupsChangeAnalyzer.SetPercentageForMatch");
        SetPercentageForMatch = MatchPercentEnv == null ? 0.40 : Double.parseDouble(MatchPercentEnv);
    }

    private static final Logger  log                       = Logger.getLogger(GroupsChangeAnalyzer.class.getCanonicalName());

    private final Queryable<TGroup>                         originalGroups;
    private final Queryable<TGroup>                         newGroups;
    private final Func1<? super TGroup, Queryable<TMember>> groupMemberSelector;

    private final LinqingMap<TGroup, Queryable<TMember>> groupMembersBySourceGroupCache = new LinqingMap<>();
    private final LinqingList<TGroup>                    remainingDestinationGroups     = new LinqingList<>();

    //this class is a hack to leverage the concurrent modification stuff in iterators
    //to save us from modifications to groups in the groups change analyzer
    private static class ModificationFinder {
        public static ModificationFinder emptyLock = new ModificationFinder(empty());

        private final boolean     startedEmpty;
        private final Iterable<?> source;

        private Iterator<?> currentIterator;

        private ModificationFinder(Iterable<?> collectionToLock) {
            this.source = collectionToLock;
            this.startedEmpty = isEmpty(collectionToLock);

            this.currentIterator = collectionToLock.iterator();
        }

        /**
         * returns true if a change was made
         */
        public boolean sawModification() throws ConcurrentModificationException {
            if (startedEmpty && ! isEmpty(source)) {
                RuntimeException exception = new ConcurrentModificationException("a change was made to remove some number of elements from: \n\t" + currentIterator);
                log.log(Level.SEVERE, "", exception);
                return true;
            }
            else if (startedEmpty && isEmpty(source)) {
                // when the analysis was done it was empty,
                // and when we're applying the analysis its still empty,
                // => no modifications were made of if they were they happened without us noticing.
                //pass.
                return false;
            }
            else if (! startedEmpty) {
                //calling next() will cause a hashSet's iterator, a lists iterator, and pretty well all iterator's
                //to do their own checkForComodification().
                try {
                    currentIterator.next();
                }
                finally {
                    currentIterator = source.iterator();
                }
                return false;
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    public GroupsChangeAnalyzer(Queryable<TGroup> originalGroups,
                                Queryable<TGroup> newGroups,
                                Func1<? super TGroup, Queryable<TMember>> groupMembersSelector) {

        this.originalGroups = originalGroups;
        this.newGroups = newGroups;
        this.groupMemberSelector = groupMembersSelector;
    }

    public GroupChangeSetCollection<TGroup, TMember> buildChangeDescription() {
        remainingDestinationGroups.clearAndAddAll(newGroups);
        groupMembersBySourceGroupCache.clear();
        LinqingSet<Change<TGroup, TMember>> changes = new LinqingSet<>();

        for (TGroup originalGroup : originalGroups) {
            Queryable<TMember> sourceMembers = getMembers(originalGroup);

            TGroup destinationGroupOrNull = findWithPlusOrMinusSomeElements(sourceMembers);

            remainingDestinationGroups.removeElement(destinationGroupOrNull);

            changes.add(new Change<>(
                            originalGroup,
                            destinationGroupOrNull,
                            getMembers(originalGroup),
                            getMembers(destinationGroupOrNull),
                            groupMemberSelector)
            );

            groupMembersBySourceGroupCache.remove(originalGroup);
        }

        for (TGroup destinationGroup : remainingDestinationGroups) {
            changes.add(new Change<>(
                    null,
                    destinationGroup,
                    getMembers(null),
                    getMembers(destinationGroup),
                    groupMemberSelector
            ));
        }

        return new GroupChangeSetCollection<>(originalGroups, newGroups, groupMemberSelector, changes);
    }

    private @Nullable TGroup findWithPlusOrMinusSomeElements(Queryable<TMember> sourceMembers) {

        int sourceMemberCount = sourceMembers.count();
        int maxAllowedCountDeviation = (int) (Math.ceil(sourceMemberCount * SetPercentageForMatch));

        Queryable<TGroup> candidateDestinations = remainingDestinationGroups
                .where(candidate -> abs(getMembers(candidate).count() - sourceMemberCount) <= maxAllowedCountDeviation)
                .orderBy(candidate -> abs(getMembers(candidate).count() - sourceMemberCount))
                .toList();

        Optional<TGroup> result = candidateDestinations.firstOrDefault(candidate -> {
            int intersectingCount = getMembers(candidate).intersect(sourceMembers).count();
            int divisor = Math.min(getMembers(candidate).count(), sourceMembers.count());

            return ((double) intersectingCount) / divisor > (1 - SetPercentageForMatch);
        });

        return result.orElse(null);
    }

    private Queryable<TMember> getMembers(TGroup group){
        if(group == null){
            return new ReadonlyLinqingSet<>();
        }
        if ( ! groupMembersBySourceGroupCache.containsKey(group)){
            Queryable<TMember> members = groupMemberSelector.getFrom(group).immediately();
            groupMembersBySourceGroupCache.put(group, members);
        }
        return groupMembersBySourceGroupCache.getValueFor(group);
    }

    public static class GroupChangeSetCollection<TGroup, TMember> implements DefaultedQueryable<Change<TGroup, TMember>>{

        private final Queryable<TGroup>                  sourceGroups;
        private final Queryable<TGroup>                  destinationGroups;
        private final Queryable<Change<TGroup, TMember>> changes;

        private final Map<TGroup, ModificationFinder> locksBySourceGroup      = new LinqingMap<>(null, ModificationFinder.emptyLock);
        private final Map<TGroup, ModificationFinder> locksByDestinationGroup = new LinqingMap<>(null, ModificationFinder.emptyLock);
        private final ModificationFinder sourceGroupsLock;
        private final ModificationFinder destinationGroupsLock;

        private GroupChangeSetCollection(Queryable<TGroup> sourceGroups,
                                         Queryable<TGroup> destinationGroups,
                                         Func1<? super TGroup, Queryable<TMember>> membersSelector,
                                         Queryable<Change<TGroup, TMember>> changes) {
            this.sourceGroups = sourceGroups;
            this.destinationGroups = destinationGroups;
            this.changes = changes;
            sourceGroupsLock = new ModificationFinder(sourceGroups);
            destinationGroupsLock = new ModificationFinder(destinationGroups);

            locksBySourceGroup.putAll(sourceGroups.toMap(identity(), x -> new ModificationFinder(membersSelector.getFrom(x))));
            locksByDestinationGroup.putAll(destinationGroups.toMap(identity(), x -> new ModificationFinder(membersSelector.getFrom(x))));

            if (! changes.all(change -> change.isMapping() || change.isGroupAddition() || change.isGroupRemoval())) {
                throw new IllegalArgumentException("changes");
            }
        }

        @SuppressWarnings("unchecked")
        public <TMutableGroup extends MutableGroupController<TGroup, TMember>>
        void applyAllByModifyingGroups(WritableCollection<? extends TMutableGroup> mutableGroups,
                                       MutableGroupController.Factory<TGroup, TMember, TMutableGroup> factory) {
            this.applyAllByModifyingGroups(
                    (WritableCollection<MutableGroupController<TGroup, TMember>>)mutableGroups,
                    MutableGroupController::hasSameMembersAs,
                    factory::create,
                    MutableGroupController::memberPoints
            );
        }

        public <TGroupWrapper>
        void applyAllByModifyingGroups(WritableCollection<TGroupWrapper> groupsToModify,
                                       Func2<? super TGroupWrapper, ? super TGroup, Boolean> comparer,
                                       Func1<? super TGroup, ? extends TGroupWrapper> factory,
                                       Func1<? super TGroupWrapper, WritableCollection<TMember>> groupSelector) {

            if(AlwaysValidateApplication && ! matches(groupsToModify, sourceGroups, comparer, groupSelector)){
                throw new IllegalArgumentException("groupsToModify ?--doesn't match sourceGroups");
            }
            boolean failedToApplyChange = findConcurrentModificationOfContainers();


            Map<TGroupWrapper, ModificationFinder> lockByModifiedGroups = groupsToModify.distinct().toMap(x -> x, x -> new ModificationFinder(groupSelector.getFrom(x)));

            LinqingList<TGroupWrapper> remainingGroups = new LinqingList<>(groupsToModify);
            LinqingList<Change<TGroup, TMember>> remainingChanges = new LinqingList<>(changes);


            for (Change<TGroup, TMember> mappingChange : remainingChanges.where(Change::isMapping).immediately()) {
                failedToApplyChange |= findConcurrentModification(mappingChange);

                if (mappingChange.isOneToOne()) { continue; }

                TGroupWrapper groupToModify = getGroupMatchingOrLogAsMissing(comparer, remainingGroups, mappingChange);
                failedToApplyChange |= groupsToModify == null;
                if (groupToModify == null) { continue; }

                failedToApplyChange |= lockByModifiedGroups.get(groupToModify).sawModification();

                failedToApplyChange |= ! remainingChanges.removeElement(mappingChange);
                failedToApplyChange |= ! remainingGroups.removeElement(groupToModify);

                WritableCollection<TMember> membersOfGroupToModify = groupSelector.getFrom(groupToModify);
                mappingChange.applyTo(groupToModify, membersOfGroupToModify);
            }

            for (Change<TGroup, TMember> removalChange : remainingChanges.where(Change::isGroupRemoval).immediately()) {
                failedToApplyChange |= findConcurrentModification(removalChange);

                TGroupWrapper groupToRemove = getGroupMatchingOrLogAsMissing(comparer, remainingGroups, removalChange);
                failedToApplyChange |= groupToRemove == null;
                if (groupToRemove == null) { continue; }

                failedToApplyChange |= lockByModifiedGroups.get(groupToRemove).sawModification();

                failedToApplyChange |= ! remainingChanges.removeElement(removalChange);
                failedToApplyChange |= ! remainingGroups.removeElement(groupToRemove);

                groupSelector.getFrom(groupToRemove).clear();
                groupsToModify.removeElement(groupToRemove);
            }

            for (Change<TGroup, TMember> additionChange : remainingChanges.where(Change::isGroupAddition).immediately()) {

                failedToApplyChange |= ! remainingChanges.removeElement(additionChange);

                TGroupWrapper newGroup = factory.getFrom(additionChange.destination);
                failedToApplyChange |= ! groupsToModify.add(newGroup);
            }

            if (failedToApplyChange) {
                logAndCorrectFailure(groupsToModify, comparer, factory);
            }

            if(AlwaysValidateApplication && ! matches(groupsToModify, destinationGroups, comparer, groupSelector)){
                log.severe(
                        "The change set failed to apply a change or was given a bad initial group. " +
                                "This flaw was only discovered because 'AlwaysValidateApplication' was true. " +
                                "OASIS will now rebuild the list from scratch.");
                groupsToModify.clear();
                groupsToModify.addAll(destinationGroups.select(factory::getFrom));
            }
        }

        private @Nullable <TGroupWrapper>
        TGroupWrapper getGroupMatchingOrLogAsMissing(Func2<? super TGroupWrapper, ? super TGroup, Boolean> comparer,
                                                     Queryable<TGroupWrapper> remainingGroups,
                                                     Change<TGroup, TMember> change) {

            Optional<TGroupWrapper> found = remainingGroups.firstOrDefault(group -> comparer.getFrom(group, change.source));

            return found.orElseGet(() -> {
                log.log(Level.WARNING, "couldnt apply " + change);
                return null;
            });
        }
        private <TGroupWrapper> boolean matches(Queryable<TGroupWrapper> groupsToModify,
                                                Queryable<TGroup> sourceGroups,
                                                Func2<? super TGroupWrapper, ? super TGroup, Boolean> comparer,
                                                Func1<? super TGroupWrapper, WritableCollection<TMember>> groupSelector) {

            return groupsToModify.all(groupToModify -> sourceGroups.count(sourceGroup -> comparer.getFrom(groupToModify, sourceGroup)) == 1);
        }

        private <TGroupWrapper> void logAndCorrectFailure(WritableCollection<TGroupWrapper> groupsToModify,
                                                          Func2<? super TGroupWrapper, ? super TGroup, Boolean> comparer,
                                                          Func1<? super TGroup, ? extends TGroupWrapper> factory) {

            Queryable<TGroupWrapper> modifiedGroupsWithoutDestination = groupsToModify.where(
                    groupToModify -> destinationGroups.count(destination -> comparer.getFrom(groupToModify, destination)) != 1
            );
            if (modifiedGroupsWithoutDestination.any()) {
                log.log(
                        Level.WARNING,
                        "Post-condition failure: Couldn't find a matching destination in :\n\t" +
                                verticallyPrintMembers(destinationGroups) + "\n" +
                                "for the groups to modify:\n\t" +
                                verticallyPrintMembers(modifiedGroupsWithoutDestination) + "\n" +
                                "The problem will be addressed by clearing and adding all points back into the problem groups."
                );

                LinqingList<TGroup> requiredGroups = destinationGroups.toList();
                requiredGroups.removeIf(group -> groupsToModify.count(wrapper -> comparer.getFrom(wrapper, group)) == 1);

                groupsToModify.removeAll(modifiedGroupsWithoutDestination.immediately());
                groupsToModify.addAll(requiredGroups.select(factory::getFrom));
            }
        }

        //false if a concurrent modification was made
        private boolean findConcurrentModification(Change<TGroup, TMember> change) {
            boolean result = findConcurrentModificationOfContainers();
            result &= locksByDestinationGroup.get(change.getDestination()).sawModification();
            result &= locksBySourceGroup.get(change.getSource()).sawModification();
            return result;
        }
        //false if a concurrent modification were made
        private boolean findConcurrentModificationOfContainers() {
            boolean result = sourceGroupsLock.sawModification();
            result &= destinationGroupsLock.sawModification();
            return result;
        }

        @Override
        public Iterator<Change<TGroup, TMember>> iterator() {
            return changes.iterator();
        }

        @Override public String toString() {
            return "Group Change Set {\n\t" + verticallyPrintMembers(changes) + "\n}";
        }
    }

    @Immutable
    public static class Change<TGroup, TMember> {

        private final TGroup                                    source;
        private final TGroup                                    destination;
        private final Queryable<TMember>                        sourceMembers;
        private final Queryable<TMember>                        destinationMembers;
        private final Func1<? super TGroup, Queryable<TMember>> groupMemberSelector;

        private static final boolean PrintMembersOnNewLine;//= BootstrappingUtilities.getEnvBool(Change.class, "PrintMembersOnNewLine")
                                                                    //.orElse(true);

        static{
            String PrintNewlineEnv = System.getProperty("com.empowerops.linqalike.experimental.GroupsChangeAnalyzer.Change.PrintMembersOnNewLine");
            PrintMembersOnNewLine = PrintNewlineEnv == null || PrintNewlineEnv.equalsIgnoreCase("true");
        }

        /*VisibleForTesting*/ public static <TMember>
        Change<?, TMember> forPurposeOfEquality(Queryable<TMember> sourceMembers,
                                                Queryable<TMember> destinationMembers) {
            class Equality extends Change<Object, TMember> {
                Equality(ReadonlyLinqingList<TMember> sourceMembersMembers, ReadonlyLinqingList<TMember> destinationMembers) {
                    super(null, null, sourceMembersMembers, destinationMembers, null);
                }
            }
            return new Equality(sourceMembers.toReadOnly(), destinationMembers.toReadOnly());
        }

        Change(TGroup source,
               TGroup destination,
               Queryable<TMember> sourceMembers,
               Queryable<TMember> destinationMembers,
               Func1<? super TGroup, Queryable<TMember>> groupMemberSelector) {

            this.source = source;
            this.destination = destination;
            this.sourceMembers = sourceMembers;
            this.destinationMembers = destinationMembers;
            this.groupMemberSelector = groupMemberSelector;
        }

        public TGroup getSource() {
            return source;
        }

        public TGroup getDestination() {
            return destination;
        }

        public boolean isGroupAddition() {
            return source == null;
        }

        public boolean isGroupRemoval() {
            return destination == null;
        }

        public boolean isMapping() {
            return ! isGroupAddition() && ! isGroupRemoval();
        }

        public boolean isOneToOne() {
            if (! isMapping()) {
                return false;
            }
            return ((sourceMembers instanceof Set && sourceMembers.setEquals(destinationMembers))
                    || (sourceMembers instanceof List && sourceMembers.sequenceEquals(destinationMembers)));
        }

        public boolean isMemberAddition() {
            return isMapping() && ! isOneToOne() && destinationMembers.isSupersetOf(sourceMembers);
        }

        public boolean isMemberRemoval() {
            return isMapping() && ! isOneToOne() && sourceMembers.isSupersetOf(destinationMembers);
        }

        @SuppressWarnings("unchecked") //pollution is not possible
        // unless x can be a List and a WriteableCollection<T> without being a List<T>
        public <TMutableGroup>
        void applyTo(TMutableGroup groupToModify, WritableCollection<TMember> collectionToModify) {

            if (groupToModify instanceof MutableGroupController.NeedsMeta) {
                ((MutableGroupController.NeedsMeta<TGroup, TMember>) groupToModify).setImmutableSource(destination);
                //todo try-catch around this to handle exception cases?
            }

            if (collectionToModify instanceof Set) {
                collectionToModify.retainAll(destinationMembers);
                collectionToModify.addAll(destinationMembers);
            }
            else if (collectionToModify instanceof List) {
                List<TMember> actualCollectionToModify = (List<TMember>) collectionToModify;

                while (destinationMembers.size() < actualCollectionToModify.size()) {
                    actualCollectionToModify.remove(actualCollectionToModify.size() - 1);
                }

                for (int index : range(0, destinationMembers.size())) {
                    TMember member = ((List<TMember>) destinationMembers).get(index);

                    if (index >= actualCollectionToModify.size()) { actualCollectionToModify.add(member); }
                    else if (index < actualCollectionToModify.size()) { setSmartly(actualCollectionToModify, index, member); }
                }
            }
            else {
                log.info("modifying a collection that is neither a set nor list: '" + collectionToModify.getClass().getCanonicalName() + "'");
                collectionToModify.removeAll(collectionToModify.except(destinationMembers).immediately());
                collectionToModify.addAll(destinationMembers.except(collectionToModify).immediately());
            }
        }

        private void setSmartly(List<TMember> actualCollectionToModify, int index, TMember member) {

            // so, in a stunningly annoying part of java fx
            // if you set call observableList.set(1, observableList.get(1));
            // which (you would think) is a no-op since you're setting the exact same value
            // to the same place
            // you will in fact generate a change event with removed = elem@1, added = elem@1
            // and the kicker is that isPermutation=false.
            // this means that your change listeners need to be able to perfectly un-do whatever
            // they do on the added or removed side.
            // what a fucking pain.
            // TODO #952: https://github.com/EmpowerOperations/OASIS/issues/952

            TMember existingMemberAtIndex = actualCollectionToModify.get(index);

            if( ! CommonDelegates.nullSafeEquals(existingMemberAtIndex, member)) {
                actualCollectionToModify.set(index, member);
            }
        }

        @Override
        public String toString() {
            String description =
                    isGroupAddition() ? "new group" : isGroupRemoval() ? "delete group" :
                    isMemberAddition() ? "new member" : isMemberRemoval() ? "removed member" :
                    isMapping() ? "mapping" : "?";

            String delimeter = PrintMembersOnNewLine ? "\n\t" : "";
            Func1<Iterable<?>, String> formatter = PrintMembersOnNewLine ? Formatting::verticallyPrintMembers : Formatting::csv;
            return "Set Change -" + description + ": " + " {" + delimeter +
                    formatter.getFrom(sourceMembers) + "} " +
                    "-> {" + delimeter +
                    formatter.getFrom(destinationMembers) + "}";
        }

        //////////////////////////////////////////////////////////////////////
        // auto-generated equals and hashcode

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (! (o instanceof Change)) { return false; }

            Change change = (Change) o;

            if (! destinationMembers.setEquals(change.destinationMembers)) { return false; }
            if (! sourceMembers.setEquals(change.sourceMembers)) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = sourceMembers.toReadOnlySet().hashCode();
            result = 31 * result + destinationMembers.toReadOnlySet().hashCode();
            return result;
        }
    }
}
