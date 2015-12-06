package com.empowerops.linqalike;

import com.empowerops.linqalike.common.BiQueryAdapter;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.BiCondition;
import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;
import com.empowerops.linqalike.delegate.Func3;

import java.util.Optional;

//re: 'need inner tuple types'
// without the ability to use a template <TLeftActual super TLeft>
// I cant statically handle this case, using java's generics system.
// but it is not possible to have heap pollution assuming the ctor for Tuple doesn't change the type.
// TODO: need to ask Tom or Nick for a formalization of this

//re: see note 'Cant reuse all those supers'
//? extends Tuple<? extends TLeft, ? extends TRight> cannot be captured and re-used,
// so there is no common ancestor for the two tuple types.
// but we've guaranteed that they are similar types by
// the signature of the BiQueryable (not the Iterable<Tuple...>)
// TODO this one also
public interface DefaultedBiQueryable<TLeft, TRight> extends BiQueryable<TLeft, TRight>{

    @Override
    default Queryable<TLeft> lefts(){
        return Linq.select(this, tuple -> tuple.left);
    }

    @Override
    default Queryable<TRight> rights(){
        return Linq.select(this, tuple -> tuple.right);
    }

    default <TAccumulate>
    TAccumulate aggregate(TAccumulate seed,
                          Func3<? super TAccumulate, ? super TLeft, ? super TRight, TAccumulate> aggregator){
        return Linq.aggregate(this, seed, (accum, nextTuple) -> aggregator.getFrom(accum, nextTuple.left, nextTuple.right));
    }

    default boolean all(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.all(this, condition.toConditionOnTuple());
    }

    default boolean any(){
        return Linq.any(this);
    }

    default boolean any(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.any(this, condition.toConditionOnTuple());
    }

    default double average(Func2<? super TLeft, ? super TRight, Double> valueSelector){
        return Linq.average(this, valueSelector.asFuncOnTuple());
    }

    default <TDesiredLeft> BiQueryable<TDesiredLeft, TRight> castLeft(){
        return (BiQueryable) this;
    }

    default <TDesiredRight> BiQueryable<TLeft, TDesiredRight> castRight(){
        return (BiQueryable) this;
    }

    default <TDesiredLeft> BiQueryable<TDesiredLeft, TRight> castLeft(Class<TDesiredLeft> desiredType){
        return Linq.castLeft(this, desiredType);
    }

    default <TDesiredRight> BiQueryable<TLeft, TDesiredRight> castRight(Class<TDesiredRight> desiredType){
        return Linq.castRight(this, desiredType);
    }

    default int count(){
        return Linq.count(this);
    }

    default int count(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.count(this, condition.toConditionOnTuple());
    }

    default BiQueryable<TLeft, TRight> distinct(){
        return new BiQueryAdapter.FromPairs<>(Linq.distinct(this));
    }

    default <TCompared>
    BiQueryable<TLeft, TRight> distinct(Func2<? super TLeft, ? super TRight, TCompared> comparableSelector){
        return new BiQueryAdapter.FromPairs<>(Linq.distinct(this, comparableSelector.asFuncOnTuple()));
    }

