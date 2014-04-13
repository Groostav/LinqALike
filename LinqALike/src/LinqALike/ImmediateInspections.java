package LinqALike;

import LinqALike.Common.*;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

import static LinqALike.CommonDelegates.nullSafeEquals;
import static LinqALike.Factories.from;

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

        if ( ! sourceElements.iterator().hasNext()) {
            return null;
        }

        return single(sourceElements, uniqueCondition);
    }

    public static <TElement> TElement single(Iterable<TElement> sourceElements, Condition<? super TElement> uniqueCondition) {

        boolean found = false;
        TElement result = null;

        for (TElement current : sourceElements) {

            if ( ! uniqueCondition.passesFor(current)) {
                continue;
            }

            if(found){
                throw new SingletonSetContainsMultipleElementsException(sourceElements, from(current, result), uniqueCondition);
            }
            else{
                result = current;
                found = true;
            }
        }
        return result;
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
        return reversed(sourceElements).first(condition);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> sourceElements,
                                                    Condition<? super TElement> condition) {

        return reversed(sourceElements).firstOrDefault(condition);
    }

    public static <TElement> Queryable<TElement> reversed(Iterable<TElement> sourceElements) {
        if(sourceElements instanceof List){
            List<TElement> list = (List<TElement>) sourceElements;
            return new QueryAdapter.Iterable<>(new ListReverser<>(list));
        }
        else{
            return reverseViaStack(sourceElements);
        }
    }

    private static <TElement> Queryable<TElement> reverseViaStack(Iterable<TElement> set) {
        Stack<TElement> stack = new Stack<>();

        for(TElement element : set){
            stack.push(element);
        }
        LinqingList<TElement> returnable = new LinqingList<>();
        while( ! stack.isEmpty()){
            returnable.add(stack.pop());
        }
        return returnable;
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

    public static <TElement> boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        Queryable<TElement> leftFetched = from(left).fetch();
        for(TElement rightElement : right){
            if ( ! leftFetched.contains(rightElement)) {
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

    public static <TElement> TElement withMax(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.NEGATIVE_INFINITY, Math::max).getValue();
    }

    public static <TElement> TElement withMin(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return extrema(sourceElements, valueSelector, Double.POSITIVE_INFINITY, Math::min).getValue();
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
                    "Please either update the list to not contain null (with something like sourceElements.where(Common::notNull), \n" +
                    "or update the valueElementSelector to check for null values, and return a non-null Double instance.");
        }
    }
}
