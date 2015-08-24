package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedQueryable;

import java.util.*;

/**
 * Created by Geoff on 2014-05-19.
 */
public class EmptyQuery<TElement> implements DefaultedQueryable<TElement> {

    @Override
    public Iterator<TElement> iterator() {
        return new EmptyIterator();
    }

    private class EmptyIterator implements Iterator<TElement> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public TElement next() {
            throw new NoSuchElementException("Empty by definition!");
        }
    }
}
