package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LinqingMap<TKey, TValue> extends LinkedHashMap<TKey, TValue> implements DefaultedQueryableMap<TKey, TValue> {

    private static final long serialVersionUID = 5468979086836647070L;

    public LinqingMap() {
    }

    public LinqingMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        Iterator<? extends TValue> valueIterator = values.iterator();
        for(TKey key : keys){
            if(containsTKey(key)) { throw new IllegalArgumentException("keys -- keys must be distinct"); }
            put(key, valueIterator.next());
        }
    }

    public LinqingMap(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> initialValues){
        addAll(initialValues);
    }

    @SafeVarargs
    public LinqingMap(Map.Entry<? extends TKey, ? extends TValue> ... initialValues){
        addAll(initialValues);
    }

    public LinqingMap(TKey firstKey, TValue firstValue){
        super();
        put(firstKey, firstValue);
    }

    //only intended for use by derriving classes
    protected LinqingMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    protected LinqingMap(int initialCapacity) {
        super(initialCapacity);
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

    public void addAll(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }
    @SafeVarargs
    public final void addAll(Map.Entry<? extends TKey, ? extends TValue>... keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }

    @Override
    public TValue getValueFor(TKey key) {
        return get(key);
    }

    /*
     * Queryable to Util.Collections signature coalescing.
     */
    @Override
    public @Nonnull ForwardingLinqingSet<TKey> keySet() {
        return new ForwardingLinqingSet<>(super.keySet());
    }

    @Override
    public @Nonnull ForwardingLinqingCollection<TValue> values() {
        return new ForwardingLinqingCollection<>(super.values());
    }

    @Override
    public @Nonnull ForwardingLinqingSet<Map.Entry<TKey, TValue>> entrySet() {
        return new ForwardingLinqingSet<>(super.entrySet());
    }

    @Override
    public boolean containsTKey(TKey candidateKey) {
        return super.containsKey(candidateKey);
    }

    @Override
    public boolean containsTValue(TValue candidateValue) {
        return values().contains(candidateValue);
    }

    @Override
    public Iterator<Tuple<TKey, TValue>> iterator() {
        //why cant I just return entrySet.iterator()?
        // Map.Entry -> Tuple,
        // why are we using a tuple here?
        // spcified by BiQueryable,
        // why not use MapEntry there?
        // left and right vs key and value. Hmm.
        return entrySet().select(it -> new Tuple<>(it.getKey(), it.getValue())).iterator();
    }

    public TValue removeEntry(TKey key) {
        return super.remove(key);
    }
}

