package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Linq;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * This class is a hack to allow caller-defined {@link #hashCode()} and {@link #equals(Object)} built
 * on top of the already well hacked {@link java.util.LinkedHashSet}.
 *
 * <p>This class will be re-written as soon as I can find the time
 * to take off my software developer hat and put on my computer scientist hat and write my own
 * Hashing Set object. That object will have the same name and ahere to the same interface
 * as this class.</p>
 *
 * Created by Geoff on 2014-05-11.
 */
public class ComparingLinkedHashSet<TElement> implements QueryableSet<TElement> {

    private final LinkedHashSet<Reference<TElement>> backingSet = new LinkedHashSet<>();
    private final EqualityComparer<? super TElement> equalityComparer;
    private final Class<? super TElement> widestEquatableType;

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer){
        this.equalityComparer = equalityComparer;
        this.widestEquatableType = Object.class;
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
        return backingSet.add(makeEquatable(element));
    }

    public boolean addAll(Iterable<TElement> newItems) {
        boolean madeChange = false;
        for(TElement element : newItems){
            madeChange |= add(element);
        }
        return madeChange;
    }

    public boolean contains(TElement canddiate){
        return backingSet.contains(makeEquatable(canddiate));
    }

    public boolean remove(TElement existingElement){
        return backingSet.remove(makeEquatable(existingElement));
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

    //TODO remove, refactor, or re-introduce this. Might be post 1.0.

//    @SuppressWarnings("unchecked")
//    private <TResult> TResult attemptRuntimeTypeRestrictedAction(Object item,
//                                                                 Func1<Reference<TElement>, TResult> transform,
//                                                                 final String alternativeTypeSafeMethodName) {
//        if (widestEquatableType != null && widestEquatableType.isInstance(item)){
//            return transform.getFrom(makeEquatable((TElement) item));
//        }
//        else if ( widestEquatableType != null){
//            throw new IllegalArgumentException("the supplied argument cannot be tested for equality since it's not of a type that the equality comparer will accept");
//        }
//        else if(equalityComparer instanceof EqualityComparer.Untyped){
//            return transform.getFrom(makeEquatable((TElement) item));
//        }
//        else{
//            throw new UnsupportedOperationException(
//                    "Cannot determine a safe way to cast the argument such that it may be tested by this set's custom equality comparer '" + equalityComparer + ".\n" +
//                            "Consider\n " +
//                            "\tA) supplying either an " + "EqualityComparer.Untyped instance" + ", \n" +
//                            "\tB) supplying the constructor with the 'widestEquatableType' parameter, or \n" +
//                            "\tC) using the '" + alternativeTypeSafeMethodName + "' method."
//            );
//        }
//    }
//
//    private boolean equalitySafeContains(Collection<?> allowedElements, TElement element, String alternativeTypeSafeMethodName) {
//        boolean isContained = false;
//        for(Object allowed : allowedElements){
//            isContained = attemptRuntimeTypeRestrictedAction(
//                    allowed,
//                    x -> equalityComparer.equals((TElement)allowed, element),
//                    alternativeTypeSafeMethodName);
//            if(isContained){
//                break;
//            }
//        }
//        return isContained;
//    }

    @SuppressWarnings("unchecked")
    private Reference<TElement> makeEquatable(TElement existingElement) {
        return Reference.withSpecificEquals(existingElement, (Class) widestEquatableType, equalityComparer);
    }
}
