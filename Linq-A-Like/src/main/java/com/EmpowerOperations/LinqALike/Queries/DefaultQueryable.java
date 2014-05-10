package com.EmpowerOperations.LinqALike.Queries;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func1;
import com.EmpowerOperations.LinqALike.*;

import java.util.Comparator;


/**
 * An <i>implementation</i> (by way of default extension methods) to the {@link com.EmpowerOperations.LinqALike.Queryable} interface.
 * For a concise list of what methods this interface offers and documentation on each of the methods,
 * please inspect that interface.
 *
 * <p>All calls are forwarded to their corresponding lifted and widened methods in {@link com.EmpowerOperations.LinqALike.Linq}.</p>
 *
 * @author Geoff on 06/09/13
 */
public interface DefaultQueryable<TElement> extends Queryable<TElement> {

    @Override default public boolean all(Condition<? super TElement> condition){
        return Linq.all(this, condition);
    }
    @Override default public boolean any(){
        return Linq.any(this);
    }
    @Override default public boolean any(Condition<? super TElement> condition){
        return Linq.any(this, condition);
    }


    @Override default public double average(Func1<? super TElement, Double> valueSelector){
        return Linq.average(this, valueSelector);
    }


    @Override default public <TDerived> Queryable<TDerived> uncheckedCast(){
        return Linq.cast(this);
    }
    @Override default public <TDerived> Queryable<TDerived> cast(Class<TDerived> desiredType){
        return Linq.cast(this, desiredType);
    }


    @Override default public boolean contains(Object candidate){
        return Linq.contains(this, candidate);
    }
    @Override default public boolean containsElement(TElement candidate){
        return Linq.containsElement(this, candidate);
    }


    @Override default public int count(Condition<? super TElement> condition){
        return Linq.count(this, condition);
    }


