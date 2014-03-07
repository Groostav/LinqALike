package LinqALike;

import java.util.Iterator;
import java.util.Map;

public class LinqingMultiMap<TKey, TValue> implements QueryableMap<TKey, TValue>{

    @Override
    public Queryable<TKey> keySet() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<TValue> values() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsTKey(TKey candidateKey) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsTValue(TValue candidateValue) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Map.Entry<TKey, TValue>> iterator() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
