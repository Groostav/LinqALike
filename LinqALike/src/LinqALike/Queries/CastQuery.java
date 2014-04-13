package LinqALike.Queries;

import LinqALike.Queryable;

import java.util.Iterator;

/**
 * Created by Geoff on 13/04/2014.
 */
public class CastQuery<TOrigin, TCasted> implements Queryable<TCasted> {

    private final Iterable<TOrigin> sourceElements;

    public CastQuery(Iterable<TOrigin> sourceElements){
        this.sourceElements = sourceElements;
    }

    @Override
    public Iterator<TCasted> iterator() {
        return new CastingIterator();
    }

    private class CastingIterator implements Iterator<TCasted>{

        private final Iterator<TOrigin> source = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public TCasted next() {
            return (TCasted) source.next();
        }
    }
}
