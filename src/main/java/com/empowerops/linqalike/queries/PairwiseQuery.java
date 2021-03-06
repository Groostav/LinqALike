package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Justin on 7/21/2014.
 */
public class PairwiseQuery<TElement> implements DefaultedBiQueryable<TElement, TElement> {

    private final Iterable<TElement> sourceElements;
    private final Func<? extends TElement> defaultFactory;

    public PairwiseQuery(Iterable<TElement> sourceElements,
                         Func<? extends TElement> defaultFactory) {

        this.sourceElements = sourceElements;
        this.defaultFactory = defaultFactory;
    }

    @Override
    public Iterator<Tuple<TElement, TElement>> iterator() {
        return new PairwiseIterator();
    }

    private class PairwiseIterator implements Iterator<Tuple<TElement, TElement>> {

        private boolean hasRetrievedLast = false;
        private TElement lastRetrieved = defaultFactory.getValue();
        private Iterator<TElement> source = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return ! hasRetrievedLast;
        }

        @Override
        public Tuple<TElement, TElement> next() {

            if ( ! this.hasNext()){
                throw new NoSuchElementException();
            }

            TElement left = lastRetrieved;
            TElement right;

            if (source.hasNext()) {
                right = source.next();
            }
            else {
                right = defaultFactory.getValue();
                hasRetrievedLast = true;
            }

            Tuple nextPair = new Tuple<>(left, right);
            lastRetrieved = right;
            return nextPair;
        }
    }
}
