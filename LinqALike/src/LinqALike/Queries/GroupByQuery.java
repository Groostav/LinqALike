package LinqALike.Queries;

import LinqALike.Common.Preconditions;
import LinqALike.Delegate.Func2;
import LinqALike.Queryable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static LinqALike.CommonDelegates.referenceEquals;
import static LinqALike.Factories.from;

public class GroupByQuery<TElement> implements DefaultQueryable<Queryable<TElement>> {

    private final Iterable<TElement> sourceElements;
    private final Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator;

    public GroupByQuery(Iterable<TElement> sourceElements,
                        Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {

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

        private final Func2<? super TElement, ? super TElement, Boolean> membershipTest;

        private Queryable<TElement> ungroupedElements = from(sourceElements);

        public GroupByWithEqualityComparatorIterator(Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
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
                    .where(candidate -> membershipTest.getFrom(groupLeader.single(), candidate));

            Queryable<TElement> members = groupLeader.union(secondaryMembers, referenceEquals);

            //what if you have a same-reference entry that needs to be in 2 groups?
            //one solution is to wait until
            ungroupedElements = ungroupedElements.except(members, referenceEquals);
            return members;
        }
    }
}
