package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Queries.*;

import java.util.*;

/**
 * Created by Geoff on 2014-05-19.
 */
public class EmptyQuery<TElement> implements DefaultQueryable<TElement> {

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
