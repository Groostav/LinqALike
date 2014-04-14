package LinqALike.Queries;

import LinqALike.CommonDelegates;
import LinqALike.Queryable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static LinqALike.Factories.from;

public class OrderByQuery<TElement> implements DefaultQueryable<TElement> {

    private final Comparator<? super TElement> comparator;
    private final Iterable<TElement> source;

    public OrderByQuery(Iterable<TElement> source, Comparator<? super TElement> comparator){
        this.source = source;
        this.comparator = comparator;
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

            for(TElement candidate : remaining.skip(1)){

                if(comparator.compare(currentBest, candidate) > 0){
                    currentBest = candidate;
                }
            }

            remaining = remaining.except(from(currentBest), CommonDelegates.referenceEquals);

            return currentBest;
        }
    }
}
