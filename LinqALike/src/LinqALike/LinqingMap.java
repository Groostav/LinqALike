package LinqALike;

import LinqALike.Delegate.*;

import java.util.*;

import static LinqALike.LinqingList.from;

public class LinqingMap<TKey, TValue> extends LinkedHashMap<TKey, TValue> implements QueryableMap<TKey, TValue> {


    /*
     * Static factories
     */
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Map<TKey, TValue> existingMap){
        return new LinqingMap<>(existingMap);
    }
    public static <TKey, TValue> LinqingMap<TKey, TValue> bind(Iterable<TKey> keys, Iterable<TValue> values) {
        return new LinqingMap<>(keys, values);
    }


    /*
     * constructors
     */
    public LinqingMap(Iterable<? extends TKey> keys, Iterable<? extends TValue> values) {
        assert ! LinqBehaviour.containsDuplicates(keys);

        Iterator<? extends TValue> valueIterator = values.iterator();
        for(TKey key : keys){
            put(key, valueIterator.next());
        }
    }

    public LinqingMap() {
    }

    public LinqingMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinqingMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinqingMap(Map<? extends TKey, ? extends TValue> toCopy) {
        super(toCopy);
    }

    public LinqingMap(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> initialValues){
        putAll(initialValues);
    }

    @SafeVarargs
    public LinqingMap(Map.Entry<? extends TKey, ? extends TValue> ... initialValues){
        putAll(initialValues);
    }


    /*
     * Mutator methods
     */
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

    public void put(Map.Entry<? extends TKey, ? extends TValue> keyValuePair){
        put(keyValuePair.getKey(), keyValuePair.getValue());
    }

    public void putAll(Iterable<? extends Map.Entry<? extends TKey, ? extends TValue>> keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }
    public void putAll(Map.Entry<? extends TKey, ? extends TValue> ... keyValueTuples){
        for(Map.Entry<? extends TKey, ? extends TValue> keyValuePair : keyValueTuples){
            put(keyValuePair.getKey(), keyValuePair.getValue());
        }
    }

    /*
     * Queryable to Util.Collections signature coalescing.
     */
    @Override
    public LinqingSet<TKey> keySet() {
        return new LinqingSet<>(this.select(x -> x.getKey()));
    }

    @Override
    public LinqingList<TValue> values() {
        return new LinqingList<>(this.select(x -> x.getValue()));
    }

    @Override
    public LinqingSet<Map.Entry<TKey, TValue>> entrySet() {
        return new LinqingSet<>(this);
    }


    /*
     * Queryable implementation
     */
    @Override
    public boolean isDistinct() {
        return true;
    }

    @Override
    public boolean isSubsetOf(Iterable<? extends Map.Entry<TKey, TValue>> otherSet) {
        return LinqBehaviour.isSubsetOf(this, otherSet);
    }

    @Override
    public boolean isSetEquivalentOf(Iterable<? extends Map.Entry<TKey, TValue>> otherSet) {
        return LinqBehaviour.isSetEquivalentOf(this, otherSet);
    }

    @Override
    public boolean isSingle() {
        return size() == 1;
    }

    @Override
    public <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory) {
        return LinqBehaviour.toArray(this, arrayFactory);
    }

    @Override
    public <TNewKey, TNewValue> LinqingMap<TNewKey,TNewValue> toMap(Func1<? super Map.Entry<TKey, TValue>, TNewKey> keySelector,
                                                                    Func1<? super Map.Entry<TKey, TValue>, TNewValue> valueSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TNewKey> LinqingMap<TNewKey, Map.Entry<TKey, TValue>> toMap(Func1<? super Map.Entry<TKey, TValue>, TNewKey> keySelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TNewKey> LinqingMap<TNewKey, Map.Entry<TKey, TValue>> toMap(Iterable<TNewKey> keys) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<Map.Entry<TKey, TValue>> fetch() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LinqingSet<Map.Entry<TKey, TValue>> toSet() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LinqingList<Map.Entry<TKey, TValue>> toList() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReadonlyLinqingList<Map.Entry<TKey, TValue>> toReadOnly() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double sum(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> singleOrDefault(Condition<? super Map.Entry<TKey, TValue>> uniqueConstraint) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> singleOrDefault() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> single(Condition<? super Map.Entry<TKey, TValue>> uniqueConstraint) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> single() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TTransformed> LinqingList<TTransformed> selectMany(Func1<? super Map.Entry<TKey, TValue>, ? extends Iterable<TTransformed>> selector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TTransformed> Queryable<TTransformed> select(Func1<? super Map.Entry<TKey, TValue>, TTransformed> selector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TElementSubclass extends Map.Entry<TKey, TValue>> Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> withMin(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double min(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> withMax(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double max(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> lastOrDefault(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> lastOrDefault() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> last(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> last() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TRight, TResult> Queryable<TResult> join(Iterable<? extends TRight> right, Func2<? super Map.Entry<TKey, TValue>, ? super TRight, TResult> makeResult) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TComparable, TNewValue> QueryableMultiMap<TComparable, TNewValue> groupBy(Func1<? super Map.Entry<TKey, TValue>, TComparable> keySelector,
                                                                                      Func1<? super Map.Entry<TKey, TValue>, TNewValue> valueSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<Queryable<Map.Entry<TKey, TValue>>> groupBy(Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TComparable> Queryable<Queryable<Map.Entry<TKey, TValue>>> groupBy(Func1<? super Map.Entry<TKey, TValue>, TComparable> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> firstOrDefault(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> firstOrDefault() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> first(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> first() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TComparable> Queryable<Map.Entry<TKey, TValue>> distinct(Func1<? super Map.Entry<TKey, TValue>, TComparable> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<Map.Entry<TKey, TValue>> distinct(Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<Map.Entry<TKey, TValue>> distinct() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int count(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean any(Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(Object candidate) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TDerived> Queryable<TDerived> cast() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double average(Func1<? super Map.Entry<TKey, TValue>, Double> valueSelector) {
        assert false : "not implemented";
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean any(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean any() {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean all(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TAccumulate> TAccumulate aggregate(TAccumulate seed, Func2<TAccumulate, Map.Entry<TKey, TValue>, TAccumulate> aggregator) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<TKey, TValue> aggregate(Func2<Map.Entry<TKey, TValue>, Map.Entry<TKey, TValue>, Map.Entry<TKey, TValue>> aggregator) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> where(Condition<? super Map.Entry<TKey, TValue>> condition) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TCompared> QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude, Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> union(Iterable<? extends Map.Entry<TKey, TValue>> toInclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> union(Map.Entry<TKey, TValue>... elements) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> skip(int numberToSkip) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> skipUntil(Condition<? super Map.Entry<TKey, TValue>> toInclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> skipWhile(Condition<? super Map.Entry<TKey, TValue>> toExclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> reversed() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> orderBy(Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Integer> equalityComparator) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TCompared extends Comparable<TCompared>> QueryableMap<TKey, TValue> orderBy(Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> intersect(Map.Entry<TKey, TValue>... toIntersect) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude, Func2<? super Map.Entry<TKey, TValue>, ? super Map.Entry<TKey, TValue>, Boolean> equalityComparison) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TCompared> QueryableMap<TKey, TValue> intersect(Iterable<? extends Map.Entry<TKey, TValue>> toInclude, Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<TValue> getAll(TKey... tKeys) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queryable<TValue> getAll(Iterable<? extends TKey> tKeys) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <TCompared> QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude, Func1<? super Map.Entry<TKey, TValue>, TCompared> comparableSelector) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude, Func2<Map.Entry<TKey, TValue>, Map.Entry<TKey, TValue>, Boolean> equalityComparison) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> except(Map.Entry<TKey, TValue>... toExclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryableMap<TKey, TValue> except(Iterable<? extends Map.Entry<TKey, TValue>> toExclude) {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsTValue(TValue candidateValue) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsTKey(TKey candidateKey) {
        assert false : "not implemented";
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Map.Entry<TKey, TValue>> iterator() {
        assert false : "not implemented";
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

