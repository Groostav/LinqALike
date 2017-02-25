package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.delegate.BiCondition;

import java.util.Iterator;

/**
 * Created by Geoff on 2017-01-19.
 */
public class WhereIndexedQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<TElement>                     sourceElements;
    private final BiCondition<? super TElement, Integer> condition;

    public WhereIndexedQuery(Iterable<TElement> sourceElements, BiCondition<? super TElement, Integer> condition) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(condition, "condition");

        this.sourceElements = sourceElements;
        this.condition = condition;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new WhereIndexedIterator();
    }

    private class WhereIndexedIterator extends PrefetchingIterator<TElement> {

        private int index = 0;
        private final Iterator<TElement> previousIterator = sourceElements.iterator();

        @Override
        protected void prefetch() {
            while (!hasPrefetchedValue() && previousIterator.hasNext()) {
                TElement candidate = previousIterator.next();

                if (condition.passesFor(candidate, index++)) {
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
