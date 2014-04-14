package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import java.util.Comparator;

/**
 * @author Geoff on 06/09/13
 */
public interface Queryable<TElement> extends Iterable<TElement> {

    boolean all(Condition<? super TElement> condition);
    boolean any();
    boolean any(Condition<? super TElement> condition);


    double average(Func1<? super TElement, Double> valueSelector);


    <TDerived> Queryable<TDerived> uncheckedCast();
    <TDerived> Queryable<TDerived> cast(Class<TDerived> desiredType);


    boolean contains(Object candidate);
    boolean containsElement(TElement element);


    int count(Condition<? super TElement> condition);


    Queryable<TElement> distinct();
    Queryable<TElement> distinct(Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector);

    Queryable<TElement> except(TElement... toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                       Func2<? super TElement,? super TElement, Boolean> equalityComparison);
    <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector);


    TElement first();
    TElement first(Condition<? super TElement> condition);
    TElement firstOrDefault();
    TElement firstOrDefault(Condition<? super TElement> condition);


    <TComparable>
    Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> comparableSelector);
    Queryable<Queryable<TElement>> groupBy(Func2<? super TElement, ? super TElement, Boolean> equalityComparison);


    <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                          Func2<? super TElement, ? super TElement, Boolean> equalityComparison);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude);
    Queryable<TElement> intersect(TElement... toIntersect);

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
    Queryable<TElement> orderBy(Comparator<? super TElement> equalityComparator);


    Queryable<TElement> reversed();


    <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector);
    <TTransformed>
    Queryable<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector);


    TElement single();
    TElement single(Condition<? super TElement> uniqueConstraint);
    TElement singleOrDefault();
    TElement singleOrDefault(Condition<? super TElement> uniqueConstraint);


    Queryable<TElement> skipWhile(Condition<? super TElement> toExclude);
    Queryable<TElement> skip(int numberToSkip);


    double sum(Func1<? super TElement, Double> valueSelector);


    ReadonlyLinqingList<TElement> toReadOnly();
    LinqingList<TElement> toList();
    LinqingSet<TElement> toSet();
    Queryable<TElement> fetch();


    <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys);
    <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector);
    <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                          Func1<? super TElement, TValue> valueSelector);


    <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator);
    //note, we dont provide a method with a factory, if you need one, you should do .select(factory).toArray(Desired::new)
    <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory);
    Object[] toArray();

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
