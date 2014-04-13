package LinqALike.Queries;

import LinqALike.Delegate.Func1;
import LinqALike.Queryable;

import java.util.Iterator;

/**
 * Created by Geoff on 13/04/2014.
 */
public class SelectManyQuery<TSource, TTransformed> implements Queryable<TTransformed> {


    public SelectManyQuery(Iterable<TSource> sourceElements, Func1<? super TSource, ? extends Iterable<TTransformed>> selector) {
        assert false : "not implemented";
    }

    @Override
    public Iterator<TTransformed> iterator() {
        return null;
    }
}
