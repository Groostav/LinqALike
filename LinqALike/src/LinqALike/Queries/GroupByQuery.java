package LinqALike.Queries;

import LinqALike.Delegate.Func2;
import LinqALike.Queryable;

import java.util.Iterator;

import static LinqALike.CommonDelegates.referenceEquals;
import static LinqALike.Factories.from;

public class GroupByQuery<TElement> implements DefaultQueryable<Queryable<TElement>> {

    private final Iterable<TElement> sourceElements;
    private final Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator;

    public GroupByQuery(Iterable<TElement> sourceElements,
                        Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
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

            Queryable<TElement> groupHeader = from(ungroupedElements.first());
            Queryable<TElement> secondaryMembers = ungroupedElements
                    .except(groupHeader, referenceEquals)
                    .where(candidate -> membershipTest.getFrom(groupHeader.single(), candidate));

            Queryable<TElement> members = groupHeader.union(secondaryMembers);

            ungroupedElements = ungroupedElements.except(members);
            return members;
        }
    }
}
