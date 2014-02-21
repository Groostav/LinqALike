package LinqALike;

import LinqALike.Common.ArrayQuery;
import LinqALike.Delegate.Func1;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 20/02/14
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Factories {

    public static <TElement> Queryable<TElement> from(TElement ... elements){
        return new ArrayQuery(elements);
    }
}
