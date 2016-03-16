package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import com.empowerops.linqalike.delegate.Func1;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.empowerops.linqalike.Factories.from;


public class ObservableSelectSourceListener<TSource, TResult> implements FlexibleCollectionChangeListener<TSource> {

    public static final Logger log = Logger.getLogger(ObservableSelectSourceListener.class.getCanonicalName());

    private final ObservableQueryable<TSource>         sourceElements;
    private final WritableObservableQueryable<TResult> projectedElements;

    private final Func1<? super TSource, ObservableValue<TResult>> propertySelector;
    private final Func1<? super TSource, TResult>                  selector;

    public static <TSource, TResult> void addBehaviourFor(ObservableQueryable<TSource> sourceElements,
                                                          WritableObservableQueryable<TResult> destinationCollection,
                                                          Func1<? super TSource, ObservableValue<TResult>> selector) {

        for (TSource existingElement : sourceElements) {
            ObservableValue<TResult> value = selector.getFrom(existingElement);
            value.addListener(new ObservableSelectSelectorEndpointListener<>(sourceElements, destinationCollection, existingElement, selector));
        }

        LinqFX.addChangeListener(sourceElements, new ObservableSelectSourceListener<>(sourceElements, destinationCollection, selector));
    }

    public ObservableSelectSourceListener(ObservableQueryable<TSource> sourceElements,
                                          WritableObservableQueryable<TResult> projectedElements,
                                          Func1<? super TSource, ObservableValue<TResult>> selector) {

        this.sourceElements = sourceElements;
        this.projectedElements = projectedElements;
        this.propertySelector = selector;

        this.selector = source -> propertySelector.getFrom(source).getValue();
    }

    private boolean isOrderedProperty() {
        return from(sourceElements).select(selector).sequenceEquals(projectedElements);
    }

    private void sort() {
        projectedElements.clearAndAddAll(sourceElements.select(selector));
    }

    private void unbindListFromRemovedElements(Queryable<TSource> actuallyRemovedElements) {
        for(TSource removedElement : actuallyRemovedElements){
            ObservableValue<TResult> elementProjection = propertySelector.getFrom(removedElement);
            ObservableSelectSelectorEndpointListener<TSource, TResult> listener = ObservableSelectSelectorEndpointListener.forPurposeOfEquality(
                    sourceElements,
                    projectedElements,
                    removedElement,
                    propertySelector
            );

            elementProjection.removeListener(listener);
            listener.onElementRemoved();
        }
    }

    private void bindListToNewElements(Queryable<TSource> actuallyAddedElements) {
        for(TSource addedElement : actuallyAddedElements){
            ObservableValue<TResult> elementProjection = propertySelector.getFrom(addedElement);
            ObservableSelectSelectorEndpointListener<TSource, TResult> listener = new ObservableSelectSelectorEndpointListener<>(
                    sourceElements,
                    projectedElements,
                    addedElement,
                    propertySelector
            );
            elementProjection.addListener(listener);
            listener.onElementAdded();
        }
    }

    @Override
    public void onChanged(Collection<? extends TSource> source, Queryable<TSource> added, Queryable<TSource> removed, int from, int to, boolean isPermutation) {
        bindListToNewElements(added);
        unbindListFromRemovedElements(removed);
    }


    @Override
    public void onChangeApplied() {
        if( ! isOrderedProperty()){
            sort();
        }
    }


    private static class ObservableSelectSelectorEndpointListener<TSource, TResult> implements ChangeListener<TResult> {

        private final ObservableQueryable<TSource>                     sourceElements;
        private final WritableObservableQueryable<TResult>             projectedElements;
        private final TSource                                          sourceElement;
        private final Func1<? super TSource, ObservableValue<TResult>> selector;

        public static <TSource, TResult>
        ObservableSelectSelectorEndpointListener<TSource, TResult> forPurposeOfEquality(ObservableQueryable<TSource> sourceElements,
                                                                                        WritableObservableQueryable<TResult> projectedElements,
                                                                                        TSource removedElement,
                                                                                        Func1<? super TSource, ObservableValue<TResult>> selector) {
            return new ObservableSelectSelectorEndpointListener<>(sourceElements, projectedElements, removedElement, selector);
        }

        public ObservableSelectSelectorEndpointListener(ObservableQueryable<TSource> sourceElements,
                                                        WritableObservableQueryable<TResult> projectedElements,
                                                        TSource sourceElement,
                                                        Func1<? super TSource, ObservableValue<TResult>> selector) {
            this.sourceElements = sourceElements;
            this.projectedElements = projectedElements;
            this.sourceElement = sourceElement;
            this.selector = selector;
        }

        private void onElementAdded() {

            TResult projectionOfAddedElement = selector.getFrom(sourceElement).getValue();

            if (projectedElements instanceof Set) {
                projectedElements.add(projectionOfAddedElement);
            }
            else if (projectedElements instanceof List) {
                assert sourceElements instanceof List : "projection from bag onto a set is not supported!";
                int index = ((List) sourceElements).indexOf(sourceElement);
                ((List) projectedElements).add(index, projectionOfAddedElement);
            }
            else {
                log.info("target list is not a Set or List, but it is a writable collection? Please update this code!");
                projectedElements.add(projectionOfAddedElement);
            }
        }

        private void onElementRemoved() {
            ObservableValue<TResult> sourceProperty = selector.getFrom(sourceElement);
            TResult removedProjection = sourceProperty.getValue();

            projectedElements.removeElement(removedProjection);
            sourceProperty.removeListener(this);
        }

        @Override
        @SuppressWarnings("unchecked") //by signature collisison this must be safe
        public void changed(ObservableValue<? extends TResult> observable, TResult oldValue, TResult newValue) {
            assert projectedElements.containsElement(oldValue);

            if(projectedElements instanceof Set){
                projectedElements.removeElement(oldValue);
                projectedElements.add(newValue);
            }
            else if(projectedElements instanceof List){
                List<TResult> actualProjectedElements = (List) projectedElements;

                int index = actualProjectedElements.indexOf(oldValue);
                actualProjectedElements.remove(index);
                actualProjectedElements.add(index, newValue);
            }
            else{
                log.info("target list is not a Set or List, but it is a writable collection? Please update this code!");
                projectedElements.removeElement(newValue);
            }

        }
    }
}

