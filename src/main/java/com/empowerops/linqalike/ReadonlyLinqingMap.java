package com.empowerops.linqalike;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

import static com.empowerops.linqalike.Factories.from;

public class ReadonlyLinqingMap<TKey, TValue> extends LinqingMap<TKey, TValue> implements QueryableMap<TKey, TValue>{

    private static final long serialVersionUID = 8512110180368538233L;

    private static final ReadonlyLinqingMap emptyInstance = new ReadonlyLinqingMap();

    @SuppressWarnings("unchecked") //cant have a pollution since there are no instances to pollute with!!
    public static <TKey, TValue> ReadonlyLinqingMap<TKey, TValue> emptyInstance(){ return emptyInstance; }


    public ReadonlyLinqingMap() {
    }

    public ReadonlyLinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        ctorPutAll(from(keys).zip(values).iterator());
    }

    public ReadonlyLinqingMap(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> initialValues) {
        ctorPutAll(initialValues.iterator());
    }

    public ReadonlyLinqingMap(Map.Entry<? extends TKey, ? extends TValue>... initialValues) {
        ctorPutAll(from(initialValues).iterator());
    }

    private void ctorPutAll(Iterator<? extends Map.Entry<? extends TKey, ? extends TValue>> initialEntries){
        while(initialEntries.hasNext()){
            Map.Entry<? extends TKey, ? extends TValue> initialEntry = initialEntries.next();
            super.put(initialEntry.getKey(), initialEntry.getValue());
        }
    }

    @Override public void addAll(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> keyValueTuples) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override public void put(Map.Entry<? extends TKey, ? extends TValue> keyValuePair) {
        throw new UnsupportedOperationException("put");
    }

    @Override public TValue removeEntry(TKey key) {
        throw new UnsupportedOperationException("removeEntry");
    }

    @Override public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    @Override public void replaceAll(BiFunction<? super TKey, ? super TValue, ? extends TValue> function) {
        throw new UnsupportedOperationException("replaceAll");
    }

    @Override public TValue put(TKey key, TValue value) {
        throw new UnsupportedOperationException("put");
    }

    @Override public void putAll(Map<? extends TKey, ? extends TValue> m) {
        throw new UnsupportedOperationException("putAll");
    }

    @Override public TValue putIfAbsent(TKey key, TValue value) {
        throw new UnsupportedOperationException("putIfAbsent");
    }

    @Override public TValue replace(TKey key, TValue value) {
        throw new UnsupportedOperationException("replace");
    }

    @Override public boolean replace(TKey key, TValue oldValue, TValue newValue) {
        throw new UnsupportedOperationException("replace");
    }

    @Override public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public TValue merge(TKey key, TValue value, BiFunction<? super TValue, ? super TValue, ? extends TValue> remappingFunction) {
        throw new UnsupportedOperationException("merge");
    }
}
