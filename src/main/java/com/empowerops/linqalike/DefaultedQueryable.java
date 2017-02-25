package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.delegate.*;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Optional;

/**
 * An <i>implementation</i> (by way of default extension methods) to the {@link com.empowerops.linqalike.Queryable} interface.
 * For a concise list of what methods this interface offers and documentation on each of the methods,
 * please inspect that interface.
 *
 * <p>All calls are forwarded to their corresponding lifted and widened methods in {@link com.empowerops.linqalike.Linq}.
 *
 * @author Geoff on 06/09/13
 */
public interface DefaultedQueryable<TElement> extends Queryable<TElement> {

    /** {@inheritDoc} */ @Override default public
    Optional<TElement> aggregate(Func2<? super TElement, ? super TElement, ? extends TElement> aggregator){
        return Linq.aggregate(this, aggregator);
    }

    /** {@inheritDoc} */ @Override default public <TAccumulate>
    TAccumulate aggregate(TAccumulate seed, Func2<? super TAccumulate, ? super TElement, TAccumulate> aggregator){
        return Linq.aggregate(this, seed, aggregator);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> inlineForEach(Action1<? super TElement> sideEffectTransform) {
        return Linq.inlineForEach(this, sideEffectTransform);
    }

    /** {@inheritDoc} */ @Override default public
    boolean all(Condition<? super TElement> condition){
        return Linq.all(this, condition);
    }
    /** {@inheritDoc} */ @Override default public
    boolean any(){
        return Linq.any(this);
    }
    /** {@inheritDoc} */ @Override default public
    boolean any(Condition<? super TElement> condition){
        return Linq.any(this, condition);
    }


    /** {@inheritDoc} */ @Override default public
	double average(Func1<? super TElement, Double> valueSelector){
        return Linq.average(this, valueSelector);
    }

    /** {@inheritDoc} */ @Override default public <TDerived>
    Queryable<TDerived> cast(){
        return Linq.cast(this);
    }
    /** {@inheritDoc} */ @Override default public <TDerived>
    Queryable<TDerived> cast(Class<TDerived> desiredType){
        return Linq.cast(this, desiredType);
    }


    /** {@inheritDoc} */ @Override default public
	boolean containsElement(TElement candidate){
        return Linq.containsElement(this, candidate);
    }

    /** {@inheritDoc} */ @Override default boolean containsElement(TElement candidateElement,
                                              EqualityComparer<? super TElement> equalityComparer){
        return Linq.containsElement(this, candidateElement, equalityComparer);
    }

    /** {@inheritDoc} */ @Override default public
    int count(){
        return size();
    }

    /** {@inheritDoc} */ @Override default public
	int count(Condition<? super TElement> condition){
        return Linq.count(this, condition);
    }


    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> distinct(){
        return Linq.distinct(this);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> distinct(EqualityComparer<? super TElement> equalityComparison){
        return Linq.distinct(this, equalityComparison);
    }
    /** {@inheritDoc} */ @Override default public <TComparable>
    Queryable<TElement> distinct(Func1<? super TElement, TComparable> comparableSelector){
        return Linq.distinct(this, comparableSelector);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> except(TElement toExclude){
        return Linq.except(this, toExclude);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> except(TElement toExclude0, TElement toExclude1){
        return Linq.except(this, toExclude0, toExclude1);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2){
        return Linq.except(this, toExclude0, toExclude1, toExclude2);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2, TElement toExclude3){
        return Linq.except(this, toExclude0, toExclude1, toExclude2, toExclude3);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2, TElement toExclude3, TElement toExclude4){
        return Linq.except(this, toExclude0, toExclude1, toExclude2, toExclude3, toExclude4);
    }

    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> except(TElement... toExclude){
        return Linq.except(this, toExclude);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> except(Iterable<? extends TElement> toExclude){
        return Linq.except(this, toExclude);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                                        EqualityComparer<? super TElement> comparator){
        return Linq.except(this, toExclude, comparator);
    }
    /** {@inheritDoc} */ @Override default public <TCompared>
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               Func1<? super TElement, TCompared> comparableSelector){
        return Linq.except(this, toExclude, comparableSelector);
    }


    /** {@inheritDoc} */ @Override default public
	TElement first(){
        return Linq.first(this);
    }
    /** {@inheritDoc} */ @Override default public
	TElement first(Condition<? super TElement> condition){
        return Linq.first(this, condition);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> first(int count){
        return Linq.first(this, count);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> firstOrDefault(){
        return Linq.firstOrDefault(this);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> firstOrDefault(Condition<? super TElement> condition){
        return Linq.firstOrDefault(this, condition);
    }

    /** {@inheritDoc} */ @Override default public
    TElement second(){
        return Linq.second(this);
    }
    /** {@inheritDoc} */ @Override default public
    TElement second(Condition<? super TElement> condition){
        return Linq.second(this, condition);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> secondOrDefault(){
        return Linq.secondOrDefault(this);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> secondOrDefault(Condition<? super TElement> condition){
        return Linq.secondOrDefault(this, condition);
    }


    /** {@inheritDoc} */ @Override default public <TComparable>
    Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> equatableSelector){
        return Linq.groupBy(this, equatableSelector);
    }

    /** {@inheritDoc} */ @Override default public <TComparable>
    Queryable<Queryable<TElement>> groupByIndexed(Func2<? super TElement, Integer, TComparable> equatableSelector) {
        return Linq.groupByIndexed(this, equatableSelector);
    }

    /** {@inheritDoc} */ @Override default public
	Queryable<Queryable<TElement>> groupBy(EqualityComparer<? super TElement> equalityComparison){
        return Linq.groupBy(this, equalityComparison);
    }


    /** {@inheritDoc} */ @Override default public
	<TCompared>
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  Func1<? super TElement, TCompared> comparableSelector){
        return Linq.intersect(this, toInclude, comparableSelector);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                                           EqualityComparer<? super TElement> equalityComparison){
        return Linq.intersect(this, toInclude, equalityComparison);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> intersect(Iterable<? extends TElement> toInclude){
        return Linq.intersect(this, toInclude);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> intersect(TElement... toInclude){
        return Linq.intersect(this, toInclude);
    }

    /** {@inheritDoc} */ @Override default public
	TElement last(){
        return Linq.last(this);
    }
    /** {@inheritDoc} */ @Override default public
	TElement last(Condition<? super TElement> condition){
        return Linq.last(this, condition);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> last(int count){
        return Linq.last(this, count);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> lastOrDefault(){
        return Linq.lastOrDefault(this);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> lastOrDefault(Condition<? super TElement> condition){
        return Linq.lastOrDefault(this, condition);
    }

    /** {@inheritDoc} */ @Override default public <TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Func1<? super TElement, TCompared> valueSelector){
        return Linq.max(this, valueSelector);
    }
    /** {@inheritDoc} */ @Override default public <TCompared extends Comparable<TCompared>>
    Optional<TElement> withMax(Func1<? super TElement, TCompared> valueSelector){
        return Linq.withMax(this, valueSelector);
    }
    /** {@inheritDoc} */ @Override default public <TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Func1<? super TElement, TCompared> valueSelector){
        return Linq.min(this, valueSelector);
    }
    /** {@inheritDoc} */ @Override default public <TCompared extends Comparable<TCompared>>
    Optional<TElement> withMin(Func1<? super TElement, TCompared> valueSelector){
        return Linq.withMin(this, valueSelector);
    }

    /** {@inheritDoc} */ @Override default public
	<TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass){
        return Linq.ofType(this, desiredClass);
    }


    /** {@inheritDoc} */ @Override default public
	<TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector){
        return Linq.orderBy(this, comparableSelector);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> orderBy(Comparator<? super TElement> equalityComparator){
        return Linq.orderBy(this, equalityComparator);
    }


    /** {@inheritDoc} */ @Override default public
	BiQueryable<TElement, TElement> pairwise(){
        return Linq.pairwise(this);
    }
    /** {@inheritDoc} */ @Override default public
	BiQueryable<TElement, TElement> pairwise(Func<? extends TElement> defaultFactory){
        return Linq.pairwise(this, defaultFactory);
    }


    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> reversed(){
        return Linq.reversed(this);
    }


    /** {@inheritDoc} */ @Override default public
	<TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return Linq.select(this, selector);
    }
    /** {@inheritDoc} */ @Override default public
	<TTransformed>
    Queryable<TTransformed> selectIndexed(Func2<? super TElement, Integer, TTransformed> selector){
        return Linq.selectIndexed(this, selector);
    }

    /** {@inheritDoc} */ @Override default public
    <TTransformed> BiQueryable<TElement, TTransformed> pushSelect(Func1<? super TElement, TTransformed> selector){
        return Linq.pushSelect(this, selector);
    }

    /** {@inheritDoc} */ @Override default public
	<TTransformed>
    Queryable<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return Linq.selectMany(this, selector);
    }

    /** {@inheritDoc} */ @Override default public
    <TTransformed>
    BiQueryable<TElement, TTransformed> pushSelectMany(Func1<? super TElement, ? extends Iterable<? extends TTransformed>> selector){
        return Linq.pushSelectMany(this, selector);
    }

    /** {@inheritDoc} */ @Override default public
	TElement single(){
        return Linq.single(this);
    }
    /** {@inheritDoc} */ @Override default public
	TElement single(Condition<? super TElement> uniqueConstraint){
        return Linq.single(this, uniqueConstraint);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> singleOrDefault(){
        return Linq.singleOrDefault(this);
    }
    /** {@inheritDoc} */ @Override default public
    Optional<TElement> singleOrDefault(Condition<? super TElement> uniqueConstraint){
        return Linq.singleOrDefault(this, uniqueConstraint);
    }

    /** {@inheritDoc} */ @Override default public
	boolean setEquals(Iterable<? extends TElement> otherCollection){
        return Linq.setEquals(this, otherCollection);
    }
    /** {@inheritDoc} */ @Override default public <TCompared>
    boolean setEquals(Iterable<? extends TElement> otherCollection,
                      Func1<? super TElement, TCompared> equatableSelector){
        return Linq.setEquals(this, otherCollection, equatableSelector);
    }
    /** {@inheritDoc} */ @Override default public
	boolean setEquals(Iterable<? extends TElement> otherCollection,
                                               EqualityComparer<? super TElement> equalityComparer){
        return Linq.setEquals(this, otherCollection, equalityComparer);
    }
    /** {@inheritDoc} */ @Override default boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection){
        return Linq.sequenceEquals(this, otherOrderedCollection);
    }
    /** {@inheritDoc} */ @Override default public <TCompared>
    boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection,
                           Func1<? super TElement, TCompared> comparableSelector){
        return Linq.sequenceEquals(this, otherOrderedCollection, comparableSelector);
    }
    /** {@inheritDoc} */ @Override default public
	boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection,
                                                    EqualityComparer<? super TElement> equalityComparer){
        return Linq.sequenceEquals(this, otherOrderedCollection, equalityComparer);
    }


    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> skipWhile(Condition<? super TElement> toExclude){
        return Linq.skipWhile(this, toExclude);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> skip(int numberToSkip){
        return Linq.skip(this, numberToSkip);
    }


    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> take(int count) {
        return Linq.take(this, count);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> takeWhile(Condition<? super TElement> condition) {
        return Linq.takeWhile(this, condition);
    }

    /** {@inheritDoc} */ @Override default public
	double sum(Func1<? super TElement, Double> valueSelector){
        return Linq.sum(this, valueSelector);
    }


    /** {@inheritDoc} */ @Override default public
	ReadonlyLinqingList<TElement> toReadOnly(){
        return Linq.toReadOnly(this);
    }

    /** {@inheritDoc} */ @Override default public
    ReadonlyLinqingSet<TElement> toReadOnlySet(){
        return Linq.toReadonlySet(this);
    }

    /** {@inheritDoc} */ @Override default public
	LinqingList<TElement> toList(){
        return Linq.toList(this);
    }
    /** {@inheritDoc} */ @Override default public
    InputStream toInputStream(Func1<? super TElement, Integer> converter){
        return Linq.toInputStream(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
	LinqingSet<TElement> toSet(){
        return Linq.toSet(this);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> immediately(){
        return Linq.immediately(this);
    }


    /** {@inheritDoc} */ @Override default public <TKey>
    LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys){
        return Linq.toMap(keys, this);
    }
    /** {@inheritDoc} */ @Override default public <TKey>
    LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector){
        return Linq.toMap(this, keySelector);
    }
    /** {@inheritDoc} */ @Override default public <TKey, TValue>
    LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                                   Func1<? super TElement, TValue> valueSelector){
        return Linq.toMap(this, keySelector, valueSelector);
    }


    /** {@inheritDoc} */ @Override default public <TDesired>
    TDesired[] toArray(TDesired[] typedArray){
        return Linq.toArray(this, typedArray);
    }
    /** {@inheritDoc} */ @Override default public <TDesired>
    TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory){
        return Linq.toArray(this, arrayFactory);
    }
    /** {@inheritDoc} */ @Override default public
	Object[] toArray(){
        return Linq.toArray(this);
    }
    /** {@inheritDoc} */ @Override default public
    boolean[] toBooleanArray(Func1<? super TElement, Boolean> converter){
        return Linq.toBooleanArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
    byte[] toByteArray(Func1<? super TElement, Byte> converter){
        return Linq.toByteArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
    char[] toCharArray(Func1<? super TElement, Character> converter){
        return Linq.toCharArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
    short[] toShortArray(Func1<? super TElement, Short> converter){
        return Linq.toShortArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
	int[] toIntArray(Func1<? super TElement, Integer> converter){
        return Linq.toIntArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
	long[] toLongArray(Func1<? super TElement, Long> converter){
        return Linq.toLongArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
	float[] toFloatArray(Func1<? super TElement, Float> converter){
        return Linq.toFloatArray(this, converter);
    }
    /** {@inheritDoc} */ @Override default public
	double[] toDoubleArray(Func1<? super TElement, Double> converter){
        return Linq.toDoubleArray(this, converter);
    }

    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(TElement toInclude){
        return Linq.union(this, toInclude);
    }
    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(TElement toInclude0, TElement toInclude1){
        return Linq.union(this, toInclude0, toInclude1);
    }
    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2){
        return Linq.union(this, toInclude0, toInclude1, toInclude2);
    }
    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3){
        return Linq.union(this, toInclude0, toInclude1, toInclude2, toInclude3);
    }
    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3, TElement toInclude4){
        return Linq.union(this, toInclude0, toInclude1, toInclude2, toInclude3, toInclude4);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override default public
	Queryable<TElement> union(TElement... toInclude){
        return Linq.union(this, toInclude);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> union(Iterable<? extends TElement> toInclude){
        return Linq.union(this, toInclude);
    }
    /** {@inheritDoc} */ @Override default public <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              Func1<? super TElement, TCompared> comparableSelector){
        return Linq.union(this, toInclude, comparableSelector);
    }
    /** {@inheritDoc} */ @Override default public
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              EqualityComparer<? super TElement> equalityComparator){
        return Linq.union(this, toInclude, equalityComparator);
    }



    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> where(Condition<? super TElement> condition){
        return Linq.where(this, condition);
    }
    /** {@inheritDoc} */ @Override default public
	Queryable<TElement> whereIndexed(BiCondition<? super TElement, Integer> condition){
        return Linq.whereIndexed(this, condition);
    }


    @Override
    default Queryable<TElement> with(TElement toInclude) {
        return Linq.with(this, toInclude);
    }

    @Override
    default Queryable<TElement> with(TElement toInclude0, TElement toInclude1) {
        return Linq.with(this, toInclude0, toInclude1);
    }

    @Override
    default Queryable<TElement> with(TElement toInclude0, TElement toInclude1, TElement toInclude2) {
        return Linq.with(this, toInclude0, toInclude1, toInclude2);
    }

    @Override
    default Queryable<TElement> with(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3) {
        return Linq.with(this, toInclude0, toInclude1, toInclude2, toInclude3);
    }

    @Override
    default Queryable<TElement> with(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3, TElement toInclude4) {
        return Linq.with(this, toInclude0, toInclude1, toInclude2, toInclude3, toInclude4);
    }

    @Override
    default Queryable<TElement> with(TElement... toInclude) {
        return Linq.with(this, toInclude);
    }

    @Override
    default Queryable<TElement> with(Iterable<? extends TElement> toInclude) {
        return Linq.with(this, toInclude);
    }


    /** {@inheritDoc} */ @Override default public
	int size(){
        return Linq.size(this);
    }


    /** {@inheritDoc} */ @Override default public
	boolean isSingle(){
        return Linq.isSingle(this);
    }
    /** {@inheritDoc} */ @Override default public
	boolean isMany(){
        return Linq.isMany(this);
    }
    /** {@inheritDoc} */ @Override default public
	boolean isEmpty(){
        return Linq.isEmpty(this);
    }
    /** {@inheritDoc} */ @Override default public

	boolean isSubsetOf(Iterable<? extends TElement> possibleSuperset){
        return Linq.isSubsetOf(this, possibleSuperset);
    }
    /** {@inheritDoc} */ @Override default public
    boolean isSupersetOf(Iterable<? extends TElement> possibleSubset){
        return Linq.isSupersetOf(this, possibleSubset);
    }
    /** {@inheritDoc} */ @Override default public
    boolean isSubsequenceOf(Iterable<? extends TElement> possibleSupersequence){
        return Linq.isSubsequenceOf(this, possibleSupersequence);
    }
    /** {@inheritDoc} */ @Override default public
    boolean isSubsequenceOf(Iterable<? extends TElement> possibleSupersequence,
                            EqualityComparer<? super TElement> equalityComparer){
        return Linq.isSubsetOf(this, possibleSupersequence, equalityComparer);
    }
    /** {@inheritDoc} */ @Override default public
    boolean isSupersequenceOf(Iterable<? extends TElement> possibleSubsequence){
        return Linq.isSupersequenceOf(this, possibleSubsequence);
    }

    /** {@inheritDoc} */ @Override default public
	boolean isDistinct(){
        return Linq.isDistinct(this);
    }
    /** {@inheritDoc} */ @Override default public <TCompared>
    boolean isDistinct(Func1<? super TElement, TCompared> equatableSelector){
        return Linq.isDistinct(this, equatableSelector);
    }
    /** {@inheritDoc} */ @Override default public
	boolean isDistinct(EqualityComparer<? super TElement> equalityComparer){
        return Linq.isDistinct(this, equalityComparer);
    }

    /** {@inheritDoc} */ @Override default public <TRight, TJoined>
    Queryable<TJoined> zip(Iterable<TRight> rightElements,
                           Func2<? super TElement, ? super TRight, TJoined> joinedElementFactory){
        return Linq.zip(this, rightElements, joinedElementFactory);
    }

    /** {@inheritDoc} */ @Override default public <TRight>
    BiQueryable<TElement, TRight> zip(Iterable<TRight> rightElements){
        return Linq.zip(this, rightElements);
    }

    /** {@inheritDoc} */ @Override default public <TRight>
    void forEachWith(Iterable<TRight> rightElements, Action2<? super TElement, ? super TRight> tupleConsumer){
        Linq.forEachWith(this, rightElements, tupleConsumer);
    }

}
