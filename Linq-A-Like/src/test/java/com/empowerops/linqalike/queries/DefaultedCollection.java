package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.WritableCollection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class exists so that we have a known explicit Iterator-only (no optimization available)
 * collection implementation.
 *
 * <p>I must confess, its harder to write a cheap array list than I thought!
 */
public class DefaultedCollection<T> implements WritableCollection<T>, DefaultedQueryable<T> {

    public static final int TestCollectionMaxCapacity = 50;

    private final Object[] data = new Object[TestCollectionMaxCapacity];
    private int lastUsedIndex = -1;

    @Override public boolean add(T newElement) {
        lastUsedIndex += 1;
        data[lastUsedIndex] = newElement;
        return true;
    }

    @Override public boolean removeElement(T toRemove) {
        boolean modified = false;

        for(int i = 0; i < data.length; i++){
            if(data[i] == toRemove){
                data[i] = null;
                modified = true;
                break;
            }
        }

        return modified;
    }

    @Override public Iterator<T> iterator() {
        return new ArrayIterator<>();
    }

    private final class ArrayIterator<T> implements Iterator<T>{

        int currentIndex = 0;

        @Override public boolean hasNext() {
            bringCurrentIndexUp();

            return currentIndex < data.length;
        }

        @Override public T next() {
            bringCurrentIndexUp();


            if ( ! hasNext()){
                throw new NoSuchElementException();
            }

            T result = (T) data[currentIndex];
            currentIndex += 1;
            return result;
        }

        private void bringCurrentIndexUp() {
            while(currentIndex < data.length && data[currentIndex] == null){
                currentIndex += 1;
            }
        }
    }
}
