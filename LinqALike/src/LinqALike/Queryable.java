package LinqALike;

import LinqALike.Common.QueryableGroupSet;
import LinqALike.Delegate.*;

import java.util.Collections;

import static LinqALike.Linq.first;

/**
 * @author Geoff on 06/09/13
 */
public interface Queryable<TElement> extends Iterable<TElement> {

    default TElement aggregate(Func2<TElement, TElement, TElement> aggregator){
        return Linq.aggregate(this, aggregator);
    }
    default <TAccumulate> TAccumulate aggregate(TAccumulate seed,
                                                Func2<TAccumulate, TElement, TAccumulate> aggregator){
        return Linq.aggregate(this, seed, aggregator);
    }

    default boolean all(Condition<? super TElement> condition){
        return Linq.all(this, condition);
    }
    default boolean any(){
        return Linq.any(this);
    }
    default boolean any(Condition<? super TElement> condition){
        return Linq.any(this, condition);
    }


    default double average(Func1<? super TElement, Double> valueSelector){
        return Linq.average(this, valueSelector);
    }


    default <TDerived> Queryable<TDerived> cast(){
        return Linq.cast(this);
    }


    default boolean contains(Object candidate){
        return Linq.contains(this, candidate);
    }
    default boolean containsElement(TElement element){
        return Linq.containsElement(this, element);
    }


    default int count(Condition<? super TElement> condition){
        return Linq.count(this, condition);
    }


