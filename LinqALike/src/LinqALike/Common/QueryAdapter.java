package LinqALike.Common;

import LinqALike.Queryable;
import java.util.Iterator;

public abstract class QueryAdapter{

    public static class Array <TElement> implements Queryable<TElement> {
        private final TElement[] elements;

        public Array(TElement[] elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ArrayIterator<>(elements);
        }
    }

    public static class Iterable<TElement> implements Queryable<TElement> {
        private final java.lang.Iterable<TElement> elements;

        public Iterable(java.lang.Iterable<TElement> elements){
            this.elements = elements;
        }

        @Override
        public Iterator<TElement> iterator() {
            return elements.iterator();
        }
    }

    public static class Iterator_<TElement> implements Queryable<TElement> {

        public Iterator_(Iterable<TElement> elements){

        }

        @Override
        public Iterator<TElement> iterator() {
            assert false : "not implemented";
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
