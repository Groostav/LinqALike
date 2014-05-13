package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.Linq;

import java.util.*;

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
public class ComparingLinkedHashSet<TElement> extends AbstractSet<TElement> implements Set<TElement> {

    private final LinkedHashSet<EquatableReference<TElement>> backingSet = new LinkedHashSet<>();
    private final EqualityComparer<? super TElement> equalityComparer;
    private final Class<? super TElement> widestEquatableType;

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer){
        this.equalityComparer = equalityComparer;
        this.widestEquatableType = null;
    }

    public ComparingLinkedHashSet(EqualityComparer<? super TElement> equalityComparer, Class<? super TElement> widestEquatableType){
        this.equalityComparer = equalityComparer;
        this.widestEquatableType = widestEquatableType;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new Iterator<TElement>() {
            Iterator<EquatableReference<TElement>> backingIterator = backingSet.iterator();

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

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean add(TElement element) {
        return backingSet.add(makeEquatable(element));
    }

    @Override
    public boolean contains(Object candidate) {
        return attemptRuntimeTypeRestrictedAction(candidate, backingSet::contains, "containsElement(TElement candidate)");
    }

    public boolean containsElement(TElement canddiate){
        return backingSet.contains(makeEquatable(canddiate));
    }

    @Override
    public boolean remove(Object item) {
        return attemptRuntimeTypeRestrictedAction(item, backingSet::remove, "removeElement(TElement existingElement");
    }

    public boolean removeElement(TElement existingElement){
        return backingSet.remove(makeEquatable(existingElement));
    }

    @Override
    public boolean retainAll(Collection<?> allowedElements) {
        boolean hasChanged = false;
        for(TElement element : this){
            if( ! equalitySafeContains(allowedElements, element, "retainAllElements(Iterable<? extends TElement> allowedElements")){
                hasChanged |= removeElement(element);
            }
        }

        return hasChanged;
    }

    public boolean retainAllElements(Iterable<? extends TElement> allowedElements){
        boolean hasChanged = false;
        for(TElement element : this){
            if( ! Linq.containsElement(allowedElements, element, equalityComparer)){
                hasChanged |= removeElement(element);
            }
        }
        return hasChanged;
    }

    @Override
    public boolean removeAll(Collection<?> existingElements) {
        boolean hasChanged = false;
        for(Object existingElement : existingElements){
            hasChanged |= attemptRuntimeTypeRestrictedAction(existingElements, backingSet::remove, "removeAllElements(Iterable<? extends TElement> existingElements");
        }
        return hasChanged;
    }

    public boolean removeAllElements(Iterable<? extends TElement> existingElements) {
        boolean hasChanged = false;
        for(TElement existingElement : existingElements){
            hasChanged |= removeElement(existingElement);
        }
        return hasChanged;
    }

    @SuppressWarnings("unchecked")
    private <TResult> TResult attemptRuntimeTypeRestrictedAction(Object item, Func1<EquatableReference<TElement>, TResult> transform, final String alternativeTypeSafeMethodName) {
        if (widestEquatableType != null && widestEquatableType.isInstance(item)){
            return transform.getFrom(makeEquatable((TElement) item));
        }
        else if ( widestEquatableType != null){
            throw new IllegalArgumentException("the supplied argument cannot be tested for equality since it's not of a type that the equality comparer will accept");
        }
        else if(equalityComparer instanceof EqualityComparer.Untyped){
            return transform.getFrom(makeEquatable((TElement) item));
        }
        else{
            throw new UnsupportedOperationException(
                    "Cannot determine a safe way to cast the argument such that it may be tested by this set's custom equality comparer '" + equalityComparer + ".\n" +
                            "Consider\n " +
                            "\tA) supplying either an " + "EqualityComparer.Untyped instance" + ", \n" +
                            "\tB) supplying the constructor with the 'widestEquatableType' parameter, or \n" +
                            "\tC) using the '" + alternativeTypeSafeMethodName + "' method."
            );
        }
    }

    private boolean equalitySafeContains(Collection<?> allowedElements, TElement element, String alternativeTypeSafeMethodName) {
        boolean isContained = false;
        for(Object allowed : allowedElements){
            isContained = attemptRuntimeTypeRestrictedAction(allowed,
                    x -> equalityComparer.equals((TElement)allowed, element),
                    alternativeTypeSafeMethodName);
            if(isContained){
                break;
            }
        }
        return isContained;
    }

    private EquatableReference<TElement> makeEquatable(TElement existingElement) {
        return new EquatableReference<>(existingElement, equalityComparer);
    }
}
