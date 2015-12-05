package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.queries.FastSize;
import org.pcollections.PSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Geoff on 2015-12-02.
 */
public final class ISet<TElement> implements Set<TElement>, ImmutableCollection<TElement>, DefaultedQueryable<TElement>, FastSize{

    private static final ISet Empty = new ISet();
    @SuppressWarnings("unchecked") //thanks to immutability this is safe!
    public static <T> ISet<T> empty(){ return Empty; }


    private final PSet<TElement> backingSet;

    public ISet(){
        backingSet = org.pcollections.Empty.orderedSet();
    }

    public ISet(PSet<TElement> source) {
        backingSet = source;
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingSet.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingSet.containsAll(c);
    }

    @Override
    public Iterator<TElement> iterator() {
        return backingSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return backingSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backingSet.toArray(a);
    }

    public ISet<TElement> union(TElement toInclude) {
        return new ISet<>(backingSet.plus(toInclude));
    }

    @Override
    @SafeVarargs
    public final ISet<TElement> union(TElement... toInclude) {
        Preconditions.notNull(toInclude, "toInclude");
        return new ISet<>(backingSet.plusAll(Arrays.asList(toInclude)));
    }

    @Override
    public ISet<TElement> union(Iterable<? extends TElement> toInclude) {
        Preconditions.notNull(toInclude, "toInclude");

        Collection<? extends TElement> source = toInclude instanceof Collection
                ? (Collection) toInclude
                : Factories.asList(toInclude);
        return new ISet<>(backingSet.plusAll(source));
    }

    @Override
    public <TCompared> Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                                                 Func1<? super TElement, TCompared> comparableSelector) {
        //TODO
        return comparableSelector == CommonDelegates.identity()
                ? union(toInclude)
                : DefaultedQueryable.super.union(toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                                     EqualityComparer<? super TElement> equalityComparator) {
        //TODO
        return equalityComparator == CommonDelegates.DefaultEquality
                ? union(toInclude)
                : DefaultedQueryable.super.union(toInclude, equalityComparator);
    }

    public ISet<TElement> except(TElement toExclude) {
        return new ISet<>(backingSet.minus(toExclude));
    }

    @Override
    @SafeVarargs
    public final ISet<TElement> except(TElement... toExclude) {
        Preconditions.notNull(toExclude, "toExclude");

        return new ISet<>(backingSet.minusAll(Arrays.asList(toExclude)));
    }

    @Override
    public ISet<TElement> except(Iterable<? extends TElement> toExclude) {
        Preconditions.notNull(toExclude, "toExclude");

        Collection<? extends TElement> source = toExclude instanceof Collection
                ? (Collection) toExclude
                : Factories.asList(toExclude);
        return new ISet<>(backingSet.minusAll(source));
    }

    @Override
    public Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                      EqualityComparer<? super TElement> comparator) {
        //TODO
        return DefaultedQueryable.super.except(toExclude, comparator);
    }

    @Override
    public <TCompared> Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                                  Func1<? super TElement, TCompared> comparableSelector) {
        //TODO
        return DefaultedQueryable.super.except(toExclude, comparableSelector);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #union(Object)}
     */
    @Override
    public boolean add(TElement tElement) {
        return backingSet.add(tElement);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #except(Object)}
     */
    @Override
    public boolean remove(Object o) {
        return backingSet.remove(o);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #union(Iterable)}
     */
    @Override
    public boolean addAll(Collection<? extends TElement> c) {
        return backingSet.addAll(c);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return backingSet.retainAll(c);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #except(Iterable)}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return backingSet.removeAll(c);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     */
    @Override
    public void clear() {
        backingSet.clear();
    }
}
