package LinqALike;

import LinqALike.Common.*;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;
import LinqALike.Queries.*;

import java.util.*;

import static LinqALike.CommonDelegates.*;
import static LinqALike.Factories.firstNotNull;
import static LinqALike.Factories.from;

public class Linq {

    public static <TBase, TDerived extends TBase> LinqingList<TDerived> ofType(Iterable<TBase> set,
                                                                               Class<TDerived> desiredType) {
        LinqingList<TDerived> returnable = new LinqingList<>();

        for (TBase candidate : set) {
            if (candidate == null)
                continue;

            if (desiredType.isAssignableFrom(candidate.getClass())) {
                returnable.add((TDerived) candidate);
            }
        }

        return returnable.toReadOnly();
    }

    public static <TElement> TElement single(Iterable<TElement> elements) {
        return single(elements, Tautology);
    }

    public static <TElement> TElement single(Iterable<TElement> elements,
                                             Condition<? super TElement> uniqueCondition) {
        TElement result = singleOrDefault(elements, uniqueCondition);
        if (result == null) {
            throw new SetIsEmptyException(elements, uniqueCondition);
        }
        return result;
    }

    public static <TElement> TElement single(TElement[] listeners) {
        return single(Arrays.asList(listeners));
    }

    public static <TElement> TElement single(TElement[] set,
                                             Condition<? super TElement> condition) {
        return single(Arrays.<TElement>asList(set), condition);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> elements) {
        return singleOrDefault(elements, Tautology);
    }

