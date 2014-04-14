package LinqALike.Queries;

import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.Factories;
import LinqALike.Queryable;

import java.util.Iterator;

import static LinqALike.CommonDelegates.nullSafeEquals;

public abstract class GroupByQuery<TElement> implements DefaultQueryable<Queryable<TElement>> {

    protected final Iterable<? extends TElement> setToGroup;

    protected GroupByQuery(Iterable<? extends TElement> setToGroup){
        this.setToGroup = setToGroup;
    }


    public static class WithComparable<TElement, TComparable> extends GroupByQuery<TElement>{

        private final Func1<? super TElement, TComparable> groupByPropertySelector;

        public WithComparable(Iterable<? extends TElement> setToGroup,
                              Func1<? super TElement, TComparable> groupByPropertySelector) {
            super(setToGroup);
            this.groupByPropertySelector = groupByPropertySelector;
        }

        @Override
        public Iterator<Queryable<TElement>> iterator() {
            return this.new GroupByWithComparableIterator<TComparable>(groupByPropertySelector);
        }

    }

    public static class WithEqualityComparator<TElement> extends GroupByQuery<TElement> {

        private final Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator;

        public WithEqualityComparator(Iterable<? extends TElement> setToGroup,
                                      Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
            super(setToGroup);
            this.groupMembershipComparator = groupMembershipComparator;
        }

        @Override
        public Iterator<Queryable<TElement>> iterator() {
            return this.new GroupByWithEqualityComparatorIterator(groupMembershipComparator);
        }
    }

    protected class GroupByWithComparableIterator<TComparable> implements Iterator<Queryable<TElement>> {

        private final Func1<? super TElement, TComparable> comparable;

        private Queryable<? extends TElement> ungroupedElements = Factories.from(setToGroup);

        public GroupByWithComparableIterator(Func1<? super TElement, TComparable> groupByPropertySelector) {
            this.comparable = groupByPropertySelector;
        }

        @Override
        public boolean hasNext() {
            return ungroupedElements.any();
        }

        @SuppressWarnings("unchecked") //line below consume a Queryable<capture<? extends TElement>> in a read-only (covariant) nature,
                                       // so the cast: `(Queryable<TElement>) queryablethatHasWildcardExtendsTElement` is safe.
        @Override
        public Queryable<TElement> next() {
            TElement groupHeader = ungroupedElements.first();
            TComparable groupKey = comparable.getFrom(groupHeader);

            Queryable<TElement> members = ((Queryable<TElement>)ungroupedElements).except(groupHeader)
                                                                                  .where(candidate -> nullSafeEquals(comparable.getFrom(candidate), groupKey));

            ungroupedElements = ((Queryable<TElement>)ungroupedElements).except(members.union(groupHeader));

            return members;
        }
    }

    protected class GroupByWithEqualityComparatorIterator implements Iterator<Queryable<TElement>> {

        private final Func2<? super TElement, ? super TElement, Boolean> membershipTest;

        private Queryable<? extends TElement> ungroupedElements = Factories.from(setToGroup);

        public GroupByWithEqualityComparatorIterator(Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
            this.membershipTest = groupMembershipComparator;
        }

        @Override
        public boolean hasNext() {
            return ungroupedElements.any();
        }

        @SuppressWarnings("unchecked") //same as above. I really should consolidate this code.
        @Override
        public Queryable<TElement> next() {
            TElement groupHeader = ungroupedElements.first();

            Queryable<TElement> members = ((Queryable<TElement>)ungroupedElements).except(groupHeader)
                                                                                  .where(candidate -> membershipTest.getFrom(candidate, groupHeader));

            ungroupedElements = ((Queryable<TElement>)ungroupedElements).except(members.union(groupHeader));

            return members;
        }
    }
}
