package com.EmpowerOperations.LinqALike.Common;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<TElement> implements Iterator<TElement> {

    private final TElement[] arrayToIterateOver;
    private int currentIndex;

    public ArrayIterator(TElement[] arrayToIterateOver){
        this.arrayToIterateOver = arrayToIterateOver;
        currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < arrayToIterateOver.length;
    }

    @Override
    public TElement next() {
        if(currentIndex == arrayToIterateOver.length){
            throw new NoSuchElementException("attempting to iterate past the end of an array");
        }
        return arrayToIterateOver[currentIndex++];
    }

    @Override
    public void remove() {
        arrayToIterateOver[currentIndex++] = null;
    }
}
