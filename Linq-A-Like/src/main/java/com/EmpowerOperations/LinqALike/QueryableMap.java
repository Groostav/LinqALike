package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.util.Comparator;
import java.util.Map;

public interface QueryableMap<TKey, TValue> extends DefaultQueryable<Map.Entry<TKey, TValue>> {

    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    default boolean containsTKey(TKey candidateKey){
        return keySet().containsElement(candidateKey);
    }
    default boolean containsTValue(TValue candidateValue){
        return values().containsElement(candidateValue);
    }

    @Override default QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                                                        EqualityComparer<? super Map.Entry<TKey, TValue>> comparator){
        assert false : "not implemented";
        return null;
    }
    default @Override <TCompared>
    QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                               Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }


    default TValue get(TKey key){
        assert false : "not implemented";
        return null;
    }
    default Queryable<TValue> getAll(Iterable<? extends TKey> keys){
        assert false : "not implemented";
        return null;
    }
    default Queryable<TValue> getAll(TKey ... keys){
        assert false : "not implemented";
        return null;
    }


    @Override default <TCompared>
    QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                         Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                                           EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparison){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> intersect(Map.Entry<TKey, TValue>... toIntersect){
        assert false : "not implemented";
        return null;
    }


    @Override default <TCompared extends Comparable<TCompared>>
    QueryableMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> orderBy(Comparator<? super Map.Entry<TKey, TValue>> equalityComparator){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMap<TKey, TValue> reversed(){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, TValue>> toExclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> skip(int numberToSkip){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMap<TKey, TValue> union(Map.Entry<TKey, TValue>... elements){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude){
        assert false : "not implemented";
        return null;
    }

    @Override
    default <TCompared>
    QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                              Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, TValue>> condition){
        assert false : "not implemented";
        return null;
    }
}

