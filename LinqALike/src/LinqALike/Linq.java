package LinqALike;

import LinqALike.Common.*;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.Queries.*;

import java.util.*;

import static LinqALike.CommonDelegates.*;
import static LinqALike.Factories.firstNotNull;
import static LinqALike.Factories.from;

public class Linq {

    public static <TBase, TDerived extends TBase> Queryable<TDerived> ofType(Iterable<TBase> sourceElements,
                                                                               Class<TDerived> desiredType) {
        return cast(where(sourceElements, desiredType::isInstance));
    }

    public static <TElement> TElement single(Iterable<TElement> elements) {
        return single(elements, Tautology);
    }

    public static <TElement> TElement single(Iterable<TElement> elements,
                                             Condition<? super TElement> uniqueCondition) {
        return ImmediateInspections.single(elements, uniqueCondition);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> sourceElements) {
        return singleOrDefault(sourceElements, Tautology);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> sourceElements,
                                                      Condition<? super TElement> uniqueCondition) {
        return ImmediateInspections.singleOrDefault(sourceElements, uniqueCondition);
    }

    public static <TElement> TElement first(Iterable<TElement> elements) {
        return first(elements, Tautology);
    }

    public static <TElement> TElement first(Iterable<TElement> sourceElements,
                                            Condition<? super TElement> condition) {

        return ImmediateInspections.first(sourceElements, condition);
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> sourceElements) {

        return ImmediateInspections.firstOrDefault(sourceElements, Tautology);
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> sourceElements,
                                                     Condition<? super TElement> condition) {

        return ImmediateInspections.firstOrDefault(sourceElements, condition);
    }

    public static <TElement> TElement last(Iterable<TElement> sourceElements){
        return last(sourceElements, Tautology);
    }
    public static <TElement> TElement last(Iterable<TElement> sourceElements,
                                           Condition<? super TElement> condition) {
        return ImmediateInspections.last(sourceElements, condition);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> sourceElements) {

        return lastOrDefault(sourceElements, Tautology);
    }
    public static <TElement> TElement lastOrDefault(Iterable<TElement> sourceElements,
                                                    Condition<? super TElement> condition) {

        return ImmediateInspections.lastOrDefault(sourceElements, condition);
    }

    public static <TElement> Queryable<TElement> where(Iterable<TElement> sourceElements,
                                                       Condition<? super TElement> condition) {

        return new WhereQuery<>(sourceElements, condition);
    }

    public static <TElement, TResult> Queryable<TResult> select(Iterable<TElement> sourceElements,
                                                                Func1<? super TElement, TResult> targetSite) {
        return new SelectQuery<>(sourceElements, targetSite);
    }

    public static <TElement> boolean any(Iterable<? extends TElement> sourceElements) {
        return any(sourceElements, Tautology);
    }
    public static <TElement> boolean any(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.any(sourceElements, condition);
    }

    public static <TElement> boolean isEmpty(Iterable<TElement> sourceElements) {
        return ! ImmediateInspections.any(sourceElements, Tautology);
    }

    public static <TElement> boolean contains(Iterable<? extends TElement> sourceElemens, Object candidate) {
        return ImmediateInspections.contains(sourceElemens, candidate);
    }

    public static <TTransformed, TElement>
    Queryable<TTransformed> selectMany(Iterable<TElement> set,
                                         Func1<? super TElement, ? extends Iterable<TTransformed>> selector) {
        return new SelectManyQuery<>(set, selector);
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, TElement... toInclude) {
        return new UnionQuery<>(left, from(toInclude), identity());
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return new UnionQuery<>(left, right, identity());
    }

