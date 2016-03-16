package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.LinqingSet;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.beans.InvalidationListener;
import javafx.collections.SetChangeListener;

import java.util.Set;
import java.util.function.Predicate;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2015-02-23.
 */
public class ObservableLinqingSet<TElement> extends    ObservableSetWrapper<TElement>
                                            implements WritableObservableQueryable<TElement>,
                                                       DefaultedQueryable<TElement>{

    private final LinqingList<SetChangeListener<? super TElement>> changeListenerManifest       = new LinqingList<>();
    private final LinqingList<InvalidationListener>                invalidationListenerManifest = new LinqingList<>();

    public ObservableLinqingSet() {
        super(new LinqingSet<>());
    }

    public ObservableLinqingSet(Iterable<TElement> initialElements) {
        super(from(initialElements).toSet());
    }
    public ObservableLinqingSet(Set<TElement> backingSet) {
        super(backingSet);
    }

    public ObservableLinqingSet(TElement... initialElements) {
        super(from(initialElements).toSet());
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        return super.remove(toRemove);
    }

    @Override public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }

    @Override public void bindToContentOf(ObservableQueryable<TElement> source) {
        LinqFX.createContentBinding(this, source);
    }

    @Override public void addListener(FlexibleCollectionChangeListener<TElement> listener) {
        addListener((SetChangeListener<TElement>) listener);
    }

    @Override public void addListener(InvalidationListener listener) {
        invalidationListenerManifest.add(listener);
        super.addListener(listener);
    }
    @Override public void addListener(SetChangeListener<? super TElement> listener) {
        changeListenerManifest.add(listener);
        super.addListener(listener);
    }
    @Override public void removeListener(InvalidationListener listener) {
        super.removeListener(listener);
        invalidationListenerManifest.remove(listener);
    }
    @Override public void removeListener(SetChangeListener<? super TElement> listener) {
        super.removeListener(listener);
        invalidationListenerManifest.remove(listener);
    }

    public void clearListeners(){
        invalidationListenerManifest.immediately().forEach(this::removeListener);
        changeListenerManifest.immediately().forEach(this::removeListener);
    }
}

