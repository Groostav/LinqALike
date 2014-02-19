package LinqALike;

import LinqALike.Common.QueryableSet;
import LinqALike.Delegate.*;

import java.util.*;
import java.util.function.Consumer;

import static LinqALike.LinqingList.from;

public class LinqingMap<TKey, TValue> extends LinkedHashMap<TKey, TValue> implements QueryableMap<TKey, TValue> {

    private static final <TKey, TValue> Func1<Map.Entry<TKey, TValue>, Tuple<TKey, TValue>> EntryToTuple(){
        return keyValuePair -> new Tuple(keyValuePair.getKey(), keyValuePair.getValue());
    }


    private Func1<TKey, TValue> GetValue = new Func1<TKey, TValue>() {
        public TValue getFrom(TKey key) {
            return LinqingMap.this.get(key);
        }
    };

    @SafeVarargs
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Tuple<TKey, TValue>... values){
        return new LinqingMap<TKey, TValue>(values);
    }
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Map<TKey, TValue> existingMap){
        return new LinqingMap<>(existingMap);
    }
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Iterable<TKey> keys, Iterable<TValue> values) {
        return new LinqingMap<>(keys, values);
    }

    public static <TKey, TValue> LinqingMap<TKey, TValue> empty(){
        return new LinqingMap<>();
    }

    public LinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        assert ! LinqBehaviour.containsDuplicates(keys);

        Iterator<? extends TValue> valueIterator = values.iterator();
        for(TKey key : keys){
            put(key, valueIterator.next());
        }
    }

    public LinqingMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinqingMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinqingMap() {
    }

    public LinqingMap(Map<? extends TKey, ? extends TValue> toCopy) {
        super(toCopy);
    }

    public LinqingMap(Iterable<Tuple<TKey, TValue>> initialValues){
        addAll(initialValues);
    }
    @SafeVarargs
    public LinqingMap(Tuple<? extends TKey, ? extends TValue> ... initialValues){
        putAll(initialValues);
    }

    private void putAll(Tuple<? extends TKey, ? extends TValue> ... values) {
        for(Tuple<? extends TKey, ? extends TValue> pair : values){
            put(pair.left, pair.right);
        }
    }

    @Override
    public ReadonlyLinqingList<Tuple<TKey, TValue>> toReadOnly() {
        return new ReadonlyLinqingList<>(this);
    }

    @Override
    public boolean containsDuplicates() {
        return values().containsDuplicates();
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> withoutDuplicates() {
        return LinqBehaviour.withoutDuplicates(this);
    }

    @Override
    public Iterator<Tuple<TKey, TValue>> iterator() {
        return from(entrySet()).select(LinqingMap.<TKey, TValue>EntryToTuple()).iterator();
    }

    @Override
    public void forEach(Consumer<? super Tuple<TKey, TValue>> action) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Spliterator<Tuple<TKey, TValue>> spliterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TTransformed> Queryable<TTransformed> select(Func1<? super Tuple<TKey, TValue>, TTransformed> selector) {
        return LinqBehaviour.select(this, selector);
    }

    @Override
    public <TTransformed> LinqingList<TTransformed> selectMany(Func1<? super Tuple<TKey, TValue>, ? extends Iterable<TTransformed>> selector) {
        return LinqBehaviour.selectMany(this, selector);
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> where(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.where(this, condition);
    }

    @Override
    public <TDesired extends Tuple<TKey, TValue>> LinqingList<TDesired> ofType(Class<TDesired> desiredClass) {
        assert false : "not implemented, also erasure makes this difficult";
        return null;
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> reversed() {
        assert false : "not implemented, I'm lazy";
        return null;
    }

    @Override
    public LinqingList<Tuple<TKey, TValue>> toList() {
        return new LinqingList<>(this);
    }

    @Override
    public <TOuterKey> LinqingMap<TOuterKey, Tuple<TKey, TValue>> toMap(Func1<Tuple<TKey, TValue>, TOuterKey> keySelector) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Object[] toArray() {
        return from(this).toArray();
    }

    @Override
    public <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Tuple<TKey, TValue> single() {
        return LinqBehaviour.single(this);
    }

    @Override
    public Tuple<TKey, TValue> single(Condition<? super Tuple<TKey, TValue>> uniqueConstraint) {
        return LinqBehaviour.single(this, uniqueConstraint);
    }

    @Override
    public Tuple<TKey, TValue> singleOrDefault() {
        return LinqBehaviour.singleOrDefault(this);
    }

    @Override
    public Tuple<TKey, TValue> singleOrDefault(Condition<? super Tuple<TKey, TValue>> uniqueConstraint) {
        return LinqBehaviour.singleOrDefault(this, uniqueConstraint);
    }

    @Override
    public Tuple<TKey, TValue> first() {
        return LinqBehaviour.first(this);
    }

    @Override
    public Tuple<TKey, TValue> first(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.first(this, condition);
    }

    @Override
    public Tuple<TKey, TValue> firstOrDefault() {
        return LinqBehaviour.firstOrDefault(this, CommonDelegates.Tautology);
    }

    @Override
    public Tuple<TKey, TValue> firstOrDefault(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.firstOrDefault(this, condition);
    }

    @Override
    public boolean isSingle() {
        return this.size() == 1;
    }

    @Override
    public boolean containsSingle(Condition<? super Tuple<TKey, TValue>> condition) {
        return where(condition).isSingle();
    }

    @Override
    public boolean any() {
        return ! this.isEmpty();
    }

    @Override
    public boolean any(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.any(this, condition);
    }

    @Override
    public int count(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.count(this, condition);
    }

    @Override
    public boolean all(Condition<? super Tuple<TKey, TValue>> condition) {
        return LinqBehaviour.all(this, condition);
    }

    @Override
    public boolean isSetEquivalentOf(Iterable<Tuple<TKey, TValue>> otherSet) {
        return LinqBehaviour.isSameSetAs(this, otherSet);
    }

    @Override
    public boolean isSubsetOf(Iterable<Tuple<TKey, TValue>> otherSet) {
        return LinqBehaviour.isSubsetOf(this, otherSet);
    }

    @Override
    public boolean contains(Object candidate) {
        return LinqBehaviour.contains(this, candidate);
    }

    @Override
    public boolean contains(Condition<Tuple<TKey, TValue>> candidateRequirement) {
        return LinqBehaviour.contains(this, candidateRequirement);
    }

    @Override
    public void forEach(Action1<? super Tuple<TKey, TValue>> function) {
        LinqBehaviour.forEach(this, function);
    }

    @Override
    public <TOther> Queryable<Tuple<Tuple<TKey, TValue>, TOther>> cartesianProduct(Queryable<TOther> allKnownSubclasses) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> fetch() {
        return LinqBehaviour.toList(this);
    }

    @Override
    public Tuple<TKey, TValue> firstOr(Tuple<TKey, TValue> alternative) {
        return LinqBehaviour.firstOr(this, alternative);
    }

    @Override
    public <TRight, TResult>
    Queryable<TResult> join(Iterable<TRight> right,
                            Func2<Tuple<TKey, TValue>, TRight, TResult> makeResult) {
        return LinqBehaviour.join(this, right, makeResult);
    }

    @Override
    public Tuple<TKey, TValue> last() {
        return LinqBehaviour.last(this);
    }

    @Override
    public Tuple<TKey, TValue> lastOrDefault() {
        return LinqBehaviour.lastOrDefault(this);
    }

    @Override
    public Tuple<TKey, TValue> withMinimum(Func1<Tuple<TKey, TValue>, Number> valueSelector) {
        return LinqBehaviour.withMinimum(this, valueSelector);
    }

    @Override
    public <TRight> LinqingList<Tuple<Tuple<TKey, TValue>, TRight>> join(TRight[] right) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public <TRight> LinqingList<Tuple<Tuple<TKey, TValue>, TRight>> join(Iterable<TRight> right) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> except(Iterable<? extends Tuple<TKey, TValue>> toExclude) {
        return LinqBehaviour.excluding(from(this), from(toExclude));
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> except(Tuple<TKey, TValue>... toExclude) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public <TCompared> Queryable<Tuple<TKey, TValue>> except(Iterable<? extends Tuple<TKey, TValue>> toExclude, Func1<? super Tuple<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public LinqingMap<TKey, TValue> union(Iterable<? extends Tuple<TKey, TValue>> toInclude) {
        return new LinqingMap<>(LinqBehaviour.union(this, from(toInclude)));
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> skipWhile(Condition<? super Tuple<TKey, TValue>> toExclude) {
        return LinqBehaviour.skipWhile(this, toExclude);
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> skipUntil(Condition<? super Tuple<TKey, TValue>> toInclude) {
        return LinqBehaviour.skipUntil(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<Tuple<TKey, TValue>> union(Iterable<? extends Tuple<TKey, TValue>> toInclude, Func1<? super Tuple<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> skip(int numberToSkip) {
        return LinqBehaviour.skip(this, numberToSkip);
    }

    @Override
    public <TComparable> QueryableMultiMap<TComparable, Tuple<TKey, TValue>> groupBy(Func1<Tuple<TKey, TValue>, TComparable> comparableSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TDerived> Queryable<TDerived> cast() {
        assert false : "not implemented";
        return null;
    }

    @Override
    public LinqingMap<TKey, TValue> union(Tuple<TKey, TValue>... toInclude) {
        return new LinqingMap<>(LinqBehaviour.union(this, from(toInclude)));
    }

    @Override
    public <TTransformedValue> QueryableMap<TKey, TTransformedValue> selectValues(Func1<TValue, TTransformedValue> valueSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<TValue> getAll(Iterable<? extends TKey> keys) {
        return from(keys).select(GetValue);
    }

    @Override
    public Map<TKey, TValue> toMap() {
        return new HashMap<>(this);
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> intersect(Iterable<? extends Tuple<TKey, TValue>> toInclude) {
        return LinqBehaviour.intersection(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<Tuple<TKey, TValue>> intersect(Iterable<? extends Tuple<TKey, TValue>> toInclude, Func1<? super Tuple<TKey, TValue>, TCompared> comparableSelector) {
        return LinqBehaviour.intersection(this, toInclude, comparableSelector);
    }

    @Override
    public Queryable<Tuple<TKey, TValue>> intersect(Tuple<TKey, TValue>... toIntersect) {
        return LinqBehaviour.intersection(this, toIntersect);
    }

    @Override
    public QueryableSet<TKey> keySet(){
        return new LinqingSet<>(super.keySet());
    }

    @Override
    public ReadonlyLinqingList<TValue> values(){
        return new ReadonlyLinqingList<>(super.values());
    }

    public void add(Tuple<TKey, TValue> keyValuePair){
        put(keyValuePair.left, keyValuePair.right);
    }
    
    public TValue getOrMake(TKey key, Func<? extends TValue> makeValue){
        if(containsKey(key)){
            return get(key);
        }
        else{
            TValue newValue = makeValue.getValue();
            put(key, newValue);
            return newValue;
        }
    }

    public void addAll(Iterable<Tuple<TKey, TValue>> keyValueTuples){
        for(Tuple<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.left, keyValuePair.right);
        }
    }

    public void putAll(Iterable<? extends TKey> keys, Iterable<? extends TValue> newValues) {
        Iterator<? extends TValue> values = newValues.iterator();
        for(TKey key : keys){
            put(key, values.next());
        }
    }
}

