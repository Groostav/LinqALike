package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CastQuery<TUncasted, TCasted> implements DefaultedQueryable<TCasted> {

    private final Iterable<TUncasted> sourceElements;
    private final Class<TCasted> desiredType;

    public CastQuery(Iterable<TUncasted> sourceElements, Class<TCasted> desiredType) {
        this.sourceElements = sourceElements;
        this.desiredType = desiredType;
    }

    @Override
    public Iterator<TCasted> iterator() {
        return new CastIterator();
    }

    private class CastIterator implements Iterator<TCasted>{

        private final Iterator<TUncasted> sourceItr = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return sourceItr.hasNext();
        }

        @Override
        public TCasted next() {
            if ( ! hasNext()) { throw new NoSuchElementException(); }
            return desiredType.cast(sourceItr.next());
        }
    }
}
