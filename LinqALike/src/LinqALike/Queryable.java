package LinqALike;

import LinqALike.Delegate.*;

import java.util.function.IntFunction;

/**
 * @author Geoff on 06/09/13
 */
@SuppressWarnings("SpellCheckingInspection")
public interface Queryable<TElement> extends Iterable<TElement> {

    TElement aggregate(Func2<TElement, TElement, TElement> aggregator);
    <TAccumulate> TAccumulate aggregate(TAccumulate seed,
                                        Func2<TAccumulate, TElement, TAccumulate> aggregator);


    boolean all(Condition<? super TElement> condition);
    boolean any();
    boolean any(Condition<? super TElement> condition);


    double average(Func1<? super TElement, Double> valueSelector);


    <TDerived> Queryable<TDerived> cast();


    boolean contains(Object candidate);
    boolean contains(Func2<? super TElement, ? super TElement, Boolean> equalityComparison);


    int count(Condition<? super TElement> condition);


    Queryable<TElement> distinct();
    Queryable<TElement> distinct(Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector);


    Queryable<TElement> except(Iterable<? extends TElement> toExclude);
    Queryable<TElement> except(TElement... toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func2<TElement, TElement, Boolean> equalityComparison);
    <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector);


    TElement first();
    TElement first(Condition<? super TElement> condition);
    TElement firstOrDefault();
    TElement firstOrDefault(Condition<? super TElement> condition);


    <TComparable>
    QueryableMultiMap<TComparable, TElement> groupBy(Func1<? super TElement, TComparable> comparableSelector);
    Queryable<TElement> groupBy(Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    <TComparable, TValue>
    QueryableMultiMap<TComparable, TValue> groupBy(Func1<? super TElement, TComparable> keySelector,
                                                   Func1<? super TElement, TValue> valueSelector);


    <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude);
    Queryable<TElement> intersect(TElement... toIntersect);


    <TRight, TResult>
    Queryable<TResult> join(Iterable<? extends TRight> right,
                            Func2<? super TElement, ? super TRight, TResult> makeResult);
    //TODO: lots of work around joins. v1.1


    TElement last();
    TElement last(Condition<? super TElement> condition);
    TElement lastOrDefault();
    TElement lastOrDefault(Condition<? super TElement> condition);


    double max(Func1<? super TElement, Double> valueSelector);
    TElement withMax(Func1<? super TElement, Double> valueSelector);
    double min(Func1<? super TElement, Double> valueSelector);
    TElement withMin(Func1<? super TElement, Double> valueSelector);


    <TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass);


    <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> orderBy(Func2<? super TElement, ? super TElement, Integer> comparator);


    Queryable<TElement> reversed();


    <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector);
    <TTransformed>
    LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector);


    TElement single();
    TElement single(Condition<? super TElement> uniqueConstraint);
    TElement singleOrDefault();
    TElement singleOrDefault(Condition<? super TElement> uniqueConstraint);


    Queryable<TElement> skipWhile(Condition<? super TElement> toExclude);
    Queryable<TElement> skipUntil(Condition<? super TElement> toInclude);
    Queryable<TElement> skip(int numberToSkip);


    double sum(Func1<? super TElement, Double> valueSelector);


    ReadonlyLinqingList<TElement> toReadOnly();
    LinqingList<TElement> toList();
    LinqingSet<TElement> toSet();
    Queryable<TElement> fetch();


    <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys);
    <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector);
    <TKey, TValue> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector,
                                                    Func1<? super TElement, TValue> valueSelector);
    <TKey, TValue> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys,
                                                    Func1<? super TElement, TValue> valueSelector);
    //TODO toMultiMap();


    Object[] toArray();
    <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator);
    <TDesired> TDesired[] toArray(Func1<Integer, TDesired> arrayFactory);


    Queryable<TElement> union(TElement... elements);
    Queryable<TElement> union(Iterable<? extends TElement> toInclude);
    <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              Func1<? super TElement, TCompared> comparableSelector);


    Queryable<TElement> where(Condition<? super TElement> condition);


    int size();


    boolean isSingle();
    boolean isEmpty();
    boolean isSetEquivalentOf(Iterable<? extends TElement> otherSet);
    boolean isSubsetOf(Iterable<? extends TElement> otherSet);
    boolean isDistinct();
}
