package com.empowerops.linqalike;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ForwardingNavigableSet<TElement> implements
        DefaultedQueryable<TElement>,
        QueryableNavigableSet<TElement>,
        Collection<TElement> {

    private final NavigableSet<TElement> source;

    public ForwardingNavigableSet(NavigableSet<TElement> source) {
        this.source = source;
    }

    @Override
    public Comparator<? super TElement> comparator() {
        return source.comparator();
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> subSet(TElement fromElement, TElement toElement) {
        NavigableSet<TElement> source = this.source.subSet(fromElement, true, toElement, false);

        return castOrForward(source);
    }

    @Override
    public TElement lower(TElement tElement) {
        return source.lower(tElement);
    }

    @Override
    public TElement floor(TElement tElement) {
        return source.floor(tElement);
    }

    @Override
    public TElement ceiling(TElement tElement) {
        return source.ceiling(tElement);
    }

    @Override
    public TElement higher(TElement tElement) {
        return source.higher(tElement);
    }

    @Override
    public TElement pollFirst() {
        return source.pollFirst();
    }

    @Override
    public TElement pollLast() {
        return source.pollLast();
    }

    @Override
    public @Nonnull NavigableSet<TElement> descendingSet() {
        return source.descendingSet();
    }

    @Override
    public @Nonnull Iterator<TElement> descendingIterator() {
        return source.descendingIterator();
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> subSet(TElement fromElement,
                                                  boolean fromInclusive,
                                                  TElement toElement,
                                                  boolean toInclusive) {
        NavigableSet<TElement> source = this.source.subSet(fromElement, fromInclusive, toElement, toInclusive);
        return castOrForward(source);
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> headSet(TElement toElement) {
        NavigableSet<TElement> source = this.source.headSet(toElement, false);

        return castOrForward(source);
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> headSet(TElement toElement, boolean inclusive) {
        NavigableSet<TElement> source = this.source.headSet(toElement, inclusive);
        return castOrForward(source);
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> tailSet(TElement fromElement) {
        NavigableSet<TElement> source = this.source.tailSet(fromElement, true);
        return castOrForward(source);
    }

    @Override
    public Spliterator<TElement> spliterator() {
        return source.spliterator();
    }

    @Override
    public Stream<TElement> stream() {
        return source.stream();
    }

    @Override
    public Stream<TElement> parallelStream() {
        return source.parallelStream();
    }

    @Override
    public @Nonnull QueryableNavigableSet<TElement> tailSet(TElement fromElement, boolean inclusive) {
        NavigableSet<TElement> source = this.source.tailSet(fromElement, inclusive);
        return castOrForward(source);
    }

    @Override
    public boolean isEmpty(){
        return source.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return source.contains(o);
    }

    @Override
    public boolean add(TElement tElement) {
        return source.add(tElement);
    }

    @Override
    public boolean remove(Object o) {
        return source.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return source.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends TElement> c) {
        return source.addAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return source.retainAll(c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return source.removeAll(c);
    }



    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        return source.removeIf(filter);
    }

    @Override
    public void clear() {
        source.clear();
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public TElement first() {
        return source.first();
    }

    @Override
    public TElement last() {
        return source.last();
    }

    @Override
    public @Nonnull Iterator<TElement> iterator() {
        return source.iterator();
    }

    @Override
    public Object[] toArray() {
        return DefaultedQueryable.super.toArray();
    }

    @Override
    public <TDesired> TDesired[] toArray(TDesired[] target) {
        return DefaultedQueryable.super.toArray(target);
    }

    private QueryableNavigableSet<TElement> castOrForward(NavigableSet<TElement> source) {
        return source instanceof QueryableNavigableSet
                ? ((QueryableNavigableSet<TElement>) source)
                : new ForwardingNavigableSet<>(source);
    }

}
