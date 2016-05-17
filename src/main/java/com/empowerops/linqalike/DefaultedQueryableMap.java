package com.empowerops.linqalike;

public interface DefaultedQueryableMap<TKey, TValue> extends QueryableMap<TKey, TValue>, DefaultedBiQueryable<TKey, TValue> {

    @Override default TValue getValueFor(TKey key){
        return Linq.getFor(this, key);
    }

    @Override
    default Queryable<TValue> getAll(Iterable<? extends TKey> keys){
        return Factories.from(keys).where(keySet()::containsElement).select(this::getValueFor);
    }

    @Override
    default QueryableMap<TKey, TValue> immediately() {
        return new ReadonlyLinqingMap<>(this);
    }

    @Override
    default LinqingMap<TKey, TValue> toMap() {
        return new LinqingMap<>(this);
    }

    @Override default boolean containsTKey(TKey candidateKey){
        return keySet().containsElement(candidateKey);
    }
    @Override default boolean containsTValue(TValue candidateValue){
        return values().containsElement(candidateValue);
    }

    @Override default QueryableMap<TValue, TKey> inverted(){
        return Linq.inverted(this);
    }

    @Override default QueryableMap<TKey, TValue> reversed(){
        return Linq.reversedMap(this);
    }

    @Override default QueryableMap<TKey, TValue> skip(int numberToSkip){
        return Linq.skipMap(this, numberToSkip);
    }

    @Override
    default ReadonlyLinqingMap<TKey, TValue> toReadonlyMap(){
        return new ReadonlyLinqingMap<>(this);
    }

    @Override default <TDesiredKey>
    QueryableMap<TDesiredKey, TValue> castKeys(){
        return (QueryableMap<TDesiredKey, TValue>) this;
    }

    @Override default <TDesiredValue>
    QueryableMap<TKey, TDesiredValue> castValues(){
        return (QueryableMap<TKey, TDesiredValue>) this;
    }

    @Override default
    Queryable<TKey> keySet(){
        return lefts();
    }

    @Override default
    Queryable<TValue> values(){
        return rights();
    }
}

