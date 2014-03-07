package LinqALike;

import LinqALike.Delegate.*;

import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;

import static LinqALike.LinqBehaviour.first;

/**
 * @author Geoff on 06/09/13
 */
public interface Queryable<TElement> extends Iterable<TElement> {

    default TElement aggregate(Func2<TElement, TElement, TElement> aggregator){
        return LinqBehaviour.aggregate(this, aggregator);
    }
    default <TAccumulate> TAccumulate aggregate(TAccumulate seed,
                                                Func2<TAccumulate, TElement, TAccumulate> aggregator){
        return LinqBehaviour.aggregate(this, seed, aggregator);
    }


    default boolean all(Condition<? super TElement> condition){
        return LinqBehaviour.all(this, condition);
    }
    default boolean any(){
        return LinqBehaviour.any(this);
    }
    default boolean any(Condition<? super TElement> condition){
        return LinqBehaviour.any(this, condition);
    }


    default double average(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.average(this, valueSelector);
    }


    default <TDerived> Queryable<TDerived> cast(){
        return LinqBehaviour.cast(this);
    }


    default boolean contains(Object candidate){
        return LinqBehaviour.contains(this, candidate);
    }
    default boolean containsElement(TElement element){
        return LinqBehaviour.containsElement(this, element);
    }


    default int count(Condition<? super TElement> condition){
        return LinqBehaviour.count(this, condition);
    }


