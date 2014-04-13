package LinqALike.Common;


import LinqALike.Factories;
import LinqALike.Queryable;

import java.util.Iterator;

public class IterableCache<TElement> implements Queryable<TElement> {

    private final Iterator<TElement> origin;
    private Queryable<TElement> cache = Factories.empty();

    public IterableCache(Iterable<TElement> origin){
        this.origin = origin.iterator();
    }

    @Override
    public Iterator<TElement> iterator() {
        return new CacheInspectingIterator();
    }

    private class CacheInspectingIterator implements Iterator<TElement> {

        private Iterator<TElement> cacheIterator = cache.iterator();

        @Override
        public boolean hasNext() {
            return cacheIterator.hasNext() || origin.hasNext();
        }

        @Override
        public TElement next() {
            TElement value = cacheIterator.hasNext() ? cacheIterator.next() : origin.next();
            cache = cache.union(value);
            return value;
        }
    }
}
