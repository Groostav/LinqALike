package LinqALike;

import LinqALike.Common.*;
import LinqALike.Delegate.Action1;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.Queries.*;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static LinqALike.CommonDelegates.*;
import static LinqALike.LinqingList.firstNotNull;
import static LinqALike.LinqingList.from;
import static LinqALike.Tuple.pair;

public class LinqBehaviour {

    public static <TBase, TDerived extends TBase> LinqingList<TDerived> whereTypeIs(Iterable<TBase> set,
                                                                                    Class<TDerived> desiredType) {
        LinqingList<TDerived> returnable = new LinqingList<>();

        for (TBase candidate : set) {
            if (candidate == null)
                continue;

            if (desiredType.isAssignableFrom(candidate.getClass())) {
                returnable.add((TDerived) candidate);
            }
        }

        return returnable.asReadOnly();
    }

    public static <TElement> TElement single(Iterable<TElement> elements) {
        return single(elements, Tautology);
    }

    public static <TElement> TElement single(Iterable<TElement> elements, Condition<? super TElement> uniqueCondition) {
        TElement result = singleOrDefault(elements, uniqueCondition);
        if (result == null) {
            throw new NonEmptySetIsEmptyException(elements, uniqueCondition);
        }
        return result;
    }

    public static <TElement> TElement single(TElement[] listeners) {
        return single(Arrays.asList(listeners));
    }

    public static <TElement> TElement single(TElement[] set, Condition<? super TElement> condition) {
        return single(Arrays.<TElement>asList(set), condition);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> elements) {
        return singleOrDefault(elements, Tautology);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> elements, Condition<? super TElement> uniqueCondition) {
        Iterator<TElement> iterator = elements.iterator();

        if (!iterator.hasNext()) {
            return null;
        }

        LinqingList<TElement> results = new LinqingList<>();
        while (iterator.hasNext()) {
            TElement current = iterator.next();
            if (uniqueCondition.passesFor(current)) {
                results.add(current);
            }
            if (results.size() == 2) {
                throw new SingletonSetContainsMultipleElementsException(elements, results, uniqueCondition);
            }
        }
        return results.firstOrDefault();
    }

    public static <TElement> TElement first(Iterable<TElement> elements) {
        return first(elements, Tautology);
    }

    public static <TElement> TElement last(Iterable<TElement> elements){
        if( ! elements.iterator().hasNext()) throw new NonEmptySetIsEmptyException();
        return lastOrDefault(elements);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> set) {
        TElement last = null;
        for(TElement element : set){
            last = element;
        }
        return last;
    }

    public static <TElement> TElement first(Iterable<TElement> elements, Condition<? super TElement> condition) {
        Iterator<TElement> iterator = elements.iterator();

        if (!iterator.hasNext()) {
            throw new NonEmptySetIsEmptyException();
        }

        while (iterator.hasNext()) {
            TElement candidate = iterator.next();

            if (condition.passesFor(candidate)) {
                return candidate;
            }
        }

        throw new NonEmptySetIsEmptyException(elements, condition);
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> elements, Condition<? super TElement> condition) {
        for (TElement element : elements) {
            if (condition.passesFor(element)) {
                return element;
            }
        }
        return null;
    }

    public static <TElement> Queryable<TElement> where(Iterable<TElement> elements, Condition<? super TElement> condition) {
        return new WhereQuery<>(elements, condition);
    }

    public static <TElement, TResult> Iterable<TResult> select(TElement[] set,
                                                               Func1<? super TElement, TResult> targetSite) {
        return select(Arrays.asList(set), targetSite);
    }

    public static <TElement, TResult> Queryable<TResult> select(Iterable<TElement> set,
                                                                final Func1<? super TElement, TResult> targetSite) {
        return new SelectQuery<>(set, targetSite);
    }

    private static <TResult> ReadonlyLinqingList<TResult> markupAsReadonly(LinqingList<TResult> results) {
        return results.asReadOnly(ReadonlyLinqingList.Because.DerivedFromAnotherSet);
    }

    public static <TElement> TElement lastElementIn(TElement[] set) {
        assert set != null;
        assert set.length >= 1;

        return set[set.length - 1];
    }

    public static <TElement> boolean containsOnly(Iterable<TElement> set, TElement desiredElement) {
        boolean result = true;
        for (TElement element : set) {
            result &= nullSafeEquals(desiredElement, element);
        }
        return result;
    }

    public static <TElement> Queryable<TElement> excluding(Iterable<? extends TElement> left, Iterable<? extends TElement> right) {
        return excluding(left, right, CommonDelegates.AsIs());
    }

    public static <TElement> boolean contains(Iterable<TElement> set, Object member){
        for (TElement element : set) {
            if (nullSafeEquals(element, member)) {
                return true;
            }
        }
        return false;
    }

