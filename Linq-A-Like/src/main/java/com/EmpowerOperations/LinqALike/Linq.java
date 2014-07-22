package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.QueryAdapter;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func;
import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.Queries.*;

import java.util.Comparator;
import java.util.Map;

import static com.EmpowerOperations.LinqALike.CommonDelegates.*;
import static com.EmpowerOperations.LinqALike.Factories.from;

public class Linq {

    private Linq(){}

    public static <TBase, TDerived extends TBase>
    Queryable<TDerived> ofType(Iterable<TBase> sourceElements,
                               Class<TDerived> desiredType) {

        return cast(where(sourceElements, desiredType::isInstance));
    }

    public static <TElement>
    TElement single(Iterable<TElement> elements) {
        return single(elements, Tautology);
    }

    public static <TElement>
    TElement single(Iterable<TElement> elements,
                    Condition<? super TElement> uniqueCondition) {
        return ImmediateInspections.single(elements, uniqueCondition);
    }

    public static <TElement>
    TElement singleOrDefault(Iterable<TElement> sourceElements) {
        return singleOrDefault(sourceElements, Tautology);
    }

    public static <TElement>
    TElement singleOrDefault(Iterable<TElement> sourceElements,
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
    TElement firstOrDefault(Iterable<TElement> sourceElements) {

        return ImmediateInspections.firstOrDefault(sourceElements, Tautology);
    }

    public static <TElement>
    TElement firstOrDefault(Iterable<TElement> sourceElements,
                            Condition<? super TElement> condition) {

        return ImmediateInspections.firstOrDefault(sourceElements, condition);
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

    public static <TElement> TElement lastOrDefault(Iterable<TElement> sourceElements) {
        return lastOrDefault(sourceElements, Tautology);
    }

    public static <TElement>
    TElement lastOrDefault(Iterable<TElement> sourceElements,
                           Condition<? super TElement> condition) {

        return ImmediateInspections.lastOrDefault(sourceElements, condition);
    }

    public static <TElement>
    Queryable<TElement> where(Iterable<TElement> sourceElements,
                              Condition<? super TElement> condition) {

        return new WhereQuery<>(sourceElements, condition);
    }

    public static <TElement, TResult>
    Queryable<TResult> select(Iterable<TElement> sourceElements,
                              Func1<? super TElement, TResult> targetSite) {
        return new SelectQuery<>(sourceElements, targetSite);
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

    public static <TElement>
    boolean contains(Iterable<? extends TElement> sourceElemens, Object candidate) {
        return ImmediateInspections.contains(sourceElemens, candidate);
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

    public static <TElement>
    Queryable<TElement> union(Iterable<? extends TElement> left, TElement... toInclude) {
        return new UnionQuery<>(left, from(toInclude), performEqualsUsing(identity()));
    }

    public static <TElement>
    Queryable<TElement> union(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return new UnionQuery<>(left, right, performEqualsUsing(identity()));
    }

    public static <TElement, TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> left,
                              Iterable<? extends TElement> right,
                              Func1<? super TElement, TCompared> comparableSelector){

        return new UnionQuery<>(left, right, performEqualsUsing(memoized(comparableSelector)));
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
        else {
            return new QueryAdapter.FromIterable<TDesired>((Iterable)sourceElements);
        }
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
        return ImmediateInspections.setEquals(left, right, performEqualsUsing(memoized(comparableSelector)));
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
        return ImmediateInspections.sequenceEquals(left, right, performEqualsUsing(memoized(comparableSelector)));
    }

    public static <TElement> boolean sequenceEquals(Iterable<TElement> left,
                                                    Iterable<? extends TElement> right,
                                                    EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.sequenceEquals(left, right, equalityComparer);
    }

    public static <TElement>
    Queryable<TElement> skipWhile(Iterable<TElement> sourceElements,
                                                           Condition<? super TElement> excludingCondition) {

        return new SkipQuery<>(sourceElements, excludingCondition);
    }

    public static <TElement>
    Queryable<TElement> reversed(Iterable<TElement> sourceElements) {
        return new ReversedQuery<>(sourceElements);
    }

    public static <TElement>
    boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.isSubsetOf(left, right, CommonDelegates.DefaultEquality);
    }
    public static <TElement, TCompared>
    boolean isSubsetOf(Iterable<TElement> left,
                       Iterable<? extends TElement> right,
                       Func1<? super TElement, TCompared> comparableSelector) {
        return ImmediateInspections.isSubsetOf(left, right, performEqualsUsing(memoized(comparableSelector)));
    }
    public static <TElement>
    boolean isSubsetOf(Iterable<TElement> left,
                       Iterable<? extends TElement> right,
                       EqualityComparer<? super TElement> equalityComparer) {
        return ImmediateInspections.isSubsetOf(left, right, equalityComparer);
    }

    public static <TElement>
    int count(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.count(sourceElements, condition);
    }

    public static <TElement>
    Queryable<TElement> except(Iterable<? extends TElement> source, TElement... toExclude) {

        return new ExceptQuery<>(source, from(toExclude), performEqualsUsing(identity()));
    }

    public static <TElement>
    Queryable<TElement> except(Iterable<? extends TElement> left,
                               Iterable<? extends TElement> right) {

        return new ExceptQuery<>(left, right, performEqualsUsing(CommonDelegates.<TElement>identity()));
    }

    public static <TElement, TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                               Iterable<? extends TElement> membersToExclude,
                               Func1<? super TElement, TCompared> comparableSelector) {

        return new ExceptQuery<>(originalMembers, membersToExclude, performEqualsUsing(memoized(comparableSelector)));
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
        return new SkipQuery<>(sourceElements, numberToSkip);
    }

    public static <TElement> Object[] toArray(Queryable<TElement> sourceElements) {
        return Factories.asArray(sourceElements);
    }

    public static <TElement, TDesired> TDesired[] toArray(Queryable<TElement> originalSet,
                                                          TDesired[] targetArray) {
        return Factories.asArray(originalSet, targetArray);
    }

    public static <TElement> LinqingList<TElement> toList(Iterable<TElement> set) {
        return Factories.asList(set);
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
        return new GroupByQuery<>(setToGroup, performEqualsUsing(memoized(groupByPropertySelector)));
    }

    public static <TElement>
    Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                           EqualityComparer<? super TElement> groupMembershipComparator) {
        return new GroupByQuery<>(setToGroup, groupMembershipComparator);
    }

