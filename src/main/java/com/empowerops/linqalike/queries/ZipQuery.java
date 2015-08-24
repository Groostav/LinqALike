package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.delegate.Func2;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Geoff on 2014-10-24.
 */
public class ZipQuery<TLeft, TRight, TJoined> implements DefaultedQueryable<TJoined> {

    private final Iterable<TLeft>                               leftSourceElements;
    private final Iterable<TRight>                              rightSourceElements;
    private final Func2<? super TLeft, ? super TRight, TJoined> resultSelector;

    public ZipQuery(Iterable<TLeft> leftSourceElements,
                    Iterable<TRight> rightSourceElements,
                    Func2<? super TLeft, ? super TRight, TJoined> resultSelector) {

        Preconditions.fastSameSize(leftSourceElements, rightSourceElements);

        this.leftSourceElements = leftSourceElements;
        this.rightSourceElements = rightSourceElements;
        this.resultSelector = resultSelector;
    }

    @Override
    public Iterator<TJoined> iterator() {
        return new JoinIterator();
    }

    private class JoinIterator implements Iterator<TJoined>{

        private final Iterator<TLeft> left = leftSourceElements.iterator();
        private final Iterator<TRight> right = rightSourceElements.iterator();

        @Override
        public boolean hasNext() {
            if(left.hasNext() ^ right.hasNext()){
                throw new IllegalArgumentException("right -- has different number of elements from left");
            }
            return left.hasNext();
        }

        @Override
        public TJoined next() {
            if ( ! hasNext()){
                throw new NoSuchElementException();
            }

            return resultSelector.getFrom(left.next(), right.next());
        }
    }
}
