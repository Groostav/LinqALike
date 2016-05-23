package com.empowerops.linqalike;

import java.util.Map;

/**
 * ~deprecated: use BiQueryable; this interface is misleading as all queries implement get() in linear time,
 * so its less misleading to use a BiQueryable.
 *
 * Created by Geoff on 2014-05-22.
 */
public interface QueryableMap<TKey, TValue> extends BiQueryable<TKey, TValue> {

    /**
     * gets the value that this key maps to, or <code>null</code>
     * if not suitable key-value pair exists in this map.
     *
     * Type-safe implementation of {@link Map#get(Object)}
     *
     * @see Map#get(Object)
     */
    @Override
    TValue getValueFor(TKey key);

    @Override
    Queryable<TValue> getAll(Iterable<? extends TKey> keys);
    /**
     * @see java.util.Map#keySet()
     */
    Queryable<TKey> keySet();

    Queryable<TValue> values();

    boolean containsTKey(TKey candidateKey);
    boolean containsTValue(TValue candidateValue);

    QueryableMap<TValue, TKey> inverted();

    @Override
    QueryableMap<TKey, TValue> immediately();

    ReadonlyLinqingMap<TKey, TValue> toReadonlyMap();

    <TDesiredValue> QueryableMap<TKey, TDesiredValue> castValues();
    <TDesiredKey> QueryableMap<TDesiredKey, TValue> castKeys();
}