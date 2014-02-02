package LinqALike;

import LinqALike.Common.QueryableSet;

public class LinqingSet<TElement> extends LinqingList<TElement> implements QueryableSet<TElement> {
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
