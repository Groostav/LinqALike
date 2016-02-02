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
import java.util.function.Function;
import java.util.function.Predicate;

public class IndexedLinqingSet<TElement> extends LinqingSet<TElement> {

//    private final Map<Method, LinqingSet<TElement>> hashIndexes = new HashMap<>();
    private final Map<Method, SortedLinqingSet<TElement>> treeIndexes = new HashMap<>();
    private final LinqingMap<Class, Method> indexAliasesByLambdaType = new LinqingMap<>();
    private final HashSet<Class> nonIndexedLambdaClasses = new HashSet<>();

    private final Class<TElement> hostType;

    public IndexedLinqingSet(Class<TElement> hostType) {
        super();
        this.hostType = hostType;
    }

    public IndexedLinqingSet(Class<TElement> hostType, int size) {
        super(size);
        this.hostType = hostType;
    }

    public IndexedLinqingSet(Class<TElement> hostType, TElement... tElements) {
        super(tElements);
        this.hostType = hostType;
    }

    public IndexedLinqingSet(Class<TElement> hostType, Iterator<? extends TElement> elements) {
        super(elements);
        this.hostType = hostType;
    }

    public IndexedLinqingSet(Class<TElement> hostType, Iterable<? extends TElement> tElements) {
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

        if(nonIndexedLambdaClasses.contains(lambdaClass)){
            return super.orderBy(comparableSelector);
        }

        Method recognizedProperty = LambdaComprehention.getReferredProperty(comparableSelector.getClass(), hostType).getLeft();
        if (recognizedProperty != null && treeIndexes.containsKey(recognizedProperty)){
            return treeIndexes.get(recognizedProperty);
        }

        nonIndexedLambdaClasses.add(lambdaClass);
        return super.orderBy(comparableSelector);
    }

    public <TIndexed extends Comparable<TIndexed>> void treeIndex(LambdaComprehention.PropertyGetter<TElement, Comparable<?>> propertyToIndex){
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
        boolean changed = true;
        try { return changed = super.addAllRemaining(valuesToBeAdded); }
        finally { if(changed) onChange(); }
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
