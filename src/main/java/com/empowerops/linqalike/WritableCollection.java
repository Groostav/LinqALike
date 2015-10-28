package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static com.empowerops.linqalike.CommonDelegates.Not;

/**
 * Created by Geoff on 18/12/2014.
 */
public interface WritableCollection<TElement> extends Iterable<TElement>, Queryable<TElement> {

    boolean add(TElement newElement);
    // The name cant be 'remove' since that would cause it to clash with Collection.remove(Object o);
    // the issue is not a compilation one --having an interface define a method that already exists is OK
    // but it would mean that there would be no type-safe remove method.
    boolean removeElement(TElement toRemove);

    public default boolean addAll(TElement... valuesToBeAdded){
        boolean wasModified = false;
        for(TElement element : valuesToBeAdded){
            wasModified |= add(element);
        }
        return wasModified;
    }

    public default boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        boolean wasModified = false;
        for(TElement element : valuesToBeAdded){
            wasModified |= add(element);
        }
        return wasModified;
    }

    public default boolean addAllRemaining(Iterator<? extends TElement> valuesToBeAdded){
        boolean wasModified = false;
        while(valuesToBeAdded.hasNext()){
            wasModified |= add(valuesToBeAdded.next());
        }
        return wasModified;
    }

    public default boolean removeAll(Iterable<? extends TElement> valuesToRemove) {
        boolean wasModified = false;
        for(TElement element : valuesToRemove){
            wasModified |= removeElement(element);
        }
        return wasModified;
    }

    public default boolean retainAll(Iterable<? extends TElement> valuesToKeep){
        HashSet<? extends TElement> setToKeep = Factories.asReadonlySet(valuesToKeep);

        return removeIf(Not(setToKeep::contains)::passesFor);
    }

    public default boolean removeIf(Predicate<? super TElement> predicate){
        boolean wasModified = false;
        Iterator<TElement> iterator = iterator();
        while(iterator.hasNext()){
            TElement next = iterator.next();

            if(predicate.test(next)){
                iterator.remove();
                wasModified = true;
            }
        }
        return wasModified;
    }

    public default void addIfNotNull(TElement element) {
        if(element != null){
            add(element);
        }
    }

    public default void clear(){
        for(TElement element : this){
            removeElement(element);
        }
    }

    public default void clearAndAddAll(Iterable<? extends TElement> newItems){
        clear();
        addAll(newItems);
    }

    default void replace(TElement oldItem, TElement newItem) {
        replace(oldItem, newItem, CommonDelegates.DefaultEquality);
    }

    /**
     * Removes the old item and adds the new item
     * at the same index as the old item if <tt>this</tt> is a {@link List}.
     *
     * <p>this method does <i>not</i> maintain iteration order unless this is
     * a {@link List}, or {@link #add} has some other means of ordering
     * (such as for an {@link java.util.SortedSet})
     *
     * @param oldItem the item already in this collection to be replaced
     * @param newItem the new item to replace the old item with
     * @throws IllegalArgumentException if <code>oldItem</code> is not contained in this collection
     */
    @SuppressWarnings("unchecked") //for this to be a problem
    // this class would have to extend List<TBad != TElement>
    // which is forbidden by java, since there'd be signature collisions.
    // (ie, that class would have to implement both replace(TElement, TElement) and replace(TBad, TBad),
    // which both erase to replace(Object, Object) -> collision
    default void replace(TElement oldItem, TElement newItem, EqualityComparer<? super TElement> comparer) {

        if(this instanceof List) {
            List thisActual = (List) this;
            int index = Linq.indexOf(thisActual, oldItem, comparer);
            if (index == -1){ throw new IllegalArgumentException("this does not contain '" + oldItem + "'"); }
            thisActual.set(index, newItem);
        }
        else {
            // its a set, which means we're not breaking indexes,
            // though we're breaking iteration order. hmm.
            boolean changed = removeElement(oldItem);
            if( ! changed){
                add(oldItem);
                throw new IllegalArgumentException("this does not contain '" + oldItem + "'");
            }
            add(newItem);
        }
    }

}
