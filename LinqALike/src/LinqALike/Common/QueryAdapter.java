package LinqALike.Common;

import LinqALike.Queryable;
import java.util.Iterator;

public abstract class QueryAdapter{

    public static class Array <TElement> extends QueryAdapter implements Queryable<TElement> {
        private final TElement[] elements;

        public Array(TElement[] elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ArrayIterator<TElement>(elements);
        }
    }

    public static class Iterable<TElement> extends QueryAdapter implements Queryable<TElement> {
        private final java.lang.Iterable<TElement> elements;

        public Iterable(java.lang.Iterable<TElement> elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return elements.iterator();
        }
    }

}
