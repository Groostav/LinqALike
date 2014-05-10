package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.QueryableSet;
import com.EmpowerOperations.LinqALike.Factories;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class UnionQuery<TElement, TCompared> implements DefaultQueryable<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;
    private final EqualityComparer<? super TElement> equalityComparator;

    public UnionQuery(Iterable<? extends TElement> left,
                      Iterable<? extends TElement> right,
                      EqualityComparer<? super TElement> equalityComparator) {

        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");
        Preconditions.notNull(equalityComparator, "equalityComparator");

        this.left = left;
        this.right = right;
        this.equalityComparator = equalityComparator;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new UnionIterator();
    }

    private class UnionIterator implements Iterator<TElement>{

        private final Iterator<? extends TElement> lefts = left.iterator();
        private final Iterator<? extends TElement> rights;

        private boolean leftsWereAvailable = true;
        private boolean rightsWereAvailable = true;

        @SuppressWarnings("unchecked")//LinqingList.from consumes its argument in a read-only nature,
        // making its argument's type parameter covariant. Thus we wont get a run-time exception from this uncheckedCast
        //now or further on in the program.
        private UnionIterator(){
            rights = left instanceof QueryableSet
                    ? Factories.from((Iterable<TElement>) right).except(left, equalityComparator).iterator()
                    : right.iterator();
        }

        @Override
        public boolean hasNext() {
            return lefts.hasNext() || rights.hasNext();
        }

        @Override
        public TElement next() {
            leftsWereAvailable = leftsWereAvailable && lefts.hasNext();
            if(leftsWereAvailable){
                return lefts.next();
            }

            rightsWereAvailable = rightsWereAvailable && rights.hasNext();
            if(rightsWereAvailable){
                return rights.next();
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
