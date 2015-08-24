package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.delegate.Condition;

import java.util.Iterator;

public class WhereQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<TElement>          sourceElements;
    private final Condition<? super TElement> condition;

    public WhereQuery(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(condition, "condition");

        this.sourceElements = sourceElements;
        this.condition = condition;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new WhereIterator();
    }

    private class WhereIterator extends PrefetchingIterator<TElement> {

        private final Iterator<TElement> previousIterator = sourceElements.iterator();

        @Override
        protected void prefetch() {
            while (!hasPrefetchedValue() && previousIterator.hasNext()) {
                TElement candidate = previousIterator.next();

                if (condition.passesFor(candidate)) {
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


