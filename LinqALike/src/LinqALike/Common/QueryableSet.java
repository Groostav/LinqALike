package LinqALike.Common;

import LinqALike.Queryable;

import java.util.Set;

public interface QueryableSet<TElement> extends Set<TElement>, Queryable<TElement> {
}
