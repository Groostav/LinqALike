package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.ForwardingLinqingList;
import com.empowerops.linqalike.QueryableList;
import com.empowerops.linqalike.common.FlexibleCollectionChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ObservableLinqingList<TElement>  extends    ModifiableObservableListBase<TElement>
                                              implements WritableObservableQueryable<TElement>,
                                                         QueryableList<TElement>,
                                                         DefaultedQueryable<TElement> {

    private static final Logger Log = Logger.getLogger(ObservableLinqingList.class.getCanonicalName());
    public static final boolean FixJavaFXsMess;

    static {
        String fromEnv = System.getProperty("com.empowerops.linqalike.experimental.ObservableLinqingList.FixJavaFXsMess");
        FixJavaFXsMess = fromEnv == null || fromEnv.isEmpty() || ! fromEnv.equalsIgnoreCase("false");
    }

    private final List<TElement> backingList;
    private boolean isImmutableDerrivative;

    public ObservableLinqingList(List<TElement> backingList){
        this.backingList = backingList;
        isImmutableDerrivative = false;
    }

    public ObservableLinqingList(){
        this.backingList = new ArrayList<>();
        this.isImmutableDerrivative = false;
    }

    @SafeVarargs
    public ObservableLinqingList(TElement... initialElements){
        this();
        addAll(Factories.asList(initialElements));
    }

    public ObservableLinqingList(Iterable<TElement> initialElements){
        this();
        addAll(Factories.from(initialElements).toReadOnly());
    }

    private ObservableLinqingList(boolean isImmutableDerrivative, Iterable<TElement> initialElements){
        //no constructor chaining
        this.backingList = new ArrayList<>();
        this.isImmutableDerrivative = isImmutableDerrivative;
        this.addAll(Factories.from(initialElements).toReadOnly());
    }

    public void add(TElement elementToAdd, TElement leftNeighbour, TElement rightNeighbour){

        int index = indexOf(leftNeighbour);
        //Very occasionally hits this, when you move elements really fast.
        assert indexOf(rightNeighbour) == index + 1;

        add(index + 1, elementToAdd);
    }

    @Override
    public TElement get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    protected void doAdd(int index, TElement element) {
        backingList.add(index, element);
    }

    @Override
    protected TElement doSet(int index, TElement element) {
        return backingList.set(index, element);
    }

    @Override
    protected TElement doRemove(int index) {
        return backingList.remove(index);
    }

    @Override
    public Iterator<TElement> iterator() {
        return listIterator();
    }

    @Override
    public boolean removeElement(TElement toRemove) {
        return remove(toRemove);
    }

    @Override public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }

    @Override public QueryableList<TElement> subList(int fromIndex, int toIndex) {
        if(FixJavaFXsMess) {
            String errorHeader = "Asked to sublist with fromIndex=" + fromIndex + " and toIndex=" + toIndex + ", ";
            if (fromIndex > size()){
                int newFromIndex = size() - 1;
                Log.warning(errorHeader + "but this list only has " + size() + " elements. Using " + newFromIndex + " for 'fromIndex' instead.");
                fromIndex = newFromIndex;
            }
            if (toIndex > size()) {
                Log.warning(errorHeader + "but this list only has " + size() + " elements. Using " + size() + " for 'toIndex' instead.");
                toIndex = size();
            }
            if (fromIndex < 0) {
                Log.warning(errorHeader + "Using 0 for 'fromIndex' instead");
                fromIndex = 0;
            }
            if (toIndex < 0){
                Log.warning(errorHeader + "Using 0 for 'toIndex' instead");
                toIndex = 0;
            }
            if(toIndex < fromIndex){
                Log.warning(errorHeader + "which is a negative range, so I'm swapping the indexes.");
                int temp = fromIndex;
                fromIndex = toIndex;
                toIndex = temp;
            }
        }

        return new ForwardingLinqingList<>(super.subList(fromIndex, toIndex));
    }
    @Override
    public void bindToContentOf(ObservableQueryable<TElement> source) {
        LinqFX.createContentBinding(this, source);
    }

    @Override public void addListener(FlexibleCollectionChangeListener<TElement> listener) {
        addListener((ListChangeListener<TElement>) listener);
    }
}

