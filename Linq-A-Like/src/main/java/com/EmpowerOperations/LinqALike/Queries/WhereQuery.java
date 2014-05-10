package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.Delegate.Condition;

import java.util.Iterator;

public class WhereQuery<TElement> implements DefaultQueryable<TElement> {

    private final Iterable<TElement> elements;
    private final Condition<? super TElement> condition;

    public WhereQuery(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(condition, "condition");

        this.elements = sourceElements;
        this.condition = condition;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new SelectIterator();
    }

    private class SelectIterator extends PrefetchingIterator<TElement> {

        private final Iterator<TElement> previousIterator = elements.iterator();

        @Override
        protected void prefetch() {
            while( ! hasPrefetchedValue() && previousIterator.hasNext()){
                TElement candidate = previousIterator.next();

                if(condition.passesFor(candidate)){
                    setPrefetchedValue(candidate);
                }
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


