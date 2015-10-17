package com.empowerops.linqalike;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

public final class ForwardingLinqingMap<TKey, TValue> implements QueryableMap<TKey, TValue>,
                                                                 Map<TKey, TValue>,
                                                                 DefaultedQueryableMap<TKey, TValue>{

    private final Map<TKey, TValue> source;

    public ForwardingLinqingMap(Map<TKey, TValue> source) {
        this.source = source;
    }

    @Override public Iterator<Entry<TKey, TValue>> iterator() {
        return source.entrySet().iterator();
    }
    @Override public boolean containsKey(Object key) {
        return source.containsKey(key);
    }
    @Override public boolean containsValue(Object value) {
        return source.containsValue(value);
    }
    @Override public TValue get(Object key) {
        return source.get(key);
    }
    @Override public TValue getValueFor(TKey key) {
        return source.get(key);
    }
    @Override public TValue put(TKey key, TValue value) {
        return source.put(key, value);
    }
    @Override public TValue remove(Object key) {
        return source.remove(key);
    }
    @Override public void putAll(Map<? extends TKey, ? extends TValue> m) {
        source.putAll(m);
    }
    @Override public void clear() {
        source.clear();
    }
    @Override public boolean isEmpty() {
        return source.isEmpty();
    }
    @Override public int size() {
        return source.size();
    }
    @Override @Nonnull public ForwardingLinqingSet<Entry<TKey, TValue>> entrySet() {
        return new ForwardingLinqingSet<>(source.entrySet());
    }
    @Override @Nonnull public ForwardingLinqingCollection<TValue> values() {
        return new ForwardingLinqingCollection<>(source.values());
    }
    @Override public ForwardingLinqingSet<TKey> keySet() {
        return new ForwardingLinqingSet<>(source.keySet());
    }
}
