package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.Queries.*;

import java.util.Comparator;

import static LinqALike.CommonDelegates.*;
import static LinqALike.Factories.from;

public class Linq {

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
        return any(sourceElements, Tautology);
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
                              Func2<? super TElement, ? super TElement, Boolean> equalsComparator){

        return new UnionQuery<>(left, right, equalsComparator);
    }

    public static <TKey, TValue>
    LinqingMap<TKey,TValue> toMap(Iterable<TKey> keys, Iterable<TValue> values) {
        return Factories.asMap(keys, values);
    }

    public static <TDerived, TElement>
    Queryable<TDerived> cast(Iterable<TElement> sourceElements) {
        if(sourceElements instanceof Queryable){
            return (Queryable<TDerived>) sourceElements;
        }
        else {
            return new CastQuery<>(sourceElements, Object.class);
        }
    }

    public static <TDerived, TElement>
    Queryable<TDerived> cast(Iterable<TElement> sourceElements, Class<TDerived> desiredType) {
        return new CastQuery<>(sourceElements, desiredType);
    }

    public static <TElement>
    boolean all(Iterable<TElement> sourceElements, Condition<? super TElement> condition) {
        return ImmediateInspections.all(sourceElements, condition);
    }

    public static <TElement>
    boolean isSetEquivalentOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.isSetEquivalentOf(left, right);
    }

    public static <TElement>
    Queryable<TElement> skipWhile(Iterable<TElement> sourceElements,
                                                           Condition<? super TElement> excludingCondition) {

        return new SkipQuery<>(sourceElements, excludingCondition);
    }

    public static <TElement>
    Queryable<TElement> reversed(Iterable<TElement> sourceElements) {
        return ImmediateInspections.reversed(sourceElements);
    }

    public static <TElement>
    boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        return ImmediateInspections.isSubsetOf(left, right);
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
                                                        Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new ExceptQuery<>(originalMembers, membersToExclude, comparableSelector);
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
                                                           Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new IntersectionQuery.WithEqualityComparator<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> skip(Iterable<TElement> sourceElements, int numberToSkip) {
        return new SkipQuery<>(sourceElements, numberToSkip);
    }

    public static <TElement> Object[] toArray(Queryable<TElement> set) {
        Object[] copy = new Object[set.size()];
        int i = 0;
        for(TElement element : set){
            copy[i++] = element;
        }
        return copy;
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
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

    public static <TElement> Queryable<TElement> distinct(Iterable<TElement> candidateWithDuplicates) {
        return new DistinctQuery.WithNaturalEquality<>(candidateWithDuplicates);
    }

    public static <TElement> double average(Iterable<? extends TElement> sourceElements,
                                            Func1<? super TElement, Double> valueSelector) {

        return ImmediateInspections.average(sourceElements, valueSelector);
    }

    public static <TElement> boolean containsElement(Iterable<? extends TElement> set,
                                                     TElement candidate) {
        return contains(set, candidate);
    }

    public static <TElement, TComparable>
    Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                           Func1<? super TElement, TComparable> groupByPropertySelector) {
        return new GroupByQuery<>(setToGroup, performEqualsUsing(memoized(groupByPropertySelector)));
    }

    public static <TElement>
    Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                           Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
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
}

