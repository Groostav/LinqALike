package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.CommonDelegates;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Queryable;

import java.util.Iterator;

public abstract class IntersectionQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;

    protected IntersectionQuery(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "left");

        this.left = left;
        this.right = right;
    }

    public static class WithComparable<TElement, TCompared> extends IntersectionQuery<TElement>{

        private final Func1<? super TElement, TCompared> comparable;

        public WithComparable(Iterable<? extends TElement> left,
                              Iterable<? extends TElement> right,
                              Func1<? super TElement, TCompared> comparableSelector) {
            super(left, right);
            this.comparable = comparableSelector;
        }

        @Override
        public Iterator<TElement> iterator() {
            return this.new IntersectionWithComparableIterator<TCompared>(comparable);
        }
    }

    public static class WithEqualityComparator<TElement> extends IntersectionQuery<TElement>{

        private final EqualityComparer<? super TElement> comparator;

        public WithEqualityComparator(Iterable<? extends TElement> left,
                                      Iterable<? extends TElement> right,
                                      EqualityComparer<? super TElement> comparator) {
            super(left, right);
            this.comparator = comparator;
        }

        @Override
        public Iterator<TElement> iterator() {
            return this.new IntersectionWithEqualityIterator(comparator);
        }
    }

    public static class WithNaturalEquality<TElement> extends WithComparable<TElement, TElement>{

        public WithNaturalEquality(Iterable<? extends TElement> left, Iterable<? extends TElement> right) {
            super(left, right, CommonDelegates.identity());
        }
    }

    protected class IntersectionWithEqualityIterator extends PrefetchingIterator<TElement>{

        private final Iterator<? extends TElement> leftIterator = left.iterator();
        private final Queryable<? extends TElement> rightIterator = Factories.cache(right);
        private final EqualityComparer<? super TElement> equalityComparor;

        public IntersectionWithEqualityIterator(EqualityComparer<? super TElement> equalityComparor){
            this.equalityComparor = equalityComparor;
        }

        protected void prefetch() {

            while(leftIterator.hasNext() && ! hasPrefetchedValue()){
                TElement candidate = leftIterator.next();

                if(rightIterator.any(x -> equalityComparor.equals(x, candidate))){
                    setPrefetchedValue(candidate);
                }
            }
        }
    }

    protected class IntersectionWithComparableIterator<TCompared> extends PrefetchingIterator<TElement> {

        private final Queryable<TCompared> rights;
        private final Iterator<? extends TElement> leftIterator = left.iterator();
        private final Func1<? super TElement, TCompared> comparableSelector;

        public IntersectionWithComparableIterator(Func1<? super TElement, TCompared> comparableSelector){
            this.comparableSelector = comparableSelector;
            rights = Factories.cache(right).select(comparableSelector);
        }

        @Override
        protected void prefetch() {

            while(leftIterator.hasNext() && ! hasPrefetchedValue()){

                TElement candidate = leftIterator.next();
                TCompared candidateComparable = comparableSelector.getFrom(candidate);

                if (rights.containsElement(candidateComparable)) {
                    setPrefetchedValue(candidate);
                }
            }
        }
    }
}
