package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.LinqingList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OrderByQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Comparator<? super TElement> comparator;
    private final Iterable<TElement>           sourceElements;

    public OrderByQuery(Iterable<TElement> sourceElements, Comparator<? super TElement> comparator) {
        this.sourceElements = sourceElements;
        this.comparator = comparator;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new OrderByIterator();
    }

    public class OrderByIterator implements Iterator<TElement>{

        private Iterator<TElement> sortedIterator;

        @Override
        public boolean hasNext() {
            return sortedIterator == null
                    ? Linq.any(sourceElements)
                    : sortedIterator.hasNext();
        }

        @Override
        public TElement next() {
            ensureListIsSorted();
            return sortedIterator.next();
        }

        private void ensureListIsSorted() {
            if(sortedIterator != null){
                return;
            }

            List<TElement> sorted = new LinqingList<>(sourceElements);
            sorted.sort(comparator);
            sortedIterator = sorted.iterator();
        }
    }
}
