package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.*;
import com.EmpowerOperations.LinqALike.Delegate.Func1;

import java.lang.reflect.Array;
import java.util.Iterator;

public class Factories {

    @SafeVarargs
    public static <TElement> Queryable<TElement> from(TElement... sourceElements){
        return new QueryAdapter.FromArray<>(sourceElements);
    }

    public static <TElement> Queryable<TElement> from(Iterable<TElement> sourceElements){
        return new QueryAdapter.FromIterable<>(sourceElements);
    }

    @SafeVarargs
    public static <TElement> LinqingList<TElement> asList(TElement... sourceElements){
        return new LinqingList<>(sourceElements);
    }

    public static <TElement> LinqingList<TElement> asList(Iterable<TElement> sourceElements){
        return new LinqingList<>(sourceElements);
    }
    /**
     * this code is identical to the {@link #asList(Iterable)} method above, except it makes explciit calls to
     * {@link java.util.Iterator#next()} and {@link java.util.Iterator#hasNext()} for easier debugging.
     */
    @SuppressWarnings("WhileLoopReplaceableByForEach")
    static <TElement> LinqingList<TElement> asListWithExplicitIterationForDebugging(Iterable<TElement> sourceElements){
        LinqingList<TElement> copy = new LinqingList<>();
        Iterator<TElement> iterator = sourceElements.iterator();
        while(iterator.hasNext()){
            TElement next = iterator.next();
            copy.add(next);
        }
        return copy;
    }

    public static <TKey, TValue>
    LinqingMap<TKey, TValue> asMap(Iterable<TKey> keys,
                                   Iterable<TValue> values) {
        return new LinqingMap<>(keys, values);
    }

    public static <TKey, TValue, TElement>
    LinqingMap<TKey, TValue> asMap(Iterable<TElement> sourceElements,
                                   Func1<? super TElement, TKey> keySelector,
                                   Func1<? super TElement, TValue> valueSelector) {

        Iterable<TKey> keys = Linq.select(sourceElements, keySelector);
        Iterable<TValue> values = Linq.select(sourceElements, valueSelector);
        return new LinqingMap<>(keys, values);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNullOrDefault(TElement ... set){
        return from(set).firstOrDefault(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNull(TElement ... set){
        return from(set).first(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TSet extends Iterable<?>> TSet firstNotEmpty(TSet ... sets){
        return from(sets).first(x -> x.iterator().hasNext());
    }

    public static Iterable<Integer> range(int lowerInclusive, int upperExclusive) {
        return () -> new RangeIterator(lowerInclusive, upperExclusive);
    }

    public static <TElement> Iterable<TElement> repeat(final TElement valueToRepeat) {
        return () -> new RepeatingIterator<>(valueToRepeat);
    }

    public static <TElement> Queryable<TElement> cache(Iterable<TElement> origin){
        return new IterableCache<>(origin);
    }

    public static <TElement> Queryable<TElement> empty() {
        return new LinqingList<>();
    }

    public static <TElement> ReadonlyLinqingList<TElement> asReadonlyList(Iterable<TElement> sourceElements) {
        return new ReadonlyLinqingList<>(sourceElements);
    }

    public static <TElement> LinqingSet<TElement> asSet(Iterable<TElement> sourceElements) {
        return new LinqingSet<>(sourceElements);
    }

    public static Object[] asArray(Iterable<?> sourceElements){
        Object[] array = new Object[ImmediateInspections.size(sourceElements)];
        copyIntoArray(sourceElements, array, Object.class);
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <TSourceElement, TArrayElement>
    TArrayElement[] asArray(Iterable<? extends TSourceElement> sourceElements, TArrayElement[] targetArray) {

        Class<?> arrayElementType = targetArray.getClass().getComponentType();
        int neededSize = Linq.size(sourceElements);

        if(neededSize < targetArray.length){
            copyIntoArray(sourceElements, targetArray, arrayElementType);
        }
        else{
            targetArray = (TArrayElement[]) Array.newInstance(arrayElementType, neededSize);
            copyIntoArray(sourceElements, targetArray, arrayElementType);
        }

        return targetArray;
    }

    public static <TElement> TElement[] asArray(Iterable<? extends TElement> sourceElements, Class<TElement> arrayElementType){
        int size = ImmediateInspections.size(sourceElements);

        Object newRawArray = Array.newInstance(arrayElementType, size);

        @SuppressWarnings("unchecked")
        TElement[] newArray = (TElement[]) newRawArray;

        copyIntoArray(sourceElements, newArray, arrayElementType);

        return newArray;
    }

    public static <TElement, TDesired> TDesired[] asArray(Iterable<TElement> sourceElements,
                                                          Func1<Integer, TDesired[]> arrayFactory) {
        int size = ImmediateInspections.size(sourceElements);
        Object[] array = arrayFactory.getFrom(size);

        if(array.length < size){
            throw new IllegalArgumentException(
                    "the array factory " + Formatting.getDebugString(arrayFactory) + " " +
                    "returned an array of size " + array.length + " " +
                    "when it was asked for an array of size " + size);
        }

        //TODO run-time assertion on the element type provided by ArrayFactory,
        int index = 0;
        for(Object element : sourceElements){
            array[index] = element;
        }

        @SuppressWarnings("unchecked")
        TDesired[] returnable = (TDesired[]) array;

        return returnable;
    }

    private static <TSourceElement, TArrayElement> void copyIntoArray(Iterable<TSourceElement> sourceElements,
                                                                      TArrayElement[] targetArray,
                                                                      Class<?> arrayElementType) {
        int index = 0;
        for(TSourceElement element : sourceElements){

            if( ! arrayElementType.isInstance(element)){
                throw new ClassCastException(
                        "cannot cast " + Formatting.getDebugString(element) + " " +
                                "as " + arrayElementType.getCanonicalName());
            }

            @SuppressWarnings("unchecked")
            TArrayElement newElement = (TArrayElement) element;

            targetArray[index] = newElement;
            index += 1;
        }

        if(targetArray.length > index){
            targetArray[index] = null;
        }
    }
}
