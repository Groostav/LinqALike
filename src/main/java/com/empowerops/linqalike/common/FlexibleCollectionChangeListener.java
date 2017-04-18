package com.empowerops.linqalike.common;

import com.empowerops.linqalike.Queryable;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;

import java.util.Collection;

import static com.empowerops.linqalike.Factories.empty;
import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2014-08-11.
 */
@FunctionalInterface
public interface FlexibleCollectionChangeListener<TElement> extends ListChangeListener<TElement>, SetChangeListener<TElement> {

    @Override
    public default void onChanged(SetChangeListener.Change<? extends TElement> change) {
        onChanged(
                change.getSet(),
                change.wasAdded() ? from(change.getElementAdded()) : empty(),
                change.wasRemoved() ? from(change.getElementRemoved()) : empty(),
                0,
                change.getSet().size(),
                false
        );

        onChangeApplied();
    }

    @Override
    default void onChanged(ListChangeListener.Change<? extends TElement> change) {
        while(change.next()) {
            onChanged(
                    change.getList(),
                    from(change.getAddedSubList()).unsafeCast(),
                    from(change.getRemoved()).unsafeCast(),
                    change.getFrom(),
                    change.getTo(),
                    change.wasPermutated()
            );
        }

        onChangeApplied();
    }

    void onChanged(Collection<? extends TElement> source, Queryable<TElement> added, Queryable<TElement> removed, int from, int to, boolean isPermutation);
    default void onChangeApplied(){}
}