    public static <TElement> double min(Queryable<TElement> sourceElements, Func1<? super TElement,Double> valueSelector) {
        return ImmediateInspections.min(sourceElements, valueSelector);
    }

    public static <TElement> double max(Iterable<TElement> sourceElements, Func1<? super TElement, Double> valueSelector) {
        return ImmediateInspections.max(sourceElements, valueSelector);
    }

    public static <TElement> TElement withMax(Queryable<TElement> sourceElements,
                                              Func1<? super TElement ,Double> valueSelector) {
        return ImmediateInspections.withMax(sourceElements, valueSelector);
    }

    public static <TElement> TElement withMin(Queryable<TElement> sourceElements,
                                              Func1<? super TElement, Double> valueSelector) {
        return ImmediateInspections.withMin(sourceElements, valueSelector);
    }

    public static <TElement, TCompared extends Comparable<TCompared>> Queryable<TElement> orderBy(Queryable<TElement> sourceElements,
                                                                                                 Func1<? super TElement, TCompared> comparableSelector) {
        return new OrderByQuery<>(sourceElements, performComparisonUsing(memoized(comparableSelector)));
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

    public static <TElement> LinqingSet<TElement> toSet(Iterable<TElement> source) {
        return Factories.asSet(source);
    }

    public static <TElement> Queryable<TElement> fetch(Iterable<TElement> source) {
        return Factories.asReadonlyList(source);
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

    public static <TElement> boolean isDistinct(Iterable<TElement> source) {
        return ImmediateInspections.isDistinct(source);
    }


    public static <TValue, TKey> TValue getFor(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries, TKey key) {
        if(sourceEntries instanceof Map){
            //so what if its a queryable map of one thing, and a util.map of another?
            //no, because you'd get a signature collision in keySet() and values(). So that's impossible. Thank god.
            return ((Map<TKey, TValue>)sourceEntries).get(key);
        }
        if(sourceEntries instanceof QueryableMap){
            return ((QueryableMap<TKey, TValue>) sourceEntries).getValueFor(key);
        }

        return ImmediateInspections.getFor(sourceEntries, key);
    }

    public static <TElement> Queryable<Tuple<TElement, TElement>> pairwise(Iterable<TElement> sourceElements) {
        return new PairwiseQuery<>(sourceElements, () -> null);
    }

    public static <TElement> Queryable<Tuple<TElement, TElement>> pairwise(Iterable<TElement> sourceElements,
                                                                           Func<? extends TElement> defaultFactory) {
        return new PairwiseQuery<>(sourceElements, defaultFactory);
    }

    public static class MapSpecific {
        private MapSpecific(){}


        public static <TKey, TValue> Queryable<TValue> getAll(Iterable<? extends Map.Entry<TKey, TValue>> entries,
                                                              Iterable<? extends TKey> keys) {
            return where(entries, x -> containsElement(keys, x.getKey())).values();
        }


        public static <TKey, TValue>
        QueryableMap<TValue, TKey> inverted(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries) {
            return new InvertMapQuery<>(sourceEntries);
        }
        public static <TKey, TValue> Queryable<TKey> keySet(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries) {
            return select(sourceEntries, Map.Entry<TKey, TValue>::getKey);
        }
        public static <TValue, TKey> Queryable<TValue> values(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries) {
            return select(sourceEntries, Map.Entry<TKey, TValue>::getValue);
        }
        public static <TKey, TValue> LinqingMap<TKey, TValue> toMap(Iterable<? extends Map.Entry<TKey, TValue>> entries) {
            return new LinqingMap<>(entries);
        }

        public static <TKey, TValue>
        QueryableMap<TKey, TValue> distinct(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries) {
            return new QueryAdapter.ToQueryableMap<>(new DistinctQuery.WithNaturalEquality<>(sourceEntries));
        }
        public static <TCompared, TKey, TValue>
        QueryableMap<TKey, TValue> distinct(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                            Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
            return new QueryAdapter.ToQueryableMap<>(new DistinctQuery.WithComparable<>(sourceEntries, comparableSelector));
        }

        public static <TKey, TValue>
        QueryableMap<TKey, TValue> distinct(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                            EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparison) {
            return new QueryAdapter.ToQueryableMap<>(new DistinctQuery.WithEqualityComparable<>(sourceEntries, equalityComparison));
        }

        public static <TKey, TValue>
        QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> sourceElements,
                                          Map.Entry<TKey, TValue>... toExclude) {
            return new QueryAdapter.ToQueryableMap<>(new ExceptQuery<>(sourceElements, from(toExclude), CommonDelegates.DefaultEquality));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                          Iterable<? extends Map.Entry<TKey, TValue>> toExclude) {
            return new QueryAdapter.ToQueryableMap<>(new ExceptQuery<>(sourceEntries, toExclude, CommonDelegates.DefaultEquality));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                          Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                                          EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparison) {
            return new QueryAdapter.ToQueryableMap<>(new ExceptQuery<>(sourceEntries, toExclude, equalityComparison));
        }
        public static <TKey, TValue, TCompared>
        QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                          Iterable<? extends Map.Entry<TKey, TValue>> toExclude,
                                          Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
            return new QueryAdapter.ToQueryableMap<>(new ExceptQuery<>(sourceEntries, toExclude, performEqualsUsing(memoized(comparableSelector))));
        }


        public static <TKey, TValue, TCompared>
        QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                             Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                             Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
            return new QueryAdapter.ToQueryableMap<>(new IntersectionQuery.WithComparable<>(sourceEntries, toInclude, memoized(comparableSelector)));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                             Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                             EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparison) {
            return new QueryAdapter.ToQueryableMap<>(new IntersectionQuery.WithEqualityComparator<>(sourceEntries, toInclude, equalityComparison));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                             Iterable<? extends Map.Entry<TKey, TValue>> toInclude) {
            return new QueryAdapter.ToQueryableMap<>(new IntersectionQuery.WithNaturalEquality<>(sourceEntries, toInclude));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                             Map.Entry<TKey, TValue>... toIntersect) {
            return new QueryAdapter.ToQueryableMap<>(new IntersectionQuery.WithNaturalEquality<>(sourceEntries, from(toIntersect)));
        }


