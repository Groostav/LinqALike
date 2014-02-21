package LinqALike.Queries;

import LinqALike.Common.PrefetchingIterator;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.LinqingMap;
import LinqALike.Queryable;

import java.util.Iterator;

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
            return new ExcludingWithComparableIterator();
        }

        private class ExcludingWithComparableIterator extends PrefetchingIterator<TElement> implements Iterator<TElement> {

            private Iterator<? extends TElement> source = leftBasis.iterator();
            private Iterator<? extends TElement> toExcludes = rightSetToExclude.iterator();
            private LinqingMap<TCompared, TElement> toExcludeByTheirChampion = new LinqingMap<>();

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void prefetch() {
                TElement candidate;

                while(source.hasNext() && ! hasPrefetchedValue()){

                    candidate = source.next();
                    TCompared candidateKey = comparableSelector.getFrom(candidate);

                    flattenExcludedsUntilFound(candidateKey);

                    if ( ! toExcludeByTheirChampion.containsKey(candidateKey)) {
                        setPrefetchedValue(candidate);
                    }
                }
            }

            private void flattenExcludedsUntilFound(TCompared candidateKey) {

                while(toExcludes.hasNext()){
                    TElement next = toExcludes.next();
                    TCompared nextKey = comparableSelector.getFrom(next);
                    toExcludeByTheirChampion.put(nextKey, next);

                    if(nextKey.equals(candidateKey)){
                        break;
                    }
                }
            }
        }
    }

    public static class WithEquatable<TElement> extends ExceptQuery<TElement>{

        private Func2<? super TElement, ? super TElement, Boolean> equalityComparison;

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

        }
    }

    public static class Default extends ExceptQuery{

    }
}


