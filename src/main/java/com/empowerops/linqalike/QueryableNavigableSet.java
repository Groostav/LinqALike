package com.empowerops.linqalike;

import javax.annotation.Nonnull;
import java.util.NavigableSet;

public interface QueryableNavigableSet<TElement> extends Queryable<TElement>, NavigableSet<TElement> {

    @Override
    @Nonnull
    default QueryableNavigableSet<TElement> subSet(TElement fromElement, TElement toElement){
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    @Nonnull
    QueryableNavigableSet<TElement> subSet(TElement fromElement,
                                           boolean fromInclusive,
                                           TElement toElement,
                                           boolean toInclusive);

    @Override
    @Nonnull
    default QueryableNavigableSet<TElement> headSet(TElement toElement){
        return headSet(toElement, false);
    }

    @Override
    @Nonnull
    QueryableNavigableSet<TElement> headSet(TElement toElement, boolean inclusive);

    @Override
    @Nonnull
    default QueryableNavigableSet<TElement> tailSet(TElement fromElement){
        return tailSet(fromElement, true);
    }

    @Override
    @Nonnull
    QueryableNavigableSet<TElement> tailSet(TElement fromElement, boolean inclusive);
}

