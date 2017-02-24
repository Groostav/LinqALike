package com.empowerops.linqalike;

import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.*;
import com.empowerops.linqalike.queries.*;

import java.io.InputStream;
import java.util.*;
import java.util.Comparator;
import java.util.function.Consumer;

import static com.empowerops.linqalike.CommonDelegates.*;
import static com.empowerops.linqalike.Factories.from;

/**
 * Widened and made-static implementation of {@link Queryable} and {@link QueryableMap}.
 *
 * <p>This interface allows Linq-A-Like users to use older static-method style invocation
 * against older standard collections framework objects (namely {@link java.lang.Iterable})
 */
@SuppressWarnings("WeakerAccess")
public class Linq {

    private Linq(){}

    public static <TElement> Optional<TElement> aggregate(Iterable<TElement> sourceElements,
                                                          Func2<? super TElement, ? super TElement, ? extends TElement> aggregator) {
        return ImmediateInspections.aggregate(sourceElements, aggregator);
    }
    public static <TAccumulate, TElement> TAccumulate aggregate(Iterable<TElement> sourceElements,
                                                                TAccumulate seed,
                                                                Func2<? super TAccumulate, ? super TElement, TAccumulate> aggregator) {
        return ImmediateInspections.aggregate(sourceElements, seed, aggregator);
    }


    /**
     * Static implementation of {@link Queryable#ofType(Class)}, forwards to {@link Linq#cast(Iterable)} and
     * {@link Linq#where(Iterable, Condition)}
     */
    public static <TBase, TDerived extends TBase>
    Queryable<TDerived> ofType(Iterable<TBase> sourceElements,
                               Class<TDerived> desiredType) {

        return cast(where(sourceElements, desiredType::isInstance));
    }

    /**
     * Static implementation of {@link com.empowerops.linqalike.Queryable#single()}.
     * Forwards to {@link ImmediateInspections#single(Iterable, com.empowerops.linqalike.delegate.Condition)}
     */
    public static <TElement>
    TElement single(Iterable<TElement> elements) {
        return single(elements, Tautology);
    }

    /**
     * Static implementation of {@link Queryable#single(com.empowerops.linqalike.delegate.Condition)}.
     * Forwards to {@link ImmediateInspections#single(Iterable, com.empowerops.linqalike.delegate.Condition)}
     */
    public static <TElement>
    TElement single(Iterable<TElement> elements,
                    Condition<? super TElement> uniqueCondition) {
        return ImmediateInspections.single(elements, uniqueCondition);
    }

    public static <TElement>
    Optional<TElement> singleOrDefault(Iterable<TElement> sourceElements) {
        return singleOrDefault(sourceElements, Tautology);
    }

    public static <TElement>
    Optional<TElement> singleOrDefault(Iterable<TElement> sourceElements,
                                       Condition<? super TElement> uniqueCondition) {
        return ImmediateInspections.singleOrDefault(sourceElements, uniqueCondition);
    }

    public static <TElement>
    TElement first(Iterable<TElement> elements) {
        return first(elements, Tautology);
    }

    public static <TElement>
    TElement first(Iterable<TElement> sourceElements,
                   Condition<? super TElement> condition) {

        return ImmediateInspections.first(sourceElements, condition);
    }
    public static <TElement>
    Optional<TElement> firstOrDefault(Iterable<TElement> sourceElements) {
        return ImmediateInspections.firstOrDefault(sourceElements, Tautology);
    }
    public static <TElement>
    Optional<TElement> firstOrDefault(Iterable<TElement> sourceElements,
                                      Condition<? super TElement> condition) {
        return ImmediateInspections.firstOrDefault(sourceElements, condition);
    }

    public static <TElement> Queryable<TElement> first(Iterable<TElement> sourceElements, int count){
        return new FirstElementsQuery<>(sourceElements, count);
    }

