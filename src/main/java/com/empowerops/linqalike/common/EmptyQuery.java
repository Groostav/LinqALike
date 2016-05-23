package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.EmptyIterator;
import com.empowerops.linqalike.LinqingSet;

import java.util.*;

/**
 * Created by Geoff on 2014-05-19.
 */
public class EmptyQuery<TElement> implements DefaultedQueryable<TElement> {

    @Override
    public Iterator<TElement> iterator() {
        return EmptyIterator.getInstance();
    }
}

