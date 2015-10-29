package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.delegate.Condition;

import java.util.Iterator;

import static com.empowerops.linqalike.Factories.range;

/**
 * Created by Geoff on 13/04/2014.
 */
public abstract class SkipQuery<TElement> implements DefaultedQueryable<TElement> {

    public static class Conditional<TElement> extends SkipQuery<TElement>{

        private final Iterable<TElement> sourceElements;
        private final Condition<? super TElement> excludingCondition;

        public Conditional(Iterable<TElement> sourceElements, Condition<? super TElement> excludingCondition) {
            this.sourceElements = sourceElements;
            this.excludingCondition = excludingCondition;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ConditionalSkipIterator<>(sourceElements, excludingCondition);
        }
    }

    public static class Capped<TElement> extends SkipQuery<TElement> implements FastSize {

        private final Iterable<TElement> sourceElements;
        private final int numberToSkip;

        public Capped(Iterable<TElement> sourceElements, int numberToSkip){
            this.sourceElements = sourceElements;
            this.numberToSkip = numberToSkip;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new CountSkipIterator<>(sourceElements, numberToSkip);
        }

        @Override
        public int size() {
            return Accessors.vSize(sourceElements) - numberToSkip;
        }
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

    private static class CountSkipIterator<TElement> implements Iterator<TElement> {

        private final Iterator<TElement> source;
        private final int countToSkip;

        private boolean wasSkipped = false;

        public CountSkipIterator(Iterable<TElement> sourceElements, int countToSkip){
            this.source = sourceElements.iterator();
            this.countToSkip = countToSkip;
        }

        @Override
        public boolean hasNext() {
            ensureSkipped();
            return source.hasNext();
        }

        @Override
        public TElement next() {
            ensureSkipped();
            return source.next();
        }

        private void ensureSkipped() {
            if(wasSkipped){
                return;
            }

            for(int ignored : range(0, countToSkip)){
                if( ! source.hasNext()){
                    break;
                }

                source.next();
            }

            wasSkipped = true;
        }
    }
}
