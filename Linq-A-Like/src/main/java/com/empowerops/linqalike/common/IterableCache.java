package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedQueryable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;
import static com.empowerops.linqalike.ImmediateInspections.hasFastSize;

public class IterableCache<TElement> implements DefaultedQueryable<TElement> {

    private final Iterator<TElement> sourceCursor;
    private final List<TElement> cache;

    public IterableCache(Iterable<TElement> sourceElements) {
        this.sourceCursor = sourceElements.iterator();
        this.cache = hasFastSize(sourceElements)
                ? new ArrayList<>(fastSizeIfAvailable(sourceElements))
                : new ArrayList<>();
    }

    private synchronized void moveUpIfNecessary(int desiredIndex) {

        while(desiredIndex >= cache.size() && sourceCursor.hasNext()){
            cache.add(sourceCursor.next());
        }
    }

    @Override
    public Iterator<TElement> iterator() {
        return new CacheInspectingIterator();
    }

    private class CacheInspectingIterator implements Iterator<TElement> {

        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < cache.size() || sourceCursor.hasNext();
        }

        @Override
        public TElement next() {
            moveUpIfNecessary(currentIndex);
            TElement result = cache.get(currentIndex);
            currentIndex += 1;
            return result;
        }
    }
}
