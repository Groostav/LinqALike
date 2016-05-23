package com.empowerops.linqalike;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Geoff on 5/22/2016.
 */
public final class EmptyIterator implements Iterator<Object> {

    private static EmptyIterator instance = new EmptyIterator();

    @SuppressWarnings("unchecked") //safe through covariance
    public static <T> Iterator<T> getInstance() {
        return (Iterator) instance;
    }

    private EmptyIterator(){}

    @Override public boolean hasNext() { return false; }
    @Override public Object next() { throw new NoSuchElementException();}
}
