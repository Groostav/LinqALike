package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func1;

import java.util.Comparator;

/**
 * @author Geoff on 06/09/13
 */
public interface Queryable<TElement> extends Iterable<TElement> {

    /**
     * Determines if all elements in the set pass the given condition.
     *
     * <p>note that if this is called on an empty collection it will return true.
     * In other words, all members of the empty set pass a given condition.</p>
     *
     * @param condition the condition to test each member in the set with
     * @return <tt>true</tt> iff all members of the set pass the supplied condition, or the set is empty
     */
    boolean all(Condition<? super TElement> condition);

    /**
     * Determines if the set contains any elements.
     *
     * <p>functionally identical to <code>! source.isEmpty()</code></p>
     *
     * @return true iff the set contains any elements
     */
    boolean any();

    /**
     * Determines if the set contains any elements that pass the given condition.
     *
     * @param condition the condition that must be satisfied
     * @return <tt>true</tt> if there is at least one element in the set that passes the supplied condition
     */
    boolean any(Condition<? super TElement> condition);


    /**
     * Determines the average value of the set, where each elements value is found with the supplied valueSelector
     *
     * @param valueSelector the transform needed to get a scalar value from each member of the set
     * @return the average value of the set as per the supplied value selector
     */
    double average(Func1<? super TElement, Double> valueSelector);


    /**
     * Returns a cast of this collection as the desired type.
     *
     * <p><b>This method may lead to heap pollution</b> as it is equivalent to
     * <code>(Queryable&ltTDesired&gt)source}</code>
     * This method should only be called if, by some means not known to the static type system,
     * the caller is <i>certain</i> that this collection only contains members that implement
     * <tt>TSuper</tt>. If they do not, a {@link java.lang.ClassCastException} will be thrown
     * when the problem member is iterated to. Please consider using {@link #ofType(Class)} or
     * doing a check to ensure this cast is safe with
     * <code>
     * collection.all(desiredType::isInstance)
     * </code>
     * to avoid heap pollution.
     *
     * @return a Queryable statically parameterized to the specified type. No run-time type checking is performed.
     */
    <TDesired> Queryable<TDesired> cast();

    /**
     * Determines if this Queryable contains the supplied candidate element.
     *
     * <p>consider using {@link #containsElement(Object)}</p> as a typed alternative.</p>
     *
     * @param candidate element whose presence in this collection is to be determined
     * @return true of the supplied candidate is in this collection
     * @see java.util.Collection#contains(Object)
     * @see #containsElement(Object)
     */
    boolean contains(Object candidate);
    /**
     * Determines if this Queryable contains the supplied candidate element.
     *
     * This method is a typed <code>contains</code> method that will likely protect users from common
     * wrong argument bugs relating to the {@link java.util.Collection#contains(Object)}
     * method.
     *
     * @param candidate element whose presence in this collection is to be determined
     * @return true of the supplied candidate is in this collection
     */
    boolean containsElement(TElement candidate);


    /**
     * Determines the count of elements that pass the supplied condition.
     *
     * @param condition a condition that elements must pass to be included in the count
     * @return the number of elements that passed the given condition
     */
    int count(Condition<? super TElement> condition);


    /**
     * Returns a Queryable that contains no duplicates.
     *
     * <p>A duplicate is found by using natural equality comparison.</p>
     *
     * <p>The returned Queryable will contain each element in the source Queryable in the
     * same order as the source Queryable, but with only the first elements of any duplicate
     * elements in its result.</p>
     *
     * @return the source set skipping any duplicates
     */
    Queryable<TElement> distinct();

    /**
     * Returns a Queryable that contains no duplicates as specified by comparing elements transformed
     * through the selector.
     *
     * <p>A duplicate is found by using the natural equality on the projection found using
     * the comparableSelector.</p>
     *
     * @param comparableSelector the projection to use to find comparable values from each element
     * @param <TCompared> the type returned by the comparableSelector, and the type that is compored
     * @return the source set skipping any duplicates
     */
    <TCompared>
    Queryable<TElement> distinct(Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Returns a Queryable that contains no duplicates as specified by the supplied equality comparison
     * @param equalityComparison
     * @return
     */
    Queryable<TElement> distinct(EqualityComparer<? super TElement> equalityComparison);

    Queryable<TElement> except(TElement... toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude);
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               EqualityComparer<? super TElement> equalityComparison);
    <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector);


    TElement first();
    TElement first(Condition<? super TElement> condition);
    TElement firstOrDefault();
    TElement firstOrDefault(Condition<? super TElement> condition);


    <TComparable>
    Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> comparableSelector);
    Queryable<Queryable<TElement>> groupBy(EqualityComparer<? super TElement> equalityComparison);


    <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector);
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  EqualityComparer<? super TElement> equalityComparison);
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
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              EqualityComparer<? super TElement> equalityComparator);


    Queryable<TElement> where(Condition<? super TElement> condition);


    int size();


    boolean isSingle();
    boolean isEmpty();
    boolean isSetEquivalentOf(Iterable<? extends TElement> otherSet);
    boolean isSubsetOf(Iterable<? extends TElement> otherSet);
    boolean isDistinct();
}
