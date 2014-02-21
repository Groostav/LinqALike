package LinqALike.Queries;

import LinqALike.Common.PrefetchingIterator;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.LinqingSet;
import LinqALike.Queryable;

import java.util.*;

public abstract class DistinctQuery<TElement> implements Queryable<TElement> {

    protected final Iterable<? extends TElement> duplicateCandidates;

    protected DistinctQuery(Iterable<? extends TElement> duplicateCandidates){
        this.duplicateCandidates = duplicateCandidates;
    }

    public static class WithNaturalEquality<TElement> extends DistinctQuery<TElement>{

        public WithNaturalEquality(Iterable<? extends TElement> duplicateCandidates){
            super(duplicateCandidates);
        }

        @Override
        public Iterator<TElement> iterator(){
            return new DistinctWithNatrualEqualityIterable();
        }

        private class DistinctWithNatrualEqualityIterable extends PrefetchingIterator<TElement> {

            private final Set<TElement> found = new HashSet<TElement>();
            private final Iterator<? extends TElement> candidates = duplicateCandidates.iterator();

            @Override
            protected void prefetch() {
                while(candidates.hasNext() && ! hasPrefetchedValue()){

                    TElement candidate = candidates.next();

                    if(found.contains(candidate)){
                        continue;
                    }

                    found.add(candidate);

                    setPrefetchedValue(candidate);
                }
            }
        }
    }

    public static class WithComparable<TElement, TComparable> extends DistinctQuery<TElement>{

        private final Func1<? super TElement, TComparable> comparableSelector;

        public WithComparable(Iterable<? extends TElement> duplicateCandidates,
                              Func1<? super TElement, TComparable> comparableSelector){
            super(duplicateCandidates);
            this.comparableSelector = comparableSelector;
        }

        @Override
        public Iterator<TElement> iterator(){
            return new DistinctWithComparableIterator();
        }

        private class DistinctWithComparableIterator extends PrefetchingIterator<TElement>{

            private final Map<TComparable, TElement> found = new Hashtable<>();
            private final Iterator<? extends TElement> candidates = duplicateCandidates.iterator();

            @Override
            protected void prefetch() {
                while(candidates.hasNext() && ! hasPrefetchedValue()){

                    TElement candidate = candidates.next();
                    TComparable key = comparableSelector.getFrom(candidate);

                    if(found.containsKey(key)){
                        continue;
                    }

                    found.put(key, candidate);
                    setPrefetchedValue(candidate);
                }
            }
        }
    }

    public static class WithEqualityComparable<TElement, TComparable> extends DistinctQuery<TElement>{

        private final Func2<? super TElement, ? super TElement, Boolean> comparator;

        public WithEqualityComparable(Iterable<? extends TElement> duplicateCandidates,
                                      Func2<? super TElement, ? super TElement, Boolean> equalityComparator){

            super(duplicateCandidates);
            this.comparator = equalityComparator;
        }

        @Override
        public Iterator<TElement> iterator(){
            return new DistinctWithComparableIterator();
        }

        private class DistinctWithComparableIterator extends PrefetchingIterator<TElement>{

            private final LinqingSet<TElement> found = new LinqingSet<>();
            private final Iterator<? extends TElement> candidates = duplicateCandidates.iterator();

            @Override
            protected void prefetch() {
                while(candidates.hasNext() && ! hasPrefetchedValue()){

                    TElement candidate = candidates.next();

                    boolean hasDuplicate = found.any(x -> comparator.getFrom(x, candidate));

                    if(hasDuplicate){
                        continue;
                    }

                    found.add(candidate);

                    setPrefetchedValue(candidate);
                }
            }
        }
    }
}
