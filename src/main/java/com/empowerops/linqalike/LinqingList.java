package com.empowerops.linqalike;

import com.empowerops.linqalike.queries.FastSize;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;

/**
 * An {@link java.util.ArrayList} decorated with support {@link com.empowerops.linqalike.Queryable}, probably the most
 * useful instantiable class in this library.
 *
 * In relational terms a Java list is an <i>ordered bag</i>, meaning it may contain duplicates, and it maintains
 * its own (indexed) order.
 *
 * @see java.util.ArrayList
 * @see com.empowerops.linqalike.Queryable
 * @see DefaultedQueryable
 */
public class LinqingList<TElement> extends ArrayList<TElement> implements
        Queryable.PreservingInsertionOrder<TElement>,
        Queryable.SupportsNull<TElement>,
        QueryableList<TElement>,
        DefaultedQueryable<TElement>,
        WritableCollection<TElement>,
        FastSize, Serializable {

    private static final Comparator<? super Queryable<? extends Comparable>> SequentialComparator = (left, right) -> {
        Iterator<? extends Comparable> leftItr = left.iterator();
        Iterator<? extends Comparable> rightItr = right.iterator();

        while (leftItr.hasNext() && rightItr.hasNext()) {

            Comparable nextLeft = leftItr.next();
            Comparable nextRight = rightItr.next();

            @SuppressWarnings("unchecked") //this is a real problem and there's not much we can do about it.
            // Because the 'SequentialComparator' field is gaurded by a typed method,
            // hopefully we wont get polluted or cast collections.
            int result = nextLeft.compareTo(nextRight);

            if (result != 0) { return result; }
        }
        //shorter one wins if we made it here
        return /*iff left still has one, then right won*/leftItr.hasNext() ? + 1 :
               /*iff right still has one, then left won*/rightItr.hasNext() ? - 1 :
               /*else both ran out -> they're equal*/ 0;
    };

    public static <TCompared> Comparator<? super Queryable<? extends Comparable<TCompared>>> SequentialComparator() {
        return SequentialComparator;
    }

    private static final long serialVersionUID = - 5504726367113690047L;
    private static final int DEFAULT_CAPACITY = 10;

    public LinqingList() {
        super();
    }

    public LinqingList(int initialSize) {
        super(initialSize);
    }

    @SafeVarargs
    public LinqingList(TElement... elements) {
        this(elements.length);
        addAll(elements);
    }

    public LinqingList(Iterator<? extends TElement> elements) {
        this();
        addAllRemaining(elements);
    }
    public LinqingList(Iterable<? extends TElement> elements) {
        this(fastSizeIfAvailable(elements).orElse(DEFAULT_CAPACITY));
        addAll(elements);
    }

    @Override
    public @Nonnull Iterator<TElement> iterator() {
        return this.listIterator();
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        return remove(toRemove);
    }

    @Override public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }

    @Override public ForwardingLinqingList<TElement> subList(int fromIndex, int toIndex) {
        return new ForwardingLinqingList<>(super.subList(fromIndex, toIndex));
    }
    @Override public int indexOfElement(TElement elementToFind) {
        return super.indexOf(elementToFind);
    }
    @Override public int lastIndexOfElement(TElement elementToFind) {
        return super.lastIndexOf(elementToFind);
    }
}


