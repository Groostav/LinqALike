package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

public interface QueryableMultiMap<TKey, TValue> extends QueryableMap<TKey, Queryable<TValue>>{


    Queryable<TElement> except(Iterable<? extends TElement> toExclude);
    Queryable<TElement> except(TElement... toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func2<TElement, TElement, Boolean> equalityComparison);
    <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector);


    <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude);
    Queryable<TElement> intersect(TElement... toIntersect);



    double max(Func1<? super TElement, Double> valueSelector);
    TElement withMax(Func1<? super TElement, Double> valueSelector);
    double min(Func1<? super TElement, Double> valueSelector);
    TElement withMin(Func1<? super TElement, Double> valueSelector);


    <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> orderBy(Func2<? super TElement, ? super TElement, Integer> comparator);


    Queryable<TElement> reversed();


    <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector);
    <TTransformed>
    LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector);
    <TTransformedValue>
    QueryableMultiMap<TKey, TTransformedValue> selectFromGroup(Func1<TValue, TTransformedValue> valueSelector);




    Queryable<TElement> skipWhile(Condition<? super TElement> toExclude);
    Queryable<TElement> skipUntil(Condition<? super TElement> toInclude);
    Queryable<TElement> skip(int numberToSkip);



    ReadonlyLinqingList<TElement> toReadOnly();
    LinqingList<TElement> toList();
    LinqingSet<TElement> toSet();
    Queryable<TElement> fetch();



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