    public static <TElement> boolean contains(TElement[] set, TElement member) {
        return from(set).contains(member);
    }

    public static <TElement> boolean any(Iterable<TElement> set, Condition<? super TElement> condition) {

        for (TElement candidate : set) {
            if (condition.passesFor(candidate)) {
                return true;
            }
        }
        return false;
    }

    public static <TElement> boolean isEmpty(TElement[] set) {
        return set.length == 0;
    }

    public static <TElement> boolean isEmpty(Iterable<TElement> set) {
        return ! set.iterator().hasNext();
    }

    public static <TElement> void forEach(Iterable<TElement> elements, Action1<? super TElement> function) {
        for (TElement element : elements) {
            function.doUsing(element);
        }
    }

    public static Iterable<Integer> range(final int lowerInclusive, final int upperExclusive) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    public int current = lowerInclusive;

                    @Override
                    public boolean hasNext() {
                        return current < upperExclusive;
                    }

                    @Override
                    public Integer next() {
                        return current++;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <TElement> Iterable<TElement> repeat(final TElement valueToRepeat) {
        return () -> new RepeatingIterator<>(valueToRepeat);
    }

    public static <TElement> boolean contains(Iterable<TElement> set, Condition<TElement> candidateRequirement) {
        for(TElement element : set){
            if(candidateRequirement.passesFor(element)){
                return true;
            }
        }

        return false;
    }

    public static <TElement> TElement withMinimum(Iterable<TElement> set, Func1<TElement, Number> valueSelector) {
        Iterator<TElement> iterator = set.iterator();
        if( ! iterator.hasNext()){
            throw new NonEmptySetIsEmptyException();
        }

        Double minValue = null;
        TElement foundElement = null;

        while(iterator.hasNext()){
            TElement containingElement = iterator.next();
            Number elementValue = valueSelector.getFrom(containingElement);

            if(elementValue == null){
                throw new NullPointerException("Trying to find the minimum of a set, " +
                        "but the value selector yielded null on one of the elements " +
                        "(and null isn't comparable.)");
            }

            if(minValue == null || minValue > elementValue.doubleValue()){
                foundElement = containingElement;
                minValue = elementValue.doubleValue();
            }
        }

        return foundElement;
    }

    public static <TLeft, TRight> LinqingList<Tuple<TLeft, TRight>> join(Iterable<TLeft> left, Iterable<TRight> right) {
        Iterator<TLeft> lefts = left.iterator();
        Iterator<TRight> rights = right.iterator();

        LinqingList<Tuple<TLeft, TRight>> returnable = new LinqingList<Tuple<TLeft, TRight>>();

        while(lefts.hasNext() && rights.hasNext()){
            returnable.add(new Tuple<TLeft, TRight>(lefts.next(), rights.next()));
        }

        assert( ! lefts.hasNext() && ! rights.hasNext()) : "expected both sets to be of the same size, but they were not.";

        return markupAsReadonly(returnable);
    }

    public static <TTransformed, TElement>
    LinqingList<TTransformed> selectMany(Iterable<TElement> set, Func1<? super TElement, ? extends Iterable<TTransformed>> selector) {
        LinqingList<TTransformed> results = new LinqingList<TTransformed>();
        for (TElement element : set) {
            Iterable<? extends TTransformed> transformedValues = selector.getFrom(element);
            results.addAll(transformedValues);
        }

        return markupAsReadonly(results);
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return union(left, right, CommonDelegates.AsIs());
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, TElement... toInclude) {
        return union(left, from(toInclude));
    }

    public static <TElement, TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> left,
                                Iterable<? extends TElement> right,
                                Func1<? super TElement, TCompared> comparableSelector){

        return new UnionQuery<>(left, right, comparableSelector);
    }

    public static <TKey, TValue> LinqingMap<TKey,TValue> toMap(Iterable<TKey> keys, Iterable<TValue> values) {
        Iterator<TValue> value = values.iterator();
        LinqingMap<TKey, TValue> returnable = new LinqingMap<>();
        for(TKey key : keys){
            returnable.put(key, value.next());
        }
        return returnable;
    }

    public static <TDerived, TElement> LinqingList<TDerived> selectCast(Iterable<TElement> set) {
        LinqingList<TDerived> returnable = new LinqingList<TDerived>();
        for(TElement element : set){
            TDerived castElement;
            try{
                castElement = (TDerived) element;
            }
            catch(ClassCastException ex){
                throw new ClassCastException("Couldn't cast element in linqing list: '" + element + "' to the specified type.");
            }
            returnable.add(castElement);
        }
        return markupAsReadonly(returnable);
    }

