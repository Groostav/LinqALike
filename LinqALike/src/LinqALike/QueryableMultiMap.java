package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import java.util.Comparator;
import java.util.Map;

public interface QueryableMultiMap<TKey, TValue> extends QueryableMap<TKey, Queryable<TValue>>{

    public Queryable<TValue> flatValues();


    @Override default QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude,
                                                     Func2<? super Map.Entry<TKey, Queryable<TValue>>, ? super Map.Entry<TKey, Queryable<TValue>>, Boolean> equalityComparison){
        assert false : "not implemented";
        return null;
    }
    @Override default <TCompared>
    QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude,
                                           Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }

    @Override default <TCompared>
    QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude,
                                              Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude,
                                                        Func2<? super Map.Entry<TKey, Queryable<TValue>>, ? super Map.Entry<TKey, Queryable<TValue>>, Boolean> equalityComparison){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> intersect(Map.Entry<TKey, Queryable<TValue>>... toIntersect){
        assert false : "not implemented";
        return null;
    }


    @Override default <TCompared extends Comparable<TCompared>>
    QueryableMultiMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> orderBy(Comparator<? super Map.Entry<TKey, Queryable<TValue>>> equalityComparator){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMultiMap<TKey, TValue> reversed(){
        assert false : "not implemented";
        return null;
    }


    @Override default QueryableMultiMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, Queryable<TValue>>> toExclude){
        assert false : "not implemented";
        return null;
    }
    @Override default QueryableMultiMap<TKey, TValue> skip(int numberToSkip){
        assert false : "not implemented";
        return null;
    }


    default ReadonlyLinqingList<Map.Entry<TKey, TValue>> toFlattenedReadOnly(){
        assert false : "not implemented";
        return null;
    }
    default LinqingList<Map.Entry<TKey, TValue>> toFlattenedList(){
        assert false : "not implemented";
        return null;
    }
    default LinqingSet<Map.Entry<TKey, TValue>> toFlattenedSet(){
        assert false : "not implemented";
        return null;
    }


    @Override
    QueryableMultiMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, Queryable<TValue>>> condition);

}
