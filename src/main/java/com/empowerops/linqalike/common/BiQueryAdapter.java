package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.Queryable;

import java.util.Iterator;

public class BiQueryAdapter{

    private BiQueryAdapter(){}

    public static class FromArray<TLeft, TRight> implements DefaultedBiQueryable<TLeft, TRight> {
        private final Tuple<TLeft, TRight>[] elements;

        @SuppressWarnings("unchecked") //safe because of immutability & read-only use of array.
        public FromArray(Tuple<? extends TLeft, ? extends TRight>[] elements){
            this.elements = (Tuple[]) elements;
        }

        @Override
        public Iterator<Tuple<TLeft, TRight>> iterator() {
            return new ArrayIterator<>(elements);
        }

        @Override public int size() {
            return elements.length;
        }
    }

    public static class FromPairs<TLeft, TRight> implements DefaultedBiQueryable<TLeft, TRight>{

        private final Queryable<Tuple<TLeft, TRight>> sourceElements;

        public FromPairs(Queryable<Tuple<TLeft, TRight>> sourceElements){
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<Tuple<TLeft, TRight>> iterator() {
            return sourceElements.iterator();
        }
    }
}
