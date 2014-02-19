package LinqALike.Queries;

import LinqALike.Delegate.Func1;
import LinqALike.QueryableBase;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static LinqALike.LinqingList.from;

public class UnionQuery<TElement, TCompared> extends QueryableBase<TElement> {

    private final Iterable<? extends TElement> left;
    private final Iterable<? extends TElement> right;
    private final Func1<? super TElement, TCompared> comparableSelector;

    public UnionQuery(Iterable<? extends TElement> left,
                      Iterable<? extends TElement> right,
                      Func1<? super TElement, TCompared> comparableSelector) {

        this.left = left;
        this.right = right;
        this.comparableSelector = comparableSelector;
    }


    @Override
    public Iterator<TElement> iterator() {
        return new UnionIterator();
    }

    private class UnionIterator implements Iterator<TElement>{

        private final Iterator<? extends TElement> lefts = left.iterator();
        private final Iterator<? extends TElement> rights = right.iterator();

        @Override
        public boolean hasNext() {
            return lefts.hasNext() || rights.hasNext();
        }
                                  // what wha                     t
        @Override
        public TElement next() {
            if(lefts.hasNext()){
                return lefts.next();
            }

            Map<TCompared, ? extends TElement> leftsByTheirChampion = from(left).toMap(comparableSelector);
            do{
                TElement candidate = rights.next();
                TCompared champion = comparableSelector.getFrom(candidate);
                if( ! leftsByTheirChampion.containsKey(champion)){
                    return candidate;
                }
            }
            while(rights.hasNext());

            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
