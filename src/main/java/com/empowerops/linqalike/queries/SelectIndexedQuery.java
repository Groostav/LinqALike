package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.delegate.Func2;

import java.util.Iterator;

/**
 * Created by Geoff on 2017-01-19.
 */
public class SelectIndexedQuery<TElement, TResult> implements DefaultedQueryable<TResult>, FastSize {

    private final Iterable<TElement> sourceElements;
    private final Func2<? super TElement, Integer, TResult> targetSite;

    public SelectIndexedQuery(Iterable<TElement> sourceElements, Func2<? super TElement, Integer, TResult> targetSite) {
        assert sourceElements != null;
        assert targetSite != null;

        this.sourceElements = sourceElements;
        this.targetSite = targetSite;
    }

    @Override
    public Iterator<TResult> iterator() { return new SelectIndexedIterator(); }

    private class SelectIndexedIterator implements Iterator<TResult>{

        private int currentIndex = 0;
        private final Iterator<TElement> baseIterator = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        @Override
        public TResult next() {
            return targetSite.getFrom(baseIterator.next(), currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    @Override
    public int size() {
        return Accessors.vSize(sourceElements);
    }
}
