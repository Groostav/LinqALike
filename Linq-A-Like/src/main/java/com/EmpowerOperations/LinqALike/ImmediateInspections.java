package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.ComparingLinkedHashSet;
import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.Preconditions;
import com.EmpowerOperations.LinqALike.Common.SetIsEmptyException;
import com.EmpowerOperations.LinqALike.Common.SingletonSetContainsMultipleElementsException;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.Delegate.Func2;
import com.EmpowerOperations.LinqALike.Queries.ReversedQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.EmpowerOperations.LinqALike.CommonDelegates.nullSafeEquals;
import static com.EmpowerOperations.LinqALike.Factories.from;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 13/04/14
 * Time: 00:35
 * To change this template use File | Settings | File Templates.
 */
public class ImmediateInspections {

    public static <TElement> boolean isSingle(Iterable<TElement> source) {
        if(source instanceof Collection){
            return ((Collection)source).size() == 1;
        }

        Iterator<TElement> iterator = source.iterator();
        boolean hasFirst = iterator.hasNext();
        if ( ! hasFirst){
            return false;
        }
        iterator.next();
        boolean hasSecond = iterator.hasNext();
        return ! hasSecond;
    }

    public static <TElement> int size(Iterable<TElement> sourceElements) {
        if(sourceElements instanceof Collection){
            return ((Collection)sourceElements).size();
        }

        int size = 0;
        for(Object ignored : sourceElements){
            size += 1;
        }
        return size;
    }

    public static <TElement> double average(Iterable<? extends TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        double sum = 0.0;
        int size = size(sourceElements);

        for(TElement element : sourceElements){
            sum += valueSelector.getFrom(element);
        }

        return sum / size;
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> sourceElements, Condition<? super TElement> uniqueCondition) {
        Func1<Queryable<TElement>, TElement> resultOnMultipleFound = problems -> {
            throw new SingletonSetContainsMultipleElementsException(sourceElements, problems, uniqueCondition);
        };

        return singleOr(sourceElements, uniqueCondition, () -> null, resultOnMultipleFound);
    }

    public static <TElement> TElement single(Iterable<TElement> sourceElements, Condition<? super TElement> uniqueCondition) {
        Supplier<TElement> resultOnNotFound = () -> {
            throw new SetIsEmptyException(sourceElements, uniqueCondition);
        };
        Func1<Queryable<TElement>, TElement> resultOnMultipleFound = multiples -> {
            throw new SingletonSetContainsMultipleElementsException(sourceElements, multiples, uniqueCondition);
        };

        return singleOr(sourceElements, uniqueCondition, resultOnNotFound, resultOnMultipleFound);
    }

    //Expose this? retrofit something similar into everything? Thanks to lambda's, this would be convenient...
    private static <TElement> TElement singleOr(Iterable<TElement> sourceElements,
                                                Condition<? super TElement> uniqueCondition,
                                                Supplier<TElement> resultOnNotFound,
                                                Func1<Queryable<TElement>, TElement> resultOnMultipleFound){

        boolean found = false;
        TElement result = null;

        for (TElement current : sourceElements) {

            if ( ! uniqueCondition.passesFor(current)) {
                continue;
            }

            if(found){
                return resultOnMultipleFound.getFrom(from(current, result));
            }
            else{
                result = current;
                found = true;
            }
        }

        return found ? result : resultOnNotFound.get();

    }

    public static <TElement> TElement first(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {

        return firstOr(sourceElements, condition, () -> {throw new SetIsEmptyException(sourceElements, condition);});
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> sourceElements,
                                                     Condition<? super TElement> condition){
        return firstOr(sourceElements, condition, () -> null);
    }

    private static <TElement> TElement firstOr(Iterable<TElement> sourceElements,
                                               Condition<? super TElement> condition,
                                               Supplier<TElement> yieldDefault) {

        Iterator<TElement> iterator = sourceElements.iterator();

        if( ! iterator.hasNext()){
            return yieldDefault.get();
        }

        while (iterator.hasNext()) {
            TElement candidate = iterator.next();

            if (condition.passesFor(candidate)) {
                return candidate;
            }
        }

        return yieldDefault.get();
    }

    public static <TElement> TElement last(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {

        if ( ! any(sourceElements, condition)){
            throw new SetIsEmptyException(sourceElements, condition);
        }

        ReversedQuery<TElement> reversed = new ReversedQuery<>(sourceElements);
        return reversed.first(condition);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> sourceElements,
                                                    Condition<? super TElement> condition) {

        ReversedQuery<TElement> reversed = new ReversedQuery<>(sourceElements);
        return reversed.firstOrDefault(condition);
    }

    public static <TElement> boolean any(Iterable<TElement> sourceElements){
        return sourceElements.iterator().hasNext();
    }

    public static <TElement> boolean any(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        for (TElement candidate : sourceElements) {
            if (condition.passesFor(candidate)) {
                return true;
            }
        }
        return false;
    }

    public static <TElement> boolean contains(Iterable<? extends TElement> sourceElements, Object candidate) {
        for(TElement element : sourceElements){
            if(nullSafeEquals(element, candidate)){
                return true;
            }
        }

        return false;
    }

    public static <TElement> boolean isSubsetOf(Iterable<TElement> left,
                                                Iterable<? extends TElement> right,
                                                EqualityComparer<? super TElement> equalityComparer) {

        ComparingLinkedHashSet<TElement> candidateSuperset = new ComparingLinkedHashSet<>(equalityComparer, right);

        for(TElement leftMember : left){
            boolean changed = candidateSuperset.add(leftMember);
            if(changed){
                return false;
            }
        }
        return true;
    }

    public static <TElement> double min(Queryable<TElement> sourceElements, Func1<? super TElement,Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.POSITIVE_INFINITY, Math::min).getKey();
    }

    public static <TElement> double max(Iterable<TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.NEGATIVE_INFINITY, Math::max).getKey();
    }

