package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Delegate.Func1;

import java.util.Iterator;

public class SelectQuery<TElement, TResult> implements DefaultQueryable<TResult> {

    private final Iterable<TElement> set;
    private final Func1<? super TElement, TResult> targetSite;

    public SelectQuery(Iterable<TElement> set, Func1<? super TElement, TResult> targetSite) {
        assert set != null;
        assert targetSite != null;
        
        this.set = set;
        this.targetSite = targetSite;
    }

    @Override
    public Iterator<TResult> iterator() {
        return new SelectIterator<>(set.iterator(), targetSite);
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
}

