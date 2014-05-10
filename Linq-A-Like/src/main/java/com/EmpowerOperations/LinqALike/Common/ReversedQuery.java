package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.util.*;

public class ReversedQuery<TElement> implements DefaultQueryable<TElement> {

    private Iterable<TElement> source;

    public ReversedQuery(Iterable<TElement> source) {
        this.source = source;
    }

    @Override
    public Iterator<TElement> iterator() {
        if(source instanceof List){
            return new ReversedListIterator<>((List<TElement>) source);
        }
        else {
            return new ReversingIterator<>(source);
        }
    }

    public static class ReversedListIterator<TElement> implements Iterator<TElement> {

        private final ListIterator<TElement> listIterator;

        public ReversedListIterator(List<TElement> source){
            this.listIterator = source.listIterator(source.size());
        }

        @Override
        public boolean hasNext() {
            return listIterator.hasPrevious();
        }

        @Override
        public TElement next() {
            return listIterator.previous();
        }

        @Override
        public void remove() {
            listIterator.remove();
        }
    }

    public static class ReversingIterator<TElement> implements Iterator<TElement>{

        private final Iterable<TElement> source;
        private Iterator<TElement> reversed;

        public ReversingIterator(Iterable<TElement> source){
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            ensureHasReversedElements();

            return reversed.hasNext();
        }

        @Override
        public TElement next() {
            ensureHasReversedElements();

            if ( ! reversed.hasNext()){
                throw new NoSuchElementException();
            }

            return reversed.next();
        }

        private void ensureHasReversedElements(){
            if (reversed != null){
                return;
            }

            List<TElement> stack = new ArrayList<>();
            for(TElement element : source){
                stack.add(element);
            }

            reversed = new ReversedListIterator<>(stack);
        }
    }
}

