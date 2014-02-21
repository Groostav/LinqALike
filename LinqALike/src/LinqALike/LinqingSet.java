package LinqALike;

import LinqALike.Common.QueryableSet;

import java.util.Set;

public class LinqingSet<TElement> extends LinqingList<TElement> implements QueryableSet<TElement>, Set<TElement> {

    public LinqingSet() {
    }

    public LinqingSet(TElement... tElements) {
        super(tElements);
    }

    public LinqingSet(Iterable<? extends TElement> tElements) {
        super(tElements);
    }

    public LinqingSet(Class<TElement> tElementClass, Object[] initialValues) {
        super(tElementClass, initialValues);
    }
}
