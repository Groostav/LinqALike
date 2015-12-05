package com.empowerops.linqalike;

import com.empowerops.linqalike.queries.FastSize;
import org.pcollections.PVector;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Geoff on 2015-12-02.
 */
@SuppressWarnings("deprecation") //intentional and carried forward.
public class IList<T> implements ImmutableCollection<T>, List<T>, DefaultedQueryable<T>, QueryableList<T>, FastSize{

    private static final IList Empty = new IList();
    @SuppressWarnings("unchecked") //thanks to immutableness this is safe!
    public static <T> IList<T> empty(){ return Empty; }


    private final PVector<T> backingList;

    public IList(){
        backingList = org.pcollections.Empty.vector();
    }

    public IList(PVector<T> backingList){
        this.backingList = backingList;
    }

    @SafeVarargs
    public IList(T... initialElements){
        PVector<T> backingList = org.pcollections.Empty.vector();
        for(T initial : initialElements){
            backingList = backingList.plus(initial);
        }
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
    public @Nonnull IList<T> subList(int fromIndex, int toIndex) {
        return new IList<>(backingList.subList(fromIndex, toIndex));
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
    public IList<T> with(T toInclude) {
        return new IList<>(backingList.plus(toInclude));
    }

    public IList<T> with(int index, T toInclude){
        //TODO hmm, this means my 'with' is their 'add' and their 'with' is juc's 'set' method.
        return new IList<>(backingList.plus(index, toInclude));
    }

    @Override
    public IList<T> with(T another0, T another1) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1)));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2)));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2, T another3) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2, another3)));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2, T another3, T another4) {
        return new IList<>(backingList.plusAll(Arrays.asList(another0, another1, another2, another3, another4)));
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final IList<T> with(T... othersToInclude) {
        return new IList<>(backingList.plusAll(Arrays.asList(othersToInclude)));
    }

    //TODO: this will break lazyness if the supplied argument(s) to with are themselves queryables on mutable collections.

    @Override
    public IList<T> with(Iterable<? extends T> toInclude) {
        Collection<? extends T> source = toInclude instanceof Collection
                ? (Collection) toInclude
                : Factories.asList(toInclude);

        return new IList<>(backingList.plusAll(source));
    }

    /** {@inheritDoc} **/
    @Override
    @Deprecated
    public boolean add(T t) {
        return backingList.add(t);
    }

    /**{@inheritDoc}*/ @Override @Deprecated public boolean remove(Object o) { return backingList.remove(o); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean containsAll(Collection<?> c) {return backingList.containsAll(c);}
    /**{@inheritDoc}*/ @Override @Deprecated public boolean addAll(Collection<? extends T> c) {return backingList.addAll(c);}
    /**{@inheritDoc}*/ @Override @Deprecated public boolean addAll(int index, Collection<? extends T> c) {return backingList.addAll(index, c);}
    /**{@inheritDoc}*/ @Override @Deprecated public boolean removeAll(Collection<?> c) {return backingList.removeAll(c);}
    /**{@inheritDoc}*/ @Override @Deprecated public boolean retainAll(Collection<?> c) {return backingList.retainAll(c);}
    /**{@inheritDoc}*/ @Override @Deprecated public void clear() {backingList.clear();}
    /**{@inheritDoc}*/ @Override @Deprecated public T set(int index, T element) {return backingList.set(index, element);}
    /**{@inheritDoc}*/ @Override @Deprecated public void add(int index, T element) {backingList.add(index, element);}
    /**{@inheritDoc}*/ @Override @Deprecated public T remove(int index) {return backingList.remove(index);}

}
