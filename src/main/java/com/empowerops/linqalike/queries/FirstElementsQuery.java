package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.empowerops.linqalike.Factories.from;

public class FirstElementsQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<TElement> sourceElements;
    private final int maxToReturn;

    public FirstElementsQuery(Iterable<TElement> sourceElements, int maxToReturn) {
        this.sourceElements = sourceElements;
        this.maxToReturn = maxToReturn;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new FirstElementsIterator();
    }

    private class FirstElementsIterator implements Iterator<TElement>{

        private Iterator<TElement> sourceStream = sourceElements.iterator();
        private int nReturned = 0;

        @Override
        public boolean hasNext() {
            return nReturned < maxToReturn && sourceStream.hasNext();
        }

        @Override
        public TElement next() {
            if ( ! hasNext()){
                throw new NoSuchElementException();
            }

            nReturned += 1;
            return sourceStream.next();
        }
    }
}
