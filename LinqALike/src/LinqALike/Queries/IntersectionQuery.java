package LinqALike.Queries;

import LinqALike.Delegate.Func1;
import LinqALike.QueryableBase;

import java.util.HashMap;
import java.util.Iterator;

public class IntersectionQuery<TElement, TCompared> extends QueryableBase<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;
    private final Func1<? super TElement, TCompared> comparableSelector;

    public IntersectionQuery(Iterable<? extends TElement> left,
                             Iterable<? extends TElement> right,
                             Func1<? super TElement, TCompared> comparableSelector) {
        this.left = left;
        this.right = right;
        this.comparableSelector = comparableSelector;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new IntersectionIterator();
    }

    private class IntersectionIterator extends PrefetchingIterator<TElement>{

        Iterator<? extends TElement> leftIterator = left.iterator();
        Iterator<? extends TElement> rightIterator = right.iterator();
        HashMap<TCompared, TElement> toIncludeByTheirChampion = new HashMap<>();

        @Override
        public TElement next() {
            return super.next();
        }

        @Override
        public boolean hasNext() {
            return super.hasNext();
        }

        @Override
        protected void prefetch() {
            TElement leftMemberCandidate;

            while(leftIterator.hasNext() && ! hasPrefetchedValue()){

                leftMemberCandidate = leftIterator.next();
                TCompared candidateKey = comparableSelector.getFrom(leftMemberCandidate);

                flattenIncludedsUntilFound(candidateKey);

                if (toIncludeByTheirChampion.containsKey(candidateKey)) {
                    setPrefetchedValue(leftMemberCandidate);
                }
            }
        }

        private void flattenIncludedsUntilFound(TCompared candidateKey) {

            while(rightIterator.hasNext()){
                TElement next = rightIterator.next();
                TCompared nextKey = comparableSelector.getFrom(next);
                toIncludeByTheirChampion.put(nextKey, next);

                if(nextKey.equals(candidateKey)){
                    break;
                }
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
