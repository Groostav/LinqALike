package com.EmpowerOperations.LinqALike;

import java.util.Map;

/**
 * Created by Geoff on 2014-05-22.
 */
public interface QueryableMap<TKey, TValue> extends Queryable<Map.Entry<TKey, TValue>> {

    public TValue getFor(TKey key);

    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    boolean containsTKey(TKey candidateKey);
    boolean containsTValue(TValue candidateValue);

    public LinqingMap<TKey, TValue> toMap();

    public QueryableMap<TValue, TKey> inverted();
}