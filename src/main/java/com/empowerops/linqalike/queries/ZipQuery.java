package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func2;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Geoff on 2014-10-24.
 */
public class ZipQuery<TLeft, TRight> implements DefaultedBiQueryable<TLeft, TRight> {

    private final Iterable<TLeft> leftSource;
    private final Iterable<TRight> rightSource;

    public ZipQuery(Iterable<TLeft> left, Iterable<TRight> right) {
        Preconditions.notNull(left, "left");
        Preconditions.fastSameSize(left, right);

        this.leftSource = left;
        this.rightSource = right;
    }

    @Override
    public Iterator<Tuple<TLeft, TRight>> iterator() {
        return new ZipIterator<>(leftSource, rightSource, Tuple::new);
    }

    public static class WithJoinFactory<TLeft, TRight, TJoined> implements DefaultedQueryable<TJoined> {

        private final Iterable<TLeft> leftSource;
        private final Iterable<TRight> rightSource;
        private final Func2<? super TLeft, ? super TRight, TJoined> joiner;

        public WithJoinFactory(Iterable<TLeft> left,
                               Iterable<TRight> right,
                               Func2<? super TLeft, ? super TRight, TJoined> joiner) {

            Preconditions.notNull(left, "left");
            Preconditions.notNull(right, "right");
            Preconditions.notNull(joiner, "joiner");

            this.leftSource = left;
            this.rightSource = right;
            this.joiner = joiner;
        }

        @Override
        public Iterator<TJoined> iterator() {
            return new ZipIterator<>(leftSource, rightSource, joiner);
        }
    }

    private static class ZipIterator<TLeft, TRight, TJoined> implements Iterator<TJoined>{

        private final Iterator<TLeft> left;
        private final Iterator<TRight> right;
        private final Func2<? super TLeft, ? super TRight, TJoined> joiner;
        private final Iterable<TLeft> leftSource;
        private final Iterable<TRight> rightSource;

        public ZipIterator(Iterable<TLeft> left,
                           Iterable<TRight> right,
                           Func2<? super TLeft, ? super TRight, TJoined> joiner){
            this.leftSource = left;
            this.rightSource = right;
            this.left = left.iterator();
            this.right = right.iterator();
            this.joiner = joiner;
        }

        @Override
        public boolean hasNext() {

            if(left.hasNext() ^ right.hasNext()){
                //read: if EITHER left has some OR right has some (but _not_ if both have some)
                throw Preconditions.makeNotSameSizeException(leftSource, rightSource);
            }
            return left.hasNext();
        }

        @Override
        public TJoined next() {
            if ( ! hasNext()){
                throw new NoSuchElementException();
            }

            TLeft pairLeft = left.next();
            TRight pairRight = right.next();

            return joiner.getFrom(pairLeft, pairRight);
        }
    }

}
