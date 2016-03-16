package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class LinqFX{

    public static <TElement> void createContentBinding(List<TElement> destination,
                                                       ObservableQueryable<TElement> sourceElements){

        FlexibleCollectionChangeListener<TElement> listener = new LinqContentListener.ForwardingToListContentListener<>(destination);

        fireChangeImmediatelyAndTBind(destination, sourceElements, listener);
    }

    public static<TElement> void createContentBinding(Set<TElement> destination,
                                                      ObservableQueryable<TElement> sourceElements){
        FlexibleCollectionChangeListener<TElement> listener = new LinqContentListener.ForwardingToSetListener<>(destination);

        fireChangeImmediatelyAndTBind(destination, sourceElements, listener);
    }

    private static <TElement> void fireChangeImmediatelyAndTBind(Collection<TElement> destination,
                                                                 ObservableQueryable<TElement> sourceElements,
                                                                 FlexibleCollectionChangeListener<TElement> listener) {
        destination.clear();
        destination.addAll(sourceElements.toList());
        addChangeListener(sourceElements, listener);
    }

    @SuppressWarnings("unchecked") //Safe because of ObservableQueryable-ObservableSet/List iterator() collision
    public static <TElement> void addChangeListener(ObservableQueryable<TElement> sourceElements,
                                                    FlexibleCollectionChangeListener<TElement> listener) {
        if(sourceElements instanceof ObservableList){
            ((ObservableList) sourceElements).addListener(listener);
        }
        else if (sourceElements instanceof ObservableSet){
            ((ObservableSet) sourceElements).addListener(listener);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    private static abstract class LinqContentListener<TElement> implements FlexibleCollectionChangeListener<TElement> {

        private final Iterable<TElement> targetCollection;

        protected LinqContentListener(Iterable<TElement> targetCollection) {
            this.targetCollection = targetCollection;
        }

        public static class ForwardingToListContentListener<TElement> extends LinqContentListener<TElement> {

            private final List<TElement> targetList;
            public static final Logger log = Logger.getLogger(LinqFX.class.getCanonicalName());

            protected ForwardingToListContentListener(List<TElement> targetList) {
                super(targetList);
                this.targetList = targetList;
            }

            @Override
            public void onChanged(Collection<? extends TElement> source, Queryable<TElement> added,
                                  Queryable<TElement> removed, int from, int to, boolean isPermutation) {

                if(Platform.isFxApplicationThread()) {
                    handleChange(source, added, removed, from, to, isPermutation);
                }
                else {
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        handleChange(source, added, removed, from, to, isPermutation);
                        latch.countDown();
                    });
                    try{
                        latch.await();
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            private void handleChange(Collection<? extends TElement> source,
                                      Queryable<TElement> added,
                                      Queryable<TElement> removed,
                                      int from,
                                      int to,
                                      boolean isPermutation) {

                //copy-pasted from com.sun.javafx.binding.ContentBinding
                if (isPermutation) {
                    if (source instanceof List) {
                        targetList.subList(from, to).clear();
                        //noinspection unchecked
                        targetList.addAll(from, ((List<TElement>) source).subList(from, to));
                    }
                    else {
                        log.info("got a permutation change on something that wasnt a list. Dont know what to do so doing nothing");
                    }
                }
                else {
                    if (removed.any()) {
                        if (source instanceof List) {
                            targetList.subList(from, from + removed.count()).clear();
                        }
                        else if (source instanceof Set) {
                            removed.forEach(targetList::remove);
                        }
                        else {
                            throw new UnsupportedOperationException();
                        }
                    }
                    if (added.any()) {
                        if (source instanceof List) {
                            // hmm, so this was causing duplicates to get added...
                            // any chance we're seeing set-like behaviour on events with source & target being lists?
                            // fix was to have the ConvergenceLineChart use a Set rather than a List,
                            // that got us into set flow which solved the problem.
                            targetList.addAll(from, added.toList());
                        }
                        else if (source instanceof Set) {
                            added.forEach(targetList::add);
                        }
                        else {
                            throw new UnsupportedOperationException();
                        }
                    }
                }
            }
        }

        public static class ForwardingToSetListener<TElement> extends LinqContentListener<TElement> {

            private final Set<TElement> targetCollection;

            protected ForwardingToSetListener(Set<TElement> targetCollection) {
                super(targetCollection);
                this.targetCollection = targetCollection;
            }

            @Override
            public void onChanged(Collection<? extends TElement> source, Queryable<TElement> added, Queryable<TElement> removed, int from, int to, boolean isPermutation) {
                targetCollection.addAll(added.toSet());
                targetCollection.removeAll(removed.toSet());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (! (o instanceof LinqContentListener)) { return false; }

            LinqContentListener that = (LinqContentListener) o;

            if (! this.getClass().equals(that.getClass())) { return false; }
            if (! targetCollection.equals(that.targetCollection)) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            return targetCollection.hashCode();
        }
    }

}
