package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedQueryable;

import java.util.Iterator;

/**
* Created with IntelliJ IDEA.
* User: Geoff
* Date: 03/11/13
* Time: 15:10
* To change this template use File | Settings | File Templates.
*/
public class RepeatQuery<TElement> implements DefaultedQueryable<TElement>{

    public static final int ForeverSignalValue = - 1;

    private final int      repititionCount;
    private final TElement valueToRepeat;

    public RepeatQuery(TElement valueToRepeat) {
        this(valueToRepeat, ForeverSignalValue);
    }

    public RepeatQuery(TElement valueToRepeat, int repititionCount) {
        this.repititionCount = repititionCount;
        this.valueToRepeat = valueToRepeat;
    }

    @Override public Iterator<TElement> iterator() {
        return new RepeatingIterator<TElement>(valueToRepeat, repititionCount);
    }

    public static class RepeatingIterator<TElement> implements Iterator<TElement> {

        private final int      reptitionCount;
        private final TElement valueToRepeat;
        private int currentIndex = 0;

        public RepeatingIterator(TElement valueToRepeat, int repititionCount) {
            this.valueToRepeat = valueToRepeat;
            this.reptitionCount = repititionCount;
        }

        @Override
        public boolean hasNext() {
            return reptitionCount == ForeverSignalValue || currentIndex < reptitionCount;
        }

        @Override
        public TElement next() {
            currentIndex += 1;
            return valueToRepeat;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
