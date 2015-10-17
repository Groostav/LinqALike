package com.empowerops.linqalike;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Created by Geoff on 2015-04-11.
 */
public final class ForwardingLinqingCollection<TElement> implements Collection<TElement>,
                                                                    QueryableCollection<TElement>,
                                                                    WritableCollection<TElement>,
                                                                    DefaultedQueryable<TElement>{

    private final Collection<TElement> source;

    public ForwardingLinqingCollection(Collection<TElement> source){
        this.source = source;
    }

    @Override public final int size() {
        return source.size();
    }
    @Override public final boolean isEmpty() {
        return source.isEmpty();
    }
    @Override public final boolean contains(Object o) {
        return source.contains(o);
    }
    @Override public final @Nonnull Iterator<TElement> iterator() {
        return source.iterator();
    }
    @Override public final @Nonnull Object[] toArray() {
        return source.toArray();
    }
    @Override public final @Nonnull <T> T[] toArray(@Nonnull T[] a) {
        return source.toArray(a);
    }
    @Override public final boolean add(TElement element) {
        return source.add(element);
    }
    @Override public final boolean removeElement(TElement toRemove) {
        return source.remove(toRemove);
    }
    @Override public final boolean remove(Object o) {
        return source.remove(o);
    }
    @Override public final boolean containsAll(@Nonnull Collection<?> c) {
        return source.containsAll(c);
    }
    @Override public final boolean addAll(@Nonnull Collection<? extends TElement> c) {
        return source.addAll(c);
    }
    @Override public final boolean removeAll(@Nonnull Collection<?> c) {
        return source.removeAll(c);
    }
    @Override public final boolean retainAll(@Nonnull Collection<?> c) {
        return source.retainAll(c);
    }
    @Override public final void clear() {
        source.clear();
    }
    @Override public boolean removeIf(Predicate<? super TElement> filter) {
        return source.removeIf(filter);
    }
}
