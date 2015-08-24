package com.empowerops.linqalike;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;

public class LinqingSet<TElement> extends LinkedHashSet<TElement> implements
        Set<TElement>,
        DefaultedQueryable<TElement> ,
        WritableCollection<TElement> {

    private static final long serialVersionUID = - 5877901452254113802L;

    public static final int DefaultLinqingSetSize = 16;

    public LinqingSet() {
        super();
    }

    public LinqingSet(int size) {
        super(size == - 1 ? DefaultLinqingSetSize : size);
    }

    @SafeVarargs
    public LinqingSet(TElement... elements) {
        this(elements.length);
        addAll(elements);
    }

    public LinqingSet(Iterator<? extends TElement> elements) {
        this();
        addAllRemaining(elements);
    }

    public LinqingSet(Iterable<? extends TElement> elements){
        this(fastSizeIfAvailable(elements));
        addAll(elements);
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        return remove(toRemove);
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }
}

