package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import java.util.ArrayList;
import java.util.Map;

public interface QueryableMultiMap<TKey, TValue> extends QueryableMap<TKey, Queryable<TValue>>{

    public Queryable<TValue> flatValues();


    @Override QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude);
    @Override QueryableMultiMap<TKey, TValue> except(ArrayList<Map.Entry<TKey, Queryable<TValue>>> toExclude);
    @Override QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude,
                                                     Func2<Map.Entry<TKey, Queryable<TValue>>, Map.Entry<TKey, Queryable<TValue>>, Boolean> equalityComparison);
    @Override <TCompared>
    QueryableMultiMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toExclude,
                                           Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector);


    @Override <TCompared>
    QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude,
                                              Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector);
    @Override QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude,
                                                        Func2<? super Map.Entry<TKey, Queryable<TValue>>, ? super Map.Entry<TKey, Queryable<TValue>>, Boolean> equalityComparison);
    @Override QueryableMultiMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, Queryable<TValue>>> toInclude);
    @Override QueryableMultiMap<TKey, TValue> intersect(Map.Entry<TKey, Queryable<TValue>>... toIntersect);


    @Override <TCompared extends Comparable<TCompared>>
    QueryableMultiMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, Queryable<TValue>>, TCompared> comparableSelector);
    @Override QueryableMultiMap<TKey, TValue> orderBy(Func2<? super Map.Entry<TKey, Queryable<TValue>>, ? super Map.Entry<TKey, Queryable<TValue>>, Integer> equalityComparator);


    @Override QueryableMultiMap<TKey, TValue> reversed();


    @Override QueryableMultiMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, Queryable<TValue>>> toExclude);
    @Override QueryableMultiMap<TKey, TValue> skipUntil(Condition<? super Map.Entry<TKey, Queryable<TValue>>> toInclude);
    @Override QueryableMultiMap<TKey, TValue> skip(int numberToSkip);


    ReadonlyLinqingList<Map.Entry<TKey, TValue>> toFlattenedReadOnly();
    LinqingList<Map.Entry<TKey, TValue>> toFlattenedList();
    LinqingSet<Map.Entry<TKey, TValue>> toFlattenedSet();


    @Override
    QueryableMultiMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, Queryable<TValue>>> condition);

}