    public static <TElement, TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> left,
                                Iterable<? extends TElement> right,
                                Func1<? super TElement, TCompared> comparableSelector){

        return new UnionQuery<>(left, right, comparableSelector);
    }

    public static <TKey, TValue> LinqingMap<TKey,TValue> toMap(Iterable<TKey> keys, Iterable<TValue> values) {
        return Factories.asMap(keys, values);
    }

    public static <TDerived, TElement> Queryable<TDerived> cast(Iterable<TElement> sourceElements) {
        return new CastQuery<>(sourceElements);
    }

    public static <TElement> boolean all(Iterable<TElement> set, Condition<? super TElement> condition) {

        for(TElement element : set){
            if( ! condition.passesFor(element)){
                return false;
            }
        }

        return true;
    }

    public static <TElement> boolean isSetEquivalentOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        if(left instanceof Collection && right instanceof Collection){
            if( ((Collection)left).size() != ((Collection)right).size()) return false;
        }

        final Map<TElement, TElement> leftHashes = new HashMap<>();
        //note, I'm not actually using the value for anything here, just the hashing key comparison.

        for(TElement leftElement : left){
            leftHashes.put(leftElement, null);
        }

        return Factories.asList(right).all(new Condition<TElement>() {
            public boolean passesFor(TElement candidate) {
                return leftHashes.containsKey(candidate);
            }
        });
    }

    public static <TElement> Queryable<TElement> skipWhile(Iterable<? extends TElement> set,
                                                           Condition<? super TElement> toExclude) {

        Ref<TElement> firstPassingValue = new Ref<>();
        Iterator<? extends TElement> iterator = moveIteratorUpUntilConditionPasses(set.iterator(), Not(toExclude), firstPassingValue);

        return firstPassingValue.target != null
                ? new LinqingList<>(new AmmendedIterator<>(firstPassingValue.target, iterator))
                : new ReadonlyLinqingList<>();
    }


    public static <TElement> Queryable<TElement> skipUntil(Iterable<? extends TElement> set,
                                                           Condition<? super TElement> toInclude) {
        Ref<TElement> firstPassingValue = new Ref<>();
        Iterator<? extends TElement> iterator = moveIteratorUpUntilConditionPasses(set.iterator(), toInclude, firstPassingValue);

        assert false : "not implemented";
        return null;
//        return firstPassingValue.target != null
//                ? new LinqingList<>(new AmmendedIterator<>(firstPassingValue.target, iterator))
//                : new ReadonlyLinqingList<>();
    }

    private static <TElement>
    Iterator<? extends TElement> moveIteratorUpUntilConditionPasses(Iterator<? extends TElement> iterator,
                                                                    Condition<? super TElement> conditionToPass,
                                                                    Ref<TElement> firstPassingValue) {
        while(iterator.hasNext()){
            TElement candidate = iterator.next();
            if(conditionToPass.passesFor(candidate)){
                firstPassingValue.target = candidate;
                break;
            }
        }
        return iterator;
    }

    public static <TElement> Queryable<TElement> reversed(Iterable<TElement> sourceElements) {
        return ImmediateInspections.reversed(sourceElements);
    }

    public static <TElement> boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.isSubsetOf(left, right);
    }

    public static <TElement> int count(Iterable<TElement> set, Condition<? super TElement> condition) {
        int count = 0;
        for(TElement element : set){
            if(condition.passesFor(element)){
                count += 1;
            }
        }
        return count;
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> source, TElement... toExclude) {

        return new ExceptQuery.WithNaturalEquality<>(source, from(toExclude));
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> left, Iterable<? extends TElement> right) {
        return new ExceptQuery.WithNaturalEquality<>(left, right);
    }

    public static <TElement, TCompared> Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                                                                   Iterable<? extends TElement> membersToExclude,
                                                                   Func1<? super TElement, TCompared> comparableSelector) {

        return new ExceptQuery.WithComparable<>(originalMembers, membersToExclude, comparableSelector);
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                                                        Iterable<? extends TElement> membersToExclude,
                                                        Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new ExceptQuery.WithEquatable<>(originalMembers, membersToExclude, comparableSelector);
    }


    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right) {

        return new IntersectionQuery.WithNaturalEquality<>(left, right);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           TElement... right) {
        return new IntersectionQuery.WithNaturalEquality<>(left, Factories.from(right));
    }

    public static <TElement, TCompared> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                                      Iterable<? extends TElement> right,
                                                                      Func1<? super TElement, TCompared> comparableSelector) {

        return new IntersectionQuery.WithComparable<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right,
                                                           Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new IntersectionQuery.WithEqualityComparator<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> skip(Iterable<TElement> setToSkip, int numberToSkip) {

        Iterator<TElement> iterator = setToSkip.iterator();

        for(int i = 0; i < numberToSkip && iterator.hasNext(); i++){
            iterator.next();
        }

        return new LinqingList<>(iterator);
    }

    public static <TElement> boolean containsDuplicates(Iterable<TElement> set) {
        HashSet<TElement> hashes = new HashSet<>();
        for(TElement element : set){
            if(hashes.contains(element)){
                return true;
            }
            hashes.add(element);
        }
        return false;
    }


    public static <TElement> Object[] toArray(Queryable<TElement> set) {
        Object[] copy = new Object[set.size()];
        int i = 0;
        for(TElement element : set){
            copy[i++] = element;
        }
        return copy;
    }

    /**
     * @see java.util.ArrayList#toArray(Object[])
     */
    //copied from JavaUtil code, so presumably this is 'safe'.
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <TElement, TDesired> TDesired[] toArray(Queryable<TElement> originalSet,
                                                          TDesired[] arrayTypeIndicator) {

        int size = originalSet.size();
        TDesired[] a = arrayTypeIndicator;
        Object[] elementData = originalSet.toArray();

        //copy-paste from java.util.ArrayList#toArray(T[] a)
        //... which is dumb since that means we create the array twice...
        assert false : "not implemented";

        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (TDesired[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public static <TElement> LinqingList<TElement> toList(Iterable<TElement> set) {
        return new LinqingList<>(set);
    }

    public static <TElement> TElement firstOr(Queryable<TElement> set,
                                              TElement alternative) {
        return firstNotNull(set.firstOrDefault(), alternative);
    }

    public static <TElement> TElement secondToLast(Iterable<TElement> set) {
        int size = size(set);
        if(size < 2){
            throw new SetSizeInsufficientException(2, size);
        }

        if(set instanceof List){
            List<TElement> actualThis = (List<TElement>) set;
            return actualThis.get(size - 2);
        }

        Iterator<TElement> iter = set.iterator();
        TElement candidate = null, last = null;
        while(iter.hasNext()){
            candidate = last;
            last = iter.next();
        }

        return candidate;
    }

    public static <TElement> int size(Iterable<TElement> sourceElements) {
        return ImmediateInspections.size(sourceElements);
    }

    public static <TElement, TDesired> TDesired[] toArray(Iterable<TElement> entries,
                                                          Func1<Integer, TDesired[]> arrayFactory) {

        int size = size(entries);
        Object[] array = arrayFactory.getFrom(size);

        int index = 0;
        for(Object element : entries){
            array[index] = element;
        }

        return (TDesired[]) array;
    }

    public static <TElement> Queryable<TElement> distinct(Iterable<TElement> candidateWithDuplicates) {
        return new DistinctQuery.WithNaturalEquality<>(candidateWithDuplicates);
    }

    public static <TElement> double average(Iterable<? extends TElement> sourceElements,
                                            Func1<? super TElement, Double> valueSelector) {

        return ImmediateInspections.average(sourceElements, valueSelector);
    }

    public static <TElement> boolean containsElement(Iterable<? extends TElement> set,
                                                     TElement candidate) {
        return contains(set, candidate);
    }

    public static <TElement, TComparable> QueryableGroupSet<TElement> groupBy(Iterable<TElement> setToGroup,
                                                                              Func1<? super TElement, TComparable> groupByPropertySelector) {
//        return new GroupByQuery.WithComparable<>(setToGroup, groupByPropertySelector);
        assert false : "what?";
        return null;
    }

    public static <TElement> Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                                                    Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
        return new GroupByQuery.WithEqualityComparator<>(setToGroup, groupMembershipComparator);
    }

    public static <TElement> double min(Queryable<TElement> sourceElements, Func1<? super TElement,Double> valueSelector) {
        return ImmediateInspections.min(sourceElements, valueSelector);
    }

    public static <TElement> double max(Iterable<TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        return ImmediateInspections.max(sourceElements, valueSelector);
    }

    public static <TElement> TElement withMax(Queryable<TElement> sourceElements,
                                              Func1<? super TElement ,Double> valueSelector) {
        return ImmediateInspections.withMax(sourceElements, valueSelector);
    }

    public static <TElement> TElement withMin(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return ImmediateInspections.withMin(sourceElements, valueSelector);
    }

    public static <TElement, TCompared extends Comparable<TCompared>> Queryable<TElement> orderBy(Queryable<TElement> sourceElements,
                                                                                                  Func1<? super TElement, TCompared> comparableSelector) {
        return new OrderByQuery<>(sourceElements, comparableSelector);
    }

    public static <TElement> Queryable<TElement> orderBy(Queryable<TElement> sourceElements,
                                                         Func2<? super TElement, ? super TElement, Integer> equalityComparator) {
        assert false : "not implemented";
        return null;
    }

    public static <TElement> double sum(Queryable<TElement> set,
                                        Func1<? super TElement, Double> valueSelector) {
        assert false;
        return 0;
    }

    public static <TElement> ReadonlyLinqingList<TElement> toReadOnly(Queryable<TElement> source) {
        return new ReadonlyLinqingList<>(source);
    }

    public static <TElement> LinqingSet<TElement> toSet(Queryable<TElement> source) {
        return new LinqingSet<>(source);
    }

    public static <TElement> Queryable<TElement> fetch(Queryable<TElement> source) {
        return new ReadonlyLinqingList<>(source);
    }

    public static <TKey, TElement> LinqingMap<TKey,TElement> toMap(Iterable<TElement> sourceElements,
                                                                   Func1<? super TElement,TKey> keySelector) {
        return Factories.asMap(sourceElements, keySelector, identity());
    }

    public static <TKey, TValue, TElement> LinqingMap<TKey,TValue> toMap(Iterable<TElement> sourceElements,
                                                                         Func1<? super TElement,TKey> keySelector,
                                                                         Func1<? super TElement,TValue> valueSelector) {
        return Factories.asMap(sourceElements, keySelector, valueSelector);
    }

    public static <TElement> boolean isSingle(Iterable<TElement> source) {
        return ImmediateInspections.isSingle(source);
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> source) {

        if(source instanceof Set || source instanceof QueryableSet){
            return true;
        }

        int size = size(source);
        HashSet<TElement> set = new HashSet<>(size, 0.90F);

        for(TElement element : source){

            boolean modified = set.add(element);

            if ( ! modified){
                return false;
            }
        }
        return true;
    }
}

