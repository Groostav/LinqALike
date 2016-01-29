package com.empowerops.linqalike.common;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;

import java.util.Iterator;
import java.util.LinkedHashSet;

import static com.empowerops.linqalike.Factories.empty;
import static com.empowerops.linqalike.Factories.from;

/**
 * This class is a hack to allow caller-defined {@link #hashCode()} and {@link #equals(Object)} built
 * on top of the already well hacked {@link java.util.LinkedHashSet}.
 *
 * <p>This class will be re-written as soon as I can find the time
 * to take off my software developer hat and put on my computer scientist hat and write my own
 * Hashing Set object. That object will have the same name and ahere to the same interface
 * as this class.
 *
 * Created by Geoff on 2014-05-11.
 */
public class ComparingLinkedHashSet<TElement> implements DefaultedQueryable<TElement> {

    private final LinkedHashSet<Reference<TElement>> backingSet = new LinkedHashSet<>();
    private final EqualityComparer<? super TElement> equalityComparer;
    private final Class<? super TElement>            widestEquatableType;

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer) {
        this(equalityComparer, empty());
    }

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer, TElement... initialElements) {
        this(equalityComparer, from(initialElements));
    }

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer, Iterable<? extends TElement> initialValues) {
        this.equalityComparer = equalityComparer;
        this.widestEquatableType = Object.class;

        for (TElement element : initialValues) {
            backingSet.add(addEqualsInterceptor(element));
        }
    }

    //TODO other constructors.

    @Override
    public Iterator<TElement> iterator() {
        return new Iterator<TElement>() {
            Iterator<Reference<TElement>> backingIterator = backingSet.iterator();

            @Override
            public boolean hasNext() {
                return backingIterator.hasNext();
            }

            @Override
            public TElement next() {
                return backingIterator.next().value;
            }
        };
    }

    public int size() {
        return backingSet.size();
    }

    public boolean add(TElement element) {
        return backingSet.add(addEqualsInterceptor(element));
    }

    public boolean addAll(Iterable<TElement> newItems) {
        boolean madeChange = false;
        for(TElement element : newItems){
            madeChange |= add(element);
        }
        return madeChange;
    }

    public boolean addAll(TElement... newItems){
        return addAll(from(newItems));
    }

    public boolean contains(TElement canddiate){
        return backingSet.contains(addEqualsInterceptor(canddiate));
    }

    public boolean remove(TElement existingElement){
        return backingSet.remove(addEqualsInterceptor(existingElement));
    }

    public boolean retainAll(Iterable<? extends TElement> allowedElements){
        boolean hasChanged = false;
        for(TElement element : this){
            if( ! Linq.containsElement(allowedElements, element, equalityComparer)){
                hasChanged |= remove(element);
            }
        }
        return hasChanged;
    }

    public boolean removeAll(Iterable<? extends TElement> existingElements) {
        boolean hasChanged = false;
        for(TElement existingElement : existingElements){
            hasChanged |= remove(existingElement);
        }
        return hasChanged;
    }



    @SuppressWarnings("unchecked")
    private Reference<TElement> addEqualsInterceptor(TElement existingElement) {
        return Reference.withSpecificEquals(existingElement, (Class) widestEquatableType, equalityComparer);
    }
}
