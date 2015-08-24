package com.empowerops.linqalike.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.empowerops.linqalike.Factories.range;

/**
* Created by Geoff on 13/04/2014.
*/
public class CountSkipIterator<TElement> implements Iterator<TElement> {

    private final Iterator<TElement> source;
    private final int countToSkip;

    private boolean wasSkipped = false;

    public CountSkipIterator(Iterable<TElement> sourceElements, int countToSkip){
        this.source = sourceElements.iterator();
        this.countToSkip = countToSkip;
    }

    @Override
    public boolean hasNext() {
        ensureSkipped();
        return source.hasNext();
    }

    @Override
    public TElement next() {
        ensureSkipped();
        return source.next();
    }

    private void ensureSkipped() {
        if(wasSkipped){
            return;
        }

        for(int currentNumberSkipped : range(0, countToSkip)){
            if( ! source.hasNext()){
                break;
            }

            source.next();
        }

        wasSkipped = true;
    }
}
