package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Common.CountSkipIterator;
import com.EmpowerOperations.LinqALike.Common.ForkableIterator;

import java.util.Iterator;

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
    public Iterator<TElement> fork() {
        Iterable<TElement> broughtCurrentCopy = () -> new CountSkipIterator<>(source, seenCount);
        return new ForkingIterator<>(broughtCurrentCopy);
    }

    @Override
    public boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public TElement next() {
        return backingIterator.next();
    }
}
