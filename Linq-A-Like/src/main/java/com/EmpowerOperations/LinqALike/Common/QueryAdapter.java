package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.lang.Iterable;
import java.util.Iterator;

public abstract class QueryAdapter{

    public static class FromArray<TElement> implements DefaultQueryable<TElement> {
        private final TElement[] elements;

        public FromArray(TElement[] elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ArrayIterator<>(elements);
        }
    }

    public static class FromIterable<TElement> implements DefaultQueryable<TElement> {
        private final Iterable<TElement> elements;

        public FromIterable(Iterable<TElement> elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return elements.iterator();
        }
    }

}
