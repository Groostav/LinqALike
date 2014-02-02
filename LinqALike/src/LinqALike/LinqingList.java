package LinqALike;

import LinqALike.Common.NonEmptySetIsEmptyException;
import LinqALike.Delegate.Action1;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class LinqingList<TElement> extends ArrayList<TElement> implements Queryable<TElement> {

    // Constructors

    public LinqingList(){
        super();
    }
    public LinqingList(TElement... elements) {
        this(Arrays.asList(elements));
    }
    public LinqingList(Iterator<? extends TElement> elements){
        while (elements.hasNext()){
            TElement next = elements.next();
            add(next);
        }
    }
    public LinqingList(Iterable<? extends TElement> elements){
        this();
        for(TElement element : elements){
            add(element);
        }
    }
    public LinqingList(ListModel<? extends TElement> elements){
        this();
        for(int i = 0; i < elements.getSize(); i++){
            add(elements.getElementAt(i));
        }
    }
    public LinqingList(Class<TElement> elementClass, Object[] initialValues){
        this();
        for(Object object : initialValues){
            if(object == null){
                add(null);
                continue;
            }
            assert object.getClass().isAssignableFrom(elementClass);

            add((TElement) object);
        }
    }

    // Static Factories

    public static <TElement> LinqingList<TElement> empty() {
        return new LinqingList<>();
    }

    public static <TElement> LinqingList<TElement> from(Iterable<TElement> set){
        return new LinqingList<>(set);
    }

    @SafeVarargs
    public static <TElement> LinqingList<TElement> from(TElement ... set){
        return new LinqingList<>(set);
    }

    public static <TElement> LinqingList<TElement> from(ListModel<TElement> listModel){
        return new LinqingList<>(listModel);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNullOrDefault(TElement ... set){
        return from(set).firstOrDefault(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TElement> TElement firstNotNull(TElement ... set){
        return from(set).first(CommonDelegates.NotNull);
    }

    @SafeVarargs
    public static <TSet extends Iterable> TSet firstNotEmpty(TSet ... sets){
        for(TSet set : sets){
            if(set.iterator().hasNext()){
                return set;
            }
        }
        throw new NonEmptySetIsEmptyException();
    }

    // Local factories -- excluding Queryable specified asReadOnly()

    public <TValue> LinqingMap<TElement,TValue> toMapWithValues(Iterable<TValue> values) {
        return LinqingMap.bind(this, values);
    }

    public <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys){
        return LinqingMap.bind(keys, this);
    }

    public <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<TElement, TKey> keySelector, Func1<TElement, TValue> valueSelector) {
        LinqingMap<TKey, TValue> returnable = new LinqingMap<>();
        for(TElement element : this){
            returnable.put(keySelector.getFrom(element), valueSelector.getFrom(element));
        }
        return returnable;
    }
    public <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector){
        LinqingMap<TKey, TElement> returnable = new LinqingMap<>();
        for(TElement element : this){
            returnable.put(keySelector.getFrom(element), element);
        }
        return returnable;
    }

    public ReadonlyLinqingList<TElement> asReadOnly(ReadonlyLinqingList.Because because){
        return new ReadonlyLinqingList<>(this, because);
    }

    // List-based Mutators  -- note the extends relationship with ArrayList, this class is an array list.

    /**
     * <p>adds all elements in the supplied ellipses set to this list, starting from the last current index, and adding them
     * left-to-right.</p>
     *
     * @param   toBeAdded the elements to be added to this linqing list.
     * @return  true if the list changed as a result of this call.
     */
    public boolean addAll(TElement... toBeAdded){
        return super.addAll(Arrays.asList(toBeAdded));
    }

    public void addAll(Iterable<? extends TElement> values) {
        addAll(new LinqingList<>(values));
    }
    public void removeAll(Iterable<TElement> values) {
        super.removeAll(new LinqingList<>(values));
    }

    public void removeSingle(Condition<? super TElement> condition){
        TElement element = this.single(condition);
        this.remove(element);
    }

    // Queryable

    @Override
    public <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector){
        return LinqBehaviour.select(this, selector);
    }

    @Override
    public LinqingList<TElement> where(Condition<? super TElement> condition){
        return LinqBehaviour.where(this, condition);
    }

    @Override
    public <TDesired extends TElement>
    Queryable<TDesired> whereTypeIs(Class<TDesired> desiredClass) {
        return LinqBehaviour.whereTypeIs(this, desiredClass);
    }

    @Override
    public Queryable<TElement> reversed(){
        return LinqBehaviour.reversed(this);
    }

    @Override
    public LinqingList<TElement> toList() {
        return LinqBehaviour.toList(this);
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
        return this.size() == 1;
    }

    @Override
    public boolean containsSingle(Condition<? super TElement> condition) {
        return LinqBehaviour.containsSingle(this, condition);
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
    public int count(Condition<? super TElement> condition) {
        return LinqBehaviour.count(this, condition);
    }

    @Override
    public void forEach(Action1<? super TElement> function) {
        LinqBehaviour.forEach(this, function);
    }

    @Override
    public boolean all(Condition<? super TElement> condition) {
        return LinqBehaviour.all(this, condition);
    }

    @Override
    public boolean isSetEquivalentOf(Iterable<TElement> otherSet) {
        return LinqBehaviour.isSameSetAs(this, otherSet);
    }

    @Override
    public boolean isSubsetOf(Iterable<TElement> otherSet) {
        return LinqBehaviour.isSubsetOf(this, otherSet);
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
        if(isEmpty()) throw new RuntimeException("cannot get the last element as the set contains no elements!");
        return get(size() - 1);
    }

    @Override
    public TElement lastOrDefault() {
        return isEmpty() ? null : get(size() - 1);
    }

    @Override
    public ReadonlyLinqingList<TElement> asReadOnly() {
        return new ReadonlyLinqingList<>(this);
    }

    @Override
    public TElement withMinimum(Func1<TElement, Number> valueSelector) {
        return LinqBehaviour.withMinimum(this, valueSelector);
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
    public Queryable<TElement> excluding(Iterable<? extends TElement> toExclude) {
        return LinqBehaviour.excluding(this, toExclude);
    }

    @Override
    public Queryable<TElement> excluding(TElement... toExclude) {
        return LinqBehaviour.excluding(this, toExclude);
    }

    @Override
    public <TCompared> Queryable<TElement> excluding(Iterable<? extends TElement> toExclude, Func1<? super TElement, TCompared> comparableSelector) {
        return LinqBehaviour.excluding(this, toExclude, comparableSelector);
    }

    @Override
    public <TTransformed> LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector){
        return LinqBehaviour.selectMany(this, selector);
    }

    @Override
    public <TOther> Queryable<Tuple<TElement, TOther>> cartesianProduct(Queryable<TOther> other) {
        return LinqBehaviour.cartesianProduct(this, other);
    }

    @Override
    public Queryable<TElement> fetch() {
        return toList();
    }

    @Override
    public TElement firstOr(TElement nullInstance) {
        return LinqBehaviour.firstOr(this, nullInstance);
    }

    @Override
    public <TRight, TResult> Queryable<TResult> join(Iterable<TRight> right, Func2<TElement, TRight, TResult> makeResult) {
        return LinqBehaviour.join(this, right, makeResult);
    }

    @Override
    public LinqingList<TElement> union(TElement... toInclude){
        return LinqBehaviour.union(this, toInclude);
    }

    @Override
    public LinqingList<TElement> union(Iterable<? extends TElement> toInclude) {
        return LinqBehaviour.union(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<TElement> union(Iterable<? extends TElement> toInclude, Func1<? super TElement, TCompared> comparableSelector) {
        return LinqBehaviour.union(this, toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> skip(int numberToSkip) {
        return LinqBehaviour.skip(this, numberToSkip);
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
    public Queryable<TElement> intersection(Iterable<? extends TElement> toInclude) {
        return LinqBehaviour.intersection(this, toInclude);
    }

    @Override
    public <TCompared> Queryable<TElement> intersection(Iterable<? extends TElement> toInclude, Func1<? super TElement, TCompared> comparableSelector) {
        return LinqBehaviour.intersection(this, toInclude, comparableSelector);
    }

    @Override
    public Queryable<TElement> intersection(TElement... toIntersect) {
        return LinqBehaviour.intersection(this, toIntersect);
    }

    @Override
    public <TDerived> LinqingList<TDerived> selectCast() {
        return LinqBehaviour.selectCast(this);
    }

    public void addIfNotNull(TElement element) {
        if(element != null){
            add(element);
        }
    }

    public void addAllNew(Iterable<TElement> setContainingNewAndExistingElements) {
        Queryable<TElement> intersection = from(setContainingNewAndExistingElements).excluding(this.intersection(setContainingNewAndExistingElements));
        this.addAll(intersection);
    }

    public void replaceAll(Queryable<Tuple<TElement, TElement>> changedItems) {
        for(Tuple<TElement, TElement> pair : changedItems){
            this.replace(pair.left, pair.right);
        }
    }

    public void clearAndAddAll(Iterable<? extends TElement> newItems){
        clear();
        addAll(newItems);
    }

    public void replace(TElement oldItem, TElement newItem) {
        int index = indexOf(oldItem);
        assert index != -1 : oldItem + " is not contained in " + this;
        this.add(index, newItem);
        this.remove(oldItem);
    }
}


