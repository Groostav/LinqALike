package LinqALike;

import LinqALike.Common.ForkableIterator;
import LinqALike.Common.IterableCache;
import LinqALike.Common.QueryAdapter;
import LinqALike.Common.RepeatingIterator;

import java.util.Iterator;

public class Factories {

    @SafeVarargs
    public static <TElement> Queryable<TElement> from(TElement ... elements){
        return new QueryAdapter.Array<>(elements);
    }

    public static <TElement> Queryable<TElement> from(Iterable<TElement> elements){
        return new QueryAdapter.Iterable<>(elements);
    }

    @SafeVarargs
    public static <TElement> LinqingList<TElement> asList(TElement... set){
        return new LinqingList<>(set);
    }

    public static <TElement> LinqingList<TElement> asList(Iterable<TElement> set){
        return new LinqingList<>(set);
    }


    @SafeVarargs
    public static <TElement> TElement firstNotNullOrDefault(TElement ... set){
        return from(set).firstOrDefault(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNull(TElement ... set){
        return from(set).first(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TSet extends Iterable<?>> TSet firstNotEmpty(TSet ... sets){
        return from(sets).first(x -> x.iterator().hasNext());
    }

    public static Iterable<Integer> range(final int lowerInclusive, final int upperExclusive) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    public int current = lowerInclusive;

                    @Override
                    public boolean hasNext() {
                        return current < upperExclusive;
                    }

                    @Override
                    public Integer next() {
                        return current++;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <TElement> Iterable<TElement> repeat(final TElement valueToRepeat) {
        return () -> new RepeatingIterator<>(valueToRepeat);
    }

    public static <TElement> Queryable<TElement> cache(Iterable<TElement> origin){
        return new IterableCache<>(origin);
    }

    public static <TElement> Queryable<TElement> empty() {
        return new LinqingList<>();
    }

    public static class ForkingIterator<TElement> implements ForkableIterator<TElement>{

        private final Iterable<TElement> source;
        private final Iterator<TElement> backingIterator;

        private int seenCount = 0;

        public ForkingIterator(Iterable<TElement> source){
            this.source = source;
            backingIterator = source.iterator();
        }

        @Override
        public Iterator<TElement> fork() {
            Queryable<TElement> broughtCurrentCopy = LinqBehaviour.skip(source, seenCount);
            return new ForkingIterator<>(broughtCurrentCopy);
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public TElement next() {
            return backingIterator.next();
        }
    }
}
