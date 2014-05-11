package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.util.Iterator;

public abstract class QueryAdapter{

    public static class Array <TElement> implements DefaultQueryable<TElement> {
        private final TElement[] elements;

        public Array(TElement[] elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ArrayIterator<>(elements);
        }
    }

    public static class Iterable<TElement> implements DefaultQueryable<TElement> {
        private final java.lang.Iterable<TElement> elements;

        public Iterable(java.lang.Iterable<TElement> elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return elements.iterator();
        }
    }

}
