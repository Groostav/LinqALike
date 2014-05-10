package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Queryable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.EmpowerOperations.LinqALike.CommonDelegates.ReferenceEquals;
import static com.EmpowerOperations.LinqALike.Factories.from;

public class GroupByQuery<TElement> implements DefaultQueryable<Queryable<TElement>> {

    private final Iterable<TElement> sourceElements;
    private final EqualityComparer<? super TElement> groupMembershipComparator;

    public GroupByQuery(Iterable<TElement> sourceElements,
                        EqualityComparer<? super TElement> groupMembershipComparator) {

        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(groupMembershipComparator, "groupMembershipComparator");

        this.sourceElements = sourceElements;
        this.groupMembershipComparator = groupMembershipComparator;
    }

    @Override
    public Iterator<Queryable<TElement>> iterator() {
        return new GroupByWithEqualityComparatorIterator(groupMembershipComparator);
    }

    private class GroupByWithEqualityComparatorIterator implements Iterator<Queryable<TElement>> {

        private final EqualityComparer<? super TElement> membershipTest;

        private Queryable<TElement> ungroupedElements = from(sourceElements);

        public GroupByWithEqualityComparatorIterator(EqualityComparer<? super TElement> groupMembershipComparator) {
            this.membershipTest = groupMembershipComparator;
        }

        @Override
        public boolean hasNext() {
            return ungroupedElements.any();
        }

        @Override
        public Queryable<TElement> next() {

            if(ungroupedElements.isEmpty()){
                throw new NoSuchElementException();
            }

            Queryable<TElement> groupLeader = from(ungroupedElements.first());
            Queryable<TElement> secondaryMembers = ungroupedElements
                    .skip(1)
                    .where(candidate -> membershipTest.equals(groupLeader.single(), candidate));

            Queryable<TElement> members = groupLeader.union(secondaryMembers, ReferenceEquals);

            //what if you have a same-reference entry that needs to be in 2 groups?
            //one solution is to wait until
            ungroupedElements = ungroupedElements.except(members, ReferenceEquals);
            return members;
        }
    }
}
