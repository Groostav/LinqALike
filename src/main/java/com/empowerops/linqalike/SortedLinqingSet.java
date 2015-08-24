package com.empowerops.linqalike;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import static com.empowerops.linqalike.Factories.empty;
import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2015-04-08.
 */
public class SortedLinqingSet<TElement> extends TreeSet<TElement>
                                        implements DefaultedQueryable<TElement>,
                                                   WritableCollection<TElement>,
                                                   NavigableSet<TElement> {

    private static final long serialVersionUID = 4188611250638519387L;

    public static <TComparableElem extends Comparable<TComparableElem>>
    SortedLinqingSet<TComparableElem> createSetForComparables(Iterable<TComparableElem> initialElements){
        Comparator<TComparableElem> comparator = Comparable::compareTo;
        return new SortedLinqingSet<>(comparator, initialElements);
    }
    @SafeVarargs //consumption of array is only for initialization
    public static <TComparableElem extends Comparable<TComparableElem>>
    SortedLinqingSet<TComparableElem> createSetSetForComparables(TComparableElem... initialElements){
        Comparator<TComparableElem> comparator = Comparable::compareTo;
        return new SortedLinqingSet<>(comparator, from(initialElements));
    }
    public static <TElement> SortedLinqingSet<TElement> creasteSortedSetFor(Comparator<? super TElement> comparator, Iterable<TElement> source){
        return new SortedLinqingSet<>(comparator, source);
    }
    @SafeVarargs //consumption of array is only for initialization
    public static <TElement> SortedLinqingSet<TElement> createSortedSetFor(Comparator<? super TElement> comparator, TElement... source){
        return new SortedLinqingSet<>(comparator, from(source));
    }
    public static <TElement> SortedLinqingSet<TElement> createSortedSetFor(Comparator<? super TElement> comparator){
        return new SortedLinqingSet<>(comparator, empty());
    }

    private SortedLinqingSet(Comparator<? super TElement> comparator, Iterable<? extends TElement> initialElements) {
        super(comparator);
        addAll(initialElements);
    }

    @Override
    public boolean remove(Object toRemove) {
        return super.remove(toRemove);
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        return super.remove(toRemove);
    }
    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }

    public int rankOf(TElement element){
        return headSet(element).size();
    }

    public TElement get(TElement target){
        return ceiling(target);
    }
}
