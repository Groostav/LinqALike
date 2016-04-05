package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.queries.FastSize;

import java.util.Iterator;

/**
 * Created by Geoff on 3/9/2016.
 */
public class PushSelectQuery<TSource, TTransformed> implements DefaultedBiQueryable<TSource, TTransformed>, FastSize{

    private Iterable<TSource> source;
    private Func1<? super TSource, TTransformed> selector;

    public PushSelectQuery(Iterable<TSource> source, Func1<? super TSource, TTransformed> selector){
        this.source = source;
        this.selector = selector;
    }

    public Queryable<TSource> popSelect(){ return lefts(); }

    @Override
    public Iterator<Tuple<TSource, TTransformed>> iterator() {
        return new PushSelectIterator();
    }

    @Override
    public int size() {
        return source instanceof FastSize
                ? ((FastSize) source).size()
                : ImmediateInspections.size(source);
    }

    private class PushSelectIterator implements Iterator<Tuple<TSource, TTransformed>>{

        private final Iterator<TSource> sourceItr = source.iterator();

        @Override
        public boolean hasNext() {
            return sourceItr.hasNext();
        }

        @Override
        public Tuple<TSource, TTransformed> next() {
            TSource nextSource = sourceItr.next();
            TTransformed nextTransformed = selector.getFrom(nextSource);
            return new Tuple<>(nextSource, nextTransformed);
        }
    }
}
