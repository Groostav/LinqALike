package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Queryable;

import java.util.Iterator;

/**
 * Created by Geoff on 12/04/14.
 */
public interface ForkableIterator<TElement> extends Iterator<TElement>{

    public Iterator<TElement> fork();
    public Queryable<TElement> remaining();
}
