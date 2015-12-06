package com.empowerops.linqalike.common;

import com.empowerops.linqalike.BiQueryable;
import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.DefaultedQueryableMap;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class QueryAdapter{

    private QueryAdapter(){};

    public static class FromArray<TElement> implements DefaultedQueryable<TElement> {
        private final TElement[] elements;

        public FromArray(TElement[] elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ArrayIterator<>(elements);
        }

        @Override public int size() {
            return elements.length;
        }
    }

    public static class FromCollection<TElement> implements DefaultedQueryable<TElement> {
        private final Collection<TElement> sourceElements;

        public FromCollection(Collection<TElement> sourceElements) {
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return sourceElements.iterator();
        }

        @Override
        public boolean containsElement(TElement candidate) {
            return sourceElements.contains(candidate);
        }

        @Override
        public int size() {
            return sourceElements.size();
        }

        @Override public <TDesired> TDesired[] toArray(TDesired[] typedArray) {
            return sourceElements.toArray(typedArray);
        }

        @Override public Object[] toArray() {
            return sourceElements.toArray();
        }
    }

    public static class FromIterable<TElement> implements DefaultedQueryable<TElement> {
        private final Iterable<TElement> elements;

        public FromIterable(Iterable<TElement> elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return elements.iterator();
        }

    }

    public static class ToQueryableMap<TKey, TValue> implements DefaultedQueryableMap<TKey, TValue> {
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
        public TValue getValueFor(TKey key) {
            return Linq.getFor(sourceQuery, key);
        }
    }

    public static class FromDoubleArray implements DefaultedQueryable<Double> {

        private final double[] sourceElements;

        public FromDoubleArray(double[] sourceElements) {
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<Double> iterator() {
            return new Iterator<Double>() {
                private int currentIndex = 0;

                @Override public boolean hasNext() { return currentIndex < sourceElements.length; }
                @Override public Double next() { return sourceElements[currentIndex++]; }
            };
        }

        @Override public int size() { return sourceElements.length; }
    }


    public static class FromLongIntegers implements DefaultedQueryable<Long> {

        private final long[] sourceElements;

        public FromLongIntegers(long[] sourceElements) {
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<Long> iterator() {
            return new Iterator<Long>() {
                private int currentIndex = 0;

                @Override public boolean hasNext() { return currentIndex < sourceElements.length; }
                @Override public Long next() { return sourceElements[currentIndex++]; }
            };
        }

        @Override public int size() { return sourceElements.length; }
    }

    public static class FromIntegerArray implements DefaultedQueryable<Integer> {

        private final int[] sourceElements;

        public FromIntegerArray(int[] sourceElements) {
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                private int currentIndex = 0;

                @Override public boolean hasNext() { return currentIndex < sourceElements.length; }
                @Override public Integer next() { return sourceElements[currentIndex++]; }
            };
        }

        @Override public int size() { return sourceElements.length; }
    }

    public static class FromByteArray implements DefaultedQueryable<Byte>{

        private final byte[] sourceElements;

        public FromByteArray(byte[] sourceElements) {
            this.sourceElements = sourceElements;
        }

        @Override
        public Iterator<Byte> iterator() {
            return new Iterator<Byte>() {
                private int currentIndex = 0;

                @Override public boolean hasNext() { return currentIndex < sourceElements.length; }
                @Override public Byte next() { return sourceElements[currentIndex++]; }
            };
        }

        @Override public int size() { return sourceElements.length; }
    }

    public static class FromCharacterSequence implements DefaultedQueryable<Character> {
        private final CharSequence sourceCharacter;

        public FromCharacterSequence(CharSequence sourceCharacter) {this.sourceCharacter = sourceCharacter;}

        @Override public Iterator<Character> iterator() {
            return new Iterator<Character>() {
                public int currentIndex = 0;

                @Override public boolean hasNext() {
                    return currentIndex < sourceCharacter.length();
                }

                @Override public Character next() {
                    if( ! hasNext()){
                        throw new NoSuchElementException();
                    }
                    return sourceCharacter.charAt(currentIndex++);
                }
            };
        }

        @Override public int size() { return sourceCharacter.length(); }
    }

    public static class FromImplicitIterator<TElement> implements DefaultedQueryable<TElement> {

        private final TElement                          seed;
        private final Func1<? super TElement, TElement> nextGetter;
        private final Condition<? super TElement>       hasNextGetter;

        public FromImplicitIterator(TElement seed,
                                    Func1<? super TElement, TElement> nextGetter,
                                    Condition<? super TElement> hasNextGetter) {
            this.seed = seed;
            this.nextGetter = nextGetter;
            this.hasNextGetter = hasNextGetter;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ImplicitIterator();
        }

        private class ImplicitIterator implements Iterator<TElement> {
            TElement current = seed;
            private boolean hasYieldedSeed;

            @Override
            public boolean hasNext() {
                return hasNextGetter.passesFor(current);
            }

            @Override
            public TElement next() {
                if ( ! hasNext()) {
                    throw new NoSuchElementException();
                }
                if ( ! hasYieldedSeed) {
                    hasYieldedSeed = true;
                    return seed;
                }
                current = nextGetter.getFrom(current);
                return current;
            }
        }
    }

    public static class FromMap<TKey, TValue> implements DefaultedQueryableMap<TKey, TValue> {

        private final Map<TKey, TValue> source;

        public FromMap(Map<TKey, TValue> source) {
            this.source = source;
        }

        @Override public int size() {
            return source.size();
        }
        @Override public boolean containsTKey(TKey candidateKey) {
            return source.containsKey(candidateKey);
        }
        @Override public boolean containsTValue(TValue candidateValue) {
            return source.containsValue(candidateValue);
        }
        @Override public Iterator<Map.Entry<TKey, TValue>> iterator() {
            return source.entrySet().iterator();
        }

        @Override public TValue getValueFor(TKey key) {
            return source.get(key);
        }
    }
}
