package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Delegate.Condition;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.EmpowerOperations.LinqALike.Factories.from;

public class FirstElementsQuery<TElement> implements DefaultQueryable<TElement>{

    private final Iterable<TElement> sourceElements;
    private final int maxToReturn;

    public FirstElementsQuery(Iterable<TElement> sourceElements, int maxToReturn, Condition<? super TElement> condition) {
        this.sourceElements = from(sourceElements).where(condition);
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

            return sourceStream.next();
        }
    }
}
