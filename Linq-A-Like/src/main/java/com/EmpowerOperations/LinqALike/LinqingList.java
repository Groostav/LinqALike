package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.Delegate.*;
import com.EmpowerOperations.LinqALike.Queries.*;

import java.util.*;

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

    public boolean addIfNew(TElement element){
        return containsElement(element) ? false : add(element);
    }

    /**
     * <p>adds all elements in the supplied ellipses set to this list, starting from the last current index, and adding them
     * left-to-right.</p>
     *
     * @param   valuesToBeAdded the elements to be added to this linqing list.
     * @return  true if the list changed as a result of this call.
     */
    public boolean addAll(TElement... valuesToBeAdded){
        boolean modified = false;
        for(TElement element : valuesToBeAdded){
            modified |= add(element);
        }
        return modified;
    }

    public boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        boolean modified = false;
        for(TElement element : valuesToBeAdded){
            modified |= add(element);
        }
        return modified;
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

    /**
     *
     * @param elementToMove The element that may be moved into a new position.
     * @param positionCondition Condition that gives the would-be right neighbour of elementToMove. */
    public void move(TElement elementToMove,
                     Condition<? super Tuple<TElement, TElement>> positionCondition,
                     Func<? extends TElement> defaultFactory){

        Preconditions.contains(this , elementToMove, "elementToMove");
        LinqingList<TElement> sourceWithoutElement = this.toList();
        sourceWithoutElement.remove(elementToMove);
        Preconditions.hasExactlyOneMatching(sourceWithoutElement.pairwise(defaultFactory), positionCondition, "positionCondition");

        int leftNeighbourIndex = -1;
        this.remove(elementToMove);

        for (Tuple<TElement, TElement> pair : this.pairwise(defaultFactory)) {
            if (positionCondition.passesFor(pair)) {
                this.add(leftNeighbourIndex + 1, elementToMove);
                break;
            }
            leftNeighbourIndex += 1;
        }

    }
}