    //TODO docs, smartly.
    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude));
    }

    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude0, toExclude1));
    }

    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude0, toExclude1, toExclude2));
    }

    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2, Tuple<? extends TLeft, ? extends TRight> toExclude3){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude0, toExclude1, toExclude2, toExclude3));
    }

    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2, Tuple<? extends TLeft, ? extends TRight> toExclude3, Tuple<? extends TLeft, ? extends TRight> toExclude4){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude0, toExclude1, toExclude2, toExclude3, toExclude4));
    }

    default BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight>... toExclude){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude));
    }

    default BiQueryable<TLeft, TRight> except(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toExclude){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude));
    }

    default <TCompared> BiQueryable<TLeft, TRight> except(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toExclude,
                                                          Func2<? super TLeft, ? super TRight, TCompared> comparableSelector){
        return new BiQueryAdapter.FromPairs<>(Linq.except((Iterable)this, toExclude, comparableSelector.asFuncOnTuple()));
    }


    default Tuple<TLeft, TRight> first(){
        return Linq.first(this);
    }

    default Tuple<TLeft, TRight> first(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.first(this, condition.toConditionOnTuple());
    }

    default BiQueryable<TLeft, TRight> first(int count){
        return new BiQueryAdapter.FromPairs<>(Linq.first(this, count));
    }

    default Optional<Tuple<TLeft, TRight>> firstOrDefault(){
        return Linq.firstOrDefault(this);
    }

    default Optional<Tuple<TLeft, TRight>> firstOrDefault(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.firstOrDefault(this, condition.toConditionOnTuple());
    }

    default Tuple<TLeft, TRight> second(){
        return Linq.second(this);
    }

    default Tuple<TLeft, TRight> second(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.second(this, condition.toConditionOnTuple());
    }

    default Optional<Tuple<TLeft, TRight>> secondOrDefault(){
        return Linq.secondOrDefault(this);
    }

    default Optional<Tuple<TLeft, TRight>> secondOrDefault(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.secondOrDefault(this, condition.toConditionOnTuple());
    }

    default BiQueryable<TLeft, TRight> intersect(BiQueryable<? extends TLeft, ? extends TRight> toInclude){
        return new BiQueryAdapter.FromPairs<>(Linq.intersect((Iterable)this, toInclude));
    }

    default Tuple<TLeft, TRight> last(){
        return Linq.last(this);
    }

    default Tuple<TLeft, TRight> last(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.last(this, condition.toConditionOnTuple());
    }

    default BiQueryable<TLeft, TRight> last(int count){
        return new BiQueryAdapter.FromPairs<>(Linq.last(this, count));
    }

    default Optional<Tuple<TLeft, TRight>> lastOrDefault(){
        return Linq.lastOrDefault(this);
    }

    default Optional<Tuple<TLeft, TRight>> lastOrDefault(BiCondition<? super TLeft, ? super TRight> condition){
        return Linq.lastOrDefault(this, condition.toConditionOnTuple());
    }

    default <TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Func2<? super TLeft, ? super TRight, TCompared> valueSelector){
        return Linq.max(this, valueSelector.asFuncOnTuple());
    }

    default <TCompared extends Comparable<TCompared>>
    Optional<Tuple<TLeft, TRight>> withMax(Func2<? super TLeft, ? super TRight, TCompared> valueSelector){
        return Linq.withMax(this.asTuples(), valueSelector.asFuncOnTuple());
    }

    default <TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Func2<? super TLeft, ? super TRight, TCompared> valueSelector){
        return Linq.min(this.asTuples(), valueSelector.asFuncOnTuple());
    }

    default <TCompared extends Comparable<TCompared>>
    Optional<Tuple<TLeft, TRight>> withMin(Func2<? super TLeft, ? super TRight, TCompared> valueSelector){
        return Linq.withMin(this.asTuples(), valueSelector.asFuncOnTuple());
    }

    default <TSubclassLeft extends TLeft>
    BiQueryable<TSubclassLeft, TRight> ofLeftType(Class<TSubclassLeft> desiredLeftClass){
        return Linq.ofLeftType(this, desiredLeftClass);
    }

    default <TSubclassRight extends TRight>
    BiQueryable<TLeft, TSubclassRight> ofRightType(Class<TSubclassRight> desiredRightClass){
        return Linq.ofRightType(this, desiredRightClass);
    }

    default <TCompared extends Comparable<TCompared>>
    BiQueryable<TLeft, TRight> orderBy(Func2<? super TLeft, ? super TRight, TCompared> comparableSelector){
        return new BiQueryAdapter.FromPairs<>(Linq.orderBy(this.asTuples(), comparableSelector.asFuncOnTuple()));
    }

    default BiQueryable<TLeft, TRight> reversed(){
        return new BiQueryAdapter.FromPairs<>(Linq.reversed(this.asTuples()));
    }

    default <TTransformed>
    Queryable<TTransformed> select(Func2<? super TLeft, ? super TRight, TTransformed> transform){
        return Linq.select(this, transform.asFuncOnTuple());
    }

    default <TLeftTransformed, TRightTransformed>
    BiQueryable<TLeftTransformed, TRightTransformed> select(Func2<? super TLeft, ? super TRight, TLeftTransformed> leftTransform,
                                                            Func2<? super TLeft, ? super TRight, TRightTransformed> rightTransform){

        Func1<Tuple<TLeft, TRight>, Tuple<TLeftTransformed, TRightTransformed>> projector = tuple -> {
            TLeftTransformed projectedLeft = leftTransform.getFrom(tuple.left, tuple.right);
            TRightTransformed projectedRight = rightTransform.getFrom(tuple.left, tuple.right);
            return new Tuple<>(projectedLeft, projectedRight);
        };
        return new BiQueryAdapter.FromPairs<>(Linq.select(this.asTuples(), projector));
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default <TTransformed>
    Queryable<TTransformed> selectMany(Func2<? super TLeft, ? super TRight, ? extends Iterable<TTransformed>> selector){
        return Linq.selectMany(this.asTuples(), selector.asFuncOnTuple());
    }

    default Tuple<TLeft, TRight> single(){ return Linq.single(this.asTuples()); }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default Tuple<TLeft, TRight> single(BiCondition<? super TLeft, ? super TRight> uniqueConstraint){
        return Linq.single(this.asTuples(), uniqueConstraint.toConditionOnTuple());
    }

    default Optional<Tuple<TLeft, TRight>> singleOrDefault(){
        return Linq.singleOrDefault(this.asTuples());
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default Optional<Tuple<TLeft, TRight>> singleOrDefault(BiCondition<? super TLeft, ? super TRight> uniqueConstraint){
        return Linq.singleOrDefault(this.asTuples(), uniqueConstraint.toConditionOnTuple());
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean setEquals(BiQueryable<? extends TLeft, ? extends TRight> otherCollection){
        return Linq.setEquals((Queryable) this.asTuples(), otherCollection.asTuples());
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean sequenceEquals(BiQueryable<? extends TLeft, ? extends TRight> otherOrderedCollection){
        return Linq.sequenceEquals((Queryable) this.asTuples(), otherOrderedCollection.asTuples());
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default BiQueryable<TLeft, TRight> skipWhile(BiCondition<? super TLeft, ? super TRight> toExclude){
        return new BiQueryAdapter.FromPairs<>(Linq.skipWhile(this.asTuples(), toExclude.toConditionOnTuple()));
    }

    default BiQueryable<TLeft, TRight> skip(int numberToSkip){
        return new BiQueryAdapter.FromPairs<>(Linq.skip(this.asTuples(), numberToSkip));
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default double sum(Func2<? super TLeft, ? super TRight, Double> valueSelector){
        return Linq.sum(this.asTuples(), valueSelector.asFuncOnTuple());
    }

    default BiQueryable<TLeft, TRight> immediately(){ throw new UnsupportedOperationException(); }

    default LinqingMap<TLeft, TRight> toMap(){ throw new UnsupportedOperationException(); }

    default <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func2<? super TLeft, ? super TRight, TKey> keySelector,
                                                  Func2<? super TLeft, ? super TRight, TValue> valueSelector){ throw new UnsupportedOperationException(); }

    default <TDesired> TDesired[] toArray(TDesired[] typedArray){ throw new UnsupportedOperationException(); }

    default <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory){ throw new UnsupportedOperationException(); }

    default Object[] toArray(){ throw new UnsupportedOperationException(); }

    @Override
    default LinqingList<Tuple<TLeft, TRight>> toList(){
        return Linq.toList(this);
    }


    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude));
    }
    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude0, toInclude1));
    }
    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude0, toInclude1, toInclude2));
    }
    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude0, toInclude1, toInclude2, toInclude3));
    }
    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3, Tuple<? extends TLeft, ? extends TRight> toInclude4){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude0, toInclude1, toInclude2, toInclude3, toInclude4));
    }

    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight>... toInclude){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude));
    }
    /** {@inheritDoc} */ default @Override public
    BiQueryable<TLeft, TRight> union(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude){
        return new BiQueryAdapter.FromPairs<>(Linq.union((Iterable) this, toInclude));
    }
    /** {@inheritDoc} */ default @Override public <TCompared>
    BiQueryable<TLeft, TRight> union(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude,
                                     Func2<? super TLeft, ? super TRight, TCompared> comparableSelector){
        return new BiQueryAdapter.FromPairs<>(Linq.union(this, (Iterable) toInclude, comparableSelector.asFuncOnTuple()));
    }


    @Override
    default BiQueryable<TLeft, TRight> with(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude0, toInclude1));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude0, toInclude1, toInclude2));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude0, toInclude1, toInclude2, toInclude3));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3, Tuple<? extends TLeft, ? extends TRight> toInclude4) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude0, toInclude1, toInclude2, toInclude3, toInclude4));
    }

    @Override
    default BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight>... toInclude) {
        return new BiQueryAdapter.FromPairs<>(Linq.with((Iterable)this, toInclude));
    }

    default BiQueryable<TLeft, TRight> where(BiCondition<? super TLeft, ? super TRight> condition){
        return new BiQueryAdapter.FromPairs<>(Linq.where(this, condition.toConditionOnTuple()));
    }

    default int size(){
        return Linq.size(this);
    }

    default boolean isSingle(){ return Linq.isSingle(this); }

    default boolean isMany(){ return Linq.isMany(this); }

    default boolean isEmpty(){ return Linq.isEmpty(this); }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean isSubsetOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSuperset){
        return Linq.isSubsetOf(this, (Iterable) possibleSuperset);
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean isSupersetOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSubset){
        return Linq.isSupersetOf(this, (Iterable) possibleSubset);
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean isSubsequenceOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSupersequence){
        return Linq.isSubsequenceOf(this, (Iterable) possibleSupersequence);
    }

    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default boolean isSupersequenceOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSubsequence){
        return Linq.isSupersequenceOf(this, (Iterable) possibleSubsequence);
    }

    default boolean isDistinct(){
        return Linq.isDistinct(this);
    }
    @SuppressWarnings("unchecked") //see note 'Need inner tuple types'
    default <TCompared> boolean isDistinct(Func2<? super TLeft, ? super TRight, TCompared> equatableSelector){
        return Linq.isDistinct(this, equatableSelector.asFuncOnTuple());
    }

    default <TThird, TJoined>
    Queryable<TJoined> zip(Iterable<TThird> rightElements,
                           Func3<? super TLeft, ? super TRight, ? super TThird, TJoined> joinedElementFactory){
        return Linq.zip(this, rightElements, (tuple, right) -> joinedElementFactory.getFrom(tuple.left, tuple.right, right));
    }

    default Queryable<Tuple<TLeft, TRight>> asTuples(){
        return Factories.from(this);
    }
}
