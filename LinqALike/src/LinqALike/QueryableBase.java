package LinqALike;

import LinqALike.Delegate.Action1;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import java.util.Arrays;
import java.util.Iterator;

import static LinqALike.LinqingList.from;
/**
 * @author Geoff on 16/09/13
 */
public abstract class QueryableBase<TElement> implements Queryable<TElement> {
    @Override
    public Queryable<TElement> fetch() {
        return LinqBehaviour.toList(this);
    }

    @Override
    public TElement firstOr(TElement alternative) {
        return LinqBehaviour.firstOr(this, alternative);
    }

    @Override
    public <TRight, TResult> Queryable<TResult> join(Iterable<TRight> right, Func2<TElement, TRight, TResult> makeResult) {
        return LinqBehaviour.join(this, right, makeResult);
    }

    @Override
    public boolean isEmpty() {
        return LinqBehaviour.isEmpty(this);
    }

    @Override
    public int count(Condition<? super TElement> condition) {
        return LinqBehaviour.where(this, condition).size();
    }

    @Override
    public <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return LinqBehaviour.select(this, selector);
    }

    @Override
    public Queryable<TElement> where(Condition<? super TElement> condition){
        return LinqBehaviour.where(this, condition);
    }

    @Override
    public <TDesired extends TElement>
    LinqingList<TDesired> ofType(Class<TDesired> desiredClass) {
        return LinqBehaviour.ofType(this, desiredClass);
    }

    @Override
    public Queryable<TElement> reversed() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LinqingList<TElement> toList() {
        return new LinqingList<>(this);
    }

    @Override
    public <TKey> LinqingMap<TKey, TElement> toMap(Func1<TElement, TKey> keySelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object[] toArray() {
        return LinqBehaviour.toArray(this);
    }

    @Override
    public <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator) {
        return LinqBehaviour.toArray(this, arrayTypeIndicator);
    }

    @Override
    public TElement single(){
        return LinqBehaviour.single(this);
    }

    @Override
    public TElement single(Condition<? super TElement> uniqueConstraint){
        return LinqBehaviour.single(this, uniqueConstraint);
    }

    @Override
    public TElement singleOrDefault(){
        return LinqBehaviour.singleOrDefault(this);
    }

    @Override
    public TElement singleOrDefault(Condition<? super TElement> uniqueConstraint){
        return LinqBehaviour.singleOrDefault(this, uniqueConstraint);
    }

    @Override
    public TElement first() {
        return LinqBehaviour.first(this, CommonDelegates.Tautology);
    }

    @Override
    public TElement first(Condition<? super TElement> condition){
        return LinqBehaviour.first(this, condition);
    }

    @Override
    public TElement firstOrDefault(){
        return LinqBehaviour.firstOrDefault(this, CommonDelegates.Tautology);
    }

    @Override
    public TElement firstOrDefault(Condition<? super TElement> condition){
        return LinqBehaviour.firstOrDefault(this, condition);
    }

    @Override
    public boolean isSingle() {
        Iterator<TElement> iterator = iterator();
        boolean hasFirst = iterator.hasNext();
        if( ! hasFirst){
            return false;
        }
        iterator.next();
        boolean hasSecond = iterator.hasNext();

        return hasFirst && ! hasSecond;
    }

    @Override
    public boolean containsSingle(Condition<? super TElement> condition) {
        return size() == 1;
    }

    @Override
    public boolean any(){
        return ! isEmpty();
    }

    @Override
    public boolean any(Condition<? super TElement> condition) {
        return LinqBehaviour.any(this, condition);
    }

    @Override
    public void forEach(Action1<? super TElement> function) {
        LinqBehaviour.forEach(this, function);
    }

    @Override
    public <TOther> Queryable<Tuple<TElement, TOther>> cartesianProduct(Queryable<TOther> orthogonalSet) {
        return LinqBehaviour.cartesianProduct(this, orthogonalSet);
    }

    @Override
    public boolean all(Condition<? super TElement> condition) {
        return LinqBehaviour.all(this, condition);
    }

    @Override
    public boolean isSetEquivalentOf(Iterable<TElement> otherSet) {
        return LinqBehaviour.isSetEquivalentOf(this, otherSet);
    }

    @Override
    public boolean isSubsetOf(Iterable<TElement> otherSet) {
        return LinqBehaviour.isSubsetOf(this, otherSet);
    }

    @Override
    public boolean contains(Object candidate) {
        return LinqBehaviour.contains(this, candidate);
    }

    @Override
    public boolean contains(Condition<TElement> candidateRequirement) {
        return LinqBehaviour.contains(this, candidateRequirement);
    }

    @Override
    public boolean containsDuplicates() {
        return LinqBehaviour.containsDuplicates(this);
    }

    @Override
    public Queryable<TElement> withoutDuplicates() {
        return LinqBehaviour.withoutDuplicates(this);
    }

    @Override
    public TElement last() {
        return LinqBehaviour.last(this);
    }

    @Override
    public TElement lastOrDefault() {
        return LinqBehaviour.lastOrDefault(this);
    }

    @Override
    public ReadonlyLinqingList<TElement> toReadOnly() {
        return new ReadonlyLinqingList<>(this);
    }

    @Override
    public TElement withMinimum(Func1<TElement, Number> valueSelector) {
        return LinqBehaviour.withMinimum(this, valueSelector);
    }

    @Override
    public int size() {
        return LinqBehaviour.size(this);
    }

    @Override
    public <TRight> LinqingList<Tuple<TElement, TRight>> join(TRight[] right) {
        return LinqBehaviour.join(this, Arrays.asList(right));
    }

    @Override
    public <TRight> LinqingList<Tuple<TElement, TRight>> join(Iterable<TRight> right) {
        return LinqBehaviour.join(this, right);
    }

    @Override
    public <TTransformed> LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return LinqBehaviour.selectMany(this, selector);
    }

    @Override
    public Queryable<TElement> except(Iterable<? extends TElement> toExclude) {
        return LinqBehaviour.excluding(LinqingList.asList(this), LinqingList.asList(toExclude));
    }

    @Override
    public Queryable<TElement> except(TElement... toExclude) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public <TCompared> Queryable<TElement> except(Iterable<? extends TElement> toExclude, Func1<? super TElement, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Queryable<TElement> union(Iterable<? extends TElement> toInclude) {
        return LinqBehaviour.union(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<TElement> union(Iterable<? extends TElement> toInclude, Func1<? super TElement, TCompared> comparableSelector) {
        return LinqBehaviour.union(this, toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> union(TElement... set) {
        return LinqBehaviour.union(this, set);
    }

    @Override
    public Queryable<TElement> skip(int numberToSkip){
        return LinqBehaviour.skip(this, numberToSkip);
    }

    @Override
    public <TComparable> QueryableMultiMap<TComparable, TElement> groupBy(Func1<TElement, TComparable> comparableSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<TElement> skipWhile(Condition<? super TElement> toExclude) {
        return LinqBehaviour.skipWhile(this, toExclude);
    }

    @Override
    public Queryable<TElement> skipUntil(Condition<? super TElement> toInclude) {
        return LinqBehaviour.skipUntil(this, toInclude);
    }

    @Override
    public Queryable<TElement> intersect(Iterable<? extends TElement> toInclude) {
        return LinqBehaviour.intersect(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                                     Func1<? super TElement, TCompared> comparableSelector) {
        return LinqBehaviour.intersect(this, toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> intersect(TElement... toIntersect) {
        return LinqBehaviour.intersect(this, toIntersect);
    }

    @Override
    public <TDerived> Queryable<TDerived> cast() {
        return LinqBehaviour.cast(this);
    }

    public TElement secondToLast() {
        return LinqBehaviour.secondToLast(this);
    }

}
