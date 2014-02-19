package LinqALike;

import LinqALike.Delegate.Func1;

public interface QueryableMultiMap<TKey, TValue> extends QueryableMap<TKey, Queryable<TValue>>{

    <TTransformedValue>
    QueryableMultiMap<TKey, TTransformedValue> selectFromGroup(Func1<TValue, TTransformedValue> valueSelector);
}
