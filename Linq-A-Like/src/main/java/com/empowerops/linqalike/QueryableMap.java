package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Map;

/**
 * Created by Geoff on 2014-05-22.
 */
public interface QueryableMap<TKey, TValue> extends Queryable<Map.Entry<TKey, TValue>> {

    /**
     * gets the value that this key maps to, or <code>null</code>
     * if not suitable key-value pair exists in this map.
     *
     * Type-safe implementation of {@link Map#get(Object)}
     *
     * @see Map#get(Object)
     */
    public TValue getValueFor(TKey key);

    /**
     * @see java.util.Map#keySet()
     */
    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    boolean containsTKey(TKey candidateKey);
    boolean containsTValue(TValue candidateValue);

    public LinqingMap<TKey, TValue> toMap();

    public QueryableMap<TValue, TKey> inverted();

    public Queryable<TValue> getAll(Iterable<? extends TKey> keys);

    @Override
    public QueryableMap<TKey, TValue> immediately();

    public ReadonlyLinqingMap<TKey, TValue> toReadonlyMap();

    public <TDesiredValue> QueryableMap<TKey, TDesiredValue> castValues();
    public <TDesiredKey> QueryableMap<TDesiredKey, TValue> castKeys();

    @Override QueryableMap<TKey, TValue> union(Map.Entry<TKey, TValue>... toInclude);

    @Override QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude);

    @Override
    <TCompared> QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                                 Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector);

    @Override
    QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                     EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparator);
}