    default Queryable<TElement> distinct(){
        return LinqBehaviour.distinct(this);
    }
    default Queryable<TElement> distinct(Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return LinqBehaviour.distinct(this);
    }
    default <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector){
        return LinqBehaviour.distinct(this);
    }


    default Queryable<TElement> except(TElement... toExclude){
        return LinqBehaviour.except(this, toExclude);
    }
    default Queryable<TElement> except(Iterable<? extends TElement> toExclude){
        return LinqBehaviour.except(this, toExclude);
    }
    default Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                       Func2<? super TElement,? super TElement, Boolean> equalityComparison){
         return LinqBehaviour.except(this, toExclude, equalityComparison);
    }
    default <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector){
        return LinqBehaviour.except(this, toExclude, comparableSelector);
    }


    default TElement first(){
        return LinqBehaviour.first(this);
    }
    default TElement first(Condition<? super TElement> condition){
        return LinqBehaviour.first(this, condition);
    }
    default TElement firstOrDefault(){
        return LinqBehaviour.firstOrDefault(this);
    }
    default TElement firstOrDefault(Condition<? super TElement> condition){
        return LinqBehaviour.firstOrDefault(this, condition);
    }


    default <TComparable>
    Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> comparableSelector){
        return LinqBehaviour.groupBy(this, comparableSelector);
    }
    default Queryable<Queryable<TElement>> groupBy(Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return LinqBehaviour.groupBy(this, equalityComparison);
    }


    default <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector){
        return LinqBehaviour.intersect(this, toInclude, comparableSelector);
    }
    default Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                          Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return LinqBehaviour.intersect(this, toInclude, equalityComparison);
    }
    default Queryable<TElement> intersect(Iterable<? extends TElement> toInclude){
        return LinqBehaviour.intersect(this, toInclude);
    }
    default Queryable<TElement> intersect(TElement... toIntersect){
        return LinqBehaviour.intersect(this, toIntersect);
    }


    default <TRight, TResult>
    Queryable<TResult> join(Iterable<? extends TRight> right,
                            Func2<? super TElement, ? super TRight, TResult> makeResult){
        assert false : "this one is also a bit difficult, so I'm skipping it for now";
        return null;
    }


    default TElement last(){
        return LinqBehaviour.last(this);
    }
    default TElement last(Condition<? super TElement> condition){
        return LinqBehaviour.last(this, condition);
    }
    default TElement lastOrDefault(){
        return LinqBehaviour.lastOrDefault(this);
    }
    default TElement lastOrDefault(Condition<? super TElement> condition){
        return LinqBehaviour.lastOrDefault(this, condition);
    }


    default double max(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.max(this, valueSelector);
    }
    default TElement withMax(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.withMax(this, valueSelector);
    }
    default double min(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.min(this, valueSelector);
    }
    default TElement withMin(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.withMin(this, valueSelector);
    }


    default <TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass){
        return LinqBehaviour.ofType(this, desiredClass);
    }


    default <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector){
        return LinqBehaviour.orderBy(this, comparableSelector);
    }
    default Queryable<TElement> orderBy(Func2<? super TElement, ? super TElement, Integer> equalityComparator){
        return LinqBehaviour.orderBy(this, equalityComparator);
    }


    default Queryable<TElement> reversed(){
        return LinqBehaviour.reversed(this);
    }


    default <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return LinqBehaviour.select(this, selector);
    }
    default <TTransformed>
    LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return LinqBehaviour.selectMany(this, selector);
    }


    default TElement single(){
        return LinqBehaviour.single(this);
    }
    default TElement single(Condition<? super TElement> uniqueConstraint){
        return LinqBehaviour.single(this, uniqueConstraint);
    }
    default TElement singleOrDefault(){
        return LinqBehaviour.singleOrDefault(this);
    }
    default TElement singleOrDefault(Condition<? super TElement> uniqueConstraint){
        return LinqBehaviour.singleOrDefault(this, uniqueConstraint);
    }


    default Queryable<TElement> skipWhile(Condition<? super TElement> toExclude){
        return LinqBehaviour.skipWhile(this, toExclude);
    }
    default Queryable<TElement> skipUntil(Condition<? super TElement> toInclude){
        return LinqBehaviour.skipUntil(this, toInclude);
    }
    default Queryable<TElement> skip(int numberToSkip){
        return LinqBehaviour.skip(this, numberToSkip);
    }


    default double sum(Func1<? super TElement, Double> valueSelector){
        return LinqBehaviour.sum(this, valueSelector);
    }


    default ReadonlyLinqingList<TElement> toReadOnly(){
        return LinqBehaviour.toReadOnly(this);
    }
    default LinqingList<TElement> toList(){
        return LinqBehaviour.toList(this);
    }
    default LinqingSet<TElement> toSet(){
        return LinqBehaviour.toSet(this);
    }
    default Queryable<TElement> fetch(){
        return LinqBehaviour.fetch(this);
    }


    default <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys){
        return LinqBehaviour.toMap(keys, this);
    }
    default <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector){
        return LinqBehaviour.toMap(this, keySelector);
    }
    default <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                          Func1<? super TElement, TValue> valueSelector){
        return LinqBehaviour.toMap(this, keySelector, valueSelector);
    }


    default <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator){
        return LinqBehaviour.toArray(this, arrayTypeIndicator);
    }
    //note, we dont provide a method with a factory, if you need one, you should do .select(factory).toArray(Desired::new)
    default <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory){
        return LinqBehaviour.toArray(this, arrayFactory);
    }
    default Object[] toArray(){
        return LinqBehaviour.toArray(this);
    }

    default Queryable<TElement> union(TElement... elements){
        return LinqBehaviour.union(this, elements);
    }
    default Queryable<TElement> union(Iterable<? extends TElement> toInclude){
        return LinqBehaviour.union(this, toInclude);
    }
    default <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              Func1<? super TElement, TCompared> comparableSelector){
        return LinqBehaviour.union(this, toInclude, comparableSelector);
    }


    default Queryable<TElement> where(Condition<? super TElement> condition){
        return LinqBehaviour.where(this, condition);
    }


    default int size(){
        return LinqBehaviour.size(this);
    }


    default boolean isSingle(){
        return LinqBehaviour.isSingle(this);
    }
    default boolean isEmpty(){
        return LinqBehaviour.isEmpty(this);
    }
    default boolean isSetEquivalentOf(Iterable<? extends TElement> otherSet){
        return LinqBehaviour.isSetEquivalentOf(this, otherSet);
    }
    default boolean isSubsetOf(Iterable<? extends TElement> otherSet){
        return LinqBehaviour.isSubsetOf(this, otherSet);
    }
    default boolean isDistinct(){
        return LinqBehaviour.isDistinct(this);
    }
}
