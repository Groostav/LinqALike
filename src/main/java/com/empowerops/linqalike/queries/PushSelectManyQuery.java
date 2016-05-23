package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.common.EmptyQuery;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Iterator;

/**
 * Created by Geoff on 5/22/2016.
 */
public class PushSelectManyQuery<TSource, TTransformed> implements DefaultedBiQueryable<TSource, TTransformed> {


    private final Iterable<? extends TSource> sourceElements;
    private final Func1<? super TSource, ? extends Iterable<? extends TTransformed>> selector;

    public PushSelectManyQuery(Iterable<? extends TSource> sourceElements,
                               Func1<? super TSource, ? extends Iterable<? extends TTransformed>> selector) {

        this.sourceElements = sourceElements;
        this.selector = selector;
    }

    @Override
    public Iterator<Tuple<TSource, TTransformed>> iterator() {
        return new PushSelectManyIterator();
    }

    private class PushSelectManyIterator extends PrefetchingIterator<Tuple<TSource, TTransformed>> {
        Iterator<? extends TSource> outerIterator = sourceElements.iterator();
        Iterator<? extends TTransformed> innerIterator = new EmptyQuery<TTransformed>().iterator();
        TSource currentOuter = null;

        @Override
        protected void prefetch() {
            ensureInnerExists();

            if ( ! innerIterator.hasNext()){
                return;
            }

            setPrefetchedValue(new Tuple<>(currentOuter, innerIterator.next()));
        }

        private void ensureInnerExists() {

            while( ! innerIterator.hasNext() && outerIterator.hasNext()) {
                currentOuter = outerIterator.next();
                Iterable<? extends TTransformed> nextBatch = selector.getFrom(currentOuter);
                if(nextBatch == null) {
                    throw new IllegalArgumentException(
                            "selector -- the selector returned null when it was called to select the group from the following object\n" +
                                    "selector: \n" + selector + "\n" +
                                    "supplied object:\n" + currentOuter + "\n"
                    );
                }
                innerIterator = nextBatch.iterator();
            }
        }
    }
}
