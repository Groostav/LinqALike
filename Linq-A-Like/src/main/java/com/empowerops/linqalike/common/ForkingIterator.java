package com.empowerops.linqalike.common;

import com.empowerops.linqalike.Queryable;

import java.util.Iterator;

import static com.empowerops.linqalike.Linq.skip;

/**
* Created by Geoff on 13/04/2014.
*/
public class ForkingIterator<TElement> implements ForkableIterator<TElement> {

    private final Iterable<TElement> source;
    private final Iterator<TElement> backingIterator;

    private int seenCount = 0;

    public ForkingIterator(Iterable<TElement> source){
        this.source = source;
        backingIterator = source.iterator();
    }

    @Override
    public ForkableIterator<TElement> fork() {
        return new ForkingIterator<>(remaining());
    }

    @Override
    public Queryable<TElement> remaining() {
        return skip(source, seenCount);
    }

    @Override
    public boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public TElement next() {
        seenCount += 1;
        return backingIterator.next();
    }
}
