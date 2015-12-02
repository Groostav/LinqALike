package com.empowerops.linqalike;


import java.util.*;
import java.util.function.Predicate;

public class NavigableLinqingSet<TElement> extends TreeSet<TElement> implements
        QueryableNavigableSet<TElement>,
        NavigableSet<TElement>,
        DefaultedQueryable<TElement>,
        WritableCollection<TElement>{

    @Override
    public boolean removeElement(TElement toRemove) {
        return this.remove(toRemove);
    }

    public NavigableLinqingSet(SortedSet<TElement> s) {
        super(s);
    }

    public NavigableLinqingSet(Iterable<? extends TElement> c) {
        super();
        addAll(c);
    }

    public NavigableLinqingSet(Comparator<? super TElement> comparator) {
        super(comparator);
    }

    public NavigableLinqingSet() {
        super();
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        return QueryableNavigableSet.super.removeIf(filter);
    }

    @Override
    public QueryableNavigableSet<TElement> subSet(TElement fromElement,
                                                  boolean fromInclusive,
                                                  TElement toElement,
                                                  boolean toInclusive) {

        return new ForwardingNavigableSet<>(super.subSet(fromElement, fromInclusive, toElement, toInclusive));
    }

    @Override
    public QueryableNavigableSet<TElement> headSet(TElement toElement, boolean inclusive) {
        return new ForwardingNavigableSet<>(super.headSet(toElement, inclusive));
    }

    @Override
    public QueryableNavigableSet<TElement> tailSet(TElement fromElement, boolean inclusive) {
        return new ForwardingNavigableSet<>(super.tailSet(fromElement, inclusive));
    }

    @Override
    public QueryableNavigableSet<TElement> subSet(TElement fromElement, TElement toElement) {
        return QueryableNavigableSet.super.subSet(fromElement, toElement);
    }

    @Override
    public QueryableNavigableSet<TElement> headSet(TElement toElement) {
        return QueryableNavigableSet.super.headSet(toElement);
    }

    @Override
    public QueryableNavigableSet<TElement> tailSet(TElement fromElement) {
        return QueryableNavigableSet.super.tailSet(fromElement);
    }
}
