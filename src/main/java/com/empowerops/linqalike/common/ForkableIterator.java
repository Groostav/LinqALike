package com.empowerops.linqalike.common;

import com.empowerops.linqalike.Queryable;

import java.util.Iterator;

/**
 * Created by Geoff on 12/04/14.
 */
public interface ForkableIterator<TElement> extends Iterator<TElement>{

    public Iterator<TElement> fork();
    public Queryable<TElement> remaining();
}
