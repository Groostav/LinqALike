package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.ImmediateInspections;
import com.EmpowerOperations.LinqALike.LinqingList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OrderByQuery<TElement> implements DefaultQueryable<TElement> {

    private final Comparator<? super TElement> comparator;
    private final Iterable<TElement> source;

    public OrderByQuery(Iterable<TElement> source, Comparator<? super TElement> comparator){
        this.source = source;
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
                    ? ImmediateInspections.any(source)
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

            List<TElement> sorted = new LinqingList<>(source);
            sorted.sort(comparator);
            sortedIterator = sorted.iterator();
        }
    }
}
