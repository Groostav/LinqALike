package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.LinqingSet;
import com.empowerops.linqalike.common.EqualityComparer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IndexedLinqingSet<TElement> extends LinqingSet<TElement> {

    private final Map<Method, SortedSet<TElement>> indicies = new HashMap<>();

    public IndexedLinqingSet() {
    }

    public IndexedLinqingSet(int size) {
        super(size);
    }

    public IndexedLinqingSet(TElement... tElements) {
        super(tElements);
    }

    public IndexedLinqingSet(Iterator<? extends TElement> elements) {
        super(elements);
    }

    public IndexedLinqingSet(Iterable<? extends TElement> tElements) {
        super(tElements);
    }

    public interface PropertyMethodRef<THost> extends Serializable {

        Object get(THost host);

    }

    public void index(LambdaComprehention.PropertyGetter<TElement, ?> propertyToIndex){
        Method target = LambdaComprehention.getReferredProperty(propertyToIndex);
    }


    private void onChange() {
        throw new UnsupportedOperationException("onChange");
    }
    
    // ----------------------------------------------------
    // overridden mutators

    @Override
    public Iterator<TElement> iterator() {
        Iterator<TElement> superIter = iterator();
        return new Iterator<TElement>() {
            @Override
            public boolean hasNext() {
                return superIter.hasNext();
            }

            @Override
            public TElement next() {
                return superIter.next();
            }

            @Override
            public void remove() {
                superIter.remove();
                onChange();
            }
        };
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = true;
		try { return changed = super.removeAll(c); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        boolean changed = true;
		try { return changed = super.removeIf(filter); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        boolean changed = true;
		try { return changed = super.removeElement(toRemove); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean addAll(Collection<? extends TElement> c) {
        boolean changed = true;
		try { return changed = super.addAll(c); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = true;
        try { return changed = super.retainAll(c); }
		finally { if(changed) onChange(); }
    }


    @Override
    public boolean add(TElement tElement) {
        boolean changed = true;
		try { return changed = super.add(tElement); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean remove(Object o) {
        boolean changed = true;
		try { return changed = super.remove(o); }
		finally { if(changed) onChange(); }
    }

    @Override
    public void clear() {
        try { super.clear(); }
        finally { onChange(); }
    }

    @Override
    public boolean retainAll(Iterable<? extends TElement> valuesToKeep) {
        boolean changed = true;
		try { return changed = super.retainAll(valuesToKeep); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean removeAll(Iterable<? extends TElement> valuesToRemove) {
        boolean changed = true;
		try { return changed = super.removeAll(valuesToRemove); }
		finally { if(changed) onChange(); }
    }

    @Override
    public boolean addAllRemaining(Iterator<? extends TElement> valuesToBeAdded) {
        return tryAndNotifyOnFailure(() -> super.addAllRemaining(valuesToBeAdded));
    }

    //consider the ~reasonable case: NPE from a hashCode() or equals() call.
    // what do? calling onChange() is likely to cause the same exception to go off.

    @SuppressWarnings({"ThrowFromFinallyBlock", "finally"})
    private boolean tryAndNotifyOnFailure(Supplier<Boolean> update) {

        boolean changed = true;
        try{
            changed = update.get();
        }
        catch(Exception | AssertionError e){
            try { onChange(); }
            finally { throw e; }
        }
    }

    @Override
    public boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        boolean changed = true;
		try { return changed = super.addAll(valuesToBeAdded); }
		finally { if(changed) onChange(); }
    }

    @Override @SafeVarargs
    public final boolean addAll(TElement... valuesToBeAdded) {
        boolean changed = true;
		try { return changed = super.addAll(valuesToBeAdded); }
		finally { if(changed) onChange(); }
    }

    @Override
    public void clearAndAddAll(Iterable<? extends TElement> newItems) {
        try { super.clearAndAddAll(newItems); }
		finally { onChange(); }
    }

    @Override
    public void replace(TElement oldItem, TElement newItem) {
        try { super.replace(oldItem, newItem); }
		finally { onChange(); }
    }

    @Override
    public void replace(TElement oldItem, TElement newItem, EqualityComparer<? super TElement> comparer) {
        try { super.replace(oldItem, newItem, comparer); }
		finally { onChange(); }
    }
}
