package LinqALike.Queries;

import LinqALike.Common.PrefetchingIterator;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.LinqingMap;
import LinqALike.LinqingSet;
import LinqALike.Queryable;

import java.util.Iterator;

import static LinqALike.CommonDelegates.identity;

public abstract class ExceptQuery<TElement> implements Queryable<TElement> {

    protected final Iterable<? extends TElement> leftBasis;
    protected final Iterable<? extends TElement> rightSetToExclude;

    protected ExceptQuery(Iterable<? extends TElement> leftBasis, Iterable<? extends TElement> rightSetToExclude){
        this.leftBasis = leftBasis;
        this.rightSetToExclude = rightSetToExclude;
    }

    public static class WithNaturalEquality<TElement> extends ExceptQuery<TElement>{
        public WithNaturalEquality(Iterable<? extends TElement> leftBasis,
                                   Iterable<? extends TElement> rightSetToExclude){
            super(leftBasis, rightSetToExclude);
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ExcludingWithComparableIterator<>(identity());
        }
    }

    public static class WithComparable<TElement, TCompared> extends ExceptQuery<TElement>{
        private final Func1<? super TElement, TCompared> comparableSelector;

        public WithComparable(Iterable<? extends TElement> leftBasis,
                              Iterable<? extends TElement> rightSetToExclude,
                              Func1<? super TElement, TCompared> comparableSelector) {

            super(leftBasis, rightSetToExclude);
            this.comparableSelector = comparableSelector;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ExcludingWithComparableIterator<>(comparableSelector);
        }

    }

    public static class WithEquatable<TElement> extends ExceptQuery<TElement>{

        private Func2<? super TElement, ? super TElement, Boolean> equalityComparison;
        private Iterator<? extends TElement> source = leftBasis.iterator();
        private Iterator<? extends TElement> toExcludes = rightSetToExclude.iterator();
        private LinqingSet<TElement> toExcludeByTheirChampion = new LinqingSet<>();

        public WithEquatable(Iterable<? extends TElement> leftBasis,
                             Iterable<? extends TElement> rightSetToExclude,
                             Func2<? super TElement, ? super TElement, Boolean> equalityComparison) {

            super(leftBasis, rightSetToExclude);
            this.equalityComparison = equalityComparison;
        }

        @Override
        public Iterator<TElement> iterator() {
            return new ExcludingWithEqualityComparatorIterator();
        }

        private class ExcludingWithEqualityComparatorIterator extends PrefetchingIterator<TElement> {

            @Override
            protected void prefetch() {

                while(source.hasNext() && ! hasPrefetchedValue()){

                    TElement candidate = source.next();

                    flattenExcludedsUntilFound(candidate);

                    if ( ! toExcludeByTheirChampion.any(x -> equalityComparison.getFrom(candidate, x))) {
                        setPrefetchedValue(candidate);
                    }
                }
            }

            private void flattenExcludedsUntilFound(TElement candidate) {

                while(toExcludes.hasNext()){
                    TElement next = toExcludes.next();
                    toExcludeByTheirChampion.add(next);

                    if(equalityComparison.getFrom(next, candidate)){
                        break;
                    }
                }
            }
        }
    }

    private class ExcludingWithComparableIterator<TCompared> extends PrefetchingIterator<TElement> implements Iterator<TElement> {

        private Iterator<? extends TElement> source = leftBasis.iterator();
        private Iterator<? extends TElement> toExcludes = rightSetToExclude.iterator();
        private LinqingMap<TCompared, TElement> toExcludeByTheirChampion = new LinqingMap<>();
        private final Func1<? super TElement, TCompared> comparable;

        public ExcludingWithComparableIterator(Func1<? super TElement, TCompared> comparableSelector) {
            this.comparable = comparableSelector;
        }

        @Override
        protected void prefetch() {
            TElement candidate;

            while(source.hasNext() && ! hasPrefetchedValue()){

                candidate = source.next();
                TCompared candidateKey = comparable.getFrom(candidate);

                flattenExcludedsUntilFound(candidateKey);

                if ( ! toExcludeByTheirChampion.containsKey(candidateKey)) {
                    setPrefetchedValue(candidate);
                }
            }
        }

        private void flattenExcludedsUntilFound(TCompared candidateKey) {

            while(toExcludes.hasNext()){

                TElement next = toExcludes.next();
                TCompared nextKey = comparable.getFrom(next);

                toExcludeByTheirChampion.put(nextKey, next);

                if(nextKey.equals(candidateKey)){
                    break;
                }
            }
        }
    }
}


