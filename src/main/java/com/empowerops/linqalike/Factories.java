package com.empowerops.linqalike;

import com.empowerops.linqalike.delegate.Yield;
import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func;
import com.empowerops.linqalike.delegate.Func1;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.empowerops.linqalike.CommonDelegates.memoized;

public class Factories {

    public static <TElement> Queryable<TElement> through(TElement seed,
                                                         Func1<? super TElement, TElement> nextGetter,
                                                         Condition<? super TElement> hasNextGetter){
        return new QueryAdapter.FromImplicitIterator<>(seed, nextGetter, memoized(hasNextGetter));
    }
    public static <TElement> Queryable<TElement> through(TElement seed, Func1<? super TElement, TElement> nextGetter){
        return through(seed, nextGetter, value -> value != null);
    }


    public static Queryable<Double> fromDoubles(double[] doubles){
        return new QueryAdapter.FromDoubleArray(doubles);
    }
    public static Queryable<Integer> fromIntegers(int[] integers){
        return new QueryAdapter.FromIntegerArray(integers);
    }
    public static Queryable<Long> fromLongIntegers(long[] longIntegers) {
        return new QueryAdapter.FromLongIntegers(longIntegers);
    }
    public static Queryable<Byte> fromBytes(byte[] bytes) {
        return new QueryAdapter.FromByteArray(bytes);
    }

    @SuppressWarnings("unchecked") //Safe because of Iterable -> Collection iterator() signature collision
    public static <TElement> Queryable<TElement> from(Iterable<TElement> sourceElements){
        return sourceElements instanceof Collection
                ? new QueryAdapter.FromCollection<>(((Collection) sourceElements))
                : new QueryAdapter.FromIterable<>(sourceElements);
    }
    @SafeVarargs
    public static <TElement extends Iterable> Queryable<TElement> fromOuter(TElement... sourceElementCollections){
        return from(sourceElementCollections);
    }
    @SafeVarargs
    public static <TElement> Queryable<TElement> from(TElement... sourceElements){
        return new QueryAdapter.FromArray<>(sourceElements);
    }
    public static <TKey, TValue> QueryableMap<TKey, TValue> fromMap(Map<TKey, TValue> source){
        return new QueryAdapter.FromMap<>(source);
    }
    public static Queryable<Character> fromString(CharSequence sourceCharacter){
        return new QueryAdapter.FromCharacterSequence(sourceCharacter);
    }

    @SafeVarargs
    public static <TLeft, TRight>
    BiQueryable<TLeft, TRight> pairs(Tuple<? extends TLeft, ? extends TRight>... pairs){
        return new BiQueryAdapter.FromArray<>(pairs);
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
    public static <TElement> TElement firstPresentOrDefault(Yield<? extends TElement>... candidates){
        return from(candidates).select(x -> (TElement) x.get()).firstOrDefault(candidate -> candidate != null);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNull(TElement... sourceElements){
        return from(sourceElements).first(CommonDelegates.NotNull);
    }

    public static <TNeeded> TNeeded firstNotNull(TNeeded usedIfNotNull, Func<TNeeded> alternativeIfNull) {
        return usedIfNotNull == null ? alternativeIfNull.getValue() : usedIfNotNull;
    }

    @SafeVarargs
    public static <TSet extends Iterable<?>> TSet firstNotEmpty(TSet ... sets){
        return from(sets).first(Linq::any);
    }

    public static Queryable<Integer> range(int lowerInclusive, int upperExclusive) {
        return (DefaultedQueryable<Integer>) () -> new RangeIterator(lowerInclusive, upperExclusive);
    }

    public static <TElement> Queryable<TElement> repeat(final TElement valueToRepeat) {
        return new RepeatQuery<>(valueToRepeat);
    }

    public static <TElement> Queryable<TElement> repeat(final TElement valueToRepeat, int repititionCount) {
        return new RepeatQuery<TElement>(valueToRepeat, repititionCount);
    }

    public static <TElement> Queryable<TElement> cache(Iterable<TElement> origin){
        return new IterableCache<>(origin);
    }

    public static <TElement> Queryable<TElement> empty() {
        return new ReadonlyLinqingList<>();
    }

    public static <TElement> ReadonlyLinqingList<TElement> asReadonlyList(TElement... initialElements) {
        return new ReadonlyLinqingList<>(initialElements);
    }

    public static <TElement> ReadonlyLinqingList<TElement> asReadonlyList(Iterable<TElement> initialElements) {
        return new ReadonlyLinqingList<>(initialElements);
    }

    public static <TElement> ReadonlyLinqingSet<TElement> asReadonlySet(TElement... sourceElements){
        return new ReadonlyLinqingSet<>(sourceElements);
    }

    public static <TElement> ReadonlyLinqingSet<TElement> asReadonlySet(Iterable<TElement> sourceElements) {
        return new ReadonlyLinqingSet<>(sourceElements);
    }

    public static <TElement> LinqingSet<TElement> asSet(Iterable<TElement> initialElements) {
        return new LinqingSet<>(initialElements);
    }

    public static <TElement> LinqingSet<TElement> asSet(TElement... initialElements) {
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

        int index = 0;
        Object currentElement = null;

        try {
            for(Object element : initialElements){
                currentElement = element;

                array[index] = currentElement;
                index += 1;
            }
        }
        catch(ArrayStoreException e){
            throw new ArrayStoreException(
                    "The array factory provided an instance of '" + array.getClass().getSimpleName() + "' " +
                    "but the element '" + currentElement + "' (at index " + index + ") " +
                    "cannot be stored in that type of array."
            );
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

    public static <TElement> InputStream asInputStream(Iterable<TElement> sourceElements, Func1<? super TElement, Integer> converter) {
        return new InputStream() {
            private final Iterator<TElement> elements = sourceElements.iterator();

            @Override
            public int read() throws IOException {
                if( ! elements.hasNext()){
                    return -1;
                }
                return converter.getFrom(elements.next());
            }
        };
    }

    public static <TKey, TValue> QueryableMap<TKey, TValue> emptyReadableMap() {
        return new ReadonlyLinqingMap<>();
    }
}


