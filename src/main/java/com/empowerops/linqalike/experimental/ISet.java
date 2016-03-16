package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.queries.FastSize;
import com.github.andrewoma.dexx.collection.HashSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2015-12-02.
 */
public final class ISet<TElement> implements Set<TElement>, ImmutableCollection<TElement>, DefaultedQueryable<TElement>, FastSize{

    private static final ISet Empty = new ISet();
    @SuppressWarnings("unchecked") //thanks to immutability this is safe!
    public static <T> ISet<T> empty(){ return Empty; }

    private final HashSet<TElement> backingSet;

    public ISet(){
        this(HashSet.empty());
    }

    public ISet(TElement... initialElements){
        this(HashSet.<TElement>factory().newBuilder().addAll(from(initialElements)).build());
    }

    @SuppressWarnings("unchecked")
    public ISet(Iterable<? extends TElement> initialElements){
        this((HashSet<TElement>)HashSet.<TElement>factory().newBuilder().addAll((Iterable) initialElements).build());
    }

    private ISet(HashSet<TElement> source) {
        backingSet = source;
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingSet.contains((TElement)o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object elem : c){
            if ( ! contains(elem)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<TElement> iterator() {
        return backingSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return backingSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return Linq.toArray(this, a);
    }

    @Override
    public ISet<TElement> union(TElement toInclude) {
        return new ISet<>(backingSet.add(toInclude));
    }

    @Override
    @SafeVarargs
    public final ISet<TElement> union(TElement... toInclude) {
        Preconditions.notNull(toInclude, "toInclude");

        HashSet<TElement> set = backingSet;
        for(TElement elem : toInclude){
            set = set.add(elem);
        }

        return new ISet<>(set);
    }

    @Override
    public ISet<TElement> union(Iterable<? extends TElement> toInclude) {
        Preconditions.notNull(toInclude, "toInclude");

        HashSet<TElement> set = backingSet;
        for(TElement elem : toInclude){
            set = set.add(elem);
        }

        return new ISet<>(set);
    }

    @Override
    public <TCompared> Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                                                 Func1<? super TElement, TCompared> comparableSelector) {
        //TODO
        return comparableSelector == CommonDelegates.identity()
                ? union(toInclude)
                : DefaultedQueryable.super.union(toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                                     EqualityComparer<? super TElement> equalityComparator) {
        //TODO
        return equalityComparator == CommonDelegates.DefaultEquality
                ? union(toInclude)
                : DefaultedQueryable.super.union(toInclude, equalityComparator);
    }

    public ISet<TElement> except(TElement toExclude) {
        return new ISet<>(backingSet.remove(toExclude));
    }

    @Override
    @SafeVarargs
    public final ISet<TElement> except(TElement... toExclude) {
        Preconditions.notNull(toExclude, "toExclude");
        HashSet<TElement> set = backingSet;

        for(TElement elem : toExclude){
            set = set.remove(elem);
        }

        return new ISet<>(set);
    }

    @Override
    public ISet<TElement> except(Iterable<? extends TElement> toExclude) {
        Preconditions.notNull(toExclude, "toExclude");

        HashSet<TElement> set = backingSet;
        for(TElement elem : toExclude){
            set = set.remove(elem);
        }

        return new ISet<>(set);
    }

    @Override
    public Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                      EqualityComparer<? super TElement> comparator) {
        //TODO
        return DefaultedQueryable.super.except(toExclude, comparator);
    }

    @Override
    public <TCompared> Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                                  Func1<? super TElement, TCompared> comparableSelector) {
        //TODO
        return DefaultedQueryable.super.except(toExclude, comparableSelector);
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #union(Object)}
     */
    @Override
    public boolean add(TElement tElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #except(Object)}
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #union(Iterable)}
     */
    @Override
    public boolean addAll(Collection<? extends TElement> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     * use {@link #except(Iterable)}
     */
    @Override
    public boolean removeAll(Collection<?> c){
    throw new UnsupportedOperationException();
}

    /**
     * @deprecated this implementation of 'add' is gaurenteed to throw!
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
