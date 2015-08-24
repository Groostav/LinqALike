package com.empowerops.linqalike.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Geoff on 13/04/2014.
 */
public class RangeIterator implements Iterator<Integer> {

    private final int lowerBoundInclusive;
    private final int upperBoundExclusive;

    public int current;

    public RangeIterator(int lowerBoundInclusive, int upperBoundExclusive){
        this.lowerBoundInclusive = lowerBoundInclusive;
        this.upperBoundExclusive = upperBoundExclusive;

        current = lowerBoundInclusive;
    }

    @Override
    public boolean hasNext() {
        return current < upperBoundExclusive;
    }

    @Override
    public Integer next() {
        if(current >= upperBoundExclusive){
            throw new NoSuchElementException("attempting to iterate past the end of a range");
        }

        return current++;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
