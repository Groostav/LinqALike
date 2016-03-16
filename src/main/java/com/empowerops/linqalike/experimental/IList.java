package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.ImmutableCollection;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.QueryableList;
import com.empowerops.linqalike.queries.FastSize;
import com.github.andrewoma.dexx.collection.Vector;

import javax.annotation.Nonnull;
import java.util.*;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2015-12-02.
 */
// note 1:
// safe only because indexOf uses Object.equals for comparison
// this _is_ heap pollution.

public class IList<T> implements ImmutableCollection<T>, List<T>, DefaultedQueryable<T>, QueryableList<T>, FastSize{

    private static final IList Empty = new IList();

    @SuppressWarnings("unchecked") //immutability makes this covariant.

    public static <T> IList<T> empty(){ return Empty; }

    private final Vector<T> backingList;

    public IList(){
        this(Vector.empty());
    }

    public IList(Iterable<? extends T> initialElements){
        backingList = (Vector<T>)Vector.<T>factory().newBuilder().addAll((Iterable)initialElements).build();
    }

    private IList(Vector<T> backingList){
        this.backingList = backingList;
    }

    @SafeVarargs
    public IList(T... initialElements){
        backingList = Vector.<T>factory().newBuilder().addAll(from(initialElements)).build();
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @SuppressWarnings("unchecked") //see note 1
    @Override
    public boolean contains(Object o) {
        return backingList.indexOf((T)o) != -1;
    }

    @SuppressWarnings("unchecked") //see note 1
    @Override
    public int indexOf(Object o) {
        return backingList.indexOf((T)o);
    }

    @SuppressWarnings("unchecked") //see note 1
    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf((T)o);
    }

    @Override
    public @Nonnull ListIterator<T> listIterator() {
        return new IndexedIterator(0);
    }

    @Override
    public @Nonnull ListIterator<T> listIterator(int index) {
        if(index < 0 || index > size()){ throw new IndexOutOfBoundsException("index"); }
        return new IndexedIterator(index);
    }

    @Override
    public @Nonnull IList<T> subList(int fromIndex, int toIndex) {
        return new IList<>(backingList.range(fromIndex, true, toIndex, false));
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
    public @Nonnull <T1> T1[] toArray(@Nonnull T1[] a) {
        return Linq.toArray(this, a);
    }


    @Override
    public IList<T> with(T toInclude) {
        return new IList<>(backingList.append(toInclude));
    }

    @Override
    public IList<T> with(T another0, T another1) {
        return new IList<>(backingList.append(another0).append(another1));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2) {
        return new IList<>(backingList.append(another0).append(another1).append(another2));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2, T another3) {
        return new IList<>(backingList.append(another0).append(another1).append(another2).append(another3));
    }

    @Override
    public IList<T> with(T another0, T another1, T another2, T another3, T another4) {
        return new IList<>(backingList.append(another0).append(another1).append(another2).append(another3).append(another4));
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final IList<T> with(T... othersToInclude) {
        Vector list = backingList;
        for(T elem : othersToInclude){
            list = list.append(elem);
        }
        return new IList<>(list);
    }

    //TODO: this will break lazyness if the supplied argument(s) to with are themselves queryables on mutable collections.

    @Override
    public IList<T> with(Iterable<? extends T> toInclude) {
        Vector<T> list = backingList;
        for(T elem : toInclude){
            list = list.append(elem);
        }
        return new IList<>(list);
    }

    @Override public boolean containsAll(@Nonnull Collection<?> c) {
        for(Object x : c){
            if( ! contains(x)){
                return false;
            }
        }
        return true;
    }

    /**{@inheritDoc}*/ @Override @Deprecated public boolean add(T t) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean remove(Object o) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean addAll(Collection<? extends T> c) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean addAll(int index, Collection<? extends T> c) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public void clear() { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public T set(int index, T element) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public void add(int index, T element) { throw new UnsupportedOperationException(); }
    /**{@inheritDoc}*/ @Override @Deprecated public T remove(int index) { throw new UnsupportedOperationException(); }

    private class IndexedIterator implements ListIterator<T>{

        int cursorPosition;

        IndexedIterator(int initialIndex){
            cursorPosition = initialIndex;
        }

        @Override
        public boolean hasNext() {
            return cursorPosition < size();
        }

        @Override
        public T next() {
            if ( ! hasNext()) { throw new NoSuchElementException(); }
            T result = get(cursorPosition);
            cursorPosition += 1;
            return result;
        }

        @Override
        public boolean hasPrevious() {
            return cursorPosition > 0;
        }

        @Override
        public T previous() {
            if ( ! hasPrevious()){ throw new NoSuchElementException(); }
            cursorPosition -= 1;
            return get(cursorPosition);
        }

        @Override
        public int nextIndex() {
            return cursorPosition + 1;
        }

        @Override
        public int previousIndex() {
            return cursorPosition - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }

}
