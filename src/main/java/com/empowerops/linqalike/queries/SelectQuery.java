package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Iterator;

public class SelectQuery<TElement, TResult> implements DefaultedQueryable<TResult>, FastSize {

    private final Iterable<TElement>               sourceElements;
    private final Func1<? super TElement, TResult> targetSite;

    public SelectQuery(Iterable<TElement> sourceElements, Func1<? super TElement, TResult> targetSite) {
        assert sourceElements != null;
        assert targetSite != null;

        this.sourceElements = sourceElements;
        this.targetSite = targetSite;
    }

    @Override
    public Iterator<TResult> iterator() {
        return new SelectIterator<>(sourceElements.iterator(), targetSite);
    }

    private class SelectIterator<TElement, TResult> implements Iterator<TResult>{

        private final Iterator<TElement> baseIterator;
        private final Func1<? super TElement, TResult> transform;

        public SelectIterator(Iterator<TElement> baseIterator, Func1<? super TElement, TResult> transform){
            
            this.baseIterator = baseIterator;
            this.transform = transform;
        }

        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        @Override
        public TResult next() {
            return transform.getFrom(baseIterator.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    //performance critical
    public int size() {
        return Accessors.vSize(sourceElements);
    }
}

