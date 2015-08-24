package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryableMap;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.common.Tuple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Geoff on 2014-05-22.
 */
public class InvertMapQuery<TOldKey, TNewKey> implements DefaultedQueryableMap<TNewKey, TOldKey> {

    private final Iterable<? extends Map.Entry<TOldKey, TNewKey>> sourceEntries;

    public InvertMapQuery(Iterable<? extends Map.Entry<TOldKey, TNewKey>> sourceEntries){
        this.sourceEntries = sourceEntries;
    }

    @Override
    public TOldKey getValueFor(TNewKey key) {
        return Linq.getFor(this, key);
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
