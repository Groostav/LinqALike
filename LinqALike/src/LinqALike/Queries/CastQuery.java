package LinqALike.Queries;

import LinqALike.Common.Formatting;

import java.util.Iterator;

/**
 * Provides static casting of one generic {@link java.lang.Iterable} to another,
 * withen the {@link LinqALike.Queryable} class hierarchy. This class does <i>nothing</i>
 * at run-time.
 */
public class CastQuery<TOrigin, TCasted> implements DefaultQueryable<TCasted> {

    private final Iterable<TOrigin> sourceElements;
    private final Class minimumExpectedType;

    public CastQuery(Iterable<TOrigin> sourceElements){
        this.sourceElements = sourceElements;
        this.minimumExpectedType = Object.class;
    }

    public CastQuery(Iterable<TOrigin> sourceElements, Class minimumExpectedType){
        this.sourceElements = sourceElements;
        this.minimumExpectedType = minimumExpectedType;
    }

    @Override
    public Iterator<TCasted> iterator() {
        return new CastingIterator();
    }

    @SuppressWarnings("unchecked")
    private class CastingIterator implements Iterator<TCasted>{

        private final Iterator<TOrigin> source = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public TCasted next() {

            TOrigin element = source.next();

            if( ! minimumExpectedType.isInstance(element)){
                throw new ClassCastException(
                        "the element " + Formatting.getDebugString(element) + " " +
                        "cannot be cast as " + minimumExpectedType.getCanonicalName());
            }

            return (TCasted) element;
        }
    }
}
