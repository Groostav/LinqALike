package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.common.ComparingLinkedHashSet;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.PrefetchingIterator;

import java.util.Iterator;


public class UnionQuery<TElement> implements DefaultedQueryable<TElement> {

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

    private class UnionIterator extends PrefetchingIterator<TElement> {

        private final ComparingLinkedHashSet<TElement> setSoFar = new ComparingLinkedHashSet<>(equalityComparator);

        private final Iterator<? extends TElement> lefts = left.iterator();
        private final Iterator<? extends TElement> rights = right.iterator();

        private boolean leftsWereAvailable = true;
        private boolean rightsWereAvailable = true;

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void prefetch() {
            while(leftIsAvailble()){
                TElement nextValue = lefts.next();
                boolean isNewElement = setSoFar.add(nextValue);
                if(isNewElement){
                    setPrefetchedValue(nextValue);
                    return;
                }
            }

            while(rightIsAvailble()){
                TElement nextValue = rights.next();
                boolean isNewElement = setSoFar.add(nextValue);
                if(isNewElement){
                    setPrefetchedValue(nextValue);
                    return;
                }
            }
        }

        private boolean leftIsAvailble() {
            return leftsWereAvailable = leftsWereAvailable && lefts.hasNext();
        }

        private boolean rightIsAvailble() {
            return rightsWereAvailable = rightsWereAvailable && rights.hasNext();
        }
    }

    @Override
    //Performance Critical
    public boolean containsElement(TElement candidate) {
        return Linq.containsElement(left, candidate) || Linq.containsElement(right, candidate);
    }

    @Override
    public int size() {
        return Linq.size(left) + Linq.size(right);
    }
}
