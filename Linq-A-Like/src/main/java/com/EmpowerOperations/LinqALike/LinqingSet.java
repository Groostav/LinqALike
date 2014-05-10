package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.QueryableSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class LinqingSet<TElement> extends LinkedHashSet<TElement> implements QueryableSet<TElement>, Set<TElement> {

    public LinqingSet() {
    }

    public LinqingSet(Iterable<? extends TElement> startingElements) {
        super();
        for(TElement element : startingElements){
            add(element);
        }
    }
}
