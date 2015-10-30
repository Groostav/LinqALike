package com.empowerops.linqalike;

import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.Action2;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;
import com.empowerops.linqalike.queries.ReversedQuery;

import java.util.*;
import java.util.function.Supplier;

import static com.empowerops.linqalike.CommonDelegates.nullSafeEquals;
import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.ImmediateInspections.ComparingChooser.Directionality.HigherIsBetter;
import static com.empowerops.linqalike.ImmediateInspections.ComparingChooser.Directionality.LowerIsBetter;

/**
 * implementations of the immediately-computable methods on the Linq interface.
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


    public static int cappedCount(Iterable<?> sourceElements, int maxToReturn) {
        Preconditions.notNull(sourceElements, "sourceElements");

        if(hasFastSize(sourceElements)){
            return Math.min(maxToReturn, fastSizeIfAvailable(sourceElements));
        }

        int size = 0;
        for(Object ignored : sourceElements){
            size += 1;

            if(size == maxToReturn) {
                break;
            }
        }
        return size;
    }

    public static int size(Iterable<?> sourceElements) {
        Preconditions.notNull(sourceElements, "sourceElements");

        if(hasFastSize(sourceElements)){
            return fastSizeIfAvailable(sourceElements);
        }

        int size = 0;
        for(Object ignored : sourceElements){
            size += 1;
        }
        return size;
    }

    public static <TElement> double average(Iterable<? extends TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        return sum(sourceElements, valueSelector) / size(sourceElements);
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

    public static boolean isEmpty(Iterable<?> sourceElements){
         return ! sourceElements.iterator().hasNext();
    }

    public static boolean any(Iterable<?> sourceElements){
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

    public static <TElement> boolean isSubsetOf(Iterable<? extends TElement> left,
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

    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Iterable<TElement> sourceElements, Func1<? super TElement, TCompared> valueSelector) {

        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(valueSelector, "valueSelector");
        if(Linq.isEmpty(sourceElements)) { return Optional.empty(); }

        TElement result = aggregate(sourceElements, new ComparingChooser<>(valueSelector, LowerIsBetter));
        return Optional.of(valueSelector.getFrom(result));
    }

    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Iterable<TElement> sourceElements, Func1<? super TElement, TCompared> valueSelector) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(valueSelector, "valueSelector");
        if(Linq.isEmpty(sourceElements)) { return Optional.empty(); }

        TElement result = aggregate(sourceElements, new ComparingChooser<>(valueSelector, HigherIsBetter));
        return Optional.of(valueSelector.getFrom(result));
    }


    public static <TElement, TCompared extends Comparable<TCompared>>
    TElement withMin(Iterable<TElement> sourceElements, Func1<? super TElement, TCompared> valueSelector) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(valueSelector, "valueSelector");
        Preconditions.cannotBeEmpty(sourceElements, "sourceElements");

        TElement result = aggregate(sourceElements, new ComparingChooser<>(valueSelector, LowerIsBetter));
        return result;
    }

    public static <TElement, TCompared extends Comparable<TCompared>>
    TElement withMax(Iterable<TElement> sourceElements, Func1<? super TElement, TCompared> valueSelector) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(valueSelector, "valueSelector");
        Preconditions.cannotBeEmpty(sourceElements, "sourceElements");

        TElement result = aggregate(sourceElements, new ComparingChooser<>(valueSelector, HigherIsBetter));
        return result;
    }

    public static <TElement> TElement withMinInt(Iterable<TElement> sourceElements,
                                                 Func1<? super TElement, Integer> valueSelector) {
        return any(sourceElements)
                ? aggregate(sourceElements, (left, right) -> valueSelector.getFrom(left) < valueSelector.getFrom(right) ? left : right)
                : otherwiseThrow(new SetIsEmptyException());
    }

    public static <TElement> TElement withMaxInt(Iterable<TElement> sourceElements,
                                                 Func1<? super TElement, Integer> valueSelector) {
        return any(sourceElements)
                ? aggregate(sourceElements, (left, right) -> valueSelector.getFrom(left) > valueSelector.getFrom(right) ? left : right)
                : otherwiseThrow(new SetIsEmptyException());
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> sourceElements,
                                                EqualityComparer<? super TElement> equalityComparer) {
        if (sourceElements instanceof Set) {
            return true;
        }

        ComparingLinkedHashSet<TElement> set = new ComparingLinkedHashSet<>(equalityComparer);

        for (TElement element : sourceElements) {

            boolean modified = set.add(element);
            if (! modified) {
                return false;
            }
        }
        return true;
    }

    public static <TElement> double sum(Iterable<TElement> sourceElements,
                                        Func1<? super TElement, Double> valueSelector) {
        return aggregate(sourceElements, 0.0, (left, right) -> left + valueSelector.getFrom(right));
    }

    public static <TElement> boolean setEquals(Iterable<TElement> left, Iterable<? extends TElement> right) {
        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");

        if (size(left) != size(right)) {
            return false;
        }

        Set<TElement> leftSet = new HashSet<>();
        for (TElement leftElement : left) {
            leftSet.add(leftElement);
        }

        return Factories.from(right).all(leftSet::contains);
    }

    public static <TElement> boolean all(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {

        for (TElement element : sourceElements) {
            if (! condition.passesFor(element)) {
                return false;
            }
        }

        return true;
    }

    public static <TElement> int count(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        int count = 0;
        for (TElement element : sourceElements) {
            if (condition.passesFor(element)) {
                count += 1;
            }
        }
        return count;
    }

    public static <TElement> boolean contains(Iterable<? extends TElement> sourceElements,
                                              TElement candidate,
                                              EqualityComparer<? super TElement> comparer) {
        if (comparer == CommonDelegates.DefaultEquality && sourceElements instanceof Collection) {
            return ((Collection) sourceElements).contains(candidate);
        }

        else for (TElement element : sourceElements) {
            if (comparer.equals(candidate, element)) {
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

        if (size(left) != size(right)) { return false; }

        Iterator<? extends TElement> rightMembers = right.iterator();
        for (TElement leftMember : left) {
            if (! equalityComparer.equals(leftMember, rightMembers.next())) {
                return false;
            }
        }

        return true;
    }

    public static <TElement> boolean setEquals(Iterable<TElement> left,
                                               Iterable<? extends TElement> right,
                                               EqualityComparer<? super TElement> equalityComparer) {

        if (size(left) != size(right)) { return false; }

        // order chosen to main equals left-associativity
        // (ie, if you say someSet.setEquals(anotherSet), then you would expect that
        // someSet[0].equals(anotherSet[0]) to be the first call,
        // *not* anotherSet[1].equals(someSet[0]).
        // This is done as a debugging aide, not for functionality.
        // It would be nuts to rely on this!
        ComparingLinkedHashSet<TElement> set = new ComparingLinkedHashSet<>(equalityComparer, right);

        for (TElement element : left) {
            boolean hasChange = set.add(element);
            if (hasChange) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked") //TODO not precisely sure why type system is mad at this
    public static <TElement> TElement aggregate(Iterable<TElement> sourceElements,
                                                Func2<? super TElement, ? super TElement, ? extends TElement> aggregator) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(aggregator, "aggregator");
        Preconditions.cannotBeEmpty(sourceElements, "sourceElements");

        ForkingIterator<TElement> sourceIterator = new ForkingIterator<>(sourceElements);
        TElement seed = sourceIterator.next();

        return (TElement) aggregate(sourceIterator.remaining(), seed, (Func2) aggregator);
    }
    public static <TAccumulate, TElement> TAccumulate aggregate(Iterable<TElement> sourceElements,
                                                                TAccumulate seed,
                                                                Func2<? super TAccumulate, ? super TElement, TAccumulate> aggregator) {
        Preconditions.notNull(sourceElements, "sourceElements");
        Preconditions.notNull(aggregator, "aggregator");

        for (TElement element : sourceElements) {
            seed = aggregator.getFrom(seed, element);
        }

        return seed;
    }

    private static <TResult> TResult otherwiseThrow(RuntimeException e) {
        throw e;
    }

    public static <TElement> boolean isSubsequenceOf(Iterable<? extends TElement> left,
                                                     Iterable<? extends TElement> right,
                                                     EqualityComparer<? super TElement> equalityComparer) {
        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");
        Preconditions.notNull(equalityComparer, "equalityComparer");

        if( ! any(left)){
            return true;
        }

        Iterator<? extends TElement> remainingCandidates = right.iterator();

        NextRequiredMember: for(TElement requiredMember : left){

            while(remainingCandidates.hasNext()){
                TElement nextCandidateFromSupersequence = remainingCandidates.next();

                if(equalityComparer.equals(requiredMember, nextCandidateFromSupersequence)){
                    continue NextRequiredMember;
                }
            }

            //we exausted the members of the candidate superseq and we still have a required member
            // --> the candidate superseq is not a supersequence
            return false;
        }

        //we ran out of required members --> the candidate contained all the members!
        return true;
    }
    public static <TElement, TRight> void forEachWith(Iterable<TElement> leftElements,
                                                      Iterable<TRight> rightElements,
                                                      Action2<? super TElement, ? super TRight> tupleConsumer) {
        Preconditions.fastSameSize(leftElements, rightElements);

        Iterator<TElement> leftItr = leftElements.iterator();
        Iterator<TRight> rightItr = rightElements.iterator();

        while(leftItr.hasNext() && rightItr.hasNext()){
            tupleConsumer.doUsing(leftItr.next(), rightItr.next());
        }

        if(leftItr.hasNext() ^ rightItr.hasNext()){
            throw new IllegalArgumentException("right -- has different number of elemenhts from left");
        }
    }

    public static boolean hasFastSize(Iterable<?> sourceElements){
        return sourceElements instanceof Collection;
    }

    public static int fastSizeIfAvailable(Iterable<?> sourceElements){
        return sourceElements instanceof Collection ? ((Collection) sourceElements).size() : -1;
    }

    public static <TElement> int indexOf(Iterable<? extends TElement> sourceElements,
                                         TElement elementToFind,
                                         EqualityComparer<? super TElement> equalityComparer) {

        int index = 0;
        for(TElement element : sourceElements){
            if(equalityComparer.equals(elementToFind, element)){
                return index;
            }

            index += 1;
        }

        return -1;
    }

    public static class ComparingChooser<TOrigin, TCompared extends Comparable<TCompared>>
            implements Func2<TOrigin, TOrigin, TOrigin>{

        public enum Directionality { LowerIsBetter, HigherIsBetter }

        private Func1<? super TOrigin, TCompared> selector;
        private Directionality directionality;

        public ComparingChooser(Func1<? super TOrigin, TCompared> selector, Directionality directionality) {
            this.selector = selector;
            this.directionality = directionality;
        }

        @Override
        public TOrigin getFrom(TOrigin left, TOrigin right) {
            TCompared leftComparable = selector.getFrom(left);
            TCompared rightComparable = selector.getFrom(right);

            if (left == null || right == null) {
                throw new IllegalArgumentException("valueSelector");
            }

            int result = leftComparable.compareTo(rightComparable);
            if(directionality == LowerIsBetter) {
                if ( left instanceof Double && ((Double) left).isNaN()) {
                    return right;
                }
                else {
                    return result <= 0 ? left : right;
                }
            }
            else if(directionality == HigherIsBetter){
                if ( right instanceof Double && ((Double) right).isNaN()) {
                    return left;
                }
                return result >= 0 ? left : right;
            }
            else { assert false; return left; }
        }
    }
}
