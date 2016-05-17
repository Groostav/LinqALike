package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.queries.FastSize;
import com.github.andrewoma.dexx.collection.HashMap;
import com.github.andrewoma.dexx.collection.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2/16/2016.
 */
public class IMap<TKey, TValue> implements Map<TKey, TValue>, DefaultedQueryableMap<TKey, TValue>, FastSize {

    private static final IMap Empty = new IMap();

    @SuppressWarnings("unchecked")
    public static <TKey, TValue> IMap<TKey, TValue> empty(){ return Empty; }

    private final HashMap<TKey, TValue> backingMap;

    public IMap(){
        this(HashMap.empty());
    }
    public IMap(Map.Entry<TKey, TValue>... initialValues){
        this(HashMap.<TKey, TValue>factory().newBuilder().addAll(from(initialValues).select(kvp -> new Pair<>(kvp.getKey(), kvp.getValue()))).build());
    }
    public IMap(Iterable<Map.Entry<TKey, TValue>> initialValues){
        this(HashMap.<TKey, TValue>factory().newBuilder().addAll(from(initialValues).select(kvp -> new Pair<>(kvp.getKey(), kvp.getValue()))).build());
    }
    private IMap(HashMap<TKey, TValue> backingMap){
        this.backingMap = backingMap;
    }

    @Override
    public Iterator<Tuple<TKey, TValue>> iterator() {
        return new Iterator<Tuple<TKey, TValue>>() {

            private Iterator<Pair<TKey, TValue>> backingIterator = backingMap.iterator();

            @Override
            public boolean hasNext() {
                return backingIterator.hasNext();
            }

            @Override
            public Tuple<TKey, TValue> next() {
                Pair<TKey, TValue> pair = backingIterator.next();
                return new Tuple<>(pair.component1(), pair.component2());
            }
        };
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return backingMap.containsKey((TKey)key);
    }

    @Override
    public boolean containsValue(Object value) {
        return Linq.containsElement(backingMap.values(), value);
    }

    @Override
    public TValue get(Object key) {
        return backingMap.get((TKey) key);
    }

    @Override
    public TValue put(TKey key, TValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TValue remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends TKey, ? extends TValue> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ForwardingLinqingSet<TKey> keySet() {
        return new ForwardingLinqingSet<>(backingMap.asMap().keySet());
    }

    @Override
    public ForwardingLinqingCollection<TValue> values() {
        return new ForwardingLinqingCollection<>(backingMap.asMap().values());
    }

    @Override
    public ForwardingLinqingSet<Entry<TKey, TValue>> entrySet() {
        return new ForwardingLinqingSet<>(backingMap.asMap().entrySet());
    }
}