    public static <TElement> TElement singleOrDefault(Iterable<TElement> elements,
                                                      Condition<? super TElement> uniqueCondition) {
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

    public static <TElement> TElement first(Iterable<TElement> elements,
                                            Condition<? super TElement> condition) {
        Iterator<TElement> iterator = elements.iterator();

        if (!iterator.hasNext()) {
            throw new SetIsEmptyException();
        }

        while (iterator.hasNext()) {
            TElement candidate = iterator.next();

            if (condition.passesFor(candidate)) {
                return candidate;
            }
        }

        throw new SetIsEmptyException(elements, condition);
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> elements,
                                                     Condition<? super TElement> condition) {
        for (TElement element : elements) {
            if (condition.passesFor(element)) {
                return element;
            }
        }
        return null;
    }

    public static <TElement> TElement last(Iterable<TElement> elements){
        if( ! elements.iterator().hasNext()) throw new SetIsEmptyException();
        return lastOrDefault(elements);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> set) {
        TElement last = null;
        for(TElement element : set){
            last = element;
        }
        return last;
    }

    public static <TElement> Queryable<TElement> where(Iterable<TElement> elements,
                                                       Condition<? super TElement> condition) {
        assertNotNull(condition, "condition");
        return new WhereQuery<>(elements, condition);
    }

    private static <TElement> void assertNotNull(Object parameter, String parameterName) {
        if(parameter == null){
            throw new IllegalArgumentException(parameterName);
        }
    }


    public static <TElement, TResult> Queryable<TResult> select(Iterable<TElement> set,
                                                                Func1<? super TElement, TResult> targetSite) {
        return new SelectQuery<>(set, targetSite);
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

    public static <TElement> boolean contains(Iterable<? extends TElement> set, Object candidate) {
        for(TElement element : set){
            if(nullSafeEquals(element, candidate)){
                return true;
            }
        }

        return false;
    }

    public static <TElement> TElement withMinimum(Iterable<TElement> set, Func1<TElement, Number> valueSelector) {
        Iterator<TElement> iterator = set.iterator();
        if( ! iterator.hasNext()){
            throw new SetIsEmptyException();
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

        return returnable.toReadOnly();
    }

    public static <TTransformed, TElement>
    LinqingList<TTransformed> selectMany(Iterable<TElement> set, Func1<? super TElement, ? extends Iterable<TTransformed>> selector) {
        LinqingList<TTransformed> results = new LinqingList<TTransformed>();
        for (TElement element : set) {
            Iterable<? extends TTransformed> transformedValues = selector.getFrom(element);
            results.addAll(transformedValues);
        }

        return results.toReadOnly();
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, Iterable<? extends TElement> right){
        return union(left, right, CommonDelegates.<TElement>identity());
    }

    public static <TElement> Queryable<TElement> union(Iterable<? extends TElement> left, TElement... toInclude) {
        return union(left, Factories.asList(toInclude));
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

    public static <TDerived, TElement> LinqingList<TDerived> cast(Iterable<TElement> set) {
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
        return returnable.toReadOnly();
    }

    public static <TElement> boolean all(Iterable<TElement> set, Condition<? super TElement> condition) {

        for(TElement element : set){
            if( ! condition.passesFor(element)){
                return false;
            }
        }

        return true;
    }

    public static <TElement> boolean isSetEquivalentOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        if(left instanceof Collection && right instanceof Collection){
            if( ((Collection)left).size() != ((Collection)right).size()) return false;
        }

        final Map<TElement, TElement> leftHashes = new HashMap<>();
        //note, I'm not actually using the value for anything here, just the hashing key comparison.

        for(TElement leftElement : left){
            leftHashes.put(leftElement, null);
        }

        return Factories.asList(right).all(new Condition<TElement>() {
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
                : new ReadonlyLinqingList<>();
    }


    public static <TElement> Queryable<TElement> skipUntil(Iterable<? extends TElement> set,
                                                           Condition<? super TElement> toInclude) {
        Ref<TElement> firstPassingValue = new Ref<>();
        Iterator<? extends TElement> iterator = moveIteratorUpUntilConditionPasses(set.iterator(), toInclude, firstPassingValue);

        return firstPassingValue.target != null
                ? new LinqingList<>(new AmmendedIterator<>(firstPassingValue.target, iterator))
                : new ReadonlyLinqingList<>();
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

        if(set instanceof List){
            List<TElement> list = (List<TElement>) set;
            return new QueryAdapter.Iterable<>(new ListReverser<>(list));
        }
        else{
            return reverseViaStack(set);
        }
    }

    private static <TElement> Queryable<TElement> reverseViaStack(Iterable<TElement> set) {
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

    public static <TElement> boolean isSubsetOf(Iterable<TElement> left, Iterable<? extends TElement> right) {
        LinqingList<? extends TElement> rightFetched = Factories.asList(right);
        for(TElement leftElement : left){
            if ( ! rightFetched.contains(leftElement))
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

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> source, TElement... toExclude) {

        return new ExceptQuery.WithNaturalEquality<>(source, from(toExclude));
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> left, Iterable<? extends TElement> right) {
        return new ExceptQuery.WithNaturalEquality<>(left, right);
    }

    public static <TElement, TCompared> Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                                                                   Iterable<? extends TElement> membersToExclude,
                                                                   Func1<? super TElement, TCompared> comparableSelector) {

        return new ExceptQuery.WithComparable<>(originalMembers, membersToExclude, comparableSelector);
    }

    public static <TElement> Queryable<TElement> except(Iterable<? extends TElement> originalMembers,
                                                        Iterable<? extends TElement> membersToExclude,
                                                        Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new ExceptQuery.WithEquatable<>(originalMembers, membersToExclude, comparableSelector);
    }


    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right) {

        return new IntersectionQuery.WithNaturalEquality<>(left, right);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           TElement... right) {
        return new IntersectionQuery.WithNaturalEquality<>(left, Factories.from(right));
    }

    public static <TElement, TCompared> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                                      Iterable<? extends TElement> right,
                                                                      Func1<? super TElement, TCompared> comparableSelector) {

        return new IntersectionQuery.WithComparable<>(left, right, comparableSelector);
    }

    public static <TElement> Queryable<TElement> intersect(Iterable<? extends TElement> left,
                                                           Iterable<? extends TElement> right,
                                                           Func2<? super TElement, ? super TElement, Boolean> comparableSelector) {

        return new IntersectionQuery.WithEqualityComparator<>(left, right, comparableSelector);
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


    public static <TElement> Object[] toArray(Queryable<TElement> set) {
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
    public static <TElement, TDesired> TDesired[] toArray(Queryable<TElement> originalSet,
                                                          TDesired[] arrayTypeIndicator) {

        int size = originalSet.size();
        TDesired[] a = arrayTypeIndicator;
        Object[] elementData = originalSet.toArray();

        //copy-paste from java.util.ArrayList#toArray(T[] a)
        //... which is dumb since that means we create the array twice...
        assert false : "not implemented";

        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (TDesired[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public static <TElement> LinqingList<TElement> toList(Iterable<TElement> set) {
        return new LinqingList<>(set);
    }

    public static <TElement> TElement firstOr(Queryable<TElement> set,
                                              TElement alternative) {
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

    public static <TResult, TLeft, TRight>
    Queryable<TResult> join(Iterable<TLeft> left,
                            Iterable<TRight> right,
                            Func2<? super TLeft, ? super TRight, TResult> makeResult) {
        assert false : "not implemented";
        return null;
    }

    public static <TElement, TDesired> TDesired[] toArray(Iterable<TElement> entries,
                                                          Func1<Integer, TDesired[]> arrayFactory) {

        int size = size(entries);
        Object[] array = arrayFactory.getFrom(size);

        int index = 0;
        for(Object element : entries){
            array[index] = element;
        }

        return (TDesired[]) array;
    }

    public static <TElement> Queryable<TElement> distinct(Iterable<TElement> candidateWithDuplicates) {
        return new DistinctQuery.WithNaturalEquality<>(candidateWithDuplicates);
    }

    public static <TElement> TElement aggregate(Iterable<? extends TElement> set,
                                                Func2<TElement, TElement, TElement> aggregator) {
        assert false : "not yet implemented";
        return null;
    }

    public static <TAccumulate, TElement> TAccumulate aggregate(Iterable<? extends TElement> set,
                                                                TAccumulate seed,
                                                                Func2<TAccumulate, TElement, TAccumulate> aggregator) {
        assert false : "not yet implemented";
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public static <TElement> double average(Iterable<? extends TElement> set,
                                            Func1<? super TElement, Double> valueSelector) {
        cannotBeEmpty(set);
        double sum = 0.0;
        int size = size(set);

        for(TElement element : set){
            sum += valueSelector.getFrom(element);
        }

        return sum / size;
    }

    private static <TElement> void cannotBeEmpty(Iterable<TElement> set) {
        if(isEmpty(set)){
            throw new SetIsEmptyException();
        }
    }

    public static <TElement> boolean containsElement(Iterable<? extends TElement> set,
                                                     TElement candidate) {
        return contains(set, candidate);
    }

    public static <TElement> boolean any(Iterable<? extends TElement> set) {
        return size(set) != 0;
    }

    public static <TElement, TComparable> QueryableGroupSet<TElement> groupBy(Iterable<TElement> setToGroup,
                                                                              Func1<? super TElement, TComparable> groupByPropertySelector) {
//        return new GroupByQuery.WithComparable<>(setToGroup, groupByPropertySelector);
        assert false : "what?";
        return null;
    }

    public static <TElement> Queryable<Queryable<TElement>> groupBy(Iterable<TElement> setToGroup,
                                                                    Func2<? super TElement, ? super TElement, Boolean> groupMembershipComparator) {
        return new GroupByQuery.WithEqualityComparator<>(setToGroup, groupMembershipComparator);
    }

    public static <TElement> TElement firstOrDefault(Iterable<TElement> set) {
        Iterator<TElement> iterator = set.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static <TElement> TElement last(Iterable<TElement> set,
                                           Condition<? super TElement> condition) {
        return reversed(set).first(condition);
    }

    public static <TElement> TElement lastOrDefault(Iterable<TElement> set,
                                                    Condition<? super TElement> condition) {
        return reversed(set).firstOrDefault(condition);
    }
    public static <TElement> double min(Queryable<TElement> source, Func1<? super TElement,Double> valueSelector) {
        return extrema(source, valueSelector, Double.POSITIVE_INFINITY, Math::min).getKey();
    }

    public static <TElement> double max(Iterable<TElement> set, Func1<? super TElement, Double> valueSelector) {
        return extrema(set, valueSelector, Double.NEGATIVE_INFINITY, Math::max).getKey();
    }

    private static <TElement> Tuple<Double, TElement> extrema(Iterable<TElement> source,
                                                              Func1<? super TElement, Double> valueElementSelector,
                                                              double startingValue,
                                                              Func2<Double, Double, Double> valueContentionResolver) {
        cannotBeEmpty(source);

        double previousMax = startingValue;
        double currentMax = startingValue;
        TElement chosenElement = null;

        for(TElement element : source){
            Double value = valueElementSelector.getFrom(element);
            throwNPEIfNull("value selector", valueElementSelector, element, value);

            currentMax = valueContentionResolver.getFrom(currentMax, value);

            if(previousMax != currentMax){
                chosenElement = element;
                previousMax = currentMax;
            }

        }

        return new Tuple<>(currentMax, chosenElement);
    }

    private static <TElement> void throwNPEIfNull(String delegateDescription,
                                                  Func1<? super TElement, Double> valueSelector,
                                                  TElement element,
                                                  Double value) {
        if(value == null){
            throw new NullPointerException(
                    "The " + delegateDescription + " '" + valueSelector + "' " +
                    "returned " + Formatting.NullStringRepresentation + " " +
                    "for the element '" + Formatting.nullSafeToString(element) + "', " +
                    "and null is not comparable to other double values.");
        }
    }

    public static <TElement> TElement withMax(Queryable<TElement> source,
                                              Func1<? super TElement ,Double> valueSelector) {
        return extrema(source, valueSelector, Double.NEGATIVE_INFINITY, Math::max).getValue();
    }


    public static <TElement> TElement withMin(Queryable<TElement> source,
                                              Func1<? super TElement, Double> valueSelector) {
        return extrema(source, valueSelector, Double.POSITIVE_INFINITY, Math::min).getValue();
    }

    public static <TElement, TCompared extends Comparable<TCompared>> Queryable<TElement> orderBy(Queryable<TElement> set,
                                                                                                  Func1<? super TElement, TCompared> comparableSelector) {
        return new OrderByQuery<>(set, comparableSelector);
    }

    public static <TElement> Queryable<TElement> orderBy(Queryable<TElement> tElements,
                                                         Func2<? super TElement, ? super TElement, Integer> equalityComparator) {
        assert false : "not implemented";
        return null;
    }

    public static <TElement> double sum(Queryable<TElement> set,
                                        Func1<? super TElement, Double> valueSelector) {
        assert false;
        return 0;
    }

    public static <TElement> ReadonlyLinqingList<TElement> toReadOnly(Queryable<TElement> source) {
        return new ReadonlyLinqingList<>(source);
    }

    public static <TElement> LinqingSet<TElement> toSet(Queryable<TElement> source) {
        return new LinqingSet<>(source);
    }

    public static <TElement> Queryable<TElement> fetch(Queryable<TElement> source) {
        return new ReadonlyLinqingList<>(source);
    }

    public static <TKey, TElement> LinqingMap<TKey,TElement> toMap(Queryable<TElement> source,
                                                                   Func1<? super TElement,TKey> keySelector) {
        assert false;
        return null;
    }

    public static <TKey, TValue, TElement> LinqingMap<TKey,TValue> toMap(Queryable<TElement> tElements,
                                                                         Func1<? super TElement,TKey> keySelector,
                                                                         Func1<? super TElement,TValue> valueSelector) {
        assert false;
        return null;
    }

    public static <TElement> boolean isSingle(Iterable<TElement> source) {
        if(source instanceof Collection){
            return ((Collection)source).size() == 1;
        }

        Iterator<TElement> iterator = source.iterator();
        boolean hasFirst = iterator.hasNext();
        if ( ! hasFirst){
            return false;
        }
        iterator.next();
        boolean hasSecond = iterator.hasNext();
        return ! hasSecond;
    }

    public static <TElement> boolean isDistinct(Iterable<TElement> source) {

        if(source instanceof Set || source instanceof QueryableSet){
            return true;
        }

        int size = size(source);
        HashSet<TElement> set = new HashSet<>(size, 0.90F);

        for(TElement element : source){

            boolean modified = set.add(element);

            if ( ! modified){
                return false;
            }
        }
        return true;
    }
}