    public static <TElement>
    TElement second(Iterable<TElement> sourceElements){
        return second(sourceElements, Tautology);
    }
    public static <TElement>
    TElement second(Iterable<TElement> sourceElements, Condition<? super TElement> condition){
        Queryable<TElement> filtered = from(sourceElements).where(condition).skip(1);
        return filtered.any() ? filtered.first() : Formatting.otherwiseThrow(new SetIsEmptyException(sourceElements, condition));
    }
    public static <TElement>
    Optional<TElement> secondOrDefault(Iterable<TElement> sourceElements){
        return secondOrDefault(sourceElements, Tautology);
    }
    public static <TElement>
    Optional<TElement> secondOrDefault(Iterable<TElement> sourceElements, Condition<? super TElement> condition){
        return from(sourceElements).where(condition).skip(1).firstOrDefault();
    }

    public static <TElement>
    TElement last(Iterable<TElement> sourceElements){
        return last(sourceElements, Tautology);
    }
    public static <TElement>
    TElement last(Iterable<TElement> sourceElements,
                  Condition<? super TElement> condition) {
        return ImmediateInspections.last(sourceElements, condition);
    }
    public static <TElement>
    Optional<TElement> lastOrDefault(Iterable<TElement> sourceElements) {
        return lastOrDefault(sourceElements, Tautology);
    }

    public static <TElement>
    Optional<TElement> lastOrDefault(Iterable<TElement> sourceElements,
                                     Condition<? super TElement> condition) {

        return ImmediateInspections.lastOrDefault(sourceElements, condition);
    }

    @SafeVarargs
    public static <TElement> Queryable<TElement> with(Iterable<? extends TElement> left, TElement... right){
        return new WithQuery<>(left, right);
    }

    public static <TElement> Queryable<TElement> with(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return new WithQuery<>(left, right);
    }

    public static <TElement>
    Queryable<TElement> last(Iterable<TElement> sourceElements, int count){
        return new LastElementsQuery<>(sourceElements, count);
    }

    public static <TElement>
    Queryable<TElement> where(Iterable<TElement> sourceElements,
                              Condition<? super TElement> condition) {

        return new WhereQuery<>(sourceElements, condition);
    }

    public static <TLeft, TRight>
    BiQueryable<TLeft, TRight> where(BiQueryable<TLeft, TRight> sourceElements,
                                     Condition<Tuple<? super TLeft, ? super TRight>> condition) {

        return new BiQueryAdapter.FromPairs<>(new WhereQuery<>(sourceElements, condition));
    }

    public static <TElement, TResult>
    Queryable<TResult> select(Iterable<TElement> sourceElements,
                              Func1<? super TElement, TResult> targetSite) {
        return new SelectQuery<>(sourceElements, targetSite);
    }

    public static <TElement, TTransformed>
    BiQueryable<TElement, TTransformed> pushSelect(Iterable<TElement> sourceElements,
                                                   Func1<? super TElement, TTransformed> selector) {
        return new PushSelectQuery<>(sourceElements, selector);
    }

    public static <TElement, TTransformed>
    BiQueryable<TElement, TTransformed> pushSelectMany(Iterable<? extends TElement> sourceElements,
                                                       Func1<? super TElement, ? extends Iterable<? extends TTransformed>> selector){
        return new PushSelectManyQuery<>(sourceElements, selector);
    }

    public static <TElement>
    boolean any(Iterable<? extends TElement> sourceElements) {
        return ImmediateInspections.any(sourceElements);
    }
    public static <TElement>
    boolean any(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.any(sourceElements, condition);
    }

    public static <TElement>
    boolean isEmpty(Iterable<TElement> sourceElements) {
        return ! ImmediateInspections.any(sourceElements, Tautology);
    }

    public static <TElement, TEquated>
    boolean containsElement(Iterable<? extends TElement> sourceElements,
                            TElement candidate,
                            Func1<? super TElement, TEquated> equatableSelector) {
        return ImmediateInspections.contains(sourceElements, candidate, performEqualsUsing(equatableSelector));
    }
    public static <TElement>
    boolean containsElement(Iterable<? extends TElement> sourceElements,
                            TElement candidate,
                            EqualityComparer<? super TElement> comparer){
        return ImmediateInspections.contains(sourceElements, candidate, comparer);
    }
    public static <TElement>
    boolean containsElement(Iterable<? extends TElement> sourceElements,
                            TElement candidate){
        return ImmediateInspections.contains(sourceElements, candidate, CommonDelegates.DefaultEquality);
    }


