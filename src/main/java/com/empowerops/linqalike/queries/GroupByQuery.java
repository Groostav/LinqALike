package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.ComparingLinkedHashSet;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.Queryable;

import java.util.Iterator;

import static com.empowerops.linqalike.CommonDelegates.FalsehoodEquality;
import static com.empowerops.linqalike.CommonDelegates.ReferenceEquality;
import static com.empowerops.linqalike.Factories.from;

public class GroupByQuery<TElement> implements DefaultedQueryable<Queryable<TElement>> {

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

                if(group.isEmpty()){
                    throw new IllegalArgumentException("Comparator violates its general contract");
                }

                setPrefetchedValue(group);
                return;
            }
        }
    }
}
