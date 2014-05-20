package com.EmpowerOperations.LinqALike;

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
    public void removeAll(Iterable<TElement> values) {
        super.removeAll(new LinqingList<>(values));
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
        Queryable<TElement> intersection = Factories.asList(setContainingNewAndExistingElements).except(this.intersect(setContainingNewAndExistingElements));
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
}


