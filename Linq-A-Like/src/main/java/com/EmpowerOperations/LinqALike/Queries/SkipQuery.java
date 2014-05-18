package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.CountSkipIterator;
import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.Delegate.Condition;

import java.util.Iterator;

/**
 * Created by Geoff on 13/04/2014.
 */
public class SkipQuery<TElement> implements DefaultQueryable<TElement> {

    private final Iterable<TElement> sourceElements;
    private final Condition<? super TElement> excludingCondition;
    private final int numberToSkip;

    public SkipQuery(Iterable<TElement> sourceElements, Condition<? super TElement> excludingCondition) {
        this.sourceElements = sourceElements;

        this.excludingCondition = excludingCondition;
        this.numberToSkip = -1;
    }

    public SkipQuery(Iterable<TElement> sourceElements, int numberToSkip){
        this.sourceElements = sourceElements;
        this.numberToSkip = numberToSkip;
        this.excludingCondition = null;
    }

    @Override
    public Iterator<TElement> iterator() {
        if(numberToSkip != -1){
            return new CountSkipIterator<>(sourceElements, numberToSkip);
        }
        else{
            return new ConditionalSkipIterator();
        }
    }

    private class ConditionalSkipIterator extends PrefetchingIterator<TElement> {

        private final Iterator<TElement> source = sourceElements.iterator();
        private boolean wasSkipped = false;

        @Override
        protected void prefetch() {
            if ( ! source.hasNext()){
                return;
            }

            if(wasSkipped){
                setPrefetchedValue(source.next());
            }
            else{
                TElement next;
                do{ next = source.next(); }
                while(excludingCondition.passesFor(next));

                wasSkipped = true;

                setPrefetchedValue(next);
            }
        }
    }

}
