package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.common.Tuple;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CastQuery<TUncasted, TCast> implements DefaultedQueryable<TCast>, FastSize {

    private final Iterable<TUncasted> sourceElements;
    private final Class<TCast> desiredType;

    public CastQuery(Iterable<TUncasted> sourceElements, Class<TCast> desiredType) {
        this.sourceElements = sourceElements;
        this.desiredType = desiredType;
    }

    @Override
    public Iterator<TCast> iterator() {
        return new CastIterator();
    }

    public static class Inner<TLeftCast, TLeftOriginal, TRightCast, TRightOriginal>
            implements DefaultedBiQueryable<TLeftCast, TRightCast> {

        private final Iterable<Tuple<TLeftOriginal, TRightOriginal>> sourceElements;
        private final Optional<Class<TLeftCast>> desiredLeftType;
        private final Optional<Class<TRightCast>> desiredRightType;

        public Inner(Iterable<Tuple<TLeftOriginal, TRightOriginal>> sourceElements,
                     Optional<Class<TLeftCast>> desiredLeftType,
                     Optional<Class<TRightCast>> desiredRightType) {

            assert desiredLeftType.isPresent() || desiredRightType.isPresent() : "no cast to perform?!";

            this.sourceElements = sourceElements;
            this.desiredLeftType = desiredLeftType;
            this.desiredRightType = desiredRightType;
        }

        @Override
        public Iterator<Tuple<TLeftCast, TRightCast>> iterator() {
            return new CastLeftIterator();
        }

        public class CastLeftIterator implements Iterator<Tuple<TLeftCast, TRightCast>> {

            private final Iterator<Tuple<TLeftOriginal, TRightOriginal>> sourceIterator = sourceElements.iterator();

            @Override
            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            @SuppressWarnings("unchecked") //type checked dynamically
            @Override
            public Tuple<TLeftCast, TRightCast> next() {
                Tuple<TLeftOriginal, TRightOriginal> next = sourceIterator.next();
                // nothing to be gained from using new memory here, since Tuple is immutable,
                // and java doesn't support casting operators
                // so we'll do a type-check and return the original
                desiredLeftType.ifPresent(type -> type.cast(next.left));
                desiredRightType.ifPresent(type -> type.cast(next.right));
                return (Tuple) next;
            }
        }
    }

    private class CastIterator implements Iterator<TCast>{

        private final Iterator<TUncasted> sourceItr = sourceElements.iterator();

        @Override
        public boolean hasNext() {
            return sourceItr.hasNext();
        }

        @Override
        public TCast next() {
            if ( ! hasNext()) { throw new NoSuchElementException(); }
            return desiredType.cast(sourceItr.next());
        }
    }

    @Override
    public int size() {
        return Accessors.vSize(sourceElements);
    }


}
