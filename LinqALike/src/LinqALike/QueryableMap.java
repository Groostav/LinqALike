package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import java.util.Map;

public interface QueryableMap<TKey, TValue> extends Queryable<Map.Entry<TKey, TValue>>{

    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    boolean containsTKey(TKey candidateKey);
    boolean containsTValue(TValue candidateValue);


    @Override QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude);
    @Override QueryableMap<TKey, TValue> except(Map.Entry<TKey, TValue>... toExclude);
    @Override QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                                                Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison);
    @Override <TCompared>
    QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                               Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector);


    TValue get(TKey key);
    Queryable<TValue> getAll(Iterable<? extends TKey> keys);
    Queryable<TValue> getAll(TKey ... keys);


    @Override <TCompared>
    QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                         Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector);
    @Override QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                                   Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison);
    @Override QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude);
    @Override QueryableMap<TKey, TValue> intersect(Map.Entry<TKey, TValue>... toIntersect);


    @Override <TCompared extends Comparable<TCompared>>
    QueryableMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector);
    @Override QueryableMap<TKey, TValue> orderBy(Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Integer> equalityComparator);


    @Override QueryableMap<TKey, TValue> reversed();


    @Override QueryableMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, TValue>> toExclude);
    @Override QueryableMap<TKey, TValue> skipUntil(Condition<? super Map.Entry<TKey, TValue>> toInclude);
    @Override QueryableMap<TKey, TValue> skip(int numberToSkip);


    @Override QueryableMap<TKey, TValue> union(Map.Entry<TKey, TValue>... elements);
    @Override QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude);
    @Override <TCompared>
    QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                              Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector);


    @Override QueryableMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, TValue>> condition);
}

