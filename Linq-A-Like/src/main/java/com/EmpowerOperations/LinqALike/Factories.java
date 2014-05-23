package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.Formatting;
import com.EmpowerOperations.LinqALike.Common.IterableCache;
import com.EmpowerOperations.LinqALike.Common.QueryAdapter;
import com.EmpowerOperations.LinqALike.Common.RangeIterator;
import com.EmpowerOperations.LinqALike.Common.RepeatingIterator;
import com.EmpowerOperations.LinqALike.Delegate.Func1;

import java.lang.reflect.Array;
import java.util.Map;

public class Factories {

    @SafeVarargs
    public static <TElement> Queryable<TElement> from(TElement... sourceElements){
        return new QueryAdapter.FromArray<>(sourceElements);
    }
    public static <TElement> Queryable<TElement> from(Iterable<TElement> sourceElements){
        return new QueryAdapter.FromIterable<>(sourceElements);
    }

    @SafeVarargs
    public static <TElement> LinqingList<TElement> asList(TElement... initialElements) {
        return new LinqingList<>(initialElements);
    }

    public static <TElement> LinqingList<TElement> asList(Iterable<TElement> initialElements){
        return new LinqingList<>(initialElements);
    }

    @SafeVarargs
    public static <TKey, TValue>
    LinqingMap<TKey, TValue> asMap(Map.Entry<TKey, TValue>... initialEntries){
        return new LinqingMap<>(initialEntries);
    }
    public static <TKey, TValue>
    LinqingMap<TKey, TValue> asMap(Iterable<? extends Map.Entry<TKey, TValue>> initialElements){
        return new LinqingMap<>(initialElements);
    }
    public static <TKey, TValue>
    LinqingMap<TKey, TValue> asMap(Iterable<TKey> keys,
                                   Iterable<TValue> values) {
        return new LinqingMap<>(keys, values);
    }
    public static <TKey, TValue, TElement>
    LinqingMap<TKey, TValue> asMap(Iterable<TElement> initialElements,
                                   Func1<? super TElement, TKey> keySelector,
                                   Func1<? super TElement, TValue> valueSelector) {

        Iterable<TKey> keys = Linq.select(initialElements, keySelector);
        Iterable<TValue> values = Linq.select(initialElements, valueSelector);
        return new LinqingMap<>(keys, values);
    }
    public static <TKey, TValue>
    LinqingMap<TKey, TValue> asMap(Iterable<TValue> initialValues,
                                   Func1<? super TValue, TKey> keySelector) {

        Iterable<TKey> keys = Linq.select(initialValues, keySelector);
        return new LinqingMap<>(keys, initialValues);
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
        return from(sets).first(Linq::any);
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

    public static <TElement> ReadonlyLinqingList<TElement> asReadonlyList(Iterable<TElement> initialElements) {
        return new ReadonlyLinqingList<>(initialElements);
    }

    public static <TElement> LinqingSet<TElement> asSet(Iterable<TElement> initialElements) {
        return new LinqingSet<>(initialElements);
    }

    public static Object[] asArray(Iterable<?> initialElements){
        Object[] array = new Object[ImmediateInspections.size(initialElements)];
        copyIntoArray(initialElements, array, Object.class);
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <TSourceElement, TArrayElement>
    TArrayElement[] asArray(Iterable<? extends TSourceElement> initialElements, TArrayElement[] targetArray) {

        Class<?> arrayElementType = targetArray.getClass().getComponentType();
        int neededSize = Linq.size(initialElements);

        if(neededSize < targetArray.length){
            copyIntoArray(initialElements, targetArray, arrayElementType);
        }
        else{
            targetArray = (TArrayElement[]) Array.newInstance(arrayElementType, neededSize);
            copyIntoArray(initialElements, targetArray, arrayElementType);
        }

        return targetArray;
    }

    public static <TElement> TElement[] asArray(Iterable<? extends TElement> initialElements, Class<TElement> arrayElementType){
        int size = ImmediateInspections.size(initialElements);

        Object newRawArray = Array.newInstance(arrayElementType, size);

        @SuppressWarnings("unchecked")
        TElement[] newArray = (TElement[]) newRawArray;

        copyIntoArray(initialElements, newArray, arrayElementType);

        return newArray;
    }

    public static <TElement, TDesired> TDesired[] asArray(Iterable<TElement> initialElements,
                                                          Func1<Integer, TDesired[]> arrayFactory) {
        int size = ImmediateInspections.size(initialElements);
        Object[] array = arrayFactory.getFrom(size);

        if(array.length < size){
            throw new IllegalArgumentException(
                    "the array factory " + Formatting.getDebugString(arrayFactory) + " " +
                    "returned an array of size " + array.length + " " +
                    "when it was asked for an array of size " + size);
        }

        //TODO run-time assertion on the element type provided by ArrayFactory,
        int index = 0;
        for(Object element : initialElements){
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
