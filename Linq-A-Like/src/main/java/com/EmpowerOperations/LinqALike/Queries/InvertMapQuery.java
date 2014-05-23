package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.PrefetchingIterator;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.DefaultQueryableMap;
import com.EmpowerOperations.LinqALike.ImmediateInspections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Geoff on 2014-05-22.
 */
public class InvertMapQuery<TOldKey, TNewKey> implements DefaultQueryableMap<TNewKey, TOldKey> {

    private final Iterable<? extends Map.Entry<TOldKey, TNewKey>> sourceEntries;

    public InvertMapQuery(Iterable<? extends Map.Entry<TOldKey, TNewKey>> sourceEntries){
        this.sourceEntries = sourceEntries;
    }

    @Override
    public TOldKey getValueFor(TNewKey key) {
        return ImmediateInspections.getFor(this, key);
    }

    @Override
    public Iterator<Map.Entry<TNewKey, TOldKey>> iterator() {
        return new InvertMapIterator();
    }

    private class InvertMapIterator extends PrefetchingIterator<Map.Entry<TNewKey, TOldKey>> {

        Set<TNewKey> keySet = new HashSet<>();
        Iterator<? extends Map.Entry<TOldKey, TNewKey>> backingIterator = sourceEntries.iterator();

        @Override
        protected void prefetch() {

            boolean isNewKey = false;
            Map.Entry<TOldKey, TNewKey> entry = null;

            while( ! isNewKey && backingIterator.hasNext()){
                entry = backingIterator.next();
                isNewKey = keySet.add(entry.getValue());
            }

            if ( ! isNewKey){
                return;
            }

            setPrefetchedValue(new Tuple<>(entry.getValue(), entry.getKey()));
        }
    }
}
