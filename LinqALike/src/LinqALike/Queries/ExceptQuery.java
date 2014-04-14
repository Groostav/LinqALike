package LinqALike.Queries;

import LinqALike.Common.Preconditions;
import LinqALike.Common.PrefetchingIterator;
import LinqALike.Delegate.Func2;
import LinqALike.LinqingSet;

import java.util.Iterator;

public class ExceptQuery<TElement> implements DefaultQueryable<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;
    private final Func2<? super TElement, ? super TElement, Boolean> comparator;

    public ExceptQuery(Iterable<? extends TElement> left,
                       Iterable<? extends TElement> right,
                       Func2<? super TElement, ? super TElement, Boolean> comparator){

        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");
        Preconditions.notNull(comparator, "comparator");

        this.left = left;
        this.right = right;
        this.comparator = comparator;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new ExceptIterator();
    }

    protected class ExceptIterator extends PrefetchingIterator<TElement> {

        private Iterator<? extends TElement> source = left.iterator();
        private Iterator<? extends TElement> toExcludes = right.iterator();
        private LinqingSet<TElement> toExcludeByTheirChampion = new LinqingSet<>();

        @Override
        protected void prefetch() {

            while(source.hasNext() && ! hasPrefetchedValue()){

                TElement candidate = source.next();

                flattenExcludedsUntilFound(candidate);

                if ( ! toExcludeByTheirChampion.any(x -> comparator.getFrom(candidate, x))) {
                    setPrefetchedValue(candidate);
                }
            }
        }

        private void flattenExcludedsUntilFound(TElement candidate) {

            while(toExcludes.hasNext()){
                TElement next = toExcludes.next();
                toExcludeByTheirChampion.add(next);

                if(comparator.getFrom(next, candidate)){
                    break;
                }
            }
        }
    }
}

