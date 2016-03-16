package com.empowerops.linqalike.experimental;


import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;
import com.sun.javafx.binding.ObjectConstant;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the readable face of a queryable observable list.
 *
 * <p>Note that this interface is <i>supposed</i> to be read-only in the same way that Queryable is readonly.
 * In much the same way that the result of <code>someMutableList.select(...)</code> is read-only (in that you may not
 * add to the result), many instances of ObservableQueryable are read-only, as they are
 * queryable derrivatives of other lists (ie, the result of a <code>selectObservable</code> or
 * <code>whereObservable</code> call. This means the implementation of <code>ObservableList.add(E)</code> is a
 * misnomer, and will likely throw <code>UnsupportedOperationException</code>
 *
 * Created by Geoff on 2014-08-10.
 */

public interface ObservableQueryable<TElement> extends Observable, Queryable<TElement>{

    //TODO once this finds a home, put this somewhere with protetected access

    static <TElement>
    WritableObservableQueryable<TElement> makeDefaultObservableCollectionFor(ObservableQueryable<?> sourceElements,
                                                                             Queryable<TElement> initialElements) {
        if (sourceElements instanceof Set) {
            return new ObservableLinqingSet<>(initialElements);
        }
        else if (sourceElements instanceof List) {
            return new ObservableLinqingList<>(initialElements);
        }
        else {
            Logger log = Logger.getLogger(ObservableQueryable.class.getCanonicalName());
            log.info(
                    "couldn't find an appropriate type for the derrived observable set of " +
                            "'" + sourceElements + "'"
            );
            return new ObservableLinqingList<>();
        }
    }

    public default <TResult> ObservableQueryable<TResult> selectObservable2(Func1<? super TElement, TResult> selector){
        return selectObservable(elem -> ObjectConstant.valueOf(selector.getFrom(elem)));
    }

    public default <TResult> ObservableQueryable<TResult> selectObservable(Func1<? super TElement, ObservableValue<TResult>> selector) {
        return selectObservable(selector, elems -> makeDefaultObservableCollectionFor(this, elems));
    }

    public default <TResult> ObservableQueryable<TResult> selectObservable(Func1<? super TElement, ObservableValue<TResult>> selector,
                                                                           Func1<Queryable<TResult>, ? extends WritableObservableQueryable<TResult>> newCollectionFactory){
        Queryable<TResult> initialElements = this.select(selector).select(ObservableValue::getValue);
        WritableObservableQueryable<TResult> result = newCollectionFactory.getFrom(initialElements);
        ObservableSelectSourceListener.addBehaviourFor(this, result, selector);
        return result;
    }

    public default ObservableQueryable<TElement> whereObservable(Func1<? super TElement, ObservableValue<Boolean>> predicate) {
        return whereObservable(predicate, elems -> makeDefaultObservableCollectionFor(this, elems));
    }
    public default ObservableQueryable<TElement> whereObservable2(Condition<? super TElement> predicate) {
        return whereObservable(elem -> ObjectConstant.valueOf(predicate.passesFor(elem)));
    }

    public default ObservableQueryable<TElement> whereObservable(Func1<? super TElement, ObservableValue<Boolean>> predicate,
                                                                 Func1<Queryable<TElement>, ? extends WritableObservableQueryable<TElement>> collectionFactory){

        Queryable<TElement> initialElements = this.where(elem -> predicate.getFrom(elem).getValue());
        WritableObservableQueryable<TElement> result = collectionFactory.getFrom(initialElements);
        ObservableWhereSourceListener.addBehaviourFor(this, result, predicate);
        return result;
    }

    public default ObservableLinqingList<TElement> toBoundObservableList(){
        ObservableLinqingList<TElement> result = new ObservableLinqingList<>(this);
        LinqFX.createContentBinding(result, this);
        return result;
    }

    public default ObservableLinqingSet<TElement> toBoundObservableSet(){
        ObservableLinqingSet<TElement> result = new ObservableLinqingSet<>(this);
        LinqFX.createContentBinding(result, this);
        return result;
    }

    public void addListener(FlexibleCollectionChangeListener<TElement> listener);
}