    public static <TElement> boolean all(Iterable<TElement> set, Condition<? super TElement> condition) {

        for(TElement element : set){
            if( ! condition.passesFor(element)){
                return false;
            }
        }

        return true;
    }


    //O(n)!!, _not_ O(n^2) :D
    public static <TElement> boolean isSameSetAs(Iterable<TElement> left, Iterable<TElement> right) {
        if(left instanceof Collection && right instanceof Collection){
            if( ((Collection)left).size() != ((Collection)right).size()) return false;
        }

        final Map<TElement, TElement> leftHashes = new HashMap<>();
        //note, I'm not actually using the value for anything here, just the hashing key comparison.

        for(TElement leftElement : left){
            leftHashes.put(leftElement, null);
        }

        return from(right).all(new Condition<TElement>() {
            public boolean passesFor(TElement candidate) {
                return leftHashes.containsKey(candidate);
            }
        });
    }

    public static <TElement> Queryable<TElement> skipWhile(Iterable<? extends TElement> set,
                                                           Condition<? super TElement> toExclude) {

        Ref<TElement> firstPassingValue = new Ref<>();
        Iterator<? extends TElement> iterator = moveIteratorUpUntilConditionPasses(set.iterator(), Not(toExclude), firstPassingValue);

        return firstPassingValue.target != null
                ? new LinqingList<>(new AmmendedIterator<>(firstPassingValue.target, iterator))
                : LinqingList.<TElement>empty().asReadOnly();
    }


    public static <TElement> Queryable<TElement> skipUntil(Iterable<? extends TElement> set,
                                                           Condition<? super TElement> toInclude) {
        Ref<TElement> firstPassingValue = new Ref<>();
        Iterator<? extends TElement> iterator = moveIteratorUpUntilConditionPasses(set.iterator(), toInclude, firstPassingValue);

        return firstPassingValue.target != null
                ? new LinqingList<>(new AmmendedIterator<>(firstPassingValue.target, iterator))
                : LinqingList.<TElement>empty().asReadOnly();
    }

    private static <TElement>
    Iterator<? extends TElement> moveIteratorUpUntilConditionPasses(Iterator<? extends TElement> iterator,
                                                                    Condition<? super TElement> conditionToPass,
                                                                    Ref<TElement> firstPassingValue) {
        while(iterator.hasNext()){
            TElement candidate = iterator.next();
            if(conditionToPass.passesFor(candidate)){
                firstPassingValue.target = candidate;
                break;
            }
        }
        return iterator;
    }

    public static <TElement> Queryable<TElement> reversed(Iterable<TElement> set) {
        Stack<TElement> stack = new Stack<>();

        for(TElement element : set){
            stack.push(element);
        }
        LinqingList<TElement> returnable = new LinqingList<>();
        while( ! stack.isEmpty()){
            returnable.add(stack.pop());
        }
        return returnable;
    }

    public static <TElement> Queryable<TElement> expand(TElement seed, Func1<TElement, TElement> nextValueGetter){
        assert seed != null;
        LinqingList<TElement> returnable = from(seed);
        TElement next = seed;
        do{
            returnable.add(next);
            next = nextValueGetter.getFrom(next);
        }
        while(next != null);

        return returnable;
    }
    public static <TElement> boolean isSubsetOf(Iterable<TElement> left, Iterable<TElement> right) {
        for(TElement leftElement : left){
            if ( ! from(right).contains(leftElement))
                return false;
        }
        return true;
    }


    public static <TElement> int count(Iterable<TElement> set, Condition<? super TElement> condition) {
        int count = 0;
        for(TElement element : set){
            if(condition.passesFor(element)){
                count += 1;
            }
        }
        return count;
    }

    public static <TElement, TOther> Queryable<Tuple<TElement, TOther>> cartesianProduct(Iterable<TElement> left, Iterable<TOther> right) {
        LinqingList<Tuple<TElement, TOther>> product = new LinqingList<>();
        for(TElement leftElement : left){
            for(TOther rightElement : right){
                product.add(pair(leftElement, rightElement));
            }
        }
        return product;
    }

    public static <TElement> boolean containsSingle(LinqingList<TElement> set, Condition<? super TElement> uniqueCondition) {
        Queryable<TElement> constrainedSet = set.where(uniqueCondition);
        return set.isSetEquivalentOf(constrainedSet) && constrainedSet.isSingle();
    }

    public static <TElement> Queryable<TElement> excluding(Iterable<? extends TElement> left, TElement... toExclude) {
        return excluding(left, from(toExclude));
    }

    public static <TElement, TCompared> Queryable<TElement> excluding(Iterable<? extends TElement> originalMembers,
                                                                      Iterable<? extends TElement> membersToExclude,
                                                                      Func1<? super TElement, TCompared> comparableSelector) {
        return new ExcludingQuery<>(originalMembers, membersToExclude, comparableSelector);
    }

