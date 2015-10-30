package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Preconditions;

import java.util.Iterator;
import java.util.List;

import static com.empowerops.linqalike.Factories.range;

/**
 * Created by Geoff on 13/04/2014.
 */
public class CountSkipQuery<TElement> implements DefaultedQueryable<TElement>, FastSize {

    private final Iterable<TElement> sourceElements;
    private final int skipCount;

    public CountSkipQuery(Iterable<TElement> sourceElements, int skipCount){
        Preconditions.notNegative(skipCount, "skipCount");

        this.sourceElements = sourceElements;
        this.skipCount = skipCount;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new CountSkipIterator<>(sourceElements, skipCount);
    }

    @Override
    public int size() {
        return Math.max(0, Accessors.vSize(sourceElements) - skipCount);
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

    public static class ForList<TElement> implements DefaultedQueryable<TElement>, FastSize{

        private final List<TElement> sourceElements;
        private final int skipCount;

        private List<TElement> sublist;

        public ForList(List<TElement> sourceElements, int skipCount){
            Preconditions.notNegative(skipCount, "skipCount");

            this.sourceElements = sourceElements;
            this.skipCount = skipCount;
        }

        @Override
        public Iterator<TElement> iterator() {
            pullSublist();
            return sublist.iterator();
        }

        @Override
        public int size() {
            pullSublist();
            return sublist.size();
        }

        private void pullSublist() {
            if(sublist == null) {
                int fromIndex = Math.max(0, Math.min(sourceElements.size(), skipCount));
                sublist = sourceElements.subList(fromIndex, sourceElements.size());
            }
        }
    }
}
