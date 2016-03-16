package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import com.empowerops.linqalike.common.Preconditions;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.empowerops.linqalike.CommonDelegates.nullSafeEquals;

/**
* Created by Geoff on 2014-08-11.
*/
public class ObservableWhereSourceListener<TElement> implements FlexibleCollectionChangeListener<TElement> {

    private static final Logger log = Logger.getLogger(ObservableWhereSourceListener.class.getCanonicalName());

    private final ObservableQueryable<TElement>                     sourceCollection;
    private final WritableObservableQueryable<TElement>             filteredCollection;
    private final Func1<? super TElement, ObservableValue<Boolean>> predicateSelector;
    private final Condition<TElement>                               predicate;

    public static <TElement> void addBehaviourFor(ObservableQueryable<TElement> sourceElements,
                                                  WritableObservableQueryable<TElement> destinationCollection,
                                                  Func1<? super TElement, ObservableValue<Boolean>> predicate) {

        for (TElement existingElement : sourceElements) {

            ObservableValue<Boolean> value = predicate.getFrom(existingElement);
            value.addListener(new ObservableWherePredicateListener<>(sourceElements, destinationCollection, existingElement, predicate));
        }

        LinqFX.addChangeListener(sourceElements, new ObservableWhereSourceListener<>(sourceElements, destinationCollection, predicate));
    }

    public ObservableWhereSourceListener(ObservableQueryable<TElement> sourceCollection,
                                         WritableObservableQueryable<TElement> filteredCollection,
                                         Func1<? super TElement, ObservableValue<Boolean>> predicateSelector){
        this.sourceCollection = sourceCollection;
        this.filteredCollection = filteredCollection;
        this.predicateSelector = predicateSelector;
        this.predicate = elem -> predicateSelector.getFrom(elem).getValue();
    }

    private boolean isOrderedProperty() {
        Queryable<TElement> currentElements = Factories.from(sourceCollection).where(predicate);
        return currentElements.sequenceEquals(filteredCollection);
    }

    private void bindListToNewElements(Iterable<? extends TElement> addedSubList) {
        for(TElement element : addedSubList){
            ObservableValue<Boolean> predicate = predicateSelector.getFrom(element);
            ObservableWherePredicateListener<TElement> listener = new ObservableWherePredicateListener<>(
                    sourceCollection,
                    filteredCollection,
                    element,
                    predicateSelector
            );
            predicate.addListener(listener);
            listener.onElementAdded(element);
        }
    }

    private void unbindListFromRemovedElements(Iterable<? extends TElement> removed) {
        for(TElement element : removed){
            ObservableValue<Boolean> predicate = predicateSelector.getFrom(element);
            ObservableWherePredicateListener<TElement> listener = ObservableWherePredicateListener.forPurposeOfEquality(
                    sourceCollection,
                    filteredCollection,
                    element,
                    predicateSelector
            );
            predicate.removeListener(listener);
            listener.onElementRemoved(element);
        }
    }

    @Override
    public void onChanged(Collection<? extends TElement> source,
                          Queryable<TElement> added,
                          Queryable<TElement> removed,
                          int from,
                          int to,
                          boolean isPermutation) {

        bindListToNewElements(added);
        unbindListFromRemovedElements(removed);
    }

    @Override
    public void onChangeApplied() {
        if( ! isOrderedProperty()){
            filteredCollection.clearAndAddAll(sourceCollection.where(predicate));
        }
    }

    public static class ObservableWherePredicateListener<TElement> implements ChangeListener<Boolean> {

        private final        Iterable<TElement>                                sourceElements;
        private final        WritableObservableQueryable<TElement>             destinationCollection;
        private final        TElement                                          predicatedMember;
        private final        Func1<? super TElement, ObservableValue<Boolean>> observablePredicate;
        private final        Condition<TElement>                               predicate;

        public static <TElement>
        ObservableWherePredicateListener<TElement> forPurposeOfEquality(ObservableQueryable<TElement> sourceList,
                                                                        WritableObservableQueryable<TElement> destinationList,
                                                                        TElement predicateHost,
                                                                        Func1<? super TElement, ObservableValue<Boolean>> predicate) {

            return new ObservableWherePredicateListener<>(sourceList, destinationList, predicateHost, predicate);
        }

        public ObservableWherePredicateListener(ObservableQueryable<TElement> sourceElements,
                                                WritableObservableQueryable<TElement> destinationCollection,
                                                TElement predicatedMember,
                                                Func1<? super TElement, ObservableValue<Boolean>> predicateSelector) {

            Preconditions.notNull(sourceElements, "sourceElements");
            Preconditions.notNull(destinationCollection, "filteredList");
            Preconditions.notNull(predicatedMember, "predicatedMember");
            Preconditions.notNull(predicateSelector, "predicateSelector");

            this.sourceElements = sourceElements;
            this.predicatedMember = predicatedMember;
            this.observablePredicate = predicateSelector;
            this.destinationCollection = destinationCollection;

            this.predicate = elem -> observablePredicate.getFrom(elem).getValue();
        }

        private void onElementAdded(TElement element) {
            ObservableValue<Boolean> source = observablePredicate.getFrom(element);
            //ensure its updated by claiming it was in the list previously but it failed the filter.
            changed(source, false, source.getValue());
        }

        private void onElementRemoved(TElement element) {
            ObservableValue<Boolean> source = observablePredicate.getFrom(element);
            //ensure its removed from the list by simulating its filter status is now invalid
            changed(source, source.getValue(), false);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            boolean hasBeenExcluded = (! newValue && oldValue);
            boolean hasBeenReadmitted = (! oldValue && newValue);
            boolean hasNotChanged = (newValue == oldValue);

            if (hasBeenExcluded) {
                destinationCollection.removeElement(predicatedMember);
            }
            else if (hasBeenReadmitted) {
                if (destinationCollection instanceof List) {
                    int index = getIndexInDestinationFor(predicatedMember);
                    ((List<TElement>) destinationCollection).add(index, predicatedMember);
                }
                else if (destinationCollection instanceof Set) {
                    destinationCollection.add(predicatedMember);
                }
                else {
                    log.info("collection neither a set nor list. Update this code!");
                    destinationCollection.add(predicatedMember);
                }
            }
            else if (hasNotChanged) {
                //do nothing;
            }
            else {
                throw new IllegalStateException();
            }
        }

        private int getIndexInDestinationFor(TElement desiredElement) {
            int index = 0;
            for (TElement current : Factories.from(sourceElements).where(predicate)) {
                if (nullSafeEquals(current, desiredElement)) {
                    break;
                }
                index += 1;
            }
            return index;
        }

        ///////////////////////////////////////////
        //this code was auto-generated by IntelliJ.

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;

            ObservableWherePredicateListener rhsInstance = (ObservableWherePredicateListener) rhs;

            if (! destinationCollection.equals(rhsInstance.destinationCollection)) return false;
            if (! observablePredicate.equals(rhsInstance.observablePredicate)) return false;
            if (! predicatedMember.equals(rhsInstance.predicatedMember)) return false;
            if (! sourceElements.equals(rhsInstance.sourceElements)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sourceElements.hashCode();
            result = 31 * result + predicatedMember.hashCode();
            result = 31 * result + observablePredicate.hashCode();
            result = 31 * result + destinationCollection.hashCode();
            return result;
        }

        // end auto-generated code
        ///////////////////////////////////////////
    }
}
