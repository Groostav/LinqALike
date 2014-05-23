package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Delegate.*;

import java.util.*;

public class LinqingMap<TKey, TValue> extends LinkedHashMap<TKey, TValue> implements DefaultQueryableMap<TKey, TValue> {

    public LinqingMap() {
    }
    public LinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        assert Linq.isDistinct(keys);

        Iterator<? extends TValue> valueIterator = values.iterator();
        for(TKey key : keys){
            put(key, valueIterator.next());
        }
    }

    public LinqingMap(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> initialValues){
        putAll(initialValues);
    }

    @SafeVarargs
    public LinqingMap(Map.Entry<? extends TKey, ? extends TValue> ... initialValues){
        putAll(initialValues);
    }


    /*
     * Mutator methods
     */
    public TValue getOrMake(TKey key, Func<? extends TValue> makeValue){
        if(containsKey(key)){
            return get(key);
        }
        else{
            TValue newValue = makeValue.getValue();
            put(key, newValue);
            return newValue;
        }
    }

    public void put(Map.Entry<? extends TKey, ? extends TValue> keyValuePair){
        put(keyValuePair.getKey(), keyValuePair.getValue());
    }

    public void putAll(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }
    public void putAll(Map.Entry<? extends TKey, ? extends TValue> ... keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }

    @Override
    public TValue getFor(TKey key) {
        return get(key);
    }

    /*
         * Queryable to Util.Collections signature coalescing.
         */
    @Override
    public LinqingSet<TKey> keySet() {
        return new LinqingSet<>(super.keySet());
    }

    @Override
    public LinqingList<TValue> values() {
        return new LinqingList<>(super.values());
    }

    @Override
    public boolean containsTKey(TKey candidateKey) {
        return keySet().contains(candidateKey);
    }

    @Override
    public boolean containsTValue(TValue candidateValue) {
        return values().contains(candidateValue);
    }

    @Override
    public LinqingSet<Map.Entry<TKey, TValue>> entrySet() {
        return new LinqingSet<>(this);
    }

    @Override
    public Iterator<Map.Entry<TKey, TValue>> iterator() {
        return super.entrySet().iterator();
    }
}

