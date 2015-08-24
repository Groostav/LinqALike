package com.empowerops.linqalike;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

/**
 * Created by Geoff on 2015-04-11.
 */
//methods made final in an attempt to get JVM to ignore this middle-man where it can
public final class ForwardingLinqingList<TElement> implements QueryableList<TElement>,
                                                              List<TElement>,
                                                              WritableCollection<TElement>,
                                                              DefaultedQueryable<TElement>{

    private final List<TElement> source;

    public ForwardingLinqingList(List<TElement> source) {
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
    @Override public final boolean remove(Object o) {
        return source.remove(o);
    }
    @Override public final boolean containsAll(@Nonnull Collection<?> c) {
        return source.containsAll(c);
    }
    @Override public final boolean addAll(@Nonnull Collection<? extends TElement> c) {
        return source.addAll(c);
    }
    @Override public boolean addAll(int index, @Nonnull Collection<? extends TElement> c) {
        return source.addAll(index, c);
    }
    @Override public final boolean retainAll(@Nonnull Collection<?> c) {
        return source.retainAll(c);
    }
    @Override public final boolean removeAll(@Nonnull Collection<?> c) {
        return source.removeAll(c);
    }
    @Override public final void clear() {
        source.clear();
    }
    @Override public final TElement get(int index) {
        return source.get(index);
    }
    @Override public final TElement set(int index, TElement element) {
        return source.set(index, element);
    }
    @Override public final void add(int index, TElement element) {
        source.add(index, element);
    }
    @Override public final TElement remove(int index) {
        return source.remove(index);
    }
    @Override public final int indexOf(Object o) {
        return source.indexOf(o);
    }
    @Override public final int lastIndexOf(Object o) {
        return source.lastIndexOf(o);
    }
    @Override public final @Nonnull ListIterator<TElement> listIterator() {
        return source.listIterator();
    }
    @Override public final @Nonnull ListIterator<TElement> listIterator(int index) {
        return source.listIterator(index);
    }
    @Override public final @Nonnull QueryableList<TElement> subList(int fromIndex, int toIndex) {
        return source instanceof QueryableList
                ? (QueryableList)source.subList(fromIndex, toIndex)
                : new ForwardingLinqingList<>(source.subList(fromIndex, toIndex));
    }
    @Override public int indexOfElement(TElement elementToFind) {
        return source.indexOf(elementToFind);
    }
    @Override public int lastIndexOfElement(TElement elementToFind) {
        return source.lastIndexOf(elementToFind);
    }
    @Override public final boolean removeElement(TElement toRemove) {
        return source.remove(toRemove);
    }
    @Override public boolean removeIf(Predicate<? super TElement> filter) {
        return source.removeIf(filter);
    }
}

