package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.LinqingMap;
import com.empowerops.linqalike.LinqingSet;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.SortedLinqingSet;
import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Func1;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class IndexableLinqingSet<TElement> extends LinqingSet<TElement> {

    private final Map<Method, SortedLinqingSet<TElement>> treeIndexes = new HashMap<>();
    private final LinqingMap<Class, Method> indexAliasesByLambdaType = new LinqingMap<>();
    private final HashSet<Class> nonIndexedLambdaClasses = new HashSet<>();

    private final Class<TElement> hostType;

    public IndexableLinqingSet(Class<TElement> hostType) {
        super();
        this.hostType = hostType;
    }

    public IndexableLinqingSet(Class<TElement> hostType, int size) {
        super(size);
        this.hostType = hostType;
    }

    public IndexableLinqingSet(Class<TElement> hostType, TElement... tElements) {
        super(tElements);
        this.hostType = hostType;
    }

    public IndexableLinqingSet(Class<TElement> hostType, Iterator<? extends TElement> elements) {
        super(elements);
        this.hostType = hostType;
    }

    public IndexableLinqingSet(Class<TElement> hostType, Iterable<? extends TElement> tElements) {
        super(tElements);
        this.hostType = hostType;
    }

    public interface PropertyMethodRef<THost> extends Serializable {
        Object get(THost host);
    }

    @Override
    public <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector) {

        Class lambdaClass = comparableSelector.getClass();

        if( ! (comparableSelector instanceof Serializable)
                || nonIndexedLambdaClasses.contains(lambdaClass)){
            return super.orderBy(comparableSelector);
        }

        Method recognizedProperty = LambdaComprehention.getReferredProperty((Serializable) comparableSelector, hostType).getLeft();
        if (recognizedProperty != null && treeIndexes.containsKey(recognizedProperty)){
            return treeIndexes.get(recognizedProperty);
        }

        nonIndexedLambdaClasses.add(lambdaClass);
        return super.orderBy(comparableSelector);
    }

    public <TIndexed extends Comparable<TIndexed>>
    void treeIndex(LambdaComprehention.PropertyGetter<TElement, TIndexed> propertyToIndex){
        Tuple<Method, RuntimeException> target = LambdaComprehention.getReferredProperty(propertyToIndex, hostType);

        if(target.getRight() != null){ throw target.getRight(); }

        Method targetMethod = target.getLeft();

        Function<TElement, Comparable> wrappedTargetMethod = elem -> {
            try{
                Object result = targetMethod.invoke(elem);
                return Comparable.class.cast(result);
            }
            catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };

        @SuppressWarnings("unchecked") Comparator<TElement> comparator = Comparator.comparing(wrappedTargetMethod);
        SortedLinqingSet<TElement> index = SortedLinqingSet.createFor(comparator, this);

        treeIndexes.put(targetMethod, index);
    }

    private void onChange(Consumer<SortedLinqingSet<TElement>> actionTaken) {
        treeIndexes.values().forEach(actionTaken);
    }
    
    // ----------------------------------------------------
    // overridden mutators

    @Override
    public Iterator<TElement> iterator() {
        Iterator<TElement> superIter = super.iterator();

        return new Iterator<TElement>() {

            TElement current = null;

            @Override
            public boolean hasNext() {
                return superIter.hasNext();
            }

            @Override
            public TElement next() {
                return current = superIter.next();
            }

            @Override
            public void remove() {
                superIter.remove();

                //since these are indexes, this should be O(log(n)).
                onChange(set -> set.remove(current));
            }
        };
    }



    @Override
    public boolean removeAll(Collection<?> toRemove) {
        boolean changed = true;
		try { return changed = super.removeAll(toRemove); }
		finally { if(changed) onChange(index -> index.removeAll(toRemove)); }
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        boolean changed = true;
		try { return changed = super.removeIf(filter); }
		finally { if(changed) onChange(index -> index.removeIf(filter)); }
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        boolean changed = true;
		try { return changed = super.removeElement(toRemove); }
		finally { if(changed) onChange(index -> index.removeElement(toRemove)); }
    }

    @Override
    public boolean addAll(Collection<? extends TElement> elementsToAdd) {
        boolean changed = true;
		try { return changed = super.addAll(elementsToAdd); }
		finally { if(changed) onChange(index -> index.addAll(elementsToAdd)); }
    }

    @Override
    public boolean retainAll(Collection<?> elementsToKeep) {
        boolean changed = true;
        try { return changed = super.retainAll(elementsToKeep); }
		finally { if(changed) onChange(index -> index.retainAll(elementsToKeep)); }
    }


    @Override
    public boolean add(TElement newMember) {
        boolean changed = true;
		try { return changed = super.add(newMember); }
		finally { if(changed) onChange(index -> index.add(newMember)); }
    }

    @Override
    public boolean remove(Object toRemove) {
        boolean changed = true;
		try { return changed = super.remove(toRemove); }
		finally { if(changed) onChange(index -> index.remove(toRemove)); }
    }

    @Override
    public void clear() {
        try { super.clear(); }
        finally { onChange(Collection::clear); }
    }

    @Override
    public boolean retainAll(Iterable<? extends TElement> valuesToKeep) {
        boolean changed = true;
		try { return changed = super.retainAll(valuesToKeep); }
		finally { if(changed) onChange(index -> index.retainAll(valuesToKeep)); }
    }

    @Override
    public boolean removeAll(Iterable<? extends TElement> valuesToRemove) {
        boolean changed = true;
		try { return changed = super.removeAll(valuesToRemove); }
		finally { if(changed) onChange(index -> index.removeAll(valuesToRemove)); }
    }

    @Override
    public boolean addAllRemaining(Iterator<? extends TElement> valuesToBeAdded) {
        boolean changed = true;
        try { return changed = super.addAllRemaining(valuesToBeAdded); }
        finally { if(changed) onChange(index -> index.addAllRemaining(valuesToBeAdded)); }
    }

    @Override
    public boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        boolean changed = true;
		try { return changed = super.addAll(valuesToBeAdded); }
		finally { if(changed) onChange(index -> index.addAll(valuesToBeAdded)); }
    }

    @Override @SafeVarargs
    public final boolean addAll(TElement... valuesToBeAdded) {

        return doChange(collection -> collection.addAll(valuesToBeAdded));

		try { return changed = super.addAll(valuesToBeAdded); }
		finally { if(changed) onChange(index -> index.addAll(valuesToBeAdded)); }
    }

    private boolean doChange(Func1<IndexableLinqingSet<TElement>, Boolean> transform){
        boolean changed;
        try {
            changed = transform.getFrom(this);
        }
        catch(Exception e){
            Optional<ExceptionSet> aggregateEx = onChange(x -> transform.getFrom(x));
            aggregateEx.setInitialException(e);
            throw aggregateEx;
        }
        if(changed) try {
            Optional<ExceptionSet> aggregateEx = onChange(index -> index.addAll(valuesToBeAdded));
            aggregateEx.map(exception -> { throw exception; });
            return changed;
        }

    }


    @Override
    public void setAll(Iterable<? extends TElement> newItems) {
        try { super.setAll(newItems); }
		finally { onChange(index -> index.setAll(newItems)); }
    }

    @Override
    public void replace(TElement oldItem, TElement newItem) {
        try { super.replace(oldItem, newItem); }
		finally { onChange(index -> index.replace(oldItem, newItem)); }
    }

    @Override
    public void replace(TElement oldItem, TElement newItem, EqualityComparer<? super TElement> comparer) {
        try { super.replace(oldItem, newItem, comparer); }
		finally { onChange(index -> index.replace(oldItem, newItem, comparer)); }
    }
}
