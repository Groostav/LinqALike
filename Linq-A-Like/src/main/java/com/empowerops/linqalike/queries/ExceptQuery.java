package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.LinqingList;

import java.util.Iterator;

public class ExceptQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;
    private final EqualityComparer<? super TElement> comparator;

    public ExceptQuery(Iterable<? extends TElement> left,
                       Iterable<? extends TElement> right,
                       EqualityComparer<? super TElement> comparator){

        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");
        Preconditions.notNull(comparator, "comparator");

        this.left = left;
        this.right = right;
        this.comparator = comparator;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new ExceptIterator();
    }

    protected class ExceptIterator extends PrefetchingIterator<TElement> {

        private Iterator<? extends TElement> source = left.iterator();
        private Iterator<? extends TElement> toExcludes = right.iterator();
        private LinqingList<TElement> discoveredExcludedElements = new LinqingList<>();

        @Override
        protected void prefetch() {

            while(source.hasNext() && ! hasPrefetchedValue()){

                TElement candidate = source.next();

                if( ! isTobeExcluded(candidate)){
                    setPrefetchedValue(candidate);
                }
            }
        }

        private boolean isTobeExcluded(TElement unknown) {

            for(TElement excludedElement : discoveredExcludedElements){
                boolean isExcluded = comparator.equals(excludedElement, unknown);
                if(isExcluded){
                    return true;
                }
            }

            while(toExcludes.hasNext()){
                TElement excludedElement = toExcludes.next();
                discoveredExcludedElements.add(excludedElement);

                boolean isExcluded = comparator.equals(excludedElement, unknown);
                if(isExcluded){
                    return true;
                }
            }

            return false;
        }
    }
}


