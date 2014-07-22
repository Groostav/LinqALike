package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func;
import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.Queries.DefaultQueryable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.EmpowerOperations.LinqALike.CommonDelegates.isEqualTo;
import static com.EmpowerOperations.LinqALike.CommonDelegates.memoized;
import static com.EmpowerOperations.LinqALike.CommonDelegates.performEqualsUsing;

/**
 * An {@link java.util.ArrayList} decorated with support {@link com.EmpowerOperations.LinqALike.Queryable}, probably the most
 * useful instantiable class in this library.
 *
 * In relational terms a Java list is an <i>ordered bag</i>, meaning it may contain duplicates, and it maintains
 * its own (indexed) order.
 *
 * @see java.util.ArrayList
 * @see com.EmpowerOperations.LinqALike.Queryable
 * @see com.EmpowerOperations.LinqALike.Queries.DefaultQueryable
 */
public class LinqingList<TElement> extends ArrayList<TElement> implements DefaultQueryable<TElement> {

    // Constructors
    public LinqingList(){
        super();
    }
    public LinqingList(TElement... elements) {
        this(Arrays.asList(elements));
    }
    public LinqingList(Iterator<? extends TElement> elements){
        while (elements.hasNext()){
            TElement next = elements.next();
            add(next);
        }
    }
    public LinqingList(Iterable<? extends TElement> elements){
        this();
        for(TElement element : elements){
            add(element);
        }
    }

    public LinqingList(Class<TElement> elementClass, Object[] initialValues){
        this();
        for(Object object : initialValues){
            if(object == null){
                add(null);
                continue;
            }
            if( ! object.getClass().isAssignableFrom(elementClass)){
                throw new IllegalArgumentException("initialValues contains an element of type '" + object.getClass().getSimpleName() + "'" +
                                                   "but the list to be constructed is for elements of type '" + elementClass.getSimpleName() + "'.");
            }

            add((TElement) object);
        }
    }

    /*
     * List-based Mutators
     */

    /**
     * <p>adds all elements in the supplied ellipses set to this list, starting from the last current index, and adding them
     * left-to-right.</p>
     *
     * @param   valuesToBeAdded the elements to be added to this linqing list.
     * @return  true if the list changed as a result of this call.
     */
    public boolean addAll(TElement... valuesToBeAdded){
        return addAll(Factories.asList(valuesToBeAdded));
    }

    public boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        return addAll(Factories.asList(valuesToBeAdded));
    }

    public boolean removeElement(TElement element){
        return remove(element);
    }

    public boolean removeAll(Iterable<? extends TElement> values) {
        return super.removeAll(Factories.asList(values));
    }

    public void removeSingle(Condition<? super TElement> condition){
        TElement element = this.single(condition);
        this.remove(element);
    }

    public void addIfNotNull(TElement element) {
        if(element != null){
            add(element);
        }
    }

    public boolean addIfNew(TElement element){
        return containsElement(element) ? false : add(element);
    }

    public void addAllNew(Iterable<TElement> setContainingNewAndExistingElements) {
        Queryable<TElement> intersection = Factories.from(setContainingNewAndExistingElements).except(this.intersect(setContainingNewAndExistingElements)).fetch();
        this.addAll(intersection);
    }

    public void replaceAll(Queryable<? extends Map.Entry<TElement, TElement>> changedItems) {
        for(Map.Entry<TElement, TElement> pair : changedItems){
            this.replace(pair.getKey(), pair.getValue());
        }
    }

    public void clearAndAddAll(Iterable<? extends TElement> newItems){
        clear();
        addAll(newItems);
    }

    public void replace(TElement oldItem, TElement newItem) {
        int index = indexOf(oldItem);
        assert index != -1 : oldItem + " is not contained in " + this;
        this.add(index, newItem);
        this.remove(oldItem);
    }

    public void move(int fromIndex, int toIndex){

    }

    public void move(TElement elementToMove, Condition<? super Tuple<TElement, TElement>> positionCondition){
        move(elementToMove, positionCondition, () -> null);
    }

    public void move(TElement elementToMove,
                     EqualityComparer<? super TElement> equalityComparer,
                     Condition<? super Tuple<TElement, TElement>> positionCondition){
        move(elementToMove, equalityComparer, positionCondition, () -> null);
    }

    public <TCompared> void move(TElement elementToMove,
                                 Func1<? super TElement, TCompared> equatableSelector,
                                 Condition<? super Tuple<TElement, TElement>> positionCondition){
        move(elementToMove, equatableSelector, positionCondition, () -> null);
    }

    public void move(Condition<? super TElement> elementsToMove,
                     Condition<? super Tuple<TElement, TElement>> positionCondition){
        move(elementsToMove, positionCondition, () -> null);
    }

    /**
     * Moves the specified element from its current location in the list to the specified location.
     *
     * @param elementToMove The element that may be moved into a new position.
     * @param positionCondition Condition that gives the would-be right neighbour of elementToMove.
     */
    public void move(TElement elementToMove,
                     Condition<? super Tuple<TElement, TElement>> positionCondition,
                     Func<? extends TElement> defaultFactory){

        move(isEqualTo(elementToMove, CommonDelegates.DefaultEquality), positionCondition, defaultFactory);
    }

    public void move(TElement elementToMove,
                     EqualityComparer<? super TElement> equalityComparer,
                     Condition<? super Tuple<TElement, TElement>> positionCondition,
                     Func<? extends TElement> defaultFactory){

        Preconditions.notNull(equalityComparer, "equalityComparer");

        move(isEqualTo(elementToMove, equalityComparer), positionCondition, defaultFactory);
    }

    public <TCompared> void move(TElement elementToMove,
                                 Func1<? super TElement, TCompared> equatableSelector,
                                 Condition<? super Tuple<TElement, TElement>> positionCondition,
                                 Func<? extends TElement> defaultFactory){

        Preconditions.notNull(equatableSelector, "equatableSelector");

        move(isEqualTo(elementToMove, performEqualsUsing(memoized(equatableSelector))), positionCondition, defaultFactory);
    }

    public void move(Condition<? super TElement> elementsToMove,
                     Condition<? super Tuple<TElement, TElement>> positionCondition,
                     Func<? extends TElement> defaultFactory){

        Preconditions.notNull(elementsToMove, "elementsToMove");
        Preconditions.notNull(positionCondition, "positionCondition");
        Preconditions.notNull(defaultFactory, "defaultFactory");

        Queryable<TElement> valuesToMove = this.where(elementsToMove);
        Queryable<TElement> listWithoutNomads = this.except(valuesToMove);
        Queryable<Tuple<TElement, TElement>> locationPairs = listWithoutNomads.pairwise(defaultFactory);

        Preconditions.hasExactlyOneMatching(locationPairs, positionCondition, "positionCondition");

        int leftNeighbourIndex = -1;

        for (Tuple<TElement, TElement> pair : locationPairs) {
            if (positionCondition.passesFor(pair)) {
                this.addAll(leftNeighbourIndex + 1, valuesToMove.toList());
                break;
            }
            leftNeighbourIndex += 1;
        }
    }
}


