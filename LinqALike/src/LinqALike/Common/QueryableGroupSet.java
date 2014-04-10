package LinqALike.Common;

import LinqALike.Delegate.Func1;
import LinqALike.Queryable;

/**
 * Created by Geoff on 04/04/14.
 */
public interface QueryableGroupSet<TElement> extends Queryable<Queryable<TElement>>{

    // TODO
//    default <TTransformed>
//    QueryableGroupSet<TTransformed> selectFromGroups(Func1<? super TElement, TTransformed> transformedFunc){
//        return (QueryableGroupSet<TTransformed>) this.select(row -> row.select(transformedFunc));
//    }
}
