package com.empowerops.linqalike;

import java.util.Collection;

/**
 * Denotes a collection implementation that is immutable
 *
 * <p>Created by Geoff on 2015-12-04.
 */
public interface ImmutableCollection<TElement> extends Collection<TElement>, Iterable<TElement>, Queryable<TElement>{

    // TODO: override with, except, union to co-variant versions returning ImmutableCollection.
    // This might be the first time I've been able to do something with the java type system that
    // would've been harder with the C# one... though I suppose its still do-able with explicit interface impls.

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link Queryable#union(Object[])}
     */
    @Override @Deprecated boolean add(TElement tElement);

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link Queryable#except(Object[])}
     */
    @Override @Deprecated boolean remove(Object o);

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link Queryable#union(Iterable)}
     */
    @Override @Deprecated boolean addAll(Collection<? extends TElement> c);

    /**
     * @deprecated this implementation of 'retainAll' is gaurenteed to throw!
     * use {@link Queryable#intersect(Object[])}
     */
    @Override @Deprecated boolean retainAll(Collection<?> c);

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link Queryable#except(Iterable)}
     */
    @Override @Deprecated boolean removeAll(Collection<?> c);

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * Consider re-assigning the reference to an empty collection of the appropriate type,
     * such as <code>list = IList.empty()</code> instead of <code>list.clear();</code>
     */
    @Override @Deprecated void clear();
}