    default Queryable<TElement> distinct(){
        return Linq.distinct(this);
    }
    default Queryable<TElement> distinct(Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return Linq.distinct(this);
    }
    default <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector){
        return Linq.distinct(this);
    }

    default Queryable<TElement> except(TElement... toExclude){
        return Linq.except(this, toExclude);
    }
    default Queryable<TElement> except(Iterable<? extends TElement> toExclude){
        return Linq.except(this, toExclude);
    }
    default Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                       Func2<? super TElement,? super TElement, Boolean> equalityComparison){
         return Linq.except(this, toExclude, equalityComparison);
    }
    default <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector){
        return Linq.except(this, toExclude, comparableSelector);
    }


    default TElement first(){
        return Linq.first(this);
    }
    default TElement first(Condition<? super TElement> condition){
        return Linq.first(this, condition);
    }
    default TElement firstOrDefault(){
        return Linq.firstOrDefault(this);
    }
    default TElement firstOrDefault(Condition<? super TElement> condition){
        return Linq.firstOrDefault(this, condition);
    }


    default <TComparable>
    QueryableGroupSet<TElement> groupBy(Func1<? super TElement, TComparable> comparableSelector){
        return Linq.groupBy(this, comparableSelector);
    }
    default Queryable<Queryable<TElement>> groupBy(Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return Linq.groupBy(this, equalityComparison);
    }


    default <TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector){
        return Linq.intersect(this, toInclude, comparableSelector);
    }
    default Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                          Func2<? super TElement, ? super TElement, Boolean> equalityComparison){
        return Linq.intersect(this, toInclude, equalityComparison);
    }
    default Queryable<TElement> intersect(Iterable<? extends TElement> toInclude){
        return Linq.intersect(this, toInclude);
    }
    default Queryable<TElement> intersect(TElement... toIntersect){
        return Linq.intersect(this, toIntersect);
    }


    default <TRight, TResult>
    Queryable<TResult> join(Iterable<? extends TRight> right,
                            Func2<? super TElement, ? super TRight, TResult> makeResult){
        assert false : "this one is also a bit difficult, so I'm skipping it for now";
        return null;
    }


    default TElement last(){
        return Linq.last(this);
    }
    default TElement last(Condition<? super TElement> condition){
        return Linq.last(this, condition);
    }
    default TElement lastOrDefault(){
        return Linq.lastOrDefault(this);
    }
    default TElement lastOrDefault(Condition<? super TElement> condition){
        return Linq.lastOrDefault(this, condition);
    }


    default double max(Func1<? super TElement, Double> valueSelector){
        return Linq.max(this, valueSelector);
    }
    default TElement withMax(Func1<? super TElement, Double> valueSelector){
        return Linq.withMax(this, valueSelector);
    }
    default double min(Func1<? super TElement, Double> valueSelector){
        return Linq.min(this, valueSelector);
    }
    default TElement withMin(Func1<? super TElement, Double> valueSelector){
        return Linq.withMin(this, valueSelector);
    }


    default <TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass){
        return Linq.ofType(this, desiredClass);
    }


    default <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector){

        LinqingList<TElement> ordered = this.toList();
        Collections.sort(ordered, (left, right) -> (comparableSelector.getFrom(left).compareTo(comparableSelector.getFrom(right))));

        return ordered;
    }
    default Queryable<TElement> orderBy(Func2<? super TElement, ? super TElement, Integer> equalityComparator){
        return Linq.orderBy(this, equalityComparator);
    }


    default Queryable<TElement> reversed(){
        return Linq.reversed(this);
    }


    default <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return Linq.select(this, selector);
    }
    default <TTransformed>
    LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return Linq.selectMany(this, selector);
    }


    default TElement single(){
        return Linq.single(this);
    }
    default TElement single(Condition<? super TElement> uniqueConstraint){
        return Linq.single(this, uniqueConstraint);
    }
    default TElement singleOrDefault(){
        return Linq.singleOrDefault(this);
    }
    default TElement singleOrDefault(Condition<? super TElement> uniqueConstraint){
        return Linq.singleOrDefault(this, uniqueConstraint);
    }


    default Queryable<TElement> skipWhile(Condition<? super TElement> toExclude){
        return Linq.skipWhile(this, toExclude);
    }
    default Queryable<TElement> skipUntil(Condition<? super TElement> toInclude){
        return Linq.skipUntil(this, toInclude);
    }
    default Queryable<TElement> skip(int numberToSkip){
        return Linq.skip(this, numberToSkip);
    }


    default double sum(Func1<? super TElement, Double> valueSelector){
        return Linq.sum(this, valueSelector);
    }


    default ReadonlyLinqingList<TElement> toReadOnly(){
        return Linq.toReadOnly(this);
    }
    default LinqingList<TElement> toList(){
        return Linq.toList(this);
    }
    default LinqingSet<TElement> toSet(){
        return Linq.toSet(this);
    }
    default Queryable<TElement> fetch(){
        return Linq.fetch(this);
    }


    default <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys){
        return Linq.toMap(keys, this);
    }
    default <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector){
        return Linq.toMap(this, keySelector);
    }
    default <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                          Func1<? super TElement, TValue> valueSelector){
        return Linq.toMap(this, keySelector, valueSelector);
    }


    default <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator){
        return Linq.toArray(this, arrayTypeIndicator);
    }
    //note, we dont provide a method with a factory, if you need one, you should do .select(factory).toArray(Desired::new)
    default <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory){
        return Linq.toArray(this, arrayFactory);
    }
    default Object[] toArray(){
        return Linq.toArray(this);
    }

    default Queryable<TElement> union(TElement... elements){
        return Linq.union(this, elements);
    }
    default Queryable<TElement> union(Iterable<? extends TElement> toInclude){
        return Linq.union(this, toInclude);
    }
    default <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              Func1<? super TElement, TCompared> comparableSelector){
        return Linq.union(this, toInclude, comparableSelector);
    }


    default Queryable<TElement> where(Condition<? super TElement> condition){
        return Linq.where(this, condition);
    }


    default int size(){
        return Linq.size(this);
    }


    default boolean isSingle(){
        return Linq.isSingle(this);
    }
    default boolean isEmpty(){
        return Linq.isEmpty(this);
    }
    default boolean isSetEquivalentOf(Iterable<? extends TElement> otherSet){
        return Linq.isSetEquivalentOf(this, otherSet);
    }
    default boolean isSubsetOf(Iterable<? extends TElement> otherSet){
        return Linq.isSubsetOf(this, otherSet);
    }
    default boolean isDistinct(){
        return Linq.isDistinct(this);
    }
}
