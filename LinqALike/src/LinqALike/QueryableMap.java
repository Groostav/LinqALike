package LinqALike;

import LinqALike.Delegate.Func1;

import java.util.Map;

public interface QueryableMap<TKey, TValue> extends Queryable<Tuple<TKey, TValue>>{

    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    @Override
    QueryableMap<TKey, TValue> union(Iterable<? extends Tuple<TKey, TValue>> toInclude);

    @Override
    QueryableMap<TKey, TValue> union(Tuple<TKey, TValue>... toInclude);

    <TTransformedValue>
    QueryableMap<TKey, TTransformedValue> selectValues(Func1<TValue, TTransformedValue> valueSelector);

    TValue get(Object key);
    Queryable<TValue> getAll(Iterable<? extends TKey> keys);

    boolean containsKey(Object key);
    boolean containsValue(Object value);

    Map<TKey, TValue> toMap();
}