    public static <TTransformed, TElement>
    Queryable<TTransformed> selectMany(Iterable<TElement> set,
                                         Func1<? super TElement, ? extends Iterable<TTransformed>> selector) {
        return new SelectManyQuery<>(set, selector);
    }
    public static <TTransformed, TElement>
    Queryable<TTransformed> selectMany(Iterable<TElement> set,
                                       Func1.Array<? super TElement, TTransformed> selector) {
        return new SelectManyQuery<>(set, x -> from(selector.getFrom(x)));
    }

    @SafeVarargs
    public static <TElement>
    Queryable<TElement> union(Iterable<? extends TElement> left, TElement... toInclude) {
        return new UnionQuery<>(left, from(toInclude), CommonDelegates.DefaultEquality);
    }

    public static <TElement>
    Queryable<TElement> union(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return new UnionQuery<>(left, right, CommonDelegates.DefaultEquality);
    }

    public static <TElement, TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> left,
                              Iterable<? extends TElement> right,
                              Func1<? super TElement, TCompared> comparableSelector){

        return new UnionQuery<>(left, right, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement>
    Queryable<TElement> union(Iterable<? extends TElement> left,
                              Iterable<? extends TElement> right,
                              EqualityComparer<? super TElement> equalsComparator){

        return new UnionQuery<>(left, right, equalsComparator);
    }

    public static <TKey, TValue>
    LinqingMap<TKey,TValue> toMap(Iterable<TKey> keys, Iterable<TValue> values) {
        return Factories.asMap(keys, values);
    }

    @SuppressWarnings("unchecked") //callers must be certain that their domain logic ensures
                                   //every element in the set is of the desired type!
    public static <TDesired, TElement>
    Queryable<TDesired> cast(Iterable<TElement> sourceElements) {
        if(sourceElements instanceof Queryable){
            return (Queryable) sourceElements;
        }
        else if (sourceElements instanceof Collection){
            return new QueryAdapter.FromCollection<>((Collection)sourceElements);
        }
        else {
            return new QueryAdapter.FromIterable<>((Iterable)sourceElements);
        }
    }
    public static <TDesired, TElement>
    Queryable<TDesired> cast(Iterable<TElement> sourceElements, Class<TDesired> desiredType) {
        return new CastQuery<>(sourceElements, desiredType);
    }

    public static <TLeftDesired, TLeftOriginal, TRight>
    BiQueryable<TLeftDesired, TRight> castLeft(Iterable<Tuple<TLeftOriginal, TRight>> sourceElements,
                                               Class<TLeftDesired> desiredType) {

        return new CastQuery.Inner<>(sourceElements, Optional.of(desiredType), Optional.empty());
    }

    public static <TLeft, TRightCast, TRightOriginal>
    BiQueryable<TLeft, TRightCast> castRight(Iterable<Tuple<TLeft, TRightOriginal>> sourceElements,
                                             Class<TRightCast> desiredType) {

        return new CastQuery.Inner<>(sourceElements, Optional.empty(), Optional.of(desiredType));
    }

    public static <TElement>
    boolean all(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.all(sourceElements, condition);
    }

    public static <TElement>
    boolean setEquals(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.setEquals(left, right, DefaultEquality);
    }

    public static <TElement, TCompared> boolean setEquals(Iterable<TElement> left,
                                                          Iterable<? extends TElement> right,
                                                          Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.setEquals(left, right, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> boolean setEquals(Iterable<TElement> left,
                                               Iterable<? extends TElement> right,
                                               EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.setEquals(left, right, equalityComparer);
    }

    public static <TElement> boolean sequenceEquals(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.sequenceEquals(left, right, DefaultEquality);
    }

    public static <TElement, TCompared> boolean sequenceEquals(Iterable<TElement> left,
                                                               Iterable<? extends TElement> right,
                                                               Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.sequenceEquals(left, right, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> boolean sequenceEquals(Iterable<TElement> left,
                                                    Iterable<? extends TElement> right,
                                                    EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.sequenceEquals(left, right, equalityComparer);
    }

    public static <TElement>
    Queryable<TElement> skipWhile(Iterable<TElement> sourceElements,
                                  Condition<? super TElement> excludingCondition) {

        return new ConditionalSkipQuery<>(sourceElements, excludingCondition);
    }

    public static <TElement>
    Queryable<TElement> reversed(Iterable<TElement> sourceElements) {
        return new ReversedQuery<>(sourceElements);
    }

    public static <TKey, TValue>
    QueryableMap<TKey, TValue> reversedMap(Iterable<? extends Tuple<TKey, TValue>> sourceEntries) {
        return new QueryAdapter.ToQueryableMap<>(new ReversedQuery<>(sourceEntries));
    }

    public static <TElement>
    boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.isSubsetOf(left, right, CommonDelegates.DefaultEquality);
    }
    public static <TElement, TCompared>
    boolean isSubsetOf(Iterable<TElement> left,
                       Iterable<? extends TElement> right,
                       Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.isSubsetOf(left, right, performEqualsUsing(memoizedSelector(comparableSelector)));
    }
    public static <TElement>
    boolean isSubsetOf(Iterable<TElement> left,
                       Iterable<? extends TElement> right,
                       EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.isSubsetOf(left, right, equalityComparer);
    }


    public static <TElement> boolean isSupersetOf(Iterable<TElement> sourceElements,
                                                  Iterable<? extends TElement> possibleSubset) {
        return ImmediateInspections.isSubsetOf(possibleSubset, sourceElements, CommonDelegates.DefaultEquality);
    }
    public static <TElement> boolean isSubsequenceOf(Iterable<TElement> sourceElements,
                                                     Iterable<? extends TElement> possibleSupersequence) {
        return ImmediateInspections.isSubsequenceOf(sourceElements, possibleSupersequence, CommonDelegates.DefaultEquality);
    }
    public static <TElement> boolean isSupersequenceOf(Iterable<TElement> sourceElements,
                                                       Iterable<? extends TElement> possibleSubsequence) {
        return ImmediateInspections.isSubsequenceOf(possibleSubsequence, sourceElements, CommonDelegates.DefaultEquality)
                || ImmediateInspections.sequenceEquals(sourceElements, possibleSubsequence, CommonDelegates.DefaultEquality);
    }

    public static <TElement>
    int count(Iterable<TElement> sourceElements){
        return ImmediateInspections.count(sourceElements, CommonDelegates.Tautology);
    }

    public static <TElement>
    int count(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.count(sourceElements, condition);
    }

    @SafeVarargs
    public static <TElement>
    Queryable<TElement> except(Iterable<? extends TElement> source, TElement... toExclude) {

        return new ExceptQuery<>(source, from(toExclude), CommonDelegates.DefaultEquality);
    }

    public static <TElement>
    Queryable<TElement> except(Iterable<? extends TElement> left,
                               Iterable<? extends TElement> right) {

        return new ExceptQuery<>(left, right, CommonDelegates.DefaultEquality);
    }

    public static <TElement, TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                               Iterable<? extends TElement> membersToExclude,
                               Func1<? super TElement, TCompared> comparableSelector) {

        return new ExceptQuery<>(originalMembers, membersToExclude, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                                                        Iterable<? extends TElement> membersToExclude,
                                                        EqualityComparer<? super TElement> comparableSelector) {

        return new ExceptQuery<>(originalMembers, membersToExclude, memoized(comparableSelector));
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right) {

        return new IntersectionQuery.WithNaturalEquality<>(left, right);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           TElement... right) {
        return new IntersectionQuery.WithNaturalEquality<>(left, Factories.from(right));
    }

    public static <TElement, TCompared> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                                      Iterable<? extends TElement> right,
                                                                      Func1<? super TElement, TCompared> comparableSelector) {

        return new IntersectionQuery.WithComparable<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right,
                                                           EqualityComparer<? super TElement> comparableSelector) {

        return new IntersectionQuery.WithEqualityComparator<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> skip(Iterable<TElement> sourceElements, int numberToSkip) {
        return sourceElements instanceof List
                ? (Queryable<TElement>) new CountSkipQuery.ForList<>((List) sourceElements, numberToSkip)
                : new CountSkipQuery<>(sourceElements, numberToSkip);
    }

    public static <TKey, TValue>
    QueryableMap<TKey, TValue> skipMap(Iterable<? extends Tuple<TKey, TValue>> sourceEntries, int numberToSkip) {
        return new QueryAdapter.ToQueryableMap<>(new CountSkipQuery<>(sourceEntries, numberToSkip));
    }


    public static <TElement> LinqingList<TElement> toList(Iterable<TElement> set) {
        return Factories.asList(set);
    }

    public static <TElement> InputStream toInputStream(DefaultedQueryable<TElement> sourceElements,
                                                       Func1<? super TElement, Integer> converter) {
        return Factories.asInputStream(sourceElements, converter);
    }

    public static <TElement> Object[] toArray(Queryable<TElement> sourceElements) {
        return Factories.asArray(sourceElements);
    }

    public static <TElement, TDesired> TDesired[] toArray(Queryable<TElement> originalSet,
                                                          TDesired[] targetArray) {
        return Factories.asArray(originalSet, targetArray);
    }

    public static <TElement> int size(Iterable<TElement> sourceElements) {
        return ImmediateInspections.size(sourceElements);
    }

    public static <TElement, TDesired> TDesired[] toArray(Iterable<TElement> sourceElements,
                                                          Func1<Integer, TDesired[]> arrayFactory) {

        return Factories.asArray(sourceElements, arrayFactory);
    }

    public static <TElement> Queryable<TElement> distinct(Iterable<TElement> sourceElements) {
        return new DistinctQuery.WithNaturalEquality<>(sourceElements);
    }

    public static <TElement> Queryable<TElement> distinct(Iterable<TElement> sourceElements,
                                                          EqualityComparer<? super TElement> comparer) {
        return new DistinctQuery.WithEqualityComparable<>(sourceElements, comparer);
    }

    public static <TElement, TCompared> Queryable<TElement> distinct(Iterable<TElement> sourceElements,
                                                                     Func1<? super TElement, TCompared> comparableSelector) {
        return new DistinctQuery.WithComparable<>(sourceElements, comparableSelector);
    }



    public static <TElement> double average(Iterable<? extends TElement> sourceElements,
                                            Func1<? super TElement, Double> valueSelector) {

        return ImmediateInspections.average(sourceElements, valueSelector);
    }

    public static <TElement, TComparable>
    Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                           Func1<? super TElement, TComparable> groupByPropertySelector) {
        return new GroupByQuery<>(setToGroup, performEqualsUsing(memoizedSelector(groupByPropertySelector)));
    }

    public static <TElement>
    Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                           EqualityComparer<? super TElement> groupMembershipComparator) {
        return new GroupByQuery<>(setToGroup, groupMembershipComparator);
    }

    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Iterable<TElement> sourceElements,
                            Func1<? super TElement, TCompared> valueSelector) {
        return ImmediateInspections.min(sourceElements, valueSelector);
    }
    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Iterable<TElement> sourceElements,
                            Func1<? super TElement, TCompared> valueSelector) {
        return ImmediateInspections.max(sourceElements, valueSelector);
    }
    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TElement> withMin(Queryable<TElement> sourceElements,
                               Func1<? super TElement, TCompared> valueSelector) {
        return ImmediateInspections.withMin(sourceElements, valueSelector);
    }
    public static <TElement, TCompared extends Comparable<TCompared>>
    Optional<TElement> withMax(Queryable<TElement> sourceElements,
                               Func1<? super TElement, TCompared> valueSelector) {
        return ImmediateInspections.withMax(sourceElements, valueSelector);
    }

    public static <TElement, TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Queryable<TElement> sourceElements,
                                Func1<? super TElement, TCompared> comparableSelector) {

        return new OrderByQuery<>(sourceElements, performComparisonUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> Queryable<TElement> orderBy(Iterable<TElement> sourceElements,
                                                         Comparator<? super TElement> equalityComparator) {
        return new OrderByQuery<>(sourceElements, equalityComparator);
    }


    public static <TElement> double sum(Iterable<TElement> set,
                                        Func1<? super TElement, Double> valueSelector) {
        return ImmediateInspections.sum(set, valueSelector);
    }

    public static <TElement> ReadonlyLinqingList<TElement> toReadOnly(Iterable<TElement> source) {
        return Factories.asReadonlyList(source);
    }
    public static <TElement> ReadonlyLinqingSet<TElement> toReadonlySet(Iterable<TElement> sourceElements) {
        return Factories.asReadonlySet(sourceElements);
    }


    public static <TElement> LinqingSet<TElement> toSet(Iterable<TElement> source) {
        return Factories.asSet(source);
    }

    public static <TElement> Queryable<TElement> immediately(Iterable<TElement> source) {
        return source instanceof Set ? Factories.asReadonlySet(source) : Factories.asReadonlyList(source);
    }

    public static <TKey, TElement> LinqingMap<TKey,TElement> toMap(Iterable<TElement> sourceElements,
                                                                   Func1<? super TElement,TKey> keySelector) {
        return Factories.asMap(sourceElements, keySelector, identity());
    }

    public static <TKey, TValue, TElement> LinqingMap<TKey,TValue> toMap(Iterable<TElement> sourceElements,
                                                                         Func1<? super TElement,TKey> keySelector,
                                                                         Func1<? super TElement,TValue> valueSelector) {
        return Factories.asMap(sourceElements, keySelector, valueSelector);
    }

    public static <TElement> boolean isSingle(Iterable<TElement> source) {
        return ImmediateInspections.isSingle(source);
    }

    public static <TElement> boolean isMany(Iterable<TElement> sourceElements) {
        return ImmediateInspections.any(sourceElements) && ! ImmediateInspections.isSingle(sourceElements);
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> source) {
        return ImmediateInspections.isDistinct(source, CommonDelegates.DefaultEquality);
    }

    public static <TElement, TCompared> boolean isDistinct(Iterable<TElement> sourceElements,
                                                           Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.isDistinct(sourceElements, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> sourceElements,
                                                EqualityComparer<? super TElement> equalityComparator) {
        return ImmediateInspections.isDistinct(sourceElements, equalityComparator);
    }



    public static <TValue, TKey> TValue getFor(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries, TKey key) {
        if(sourceEntries instanceof Map){
            // so what if its a iterable of one thing, and a util.map of another?
            // you'd get a signature collision in keySet() and values(). So that's impossible. Thank god.
            return ((Map<TKey, TValue>)sourceEntries).get(key);
        }

        return ImmediateInspections.getFor(sourceEntries, key);
    }

    public static <TElement> BiQueryable<TElement, TElement> pairwise(Iterable<TElement> sourceElements) {
        return new PairwiseQuery<>(sourceElements, () -> null);
    }

    public static <TElement> BiQueryable<TElement, TElement> pairwise(Iterable<TElement> sourceElements,
                                                                      Func<? extends TElement> defaultFactory) {
        return new PairwiseQuery<>(sourceElements, defaultFactory);
    }

    public static <TElement> boolean[] toBooleanArray(Iterable<TElement> sourceElements,
                                                      Func1<? super TElement, Boolean> converter) {
        return PrimitiveArrayFactories.asBooleanArray(sourceElements, converter);
    }

    public static <TElement> byte[] toByteArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Byte> converter) {
        return PrimitiveArrayFactories.asByteArray(sourceElements, converter);
    }

    public static <TElement> char[] toCharArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Character> converter) {
        return PrimitiveArrayFactories.asCharArray(sourceElements, converter);
    }

    public static <TElement> short[] toShortArray(Iterable<TElement> sourceElements,
                                                  Func1<? super TElement, Short> converter) {
        return PrimitiveArrayFactories.asShortArray(sourceElements, converter);
    }

    public static <TElement> int[] toIntArray(Iterable<TElement> sourceElements,
                                              Func1<? super TElement, Integer> converter) {
        return PrimitiveArrayFactories.asIntArray(sourceElements, converter);
    }

    public static <TElement> long[] toLongArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Long> converter) {
        return PrimitiveArrayFactories.asLongArray(sourceElements, converter);
    }

    public static <TElement> float[] toFloatArray(Iterable<TElement> sourceElements,
                                                  Func1<? super TElement, Float> converter) {
        return PrimitiveArrayFactories.asFloatArray(sourceElements, converter);
    }

    public static <TElement> double[] toDoubleArray(DefaultedQueryable<TElement> sourceElements,
                                                    Func1<? super TElement, Double> converter) {
        return PrimitiveArrayFactories.asDoubleArray(sourceElements, converter);
    }
    public static <TLeft, TRight, TJoined> Queryable<TJoined> zip(Iterable<TLeft> sourceElements,
                                                                  Iterable<TRight> rightElements,
                                                                  Func2<? super TLeft, ? super TRight, TJoined> resultSelector) {
        return new ZipQuery.WithJoinFactory<>(sourceElements, rightElements, resultSelector);
    }

    public static <TLeft, TRight> BiQueryable<TLeft, TRight> zip(Iterable<TLeft> left, Iterable<TRight> right) {
        return new ZipQuery<>(left, right);
    }

    public static <TElement, TRight> void forEachWith(Iterable<TElement> leftElements,
                                                      Iterable<TRight> rightElements,
                                                      Action2<? super TElement, ? super TRight> tupleConsumer) {
        ImmediateInspections.forEachWith(leftElements, rightElements, tupleConsumer);
    }

    public static <TElement> int indexOf(Iterable<? extends TElement> sourceElements,
                                         TElement elementToFind,
                                         EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.indexOf(sourceElements, elementToFind, equalityComparer);
    }


    public static <TElement> int lastIndexOf(Iterable<TElement> sourceElements,
                                             TElement elementToFind,
                                             EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.indexOf(reversed(sourceElements), elementToFind, equalityComparer);
    }

    @SuppressWarnings("unchecked") //cast of a dynamically assured type
    public static <TLeft, TSubclassLeft extends TLeft, TRight>
    BiQueryable<TSubclassLeft, TRight> ofLeftType(BiQueryable<TLeft, TRight> tuples, Class<TSubclassLeft> desiredLeftClass) {
        return (BiQueryable) where(tuples, tuple -> desiredLeftClass.isInstance(tuple.left));
    }

    @SuppressWarnings("unchecked") //cast of a dynamically assured type
    public static <TLeft, TSubclassRight extends TRight, TRight>
    BiQueryable<TLeft, TSubclassRight> ofRightType(BiQueryable<TLeft, TRight> tuples, Class<TSubclassRight> desiredRightClass) {
        return (BiQueryable) where(tuples, tuple -> desiredRightClass.isInstance(tuple.right));
    }

    public static <TElement, TCompared> int lastIndexOf(Iterable<TElement> sourceElements,
                                                        TElement elementToFind,
                                                        Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.indexOf(reversed(sourceElements), elementToFind, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TElement> Queryable<TElement> inlineForEach(Iterable<TElement> sourceElements,
                                                               Action1<? super TElement> sideEffectTransform) {
        return Linq.select(sourceElements, x -> { sideEffectTransform.doUsing(x); return x; } );
    }

    public static <TElement, TCompared> int indexOf(Iterable<? extends TElement> sourceElements,
                                                    TElement elementToFind,
                                                    Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.indexOf(sourceElements, elementToFind, performEqualsUsing(memoizedSelector(comparableSelector)));
    }

    public static <TValue, TKey> QueryableMap<TValue, TKey> inverted(DefaultedQueryableMap<TKey, TValue> tuples) {
        return new InvertMapQuery<>(tuples);
    }

    @SuppressWarnings("unchecked") //I really dont know, but I'm pretty sure this is safe. Type systems are hard.
    public static <TLeftTransformed, TRightTransformed, TLeft, TRight>
    BiQueryable<TLeftTransformed,TRightTransformed> selectManyPairs(Iterable<Tuple<TLeft, TRight>> sourceElements,
                                                                    Func2<? super TLeft, ? super TRight, ? extends Iterable<? extends Tuple<TLeftTransformed, TRightTransformed>>> selector) {
        return new BiQueryAdapter.FromPairs<>(new SelectManyQuery<>(sourceElements, (Func1) selector.asFuncOnTuple()));
    }
}