    public static <TElement> Queryable<TElement> intersection(Iterable<? extends TElement> left,
                                                              Iterable<? extends TElement> right) {
        return intersection(left, right, CommonDelegates.AsIs());
    }

    public static <TElement, TCompared> Queryable<TElement> intersection(Iterable<? extends TElement> left,
                                                                         Iterable<? extends TElement> right,
                                                                         Func1<? super TElement, TCompared> comparableSelector) {
        return new IntersectionQuery<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> intersection(Iterable<? extends TElement> left,
                                                              TElement... right) {
        return intersection(left, from(right), CommonDelegates.AsIs());
    }

    public static <TElement> String verticallyPrintMembers(Iterable<TElement> problemMembers) {
        String newlineIndent = "\n\t";
        return StringUtils.join(from(problemMembers).select(CommonDelegates.NullSafeToString).iterator(), newlineIndent) + "\n";
    }

    public static <TElement> Queryable<TElement> skip(Iterable<TElement> setToSkip, int numberToSkip) {

        Iterator<TElement> iterator = setToSkip.iterator();

        for(int i = 0; i < numberToSkip && iterator.hasNext(); i++){
            iterator.next();
        }

        return new LinqingList<>(iterator);
    }

    public static <TElement> boolean containsDuplicates(Iterable<TElement> set) {
        HashSet<TElement> hashes = new HashSet<>();
        for(TElement element : set){
            if(hashes.contains(element)){
                return true;
            }
            hashes.add(element);
        }
        return false;
    }


    public static <TElement> Object[] toArray(QueryableBase<TElement> set) {
        Object[] copy = new Object[set.size()];
        int i = 0;
        for(TElement element : set){
            copy[i++] = element;
        }
        return copy;
    }

    /**
     * @see java.util.ArrayList#toArray(Object[])
     */
    //copied from JavaUtil code, so presumably this is 'safe'.
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <TElement, TDesired> TDesired[] toArray(QueryableBase<TElement> originalSet, TDesired[] arrayTypeIndicator) {

        int size = originalSet.size();
        TDesired[] a = arrayTypeIndicator;
        Object[] elementData = originalSet.toArray();

        //copy-paste from java.util.ArrayList#toArray(T[] a)

        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (TDesired[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public static String verticallyPrintMembers(String... members) {
        return verticallyPrintMembers(from(members));
    }

    public static <TElement> LinqingList<TElement> toList(Iterable<TElement> set) {
        return new LinqingList<>(set);
    }

    public static <TElement> TElement firstOr(Queryable<TElement> set, TElement alternative) {
        return firstNotNull(set.firstOrDefault(), alternative);
    }

    public static <TElement> TElement secondToLast(Iterable<TElement> set) {
        int size = size(set);
        if(size < 2){
            throw new SetSizeInsufficientException(2, size);
        }

        if(set instanceof List){
            List<TElement> actualThis = (List<TElement>) set;
            return actualThis.get(size - 2);
        }

        Iterator<TElement> iter = set.iterator();
        TElement candidate = null, last = null;
        while(iter.hasNext()){
            candidate = last;
            last = iter.next();
        }

        return candidate;
    }

    public static <TElement> int size(Iterable<TElement> set) {
        if(set instanceof Collection){
            return ((Collection)set).size();
        }

        int size = 0;
        for(Object ignored : set){
            size += 1;
        }
        return size;
    }

    public static <TElement> Queryable<TElement> withoutDuplicates(Iterable<TElement> set){
        LinqingList<TElement> returnable = new LinqingList<>();

        for(TElement element : set){
            if( ! returnable.contains(element)){
                returnable.add(element);
            }
        }

        return returnable;
    }

    public static <TResult, TLeft, TRight>
    Queryable<TResult> join(Iterable<TLeft> left,
                            Iterable<TRight> right,
                            Func2<TLeft, TRight, TResult> makeResult) {

        Iterator<TLeft> leftIterator = left.iterator();
        Iterator<TRight> rightIterator = right.iterator();
        LinqingList<TResult> results = new LinqingList<>();

        while(leftIterator.hasNext() && rightIterator.hasNext()){
            TResult result = makeResult.getFrom(leftIterator.next(), rightIterator.next());
            results.add(result);
        }

        //for now we're going to assert this is a natural join. If we need to, we can get into left v right v outer v inner...
        //note we do already have a cartesianProduct method.
        if(leftIterator.hasNext() ^ rightIterator.hasNext()){
            throw new EquivalentSizeSetsDifferInSizeException(left, right);
        }

        return results;
    }
}

