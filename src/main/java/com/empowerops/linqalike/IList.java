package com.empowerops.linqalike;

import org.pcollections.Empty;
import org.pcollections.PVector;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Created by Geoff on 2015-12-02.
 */
public class IList<T> implements List<T>, DefaultedQueryable<T>{

    private final PVector<T> backingList;

    public IList(){
        backingList = Empty.vector();
    }

    public IList(PVector<T> backingList){
        this.backingList = backingList;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @Override
    public @Nonnull ListIterator<T> listIterator() {
        return backingList.listIterator();
    }

    @Override
    public @Nonnull ListIterator<T> listIterator(int index) {
        return backingList.listIterator(index);
    }

    @Override
    public @Nonnull List<T> subList(int fromIndex, int toIndex) {
        return backingList.subList(fromIndex, toIndex);
    }

    @Override
    public T get(int index) {
        return backingList.get(index);
    }

    @Override
    public @Nonnull Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public @Nonnull Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    public @Nonnull <T1> T1[] toArray(T1[] a) {
        return backingList.toArray(a);
    }

    @Override
    public Queryable<T> with(T toInclude) {
        return new IList<>(backingList.plus(toInclude));
    }

    @Override
    public Queryable<T> with(T another0, T another1) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1)));
    }

    @Override
    public Queryable<T> with(T another0, T another1, T another2) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2)));
    }

    @Override
    public Queryable<T> with(T another0, T another1, T another2, T another3) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2, another3)));
    }

    @Override
    public Queryable<T> with(T another0, T another1, T another2, T another3, T another4) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2, another3, another4)));
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final Queryable<T> with(T... othersToInclude) {
        return new IList<>(backingList.plusAll(Arrays.asList(othersToInclude)));
    }

    @Override
    public Queryable<T> with(Iterable<? extends T> toInclude) {
        Collection<? extends T> source = toInclude instanceof Collection
                ? (Collection) toInclude
                : Factories.asList(toInclude);

        return new IList<>(backingList.plusAll(source));
    }

    @Override
    public boolean add(T t) {
        return backingList.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return backingList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return backingList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return backingList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return backingList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return backingList.retainAll(c);
    }

    @Override
    public void clear() {
        backingList.clear();
    }


    @Override
    public T set(int index, T element) {
        return backingList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        backingList.add(index, element);
    }

    @Override
    public T remove(int index) {
        return backingList.remove(index);
    }

}