        public static <TKey, TValue, TCompared extends Comparable<TCompared>>
        QueryableMap<TKey, TValue> orderBy(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                           Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
            return new QueryAdapter.ToQueryableMap<>(new OrderByQuery<>(sourceEntries, performComparisonUsing(memoized(comparableSelector))));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> orderBy(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                           Comparator<? super Map.Entry<TKey, TValue>> equalityComparator) {
            return new QueryAdapter.ToQueryableMap<>(new OrderByQuery<>(sourceEntries, equalityComparator));
        }


        public static <TKey, TValue>
        QueryableMap<TKey, TValue> reversed(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries) {
            return new QueryAdapter.ToQueryableMap<>(new ReversedQuery<>(sourceEntries));
        }


        public static <TKey, TValue>
        QueryableMap<TKey, TValue> skipWhile(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                             Condition<? super Map.Entry<TKey, TValue>> toExclude) {
            return new QueryAdapter.ToQueryableMap<>(new SkipQuery<>(sourceEntries, toExclude));
        }


        public static <TKey, TValue>
        QueryableMap<TKey, TValue> skip(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                        int numberToSkip) {
            return new QueryAdapter.ToQueryableMap<>(new SkipQuery<>(sourceEntries, numberToSkip));
        }


        public static <TKey, TValue>
        QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                         Map.Entry<TKey, TValue>... entries) {
            return new QueryAdapter.ToQueryableMap<>(new UnionQuery<>(sourceEntries, from(entries), CommonDelegates.DefaultEquality));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                         Iterable<? extends Map.Entry<TKey, TValue>> toInclude) {
            return new QueryAdapter.ToQueryableMap<>(new UnionQuery<>(sourceEntries, toInclude, CommonDelegates.DefaultEquality));
        }
        public static <TCompared, TKey, TValue>
        QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                         Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                         Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
            return new QueryAdapter.ToQueryableMap<>(new UnionQuery<>(sourceEntries, toInclude, performEqualsUsing(comparableSelector)));
        }
        public static <TKey, TValue>
        QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                         Iterable<? extends Map.Entry<TKey, TValue>> toInclude,
                                         EqualityComparer<? super Map.Entry<TKey, TValue>> equalityComparator) {
            return new QueryAdapter.ToQueryableMap<>(
                    new UnionQuery<>(sourceEntries, toInclude, equalityComparator)
            );
        }

        public static <TKey, TValue>
        QueryableMap<TKey, TValue> where(Iterable<? extends Map.Entry<TKey, TValue>> sourceEntries,
                                         Condition<? super Map.Entry<TKey, TValue>> condition) {
            return new QueryAdapter.ToQueryableMap<>(
                    new WhereQuery<>(sourceEntries, condition)
            );
        }
    }
}

