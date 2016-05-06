package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Comparator;
import java.util.Map;

public interface DefaultedQueryableMap<TKey, TValue> extends QueryableMap<TKey, TValue>, DefaultedQueryable<Map.Entry<TKey, TValue>> {

    @Override default public TValue getValueFor(TKey key){
        return Linq.getFor(this, key);
    }

    @Override
    public default Queryable<TValue> getAll(Iterable<? extends TKey> keys){
        return Factories.from(keys).where(keySet()::containsElement).select(this::getValueFor);
    }

    @Override default public Queryable<TKey> keySet(){
        return new LinqingSet<>(this.select(Map.Entry::getKey));
    }
    @Override default public Queryable<TValue> values(){
        return Linq.MapSpecific.values(this);
    }

    @Override default boolean containsTKey(TKey candidateKey){
        return keySet().containsElement(candidateKey);
    }
    @Override default boolean containsTValue(TValue candidateValue){
        return values().containsElement(candidateValue);
    }

    @Override default public LinqingMap<TKey, TValue> toMap(){
        return Linq.MapSpecific.toMap(this);
    }

    @Override default public QueryableMap<TValue, TKey> inverted(){
        return Linq.MapSpecific.inverted(this);
    }

    //signature updates

    @Override default QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude){
        return Linq.MapSpecific.except(this, toExclude);
    }
    @Override default QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                                                        EqualityComparer<? super Map.Entry<TKey, TValue>> comparator){
        return Linq.MapSpecific.except(this, toExclude, comparator);
    }
    default @Override <TCompared>
    QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                               Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        return Linq.MapSpecific.except(this, toExclude, comparableSelector);
    }

    @Override default <TCompared>
    QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                         Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        return Linq.MapSpecific.intersect(this, toInclude, comparableSelector);
    }
    @Override default QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                                           EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparison){
        return Linq.MapSpecific.intersect(this, toInclude, equalityComparison);
    }
    @Override default QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude){
        return Linq.MapSpecific.intersect(this, toInclude);
    }
    @Override default QueryableMap<TKey, TValue> intersect(Map.Entry<TKey, TValue>... toInclude){
        return Linq.MapSpecific.intersect(this, toInclude);
    }

    @Override default <TCompared extends Comparable<TCompared>>
    QueryableMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        return Linq.MapSpecific.orderBy(this, comparableSelector);
    }
    @Override default QueryableMap<TKey, TValue> orderBy(Comparator<? super Map.Entry<TKey, TValue>> equalityComparator){
        return Linq.MapSpecific.orderBy(this, equalityComparator);
    }


    @Override default QueryableMap<TKey, TValue> reversed(){
        return Linq.MapSpecific.reversed(this);
    }


    @Override default QueryableMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, TValue>> toExclude){
        return Linq.MapSpecific.skipWhile(this, toExclude);
    }
    @Override default QueryableMap<TKey, TValue> skip(int numberToSkip){
        return Linq.MapSpecific.skip(this, numberToSkip);
    }


    @Override default QueryableMap<TKey, TValue> union(Map.Entry<TKey, TValue>... toInclude){
        return Linq.MapSpecific.union(this, toInclude);
    }
    @Override default QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude){
        return Linq.MapSpecific.union(this, toInclude);
    }

    @Override default QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                                       EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparator){
        return Linq.MapSpecific.union(this, toInclude, equalityComparator);
    }
    @Override default <TCompared>
    QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                     Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        return Linq.MapSpecific.union(this, toInclude, comparableSelector);
    }

    @Override default QueryableMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, TValue>> condition){
        return Linq.MapSpecific.where(this, condition);
    }

    @Override
    public default QueryableMap<TKey, TValue> immediately(){
        return new ReadonlyLinqingMap<>(this);
    }

    @Override
    public default ReadonlyLinqingMap<TKey, TValue> toReadonlyMap(){
        return new ReadonlyLinqingMap<>(this);
    }

    @Override public default <TDesiredKey>
    QueryableMap<TDesiredKey, TValue> castKeys(){
        return (QueryableMap<TDesiredKey, TValue>) this;
    }

    @Override public default <TDesiredValue>
    QueryableMap<TKey, TDesiredValue> castValues(){
        return (QueryableMap<TKey, TDesiredValue>) this;
    }


}

