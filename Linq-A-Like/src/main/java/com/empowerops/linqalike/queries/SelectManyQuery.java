package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.*;

import java.util.*;

/**
 * Created by Geoff on 13/04/2014.
 */
public class SelectManyQuery<TSource, TTransformed> implements DefaultedQueryable<TTransformed> {

    private final Iterable<TSource> sourceElements;
    private final Func1<? super TSource, ? extends Iterable<TTransformed>> selector;

    public SelectManyQuery(Iterable<TSource> sourceElements, Func1<? super TSource, ? extends Iterable<TTransformed>> selector) {
        this.sourceElements = sourceElements;
        this.selector = selector;
    }

    @Override
    public Iterator<TTransformed> iterator() {
        return new SelectManyIterator();
    }

    private class SelectManyIterator extends PrefetchingIterator<TTransformed> {
        Iterator<TSource> outerIterator = sourceElements.iterator();
        Iterator<TTransformed> innerIterator = new EmptyQuery<TTransformed>().iterator();

        @Override
        protected void prefetch() {
            ensureInnerExists();

            if ( ! innerIterator.hasNext()){
                return;
            }

            setPrefetchedValue(innerIterator.next());
        }

        private void ensureInnerExists() {

            while( ! innerIterator.hasNext() && outerIterator.hasNext()) {
                TSource next = outerIterator.next();
                Iterable<TTransformed> nextBatch = selector.getFrom(next);
                innerIterator = nextBatch.iterator();
            }
        }
    }
}
