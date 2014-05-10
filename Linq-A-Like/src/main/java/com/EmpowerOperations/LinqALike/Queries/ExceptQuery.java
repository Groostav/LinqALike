package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.LinqingList;

import java.util.Iterator;

public class ExceptQuery<TElement> implements DefaultQueryable<TElement> {

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
        private LinqingList<TElement> discoveredExcludedElements = new LinqingList<>(right);

        @Override
        protected void prefetch() {

            while(source.hasNext() && ! hasPrefetchedValue()){

                TElement candidate = source.next();

                if( ! isTobeExcluded(candidate)){
                    setPrefetchedValue(candidate);
                }
            }
        }

        private boolean isTobeExcluded(TElement undesired) {

            for(TElement element : discoveredExcludedElements){
                boolean isExcluded = comparator.equals(element, undesired);
                if(isExcluded){
                    return true;
                }
            }

//            while(toExcludes.hasNext()){
//                TElement candidate = toExcludes.next();
//                discoveredExcludedElements.add(candidate);
//
//                boolean isExcluded = comparator.equals(candidate, undesired);
//                if(isExcluded){
//                    return true;
//                }
//            }

            return false;
        }
    }
}