    @Override default public Queryable<TElement> distinct(){
        return Linq.distinct(this);
    }
    @Override default public Queryable<TElement> distinct(EqualityComparer<? super TElement> equalityComparison){
        return Linq.distinct(this, equalityComparison);
    }
    @Override default public <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector){
        return Linq.distinct(this, comparableSelector);
    }

    @Override default public Queryable<TElement> except(TElement... toExclude){
        return Linq.except(this, toExclude);
    }
    @Override default public Queryable<TElement> except(Iterable<? extends TElement> toExclude){
        return Linq.except(this, toExclude);
    }
    @Override default public Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                                        EqualityComparer<? super TElement> comparator){
        return Linq.except(this, toExclude, comparator);
    }
    @Override default public <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector){
        return Linq.except(this, toExclude, comparableSelector);
    }


    @Override default public TElement first(){
        return Linq.first(this);
    }
    @Override default public TElement first(Condition<? super TElement> condition){
        return Linq.first(this, condition);
    }
    @Override default public TElement firstOrDefault(){
        return Linq.firstOrDefault(this);
    }
    @Override default public TElement firstOrDefault(Condition<? super TElement> condition){
        return Linq.firstOrDefault(this, condition);
    }


    @Override default public <TComparable>
    Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> comparableSelector){
        return Linq.groupBy(this, comparableSelector);
    }
    @Override default public Queryable<Queryable<TElement>> groupBy(EqualityComparer<? super TElement> equalityComparison){
        return Linq.groupBy(this, equalityComparison);
    }


    @Override default public <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector){
        return Linq.intersect(this, toInclude, comparableSelector);
    }
    @Override default public Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                                           EqualityComparer<? super TElement> equalityComparison){
        return Linq.intersect(this, toInclude, equalityComparison);
    }
    @Override default public Queryable<TElement> intersect(Iterable<? extends TElement> toInclude){
        return Linq.intersect(this, toInclude);
    }
    @Override default public Queryable<TElement> intersect(TElement... toIntersect){
        return Linq.intersect(this, toIntersect);
    }

    @Override default public TElement last(){
        return Linq.last(this);
    }
    @Override default public TElement last(Condition<? super TElement> condition){
        return Linq.last(this, condition);
    }
    @Override default public TElement lastOrDefault(){
        return Linq.lastOrDefault(this);
    }
    @Override default public TElement lastOrDefault(Condition<? super TElement> condition){
        return Linq.lastOrDefault(this, condition);
    }


    @Override default public double max(Func1<? super TElement, Double> valueSelector){
        return Linq.max(this, valueSelector);
    }
    @Override default public TElement withMax(Func1<? super TElement, Double> valueSelector){
        return Linq.withMax(this, valueSelector);
    }
    @Override default public double min(Func1<? super TElement, Double> valueSelector){
        return Linq.min(this, valueSelector);
    }
    @Override default public TElement withMin(Func1<? super TElement, Double> valueSelector){
        return Linq.withMin(this, valueSelector);
    }


    @Override default public <TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass){
        return Linq.ofType(this, desiredClass);
    }


    @Override default public <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector){
        return Linq.orderBy(this, comparableSelector);
    }
    @Override default public Queryable<TElement> orderBy(Comparator<? super TElement> equalityComparator){
        return Linq.orderBy(this, equalityComparator);
    }


    @Override default public Queryable<TElement> reversed(){
        return Linq.reversed(this);
    }


    @Override default public <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return Linq.select(this, selector);
    }
    @Override default public <TTransformed>
    Queryable<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return Linq.selectMany(this, selector);
    }


    @Override default public TElement single(){
        return Linq.single(this);
    }
    @Override default public TElement single(Condition<? super TElement> uniqueConstraint){
        return Linq.single(this, uniqueConstraint);
    }
    @Override default public TElement singleOrDefault(){
        return Linq.singleOrDefault(this);
    }
    @Override default public TElement singleOrDefault(Condition<? super TElement> uniqueConstraint){
        return Linq.singleOrDefault(this, uniqueConstraint);
    }


    @Override default public Queryable<TElement> skipWhile(Condition<? super TElement> toExclude){
        return Linq.skipWhile(this, toExclude);
    }
    @Override default public Queryable<TElement> skip(int numberToSkip){
        return Linq.skip(this, numberToSkip);
    }


    @Override default public double sum(Func1<? super TElement, Double> valueSelector){
        return Linq.sum(this, valueSelector);
    }


    @Override default public ReadonlyLinqingList<TElement> toReadOnly(){
        return Linq.toReadOnly(this);
    }
    @Override default public LinqingList<TElement> toList(){
        return Linq.toList(this);
    }
    @Override default public LinqingSet<TElement> toSet(){
        return Linq.toSet(this);
    }
    @Override default public Queryable<TElement> fetch(){
        return Linq.fetch(this);
    }


    @Override default public <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys){
        return Linq.toMap(keys, this);
    }
    @Override default public <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector){
        return Linq.toMap(this, keySelector);
    }
    @Override default public <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                                   Func1<? super TElement, TValue> valueSelector){
        return Linq.toMap(this, keySelector, valueSelector);
    }


    @Override default public <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator){
        return Linq.toArray(this, arrayTypeIndicator);
    }
    //note, we dont provide a method with a factory, if you need one, you should do .select(factory).toArray(Desired::new)
    @Override default public <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory){
        return Linq.toArray(this, arrayFactory);
    }
    @Override default public Object[] toArray(){
        return Linq.toArray(this);
    }

    @Override default public Queryable<TElement> union(TElement... elements){
        return Linq.union(this, elements);
    }
    @Override default public Queryable<TElement> union(Iterable<? extends TElement> toInclude){
        return Linq.union(this, toInclude);
    }
    @Override default public <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              Func1<? super TElement, TCompared> comparableSelector){
        return Linq.union(this, toInclude, comparableSelector);
    }
    @Override default public
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              EqualityComparer<? super TElement> equalityComparator){
        return Linq.union(this, toInclude, equalityComparator);
    }


    @Override default public Queryable<TElement> where(Condition<? super TElement> condition){
        return Linq.where(this, condition);
    }


    @Override default public int size(){
        return Linq.size(this);
    }


    @Override default public boolean isSingle(){
        return Linq.isSingle(this);
    }
    @Override default public boolean isEmpty(){
        return Linq.isEmpty(this);
    }
    @Override default public boolean isSetEquivalentOf(Iterable<? extends TElement> otherSet){
        return Linq.isSetEquivalentOf(this, otherSet);
    }
    @Override default public boolean isSubsetOf(Iterable<? extends TElement> otherSet){
        return Linq.isSubsetOf(this, otherSet);
    }
    @Override default public boolean isDistinct(){
        return Linq.isDistinct(this);
    }
}
