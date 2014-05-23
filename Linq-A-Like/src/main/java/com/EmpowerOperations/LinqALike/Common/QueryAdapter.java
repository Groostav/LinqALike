package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.DefaultQueryableMap;
import com.EmpowerOperations.LinqALike.Linq;
import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.util.Iterator;
import java.util.Map;

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

    public static class ToQueryableMap<TKey, TValue> implements DefaultQueryableMap<TKey, TValue> {
        private final Iterable<? extends Map.Entry<TKey, TValue>> sourceQuery;

        public ToQueryableMap(Iterable<? extends Map.Entry<TKey, TValue>> sourceQuery){
            this.sourceQuery = sourceQuery;
        }

        @Override
        @SuppressWarnings("unchecked") // safe because of iterator is covariant
        public Iterator<Map.Entry<TKey, TValue>> iterator() {
            return (Iterator<Map.Entry<TKey, TValue>>) sourceQuery.iterator();
        }

        @Override
        public TValue getFor(TKey key) {
            return Linq.getFor(sourceQuery, key);
        }
    }
}
