package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.ComparingLinkedHashSet;
import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.Queryable;

import java.util.Iterator;
import java.util.List;

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

        @Override
        protected void prefetch() {
            for(TElement candidateLeader : sourceElements){

                boolean hasChange = alreadySeenElements.add(candidateLeader);
                if ( ! hasChange) { continue; }

                Queryable<TElement> group = sourceElements.where(groupCandidate -> groupMembershipComparator.equals(candidateLeader, groupCandidate));
                List<TElement> flattened = group.toList();

                setPrefetchedValue(group);
                return;
            }
        }
    }
}