    public static <TElement> TElement withMin(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.POSITIVE_INFINITY, Math::min).getValue();
    }

    public static <TElement> TElement withMax(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.NEGATIVE_INFINITY, Math::max).getValue();
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> sourceElements){
        if(sourceElements instanceof Set){
            return true;
        }

        HashSet<TElement> set = new HashSet<>();

        for(TElement element : sourceElements){

            boolean modified = set.add(element);
            if ( ! modified){
                return false;
            }
        }
        return true;
    }

    public static <TElement> double sum(Iterable<TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(valueSelector, "valueSelector");

        double sum = 0.0;

        for(TElement element : sourceElements){
            Double elementValue = valueSelector.getFrom(element);
            throwIllegalSelectorIfNull(element, valueSelector, elementValue);
            sum += elementValue;
        }

        return sum;
    }

    private static <TElement> Tuple<Double, TElement> extrema(Iterable<TElement> source,
                                                              Func1<? super TElement, Double> valueElementSelector,
                                                              double startingValue,
                                                              Func2<Double, Double, Double> valueContentionResolver) {
        Preconditions.notNull(source, "source");
        Preconditions.notNull(valueContentionResolver, "valueElementSelector");
        Preconditions.notNull(valueContentionResolver, "valueContentionResolver");
        Preconditions.cannotBeEmpty(source);

        double previousMax = startingValue;
        double currentMax = startingValue;
        TElement chosenElement = null;

        for(TElement element : source){
            Double value = valueElementSelector.getFrom(element);

            throwIllegalSelectorIfNull(element, valueElementSelector, value);

            currentMax = valueContentionResolver.getFrom(currentMax, value);

            if(previousMax != currentMax){
                chosenElement = element;
                previousMax = currentMax;
            }
        }

        return new Tuple<>(currentMax, chosenElement);
    }

    private static <TElement> void throwIllegalSelectorIfNull(TElement element, Func1<? super TElement, Double> valueElementSelector, Double value) {
        if(value == null){
            throw new IllegalArgumentException("" +
                    "The value selector '" + valueElementSelector + "' returned null for the element '" + element + "'.\n" +
                    "Null is not comparable.\n" +
                    "Please either update the list to not contain null (with something like sourceElements.where(com.EmpowerOperations.LinqALike.Common::notNull), \n" +
                    "or update the valueElementSelector to check for null values, and return a non-null Double instance.");
        }
    }

    public static <TElement> boolean setEquals(Iterable<TElement> left, Iterable<? extends TElement> right) {
        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");

        if(size(left) != size(right)){
            return false;
        }

        Set<TElement> leftSet = new HashSet<>();
        for(TElement leftElement : left){
            leftSet.add(leftElement);
        }

        return Factories.from(right).all(leftSet::contains);
    }

    public static <TElement> boolean all(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {

        for(TElement element : sourceElements){
            if( ! condition.passesFor(element)){
                return false;
            }
        }

        return true;
    }

    public static <TElement> int count(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        int count = 0;
        for(TElement element : sourceElements){
            if(condition.passesFor(element)){
                count += 1;
            }
        }
        return count;
    }

    public static <TElement> boolean contains(Iterable<? extends TElement> sourceElements,
                                              TElement candidate,
                                              EqualityComparer<? super TElement> comparer) {
        for(TElement element : sourceElements){
            if(comparer.equals(candidate, element)){
                return true;
            }
        }
        return false;
    }

    public static <TKey, TValue> TValue getFor(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries, TKey key) {
        return ImmediateInspections.firstOrDefault(sourceEntries, kvp -> nullSafeEquals(key, kvp.getKey())).getValue();
    }

    public static <TElement> boolean sequenceEquals(Iterable<TElement> left,
                                                    Iterable<? extends TElement> right,
                                                    EqualityComparer<? super TElement> equalityComparer) {

        if(size(left) != size(right)) { return false; }

        Iterator<? extends TElement> rightMembers = right.iterator();
        for(TElement leftMember : left){
            if( ! equalityComparer.equals(leftMember, rightMembers.next())){
                return false;
            }
        }

        return true;
    }

    public static <TElement> boolean setEquals(Iterable<TElement> left,
                                               Iterable<? extends TElement> right,
                                               EqualityComparer<? super TElement> equalityComparer) {

        if(size(left) != size(right)) { return false; }

        ComparingLinkedHashSet<TElement> set = new ComparingLinkedHashSet<>(equalityComparer, left);

        for(TElement element : right){
            boolean hasChange = set.add(element);
            if(hasChange){
                return false;
            }
        }

        return true;
    }
}
