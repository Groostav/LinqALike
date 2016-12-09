package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.DefaultedQueryableMap;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.delegate.Condition;

import java.util.Iterator;

/**
 * Created by Geoff on 2016-12-08.
 */
public class TakeWhileQuery<TElement> implements DefaultedQueryable<TElement> {

    private final Iterable<TElement> sourceElements;
    private final Condition<? super TElement> condition;

    public TakeWhileQuery(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {

        this.sourceElements = sourceElements;
        this.condition = condition;
    }

    @Override
    public Iterator<TElement> iterator() {

        return new PrefetchingIterator<TElement>() {

            private final Iterator<TElement> sourceIterator = sourceElements.iterator();
            private boolean foundNonmatch = false;

            @Override
            protected void prefetch() {
                if (foundNonmatch) return;

                while(sourceIterator.hasNext()){
                    TElement next = sourceIterator.next();
                    if (condition.passesFor(next)) {
                        setPrefetchedValue(next);
                        return;
                    }
                    else{
                        foundNonmatch = true;
                        return;
                    }
                }
            }

        };
    }
}
