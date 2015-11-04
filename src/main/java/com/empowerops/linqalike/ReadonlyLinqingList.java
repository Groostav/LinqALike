package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Immutable;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;

@Immutable
public class ReadonlyLinqingList<TElement> extends LinqingList<TElement> {

    private static final long serialVersionUID = - 1477889538915029367L;
    private static final int DEFAULT_CAPACITY = 10;

    public ReadonlyLinqingList() {
        super();
    }

    @SafeVarargs
    public ReadonlyLinqingList(TElement... elements) {
        super(elements.length);
        Factories.from(elements).forEach(super::add);
    }

    public ReadonlyLinqingList(Iterable<? extends TElement> elements) {
        super(fastSizeIfAvailable(elements).orElse(DEFAULT_CAPACITY));
        Factories.from(elements).forEach(super::add);
    }

    public ReadonlyLinqingList(Iterator<? extends TElement> initialElements){
        initialElements.forEachRemaining(super::add);
        super.trimToSize();
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        throwReadonly("removeIf");
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<TElement> operator) {
        throwReadonly("replaceAll");
    }

    @Override
    public boolean add(TElement value){
        throwReadonly("add");
        return false;
    }
    @Override
    public void add(int index, TElement value){
        throwReadonly("add");
    }
    @Override
    public boolean addAll(Collection<? extends TElement> collection){
        throwReadonly("addAll");
        return false;
    }
    @Override
    public boolean addAll(int targetIndex, Collection<? extends TElement> collection){
        throwReadonly("addAll");
        return false;
    }
    @Override
    public TElement set(int index, TElement value){
        throwReadonly("set");
        return null;
    }
    @Override
    public void clear(){
        throwReadonly("clear");
    }
    @Override
    public boolean remove(Object element){
        throwReadonly("remove");
        return false;
    }
    @Override
    public TElement remove(int index){
        throwReadonly("remove");
        return null;
    }
    @Override
    public boolean removeAll(Collection<?> collection){
        throwReadonly("removeAll");
        return false;
    }
    @Override
    public void removeRange(int left, int right){
        throwReadonly("removeRange");
    }
    @Override
    public boolean retainAll(Collection<?> collection){
        throwReadonly("retainAll");
        return false;
    }
    @Override
    public Iterator<TElement> iterator(){
        return super.listIterator();
    }

    private void throwReadonly(String methodName) {
        throw new UnsupportedOperationException(
                "cannot '" + methodName + "' to/from " +
                "a " + getClass().getSimpleName() + ""
        );
    }
}


