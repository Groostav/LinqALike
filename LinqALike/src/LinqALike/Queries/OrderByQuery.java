package LinqALike.Queries;

import LinqALike.Common.ForkableIterator;
import LinqALike.Delegate.Func1;
import LinqALike.Factories;
import LinqALike.Queryable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static LinqALike.Factories.from;

public class OrderByQuery<TElement, TCompared extends Comparable<TCompared>> implements Queryable<TElement> {

    private final Func1<? super TElement, TCompared> comparableSelector;
    private final Iterable<TElement> source;

    public OrderByQuery(Iterable<TElement> source, Func1<? super TElement, TCompared> comparableSelector){
        this.source = source;
        this.comparableSelector = comparableSelector;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new OrderByIterator();
    }

    public class OrderByIterator implements Iterator<TElement>{

        private Queryable<TElement> remaining = from(source);

        @Override
        public boolean hasNext() {
            return remaining.any();
        }

        @Override
        public TElement next() {

            if(remaining.isEmpty()){
                throw new NoSuchElementException();
            }

            TElement currentBest = remaining.first();
            TCompared currentValue = comparableSelector.getFrom(currentBest);

            for(TElement candidate : remaining.skip(1)){

                TCompared candidateValue = comparableSelector.getFrom(candidate);

                if(currentValue.compareTo(candidateValue) > 0){
                    currentBest = candidate;
                    currentValue = candidateValue;
                }
            }

            remaining = remaining.except(currentBest);


            return currentBest;
        }
    }
}
