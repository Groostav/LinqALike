package LinqALike;

import LinqALike.Delegate.*;

import java.util.*;

import static LinqALike.LinqingList.from;

public class LinqingMap<TKey, TValue> extends LinkedHashMap<TKey, TValue> implements QueryableMap<TKey, TValue> {


    /*
     * Static factories
     */
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Map<TKey, TValue> existingMap){
        return new LinqingMap<>(existingMap);
    }
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Iterable<TKey> keys, Iterable<TValue> values) {
        return new LinqingMap<>(keys, values);
    }


    /*
     * constructors
     */
    public LinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        assert ! LinqBehaviour.containsDuplicates(keys);

        Iterator<? extends TValue> valueIterator = values.iterator();
        for(TKey key : keys){
            put(key, valueIterator.next());
        }
    }

    public LinqingMap() {
    }

    public LinqingMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinqingMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinqingMap(Map<? extends TKey, ? extends TValue> toCopy) {
        super(toCopy);
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

    /*
     * Queryable to Util.Collections signature coalescing.
     */
    @Override
    public LinqingSet<TKey> keySet() {
        return new LinqingSet<>(this.select(x -> x.getKey()));
    }

    @Override
    public LinqingList<TValue> values() {
        return new LinqingList<>(this.select(x -> x.getValue()));
    }

    @Override
    public LinqingSet<Map.Entry<TKey, TValue>> entrySet() {
        return new LinqingSet<>(this);
    }
}

