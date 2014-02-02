package LinqALike;

import java.util.Map;

public interface QueryableMap<TKey, TValue> extends Queryable<Tuple<TKey, TValue>>{

    //sorry for the signature collision, I dont like the collections framework (maybe you've noticed).
    //Note that LinqingSet and LinqingList will satisfy the return types if they collide with Map.keySet() and values()
    public Queryable<TKey> keySet();
    public Queryable<TValue> values();

    @Override
    QueryableMap<TKey, TValue> union(Iterable<? extends Tuple<TKey, TValue>> toInclude);

    @Override
    QueryableMap<TKey, TValue> union(Tuple<TKey, TValue>... toInclude);

    TValue get(Object key);
    Queryable<TValue> getAll(Iterable<? extends TKey> keys);

    boolean containsKey(Object key);
    boolean containsValue(Object value);

    //TODO pull join up here and make it reasonable.

    Map<TKey, TValue> toMap();
}

