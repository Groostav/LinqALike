package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.delegate.Condition;

import java.util.Iterator;

/**
* Created by Geoff on 2015-10-30.
*/
public class ConditionalSkipQuery<TElement> implements DefaultedQueryable<TElement>{

    private final Iterable<TElement> sourceElements;
    private final Condition<? super TElement> excludingCondition;

    public ConditionalSkipQuery(Iterable<TElement> sourceElements, Condition<? super TElement> excludingCondition) {
        this.sourceElements = sourceElements;
        this.excludingCondition = excludingCondition;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new ConditionalSkipIterator<>(sourceElements, excludingCondition);
    }

    private static class ConditionalSkipIterator<TElement> extends PrefetchingIterator<TElement> {

        private final Iterator<TElement> source;
        private final Condition<? super TElement> excludingCondition;
        private boolean wasSkipped = false;

        public ConditionalSkipIterator(Iterable<TElement> sourceElements, Condition<? super TElement> excludingCondition){
            this.source = sourceElements.iterator();
            this.excludingCondition = excludingCondition;
        }

        @Override
        protected void prefetch() {
            if ( ! source.hasNext()){
                return;
            }

            if(wasSkipped){
                setPrefetchedValue(source.next());
                return;
            }

            TElement next;
            do{ next = source.next(); }
            while(excludingCondition.passesFor(next) && source.hasNext());

            wasSkipped = true;

            if ( ! excludingCondition.passesFor(next)) {
                setPrefetchedValue(next);
            }
        }
    }
}
