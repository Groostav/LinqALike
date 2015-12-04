package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.EqualityComparer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2015-12-03.
 */
public class WithQuery<TElement> implements DefaultedQueryable<TElement>, FastSize {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;

    public WithQuery(Iterable<? extends TElement> left, TElement[] right) {
        this.left = left;
        this.right = from(right);
    }

    public WithQuery(Iterable<? extends TElement> left, Iterable<? extends TElement> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new WithQueryIterator();
    }

    @Override
    public int size() {
        return Accessors.vSize(left) + Accessors.vSize(right);
    }

    public class WithQueryIterator implements Iterator<TElement>{

        private final Iterator<? extends TElement> lefts = left.iterator();
        private final Iterator<? extends TElement> rights = right.iterator();

        boolean leftsEmpty;
        boolean rightsEmpty;

        @Override
        public boolean hasNext() {
            leftsEmpty = leftsEmpty || ! lefts.hasNext();
            rightsEmpty = rightsEmpty || ! rights.hasNext();
            return !leftsEmpty || !rightsEmpty;
        }

        @Override
        public TElement next() {
            if ( ! hasNext()){ throw new NoSuchElementException(); }
            return ! leftsEmpty ? lefts.next() : rights.next();
        }
    }
}
