package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.ComparingLinkedHashSet;
import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.Queryable;

import java.util.Iterator;

import static com.EmpowerOperations.LinqALike.CommonDelegates.FalsehoodEquality;
import static com.EmpowerOperations.LinqALike.CommonDelegates.ReferenceEquality;
import static com.EmpowerOperations.LinqALike.Factories.from;

public class GroupByQuery<TElement> implements DefaultQueryable<Queryable<TElement>> {

    private final Queryable<TElement> sourceElements;
    private final EqualityComparer<? super TElement> groupMembershipComparator;

    public GroupByQuery(Iterable<TElement> sourceElements,
                        EqualityComparer<? super TElement> groupMembershipComparator) {

        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(groupMembershipComparator, "groupMembershipComparator");

        this.sourceElements = from(sourceElements);
        this.groupMembershipComparator = groupMembershipComparator;
    }

    @Override
    public Iterator<Queryable<TElement>> iterator() {
        return new GroupByWithEqualityComparatorIterator();
    }

    private class GroupByWithEqualityComparatorIterator extends PrefetchingIterator<Queryable<TElement>> {

        private ComparingLinkedHashSet<TElement> alreadySeenElements = new ComparingLinkedHashSet<>(groupMembershipComparator);
        private Iterator<TElement> unseenElements = sourceElements.iterator();

        @Override
        protected void prefetch() {
            while(unseenElements.hasNext()){
                TElement candidateLeader = unseenElements.next();

                boolean isNewElement = alreadySeenElements.add(candidateLeader);
                if ( ! isNewElement) { continue; }

                Queryable<TElement> group = sourceElements.where(groupCandidate -> groupMembershipComparator.equals(candidateLeader, groupCandidate));

                if( ! group.containsElement(candidateLeader, ReferenceEquality)){
                    group = from(candidateLeader).union(group, FalsehoodEquality);
                }

                setPrefetchedValue(group);
                return;
            }
        }
    }
}
