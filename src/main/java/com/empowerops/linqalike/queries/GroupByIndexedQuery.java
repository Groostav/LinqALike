package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.CommonDelegates;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.ComparingLinkedHashSet;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.PrefetchingIterator;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;

import java.util.Iterator;

/**
 * Created by Geoff on 2017-01-19.
 */
public class GroupByIndexedQuery<TCompared, TElement> implements DefaultedQueryable<Queryable<TElement>> {

    private final Queryable<TElement> sourceElements;
    private final Func1<Tuple<? super TElement, Integer>, TCompared> selector;

    @SuppressWarnings("unchecked") //problem is with variance through the Tuple type
    public GroupByIndexedQuery(Iterable<TElement> sourceElements,
                               Func2<? super TElement, Integer, TCompared> selector){
        this.sourceElements = Factories.from(sourceElements);
        this.selector = (Func1) selector.asFuncOnTuple();
    }

    @Override
    public Iterator<Queryable<TElement>> iterator() {
        return new IndexedGroupingIterator();
    }

    class IndexedGroupingIterator extends PrefetchingIterator<Queryable<TElement>> {

        private final EqualityComparer<? super Tuple<TElement, Integer>> equalityComparer = CommonDelegates.performEqualsUsing(selector);
        private final ComparingLinkedHashSet<Tuple<TElement, Integer>> alreadySeenElements = new ComparingLinkedHashSet<>(equalityComparer);

        private final Iterator<TElement> unseenElements = sourceElements.iterator();
        private int currentIndex = 0;

        @Override
        protected void prefetch() {
            while(unseenElements.hasNext()){
                Tuple<TElement, Integer> candidateLeader = new Tuple<>(unseenElements.next(), currentIndex++);

                boolean isNewElement = alreadySeenElements.add(candidateLeader);
                if ( ! isNewElement) { continue; }

                Queryable<TElement> group = sourceElements
                        .selectIndexed(Tuple::new)
                        .where(groupCandidate -> equalityComparer.equals(candidateLeader, groupCandidate))
                        .select(tuple -> tuple.left);

                if(group.isEmpty()){
                    //the only way this could be the case is if comparator.compare(leader, leader) returned false
                    // -> illegal comparator
                    throw new IllegalArgumentException("Comparison method violates its general contract!");
                }

                setPrefetchedValue(group);
                return;
            }
        }
    }
}
