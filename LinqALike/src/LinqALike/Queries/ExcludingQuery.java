package LinqALike.Queries;

import LinqALike.Delegate.Func1;
import LinqALike.LinqingMap;
import LinqALike.QueryableBase;

import java.util.Iterator;

public class ExcludingQuery<TElement, TCompared> extends QueryableBase<TElement> {

    private final Iterable<? extends TElement> set;
    private final Iterable<? extends TElement> membersToExclude;
    private final Func1<? super TElement, TCompared> comparableSelector;

    public ExcludingQuery(Iterable<? extends TElement> set,
                          Iterable<? extends TElement> membersToExclude,
                          Func1<? super TElement, TCompared> comparableSelector) {

        this.set = set;
        this.membersToExclude = membersToExclude;
        this.comparableSelector = comparableSelector;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new ExcludingIterator();
    }

    private class ExcludingIterator extends PrefetchingIterator<TElement> implements Iterator<TElement> {

        private Iterator<? extends TElement> source = set.iterator();
        private Iterator<? extends TElement> toExcludes = membersToExclude.iterator();
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


