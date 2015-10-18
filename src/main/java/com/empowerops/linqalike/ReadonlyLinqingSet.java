package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Immutable;
import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;

@Immutable
public class ReadonlyLinqingSet<TElement> extends LinqingSet<TElement> {

    private static final long serialVersionUID = - 1477889538915029367L;

    public ReadonlyLinqingSet(){}

    @SafeVarargs
    public ReadonlyLinqingSet(TElement... initialAndFinalMembers) {
        super(initialAndFinalMembers.length);
        from(initialAndFinalMembers).iterator().forEachRemaining(super::add);
    }

    public ReadonlyLinqingSet(Iterable<? extends TElement> initialAndFinalMembers){
        super(fastSizeIfAvailable(initialAndFinalMembers));
        initialAndFinalMembers.iterator().forEachRemaining(super::add);
    }

    public ReadonlyLinqingSet(Iterator<? extends TElement> initialAndFinalMembers){
        initialAndFinalMembers.forEachRemaining(super::add);
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        throwReadonly("removeIf");
        return false;
    }

    @Override
    public boolean add(TElement value){
        throwReadonly("add");
        return false;
    }
    @Override
    public boolean addAll(@Nonnull Collection<? extends TElement> collection){
        throwReadonly("addAll");
        return false;
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
    public boolean removeAll(Collection<?> collection){
        throwReadonly("removeAll");
        return false;
    }
    @Override
    public boolean retainAll(@Nonnull Collection<?> collection){
        throwReadonly("retainAll");
        return false;
    }

    private void throwReadonly(String methodName) {
        throw new UnsupportedOperationException(
                "cannot '" + methodName + "' to/from " +
                        "a " + getClass().getSimpleName() + ""
        );
    }
}


