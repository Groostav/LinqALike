package com.empowerops.linqalike;

import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.*;
import com.empowerops.linqalike.experimental.IList;

import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/**
 * SQL like interface for collections, akin to the Streams API.
 * <p>
 * <p>Queryable is designed for fluent use with delegates, allowing callers to <i>stack</i> an arbitrary number
 * of set operations in a single line of code, allowing for convienient access into many kinds of data.
 * <p>
 * <!-- usage example -->
 * <p>For example, lets say you had a flat list of customers and you wished to:
 * <ul>
 *      <li>get only the last names</li>
 *      <li>collect only information about seniors (customers with an age &gt; 65)</li>
 *      <li>grouped by their age</li>
 *      <li>ordered by the average purchase amount of that customer group</li>
 *      <li>and convert to a grid (a string[][]) for some legacy API</li>
 * </ul>
 * with Linq-A-Like, you would do that with:
 * <pre>{@code
 *   String[][] groupedNames = Factories.from(allCustomers)
 *       .where(customer -> customer.getAge() >= 65)
 *       .groupBy(Customer::getAge)
 *       .orderBy(group -> group.average(Customer::getPurchaseAmount))
 *       .select(group -> group.select(Customer::getLastName))
 *       .select(group -> group.toArray(String[]::new))
 *       .toArray(String[][]::new);
 * }</pre>
 * <p>
 * <p>This interface is implemented:
 * <ul>
 *      <li>as an interface with default implementations (akin to an abstract base-class)
 *      by {@link DefaultedQueryable}</li>
 *      <li>as a list by {@link com.empowerops.linqalike.LinqingList}</li>
 *      <li>as a set by {@link com.empowerops.linqalike.LinqingSet}</li>
 *      <li>as widened static methods by {@link Linq}</li>
 *      <li>and extended to the map portion of the collections framework
 *      by {@link com.empowerops.linqalike.QueryableMap}</li>
 * </ul>
 * <p>
 * <p>Most of this libraries documentation is here, but (very readable) unit tests can typically be found in either
 * <ul>
 *      <li>test/com/empowerops/linqalike/queries/[methodName]Fixture, if the return type is Queryable or</li>
 *      <li>test/com/empowerops/linqalike/immediates/[methodName]Fixture, if the return type is primiative</li>
 * </ul>
 * <p>
 * <!-- on Sets and Bags -->
 * <p>Queryable implementations generally allow any bag (collection with duplicates) to be treated as a set
 * (collection without duplicates) by <i>ignoring</i> all duplicates except the first one encountered. This means,
 * in set operations, queryables with the elements{A, A, B, C, B, D} are treated as the set {A, B, C, D}
 * (note the second A and second B are not included) for set operations. Any particular queryable can be asked
 * whether or not it contains duplicates (read: if it is a bag or a set) with the method {@link #isDistinct()}.
 *
 * <!-- on equality -->
 * <p>the {@link DefaultedQueryable} interface does not override equals, meaning <i>most</i> queryables will use
 * {@link Object#equals(Object)} (reference equality). This excludes the backing mutable implementations (eg
 * {@link com.empowerops.linqalike.LinqingSet} and {@link com.empowerops.linqalike.LinqingList}) since they
 * will likely provide whatever equals they inherit from the collections framework. the Queryable interface
 * does supply a couple means to explcitly test for equality: {@link #setEquals(Iterable)} and
 * {@link #sequenceEquals(Iterable)}. It also provides a number of ways to test for specific cases of set and
 * sequence inequality with {@link #isSubsetOf(Iterable)}, {@link #isSupersetOf(Iterable)} (Iterable)},
 * {@link #isSubsequenceOf(Iterable)}, and {@link #isSupersequenceOf(Iterable)}.
 *
 * <!-- on C# compatibility -->
 * <p>This interface is the documented portion of the Linq-A-Like implementation of C#'s Linq. In as many ways as
 * was convenient I followed
 * <a href="http://msdn.microsoft.com/en-us/library/vstudio/system.linq.enumerable_methods%28v=vs.100%29.aspx">MSDN's documentation of LINQ</a>.
 * From my experience there are no surprising differences between this implementation and Microsoft's,
 * however I cannot gaurentee that things things like the exact order of selector-execution in a call like
 * <code>aQueryable.orderBy(elem -&lt; elem.orderableThing())</code> is identical.
 * I strongly believe that there are minimal semantic differences between my implementation and Microsoft's.
 *
 * @author Geoff Groos on 06/09/13
 */
public interface Queryable<TElement> extends Iterable<TElement> {

    /**
     * Aggregates this queryable together with the specified aggregator function and seed value.
     *
     * <p><code>aggregate</code> will create an accumulator containing the value of the
     * first member of this queryable, and apply the function against the accumulator and
     * every subsequent member in this queryable, and yield the result.
     *
     * <p>eg:
     * <pre>{@code
     *   Queryable<String> names = Factories.from("Bob", "Jim", "Sam");
     *   String result = names.aggregate((accumulator, next) -> {
     *     System.out.println("accumuator:" + accumulator + ", next:" + next);
     *     return accumulator + "-" + next;
     *   });
     *   System.out.println("result is " + result);
     * }</pre>
     * would output:
     * <pre>{@code
     *   accumulator:Bob, next:Jim
     *   accumulator:Bob-Jim, next:Sam
     *   result is Bob-Jim-Sam
     * }</pre>
     *
     * <p>This means <code>aggregate</code> the effect of returning:
     * <ul>
     *     <li>{@link Optional#empty()} if this queryable is empty</li>
     *     <li>{@link #single()} if this queryable contains one element</li>
     *     <li><code>aggregator.doUsing(first(), second())</code> if this queryable contains two elements</li>
     *     <li><code>aggregator.doUsing(aggregator.doUsing(first(), skip(1).first()), skip(2).first() )</code>
     *     if this queryable contains three elements</li>
     *     <li><code>aggregator.doUsing(aggregator.doUsing(aggregator.doUsing((first(), skip(1).first()), skip(2).first()), skip(3).first())</code>
     *     if this queryable contains four elements</li>
     *     <li>and so on for many elements</li>
     * </ul>
     *
     * <p>more details about <code>aggregate</code> can be found in the docs
     * for the more general form, {@link #aggregate(Object, Func2)}.
     *
     * @param aggregator an aggregating function to apply against every element in this queryable,
     *                   if this queryable contains 2 or more elements.
     * @return the result of the aggregator function applied to each element if this queryable has many elements,
     *         <code>empty</code> if this queryable is empty, and <code>single()</code> if this queryable has one element.
     */
    Optional<TElement> aggregate(Func2<? super TElement, ? super TElement, ? extends TElement> aggregator);

    /**
     * Aggregates this queryable together with the specified aggregator function and seed value.
     *
     * <p><code>aggregate</code> will create an accumulator with the seed value provided,
     * and apply the function against the accumulator and the next element
     * in this queryable, for each element in this queryable, and yield the result.
     *
     * <p>eg:
     * <pre>{@code
     *   Queryable<String> names = Factories.from("Bob", "Jim", "Sam");
     *   String result = names.aggregate("seed", (accumulator, next) -> {
     *     System.out.println("accumuator:" + accumulator + ", next:" + next);
     *     return accumulator + "-" + next;
     *   });
     *   System.out.println("result is " + result);
     * }</pre>
     * would output:
     * <pre>{@code
     *   accumulator:seed, next:Bob
     *   accumulator:seed-Bob, next:Jim
     *   accumulator:seed-Bob-Jim, next:Sam
     *   result is seed-Bob-Jim-Sam
     * }</pre>
     *
     * <p>This means <code>aggregate</code> the effect of returning:
     * <ul>
     *     <li><code>seed</code> if this queryable is empty</li>
     *     <li><code>aggregator.doUsing(seed, first())</code> if this queryable contains one element</li>
     *     <li><code>aggregator.doUsing(aggregator.doUsing(seed, first()), second())</code>
     *     if this queryable contains two elements</li>
     *     <li><code>aggregator.doUsing(aggregator.doUsing(aggregator.doUsing(seed, first()), skip(1).first()), skip(2).first())</code>
     *     if this queryable contains three elements</li>
     *     <li><code>aggregator.doUsing(aggregator.doUsing(aggregator.doUsing(aggregator.doUsing(seed, first()), skip(1).first()), skip(2).first()), skip(3).first())</code>
     *     if this queryable contains four elements</li>
     *     <li>and so on for many elements</li>
     * </ul>
     *
     * <p>This method allows for the transformation of the entire queryable
     * (rather than individual elements as provided by {@link #select(Func1)})
     * into any other type. Some example implementations of aggregation is
     * {@link #count()}, {@link #average(Func1)}, and {@link #withMin(Func1)}.
     *
     * <p>For example, a sum function on a list can be implemented as
     * <pre>{@code
     * int size = list.aggregate(0, (accumulator, next) -> accumulator + 1);
     * System.out.println("result is " + result);
     * }</pre>
     *
     * <p>if the accumulator is the same type as an element (for example,
     * if doing some kind of aggregation of a list of decimal numbers
     * that itself produces a decimal number, like {@link #average(Func1)} )
     * the {@link #aggregate(Func2)} may be preferable as it does not require
     * a seed.
     *
     * <p>Semantically <code>aggregate</code> is identical to
     * {@link java.util.stream.Stream#reduce(BinaryOperator)},
     * and is commonly known as <code>fold</code> in the functional community.
     *
     * <p>this method is preferable to {@link #forEach(Consumer)} in the case where
     * the transform can produce a scalar and combinable result (such as a string
     * or double) or is a parsing function that returns some kind of state in a
     * state machine. In these cases, using <code>aggregate</code>, you do not
     * typically need to affect some result as part of a closure or mutable value
     * declared outside the for loop block.
     *
     * <p>If we wanted to make a copy of a list
     * <pre>{@code
     *  //using Java-7
     *  List<Customer> sameCustomers = new ArrayList<>(); //mutable variable out-side block
     *  for(Customer customer : customers){
     *    sameCustomers.add(customer);
     *  }
     *
     *  //using forEach method, Java-8
     *  List<Customer> sameCustomers = new ArrayList<>();
     *  customers.forEach(sameCustomers::add);
     *
     *  //using aggregate & IList, Java-8
     *  IList<Customer> sameCustomers = customers.aggregate(IList.empty(), IList::with);
     * }</pre>
     *
     * <p>This isn't itself very helpful (especially in light of the
     * {@link IList#with(Iterable)} and {@link java.util.List#addAll(Collection)} methods),
     * but for a parsing function and other combination functions, <code>aggregate</code>
     * encourages developers to compopse their code with pure methods
     * (methods without side-effects), which is typically worth aiming for.
     *
     * @param aggregator the aggregator function to be applied against each element
     * @return the result of aggregating all elements in this queryable together,
     *         or <code>empty</code> if this queryable is empty
     */
    <TAccumulate> TAccumulate aggregate(TAccumulate seed,
                                        Func2<? super TAccumulate, ? super TElement, TAccumulate> aggregator);

    /**
     * Determines if all elements in the set pass the given condition.
     *
     * <p>note that if this is called on an empty collection it will return true.
     * In other words, all members of the empty set pass a given condition.
     *
     * @param condition the condition to test each member in the set with
     * @return <tt>true</tt> iff all members of the set pass the supplied condition, or the set is empty
     */
    boolean all(Condition<? super TElement> condition);

    /**
     * Determines if the set contains any elements.
     * <p>
     * <p>functionally identical to <code>! source.isEmpty()</code>
     *
     * @return true iff the set contains any elements
     */
    boolean any();

    /**
     * Determines if the set contains any elements that pass the given condition.
     *
     * @param condition the condition that must be satisfied
     * @return <tt>true</tt> if there is at least one element in the set that passes the supplied condition
     */
    boolean any(Condition<? super TElement> condition);


    /**
     * Determines the average value of the set, where each elements value is found with the supplied valueSelector
     *
     * @param valueSelector the transform needed to get a scalar value from each member of the set
     * @return the average value of the set as per the supplied value selector
     */
    double average(Func1<? super TElement, Double> valueSelector);


    /**
     * Returns a cast of this queryable to a queryable of the desired type.
     * <p>
     * <p><b>This method may lead to heap pollution</b> as it is equivalent to
     * <code>(Queryable&lt;TDesired&gt;)source}</code>
     * This method should only be called if, by some means not known to the static type system,
     * the caller is <i>certain</i> that this collection only contains members that implement
     * <tt>TSuper</tt>. If they do not, a {@link java.lang.ClassCastException} will be thrown
     * when the problem member is iterated to. Please consider using {@link #ofType(Class)} or
     * doing a check to ensure this cast is safe with
     * <code>
     * collection.all(desiredType::isInstance)
     * </code>
     * to avoid heap pollution.
     *
     * @return a Queryable statically parameterized to the specified type. No run-time type checking is performed.
     */
    <TDesired> Queryable<TDesired> cast();

    /**
     * Returns a cast of this queryable to a queryable of the desired type.
     * <p>
     * <p>This method will not pollute the heap but it will cause
     * lazy {@link java.lang.ClassCastException}s where {@link #cast()} would pollute the heap.
     * The method can be used in conjunction with {@link #immediately()} to force any exceptions
     * to be thrown at the time the queryable is created, rather than having them thrown lazily
     * <p>
     * <p>for example, consider
     * <pre>{@code
     *     Queryable<Number> numbers = new LinqingList<>(1.0D, 2.0D, 3.0D, /*problem element/*4L);
     *     Queryable<Double> pollutedDoubles = numbers.cast(); //uses javas type inference
     * }</pre>
     */
    <TDesired> Queryable<TDesired> cast(Class<TDesired> desiredType);

    /**
     * Determines if this Queryable contains the supplied candidate element.
     * <p>
     * This method is a typed <code>contains</code> method that will likely protect users from common
     * wrong argument bugs relating to the {@link java.util.Collection#contains(Object)}
     * method.
     *
     * @param candidate element whose presence in this collection is to be determined
     * @return true of the supplied candidate is in this collection
     */
    boolean containsElement(TElement candidate);

    /**
     * Determines if this queryable contains the supplied candidate element using the
     * mehtod of equality specified.
     * <p>
     * <p>for example <pre>{@code
     * Factories.from("John", "Jimmy", "Kenneth", "Edwin", "Jackie", "Amy", "Andrea", "Jay")
     * .contains("JXX", (left, right) -> left.startsWith(right.charAt(0)) && left.length == right.length)
     * }</pre>
     * would return <tt>true</tt> as the name "Jay" matches that condition.
     *
     * @param candidateElement the element whose membership in this queryable is to be determined.
     * @param equalityComparer the method of equality to use
     * @return true if the specified member is in this set according to the supplied method of equality
     */
    boolean containsElement(TElement candidateElement, EqualityComparer<? super TElement> equalityComparer);


    /**
     * Gets the number of elements in this queryable. Identical to {@link #size()}
     *
     * @return the number of elements in this queryable
     */
    int count();

    /**
     * Determines the count of elements that pass the supplied condition.
     *
     * @param condition a condition that elements must pass to be included in the count
     * @return the number of elements that passed the given condition
     */
    int count(Condition<? super TElement> condition);


    /**
     * Returns a Queryable that contains no duplicates.
     * <p>
     * <p>A duplicate is found by using natural equality comparison.
     * <p>
     * <p>The returned Queryable will contain each element in the source Queryable in the
     * same order as the source Queryable, but with only the first elements of any duplicate
     * elements in its result.
     *
     * @return the source set skipping any duplicates
     */
    Queryable<TElement> distinct();

    /**
     * Returns a Queryable that contains no duplicates as specified by comparing elements transformed
     * through the selector.
     * <p>
     * <p>A duplicate is found by using the natural equality on the projection found using
     * the comparableSelector.
     *
     * @param comparableSelector the projection to use to find comparable values from each element
     * @param <TCompared>        the type returned by the comparableSelector, and the type that is compored
     * @return the source set skipping any duplicates
     */
    <TCompared> Queryable<TElement> distinct(Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Returns a Queryable that contains no duplicates as specified by the supplied equality comparison.
     * If any duplicates are found, the first is included in the result.
     *
     * @param equalityComparison the comparison method used to detect duplicates
     * @return this queryable as a proper set
     */
    Queryable<TElement> distinct(EqualityComparer<? super TElement> equalityComparison);

    //TODO docs, smartly.
    Queryable<TElement> except(TElement toExclude);

    Queryable<TElement> except(TElement toExclude0, TElement toExclude1);

    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2);

    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2, TElement toExclude3);

    Queryable<TElement> except(TElement toExclude0, TElement toExclude1, TElement toExclude2, TElement toExclude3, TElement toExclude4);

    /**
     * Returns a Queryable that does not contain the members of <code>toExclude</code>, as determined
     * by default equality.
     *
     * @param toExclude the members that are to be excluded from the resulting set
     * @return this query's members without the members specified by <code>toExclude</code>
     */
    Queryable<TElement> except(TElement... toExclude);

    /**
     * Returns a Queryable that does not contain the members of <code>toExclude</code>, as determined
     * by default equality.
     *
     * @param toExclude the members that are to be excluded from the resulting set
     * @return this query's members without the members specified by <code>toExclude</code>
     */
    Queryable<TElement> except(Iterable<? extends TElement> toExclude);

    /**
     * Returns a Queryable that does not contain the members of <code>toExclude</code> as determined
     * by the supplied equalityComparison.
     *
     * @param toExclude          the members that are to be excluded from the resulting set
     * @param equalityComparison the method that objects are to be compared compared by.
     * @return this query's members without the members in <code>toExclude</code>
     */
    Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                               EqualityComparer<? super TElement> equalityComparison);

    /**
     * Returns a Queryable that does not contain the members of <code>toExclude</code> as determined
     * by comparing the results of a transform on the candidate members of this query and <code>toExclude</code>.
     *
     * @param toExclude          the members that are to be excluded from the resulting set
     * @param comparableSelector the transform to apply to each element in this and <code>toExclude</code>
     *                           that will retrieve the object to be compared.
     * @param <TCompared>        the type of the object used for comparison and returned by
     *                           the <code>comparableSelector</code>
     * @return this query's members without the members in <code>toExclude</code>
     */
    <TCompared> Queryable<TElement> except(Iterable<? extends TElement> toExclude,
                                           Func1<? super TElement, TCompared> comparableSelector);


    /**
     * Gets the <i>first</i> element in this Queryable, as defined by the iterator,
     * or throws a {@link SetIsEmptyException} if this query is empty.
     * Functionally similar to <code>thisQueryable.iterator().next()</code>
     */
    TElement first();

    /**
     * Gets the <i>first</i> element that matches the supplied condition, or
     * throws an {@link SetIsEmptyException} if no such element is available.
     *
     * @param condition the condition that will be satisfied by the returned element
     * @return the "first" element in the set that matches the supplied condition.
     */
    TElement first(Condition<? super TElement> condition);

    /**
     * Gets the <i>first</i> <code>count</code> elements in the set, as determined by
     * the iterator, or the maximum number of elements available if <code>count</code>
     * is greater than this query's size.
     *
     * @param count the maximum number of elements to return
     * @return the front subset of this query containing either <code>count</code> members,
     * or all elements in this query.
     */
    Queryable<TElement> first(int count);

    /**
     * Gets the first element contained in this query, or <code>Queryable.empty()</code> if this query is empty.
     */
     Optional<TElement> firstOrDefault();

    /**
     * Gets the first element contained in this query that matches the supplied condition, or null
     * if no such element exists in this query.
     *
     * @param condition the condition that will be satisfied by the returned element
     */
    Optional<TElement> firstOrDefault(Condition<? super TElement> condition);

    /**
     * Gets the <i>first</i> element in this Queryable, as defined by the iterator,
     * or throws a {@link SetIsEmptyException} if this query is empty.
     * Functionally similar to <code>thisQueryable.iterator().next()</code>
     */
    TElement second();

    /**
     * Gets the <i>first</i> element that matches the supplied condition, or
     * throws an {@link SetIsEmptyException} if no such element is available.
     *
     * @param condition the condition that will be satisfied by the returned element
     * @return the "first" element in the set that matches the supplied condition.
     */
    TElement second(Condition<? super TElement> condition);

    /**
     * Gets the first element contained in this query, or <code>null</code> if this query is empty.
     */
    Optional<TElement> secondOrDefault();

    /**
     * Gets the first element contained in this query that matches the supplied condition, or null
     * if no such element exists in this query.
     *
     * @param condition the condition that will be satisfied by the returned element
     */
    Optional<TElement> secondOrDefault(Condition<? super TElement> condition);


    /**
     * Returns a set of groups, where each group contains members that are equal by comparing
     * the results of the <code>equatableSelector</code>.
     *
     * <p>If
     * <code>nullSafeEquals(equatableSelector.getFrom(oneElement), equatableSelector.getFrom(anotherElement))</code>
     * is <tt>true</tt>, then
     * <code>oneElement</code> and <code>anotherElement</code> will be in the same group,
     * and only that group.
     *
     * <p>this method does not do any form of de-duplication
     *
     * @param equatableSelector a transform that will be used to obtain a equatable component of
     *                          each element, used to determine group membership.
     * @param <TComparable>     the resulting type of the <code>equatableSelector</code>, and the type
     *                          whose natural equality will be used to determine group membership.
     * @return a set of groups (a "jagged grid"), with each member of the group being equal as
     * per the <code>equatableSelector</code>
     */
    <TComparable> Queryable<Queryable<TElement>> groupBy(Func1<? super TElement, TComparable> equatableSelector);

    /**
     * Returns a set of groups, where each group contains members that are equal by comparing
     * the results of the <code>equatableSelector</code>.
     *
     * <p>If
     * <code>equalityComparison.equals(oneElement, anotherElement)</code> returns <tt>true</tt>,
     * <code>oneElement</code> and <code>anotherElement</code> will be in the same group,
     * and only that group.
     *
     * <p>this method does not do any form of de-duplication
     *
     * @param equalityComparison a comparator that will be used to determine if two elements are in the same group.
     * @return a set of groups (a "jagged grid"), with each member of the group being equal as
     * per the <code>equalityComparison</code>
     */
    Queryable<Queryable<TElement>> groupBy(EqualityComparer<? super TElement> equalityComparison);

    /**
     * Gets the intersection of this queryable with the elements in <code>toInclude</code>
     *
     * @param toInclude the elements to be intersected
     * @return the intersection of this queryable and the specified elements
     */
    Queryable<TElement> intersect(TElement... toInclude);

    /**
     * Gets the intersection of this queryable with the elements in <code>toInclude</code>
     *
     * @param toInclude the elements to be intersected
     * @return the intersection of this queryable and the specified elements
     */
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude);

    /**
     * Gets the intersection of this queryable and the elements in <code>toInclude</code>, where eqality is
     * defined by the comparing the result of <code>comparableSelector</code>. Each element from this queryable
     * such that
     * <code>nullSafeEquals(comparableSelector.getFrom(elementOfThisQueryable), comparableSelector.getFrom(anyElementOfToInclude))</code>
     * is true will be in the resulting queryable.
     *
     * @param toInclude          the elements to be intersected
     * @param comparableSelector the transform to use to get equatable values from elements
     * @param <TCompared>        the type compared
     * @return the intersection of this queryable and the elements in <code>toInclude</code>
     */
    <TCompared> Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                              Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Gets the intersection of this queryable and the elements in <code>toInclude</code>, where
     * <code>equalityComparison</code> is used to determine membership in the resulting intersection.
     * Each element of this queryable such that
     * <code>equalityComparison.equals(elementOfThisQueryable, anyElementOfToInclude)</code>
     * is true will be in the resulting queryable.
     *
     * @param toInclude          the elements to be intersected
     * @param equalityComparison the method that objects are to be compared using
     * @return the intersection of this queryable and the elements in <code>toInclude</code>
     */
    Queryable<TElement> intersect(Iterable<? extends TElement> toInclude,
                                  EqualityComparer<? super TElement> equalityComparison);

    /**
     * Gets the last element in this queryable or throws a {@link SetIsEmptyException} if
     * the set is empty. The last element is defined as the last element returned by
     * the iterator or the element at the index <code>size() - 1</code>.
     *
     * @return the last element in this queryable
     */
    TElement last();

    /**
     * Gets the last element in this queryable that meets the supplied condition, or throws
     * a {@link SetIsEmptyException} if the set contains no elements that match the supplied condition.
     * The last element that passes the condition is the element that passes the condition with
     * the highest index.
     *
     * @param condition the condition that will be satisfied by the returned element
     * @return the last element in this queryable that passes the given condition.
     */
    TElement last(Condition<? super TElement> condition);

    /**
     * Gets the the last <code>count</code> elements in the set, in the order defined by this queryable.
     *
     * @param count the maximum number of elements to return if available
     * @return the last <code>count</code> elements if they're available
     */
    Queryable<TElement> last(int count);

    /**
     * Gets the last element in this queryable if it's available, otherwise it returns <tt>null</tt>.
     */
    Optional<TElement> lastOrDefault();

    /**
     * Gets the last element in this queryable that passes the specified condition if one exists,
     * or null if it does not.
     *
     * @param condition the condition that must be satisfied
     */
    Optional<TElement> lastOrDefault(Condition<? super TElement> condition);


    /**
     * Gets the maximum of the value provided by the <code>valueSelector</code> against each of the elements in
     * this queryable.
     * <p>
     * <p>for example, for:
     * <pre>{@code
     *     List<Person> people = arrays.asList(
     *         new Person(){{age = 23; name = "Ben";}},
     *         new Person(){{age = 42; name = "Elizabeth"}},
     *         new Person(){{age = 25; name = "Tom";}});
     *     int oldest = people.max(person -> person.age);
     * }</pre>
     * the value for <code>oldest</code> would be <code>42</code>
     * <p>
     * <p>a more general version of <code>max</code> is {@link #aggregate(Func2)}},
     * which <code>max</code> delegates to.
     * <p>
     * <p>for a method that returns the element with that value rather than simply the value itself,
     * use {@link #withMax(com.empowerops.linqalike.delegate.Func1)}
     *
     * @param valueSelector a transform that gets a comparable value from each element in this queryable
     * @return the maximum value in this queryable as per the value provided by applying <code>valueSelector</code>
     * to each element, or an {@link Optional#empty()} iff <tt>this</tt> queryable is empty
     */
    <TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Func1<? super TElement, TCompared> valueSelector);

    /**
     * @param valueSelector a transform that gets a comparable value from each element in this queryable
     * @return the element with the maximum value in this queryable as per the value provided by
     * applying <code>valueSelector</code>
     */
    <TCompared extends Comparable<TCompared>>
    Optional<TElement> withMax(Func1<? super TElement, TCompared> valueSelector);

    /**
     * Gets the minimum of the value provided by the <code>valueSelector</code> against each of the elements in
     * this queryable.
     * <p>
     * <p>for example, for:
     * <pre>{@code
     *     List<Person> people = arrays.asList(
     *         new Person(){{age = 23; name = "Ben";}},
     *         new Person(){{age = 42; name = "Elizabeth"}},
     *         new Person(){{age = 25; name = "Tom";}});
     *     int youngest = people.min(person -> person.age);
     * }</pre>
     * the value for <code>youngest</code> would be <code>23</code>
     * <p>
     * <p>a more general version of <code>min</code> is {@link #aggregate(Func2)}},
     * which <code>min</code> delegates to.
     *
     * @param valueSelector a transform that gets a comparable value from each element in this queryable
     * @return the minimum value in this queryable as per the value provided by applying <code>valueSelector</code>
     * to each element, or an {@link Optional#empty()} iff <tt>this</tt> queryable is empty
     */
    <TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Func1<? super TElement, TCompared> valueSelector);

    /**
     * @param valueSelector a transform that gets a scalar value from each element in this queryable
     * @return the element with the minimum value in this queryable as per the value provided by
     * applying <code>valueSelector</code>
     */
    <TCompared extends Comparable<TCompared>>
    Optional<TElement> withMin(Func1<? super TElement, TCompared> valueSelector);


    /**
     * Gets all the elements in this queryable that are instances of the type specified.
     * <p>
     * <p>null is defined not to be an instance of anything, meaning that using this method
     * will remove all nulls in the resutling <code>queryable</code>
     * <p>
     * <p>If you wish to cast each element in this queryable to the specified type, and have
     * an exception raised if one element is not of the correct type, consider using
     * <code>thisQueryable.&lt;DesiredClass&gt;Cast().immediately()</code>, which will raise a
     * runtime exception of one or more of the elements is not of the correct type.
     *
     * @param desiredClass       the minimum type that each element in the resulting subset will be
     * @param <TElementSubclass> static type of <code>desiredClass</code>
     * @return a subset of this queryable containing only instances of <code>TElementSubclass</code>
     */
    <TElementSubclass extends TElement>
    Queryable<TElementSubclass> ofType(Class<TElementSubclass> desiredClass);


    /**
     * Gets a queryable ordered (in ascending order) by the result from applying the
     * comparable selector against each element. The order of any duplicates is maintained.
     * <p>
     * <p>for example:<pre>
     * Factories.from(new RankedObject(){{ rank = 1; }}, new RankedObject(){{ rank = 5; }}, new RankedObject(){{ rank = 3; }}
     * .orderBy(RankedObject::getRank);
     * }
     * </pre>
     * <p>
     * would return the set {[RankedObject rank=1], [RankedObject rank=3], [RankedObject rank=5]}
     *
     * @param comparableSelector a transform that provides a comparable value for each element
     * @param <TCompared>        the type of the compared object
     * @return a new queryable that is this queryable in ascending order of the values provided
     * by <code>comparableSelector</code>
     */
    <TCompared extends Comparable<TCompared>>
    Queryable<TElement> orderBy(Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Gets a queryable ordered (in ascending order) by the specified comparator.
     *
     * @param equalityComparator the comparator to use when ordering the elements
     * @return a new queryable that is htis queryable in ascending order as per the
     * results of <code>equalityComparator</code>
     */
    Queryable<TElement> orderBy(Comparator<? super TElement> equalityComparator);


    /**
     * Gets all possible adjacent pairs in this queryable using <tt>null</tt> as the left neighbour
     * for the first element and <tt>null</tt> as the right neighbour for the last element.
     *
     * @return a new queryable containing every a tuple for every 2 adjacent elements in this queryable
     */
     BiQueryable<TElement, TElement> pairwise();

    /**
     * Gets all possible adjacent parirs in this queryable invoking the supplied factory to get
     * the left neighbour of the first element and again for the right neighbour of the last element.
     *
     * @param defaultFactory the factory that will provide the left and right neighbours of the
     *                       first and last element respectively.
     * @return a new queryable containing every a tuple for every 2 adjacent elements in this queryable
     */
     BiQueryable<TElement, TElement> pairwise(Func<? extends TElement> defaultFactory);


    /**
     * @return a new queryable containing all elements in this queryable in reverse order.
     */
    Queryable<TElement> reversed();


    /**
     * Gets a set of items by applying specified selector against each element in
     * this queryable.
     * <p>
     * <p>For example <pre>{@code
     * Factories.from("Charles", "Bob", "Ken", "Betty", "Justin")
     * .select(elem -> elem.length)
     * }</pre>
     * would return a Queryable&lt;Integer&gt; containing {7, 3, 3, 5, 6}
     * <p>
     * <p><b>No constructor calls: The lazy behaviour of <code>select</code> causes very strange behaviour
     * for selector calls to <code>new</code></b> if not followed by an iterating factory (such as
     * {@link #immediately()} or {@link #toList()}).
     * <p>
     * The following code block will call the <code>DomainType</code> constructor <i>3 times</i>.
     * <pre>{@code
     * Queryable<DomainType> objects = Factories.from("first", "second").select(DomainType::new);
     * DomainType first = objects.first();
     * DomainType second = objects.reversed().last();
     * }</pre>
     * The reason for the extra constructor call is the behaviour of first(), which will call the constructor,
     * but Linq will <i>not</i> cache the result. So when the next line's <code>skip()</code> operator is
     * called, it will call the constructor on the element "first" again so that it can be skipped, and then
     * call the constructor a third time to make the element for "second". As proof of this behaviour,
     * <code>first == second</code> is <tt>false</tt>.
     * <p>
     * <p>To get the more expected behaviour we must iterate through the list. You can use {@link #toList()},
     * but if you do not specifically need results in a list form, I thought this confusing, so you may use
     * the {@link #immediately()} method like this:
     * <pre>{@code
     * Queryable<DomainType> objects = Factories.from("first", "second")
     * .select(DomainType::new)
     * .immediately();
     * DomainType first = objects.first();
     * DomainType second = objects.skip(1).first();
     * }</pre>
     *
     * @param selector       the transform to apply against each element in this queryable
     * @param <TTransformed> the type supplied by the <code>selector</code> and the type of the
     *                       resulting queryable
     * @return a queryable containing the result of applying the <code>selector</code> against
     * each element in this queryable
     */
    <TTransformed> Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector);

    /**
     * Applies the transform against each element in this queryable, returning a zipped version of this queryable
     * and the result of the transform.
     *
     * <p>this transform is similar to {@link #select(Func1)} in that it will apply the supplied transform,
     * (lazily) against each element in this queryable. This method can be used idomatically with
     * {@link BiQueryable#popSelect()} to apply some kind of other transform
     * without loosing the shape of the initial queryable.
     *
     * <p>eg
     * <pre>{@code
     * Queryable<Customer> vars = sourceVars
     *       .pushSelect(VariableSymbol::getName)
     *       .where((cust, name) -> targetName.equals(name))
     *       .popSelect()
     *       .singleOrDefault()
     * }</pre>
     *
     * @param selector the selector to apply against each element in <tt>this</tt> queryable
     * @param <TTransformed> the type supplied by the <code>selector</code> and the type of
     *                      {@link BiQueryable#rights()} in the result.
     * @return A <code>biqueryable</code> each pair containing an element in <tt>this</tt> queryable
     *         and the result of applying <code>selector</code> against it, in the order of this queryable.
     */
    <TTransformed> BiQueryable<TElement, TTransformed> pushSelect(Func1<? super TElement, TTransformed> selector);

    /**
     * Gets a set of items by aggregating the result from the specified selector applied against
     * each element in this queryable.
     * <p>
     * <p>akin to {@link java.util.stream.Stream#map(java.util.function.Function)}
     * <p>
     * <p>For example <pre>{@code
     * Factories.from(new Persion(){{ children = {"Jeff", "James"}; }}, new Person(){{ children = {"Emma", "Kevin", "Thomas"}; }})
     * .selectMany(person -> person.children)
     * }</pre>
     * would return a Queryable&lt;String&gt; containing {"Jeff", "James", "Emma", "Kevin", "Thomas"}
     * <p>
     * <p><b>Note that the no-constructor rule given in {@link #select} applies here too.</b>
     *
     * @param selector       the transform to first apply against each element in this queryable and
     *                       then aggregate into a single result
     * @param <TTransformed> the type supplied by the transform and the type of the resulting queryable
     * @return a queryable containing the aggregated results of applying the specified transform against
     * each element
     */
    <TTransformed> Queryable<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector);

    /**
     * Gets a set of items by aggregating the result from the specified selector applied against
     * each element in this queryable, but including the original item that provided that set as the left side
     * of a tuple, with the selements retrieved from the selector as the right side of the tuple
     *
     * @param selector       the transform to first apply against each element in this queryable and
     *                       then aggregate into a single result
     * @param <TTransformed> the type supplied by the transform and the type of the right side of the resulting biqueryable
     * @return a biqueryable containing pairs each entry's left containing a member of this queryable and each entry's
     *         right containing an element supplied by the selector
     */
    <TTransformed> BiQueryable<TElement, TTransformed> pushSelectMany(Func1<? super TElement, ? extends Iterable<? extends TTransformed>> selector);

    /**
     * Gets the only element in this queryable if this queryable contains exactly one element. A
     * {@link SetIsEmptyException} is thrown if the set is empty, and an
     * {@link SingletonSetContainsMultipleElementsException} if this set contains more than one element.
     * <p>
     * <p>This method is preferrable to {@link #first()} for defensive coding.
     *
     * @return the only element in this queryable if this queryable contains exactly one element.
     */
    TElement single();

    /**
     * Gets the only element in this queryable that matches the specified condition if this
     * queryable contains exactly one element that matches the specified condition. A
     * {@link SetIsEmptyException} is thrown if the set is empty, and an
     * {@link SingletonSetContainsMultipleElementsException} if this set contains more than one element
     * that passes the <code>uniqueConstraint</code>
     * <p>
     * <p>This method is preferrable to {@link #first(Condition)} for defensive coding.
     *
     * @param uniqueConstraint a condition that will pass for exactly one element in this queryable.
     * @return the one and only element in this queryable that passes <code>uniqueConstraint</code>
     */
    TElement single(Condition<? super TElement> uniqueConstraint);

    /**
     * Gets the only element in this queryable if this queryable is not empty, else <tt>null</tt>
     * is returned. If this queryable contains more than one element a
     * {@link SingletonSetContainsMultipleElementsException} is thrown.
     * <p>
     * <p>This method is preferrable to {@link #firstOrDefault()} for defensive coding.
     *
     * @return the single element in this queryable, or null if this queryable is empty.
     */
     Optional<TElement> singleOrDefault();

    /**
     * Gets the only element in this queryable that passes <code>uniqueConstraint</code>, or null if
     * this set is empty. If more than one element pass the <code>uniqueConstraint</code> a
     * {@link SingletonSetContainsMultipleElementsException} is thrown.
     * <p>
     * <p>This method is preferrable to {@link #firstOrDefault(Condition)} for defensive coding.
     *
     * @param uniqueConstraint a condition that will pass for exactly one element in this queryable
     * @return the one element passing the <code>uniqueConstraint</code>, or null if this queryable is empty
     */
     Optional<TElement> singleOrDefault(Condition<? super TElement> uniqueConstraint);


    /**
     * Determines if this queryable has set equality with the specified elements. Two collections are set-equal
     * iff every element in each set is in the other set, and both sets have the same size.
     * Note that this means set equality does <i>not</i> consider order.
     *
     * @param otherCollection the set to test for set equality
     * @return <tt>true</tt> if this queryable is set equivalent to the specified elements
     */
    boolean setEquals(Iterable<? extends TElement> otherCollection);

    /**
     * Determines if this queryable has set equality with the specified elements using the values
     * provided by <code>equatableSelector</code> with each element to determine equality.
     * Two collections are equal iff every element in each set is in the other set,
     * and both sets have the same size.
     * Note that this means set equality does <i>not</i> consider order.
     *
     * @param otherCollection   the set to test for set equality
     * @param equatableSelector a transform that will provide an equatable value for each element
     * @param <TCompared>       the type returned by the transform and the type used to determine equality
     * @return <tt>true</tt> if this queryable is set equivalent to the specified <code>otherCollection</code>
     * by comparing the values provided by the specified <code>equatableSelector</code>
     */
    <TCompared> boolean setEquals(Iterable<? extends TElement> otherCollection, Func1<? super TElement, TCompared> equatableSelector);

    /**
     * Determines if this queryable has set equality with the specified elements
     * using the specified <code>equalityComparer</code> to determine equality.
     * Two collections are equal iff every element in each set is in the other set,
     * and both sets have the same size.
     * Note that this means set equality does <i>not</i> consider order.
     *
     * @param otherCollection  the set to test for set equality
     * @param equalityComparer the method of equality to use
     * @return <tt>true</tt> if this queryable is set equivalent to the specified <code>otherCollection</code>
     * where equality is determined by the specified <code>equalityComparer</code>
     */
    boolean setEquals(Iterable<? extends TElement> otherCollection,
                      EqualityComparer<? super TElement> equalityComparer);

    /**
     * Determines if this queryable has sequence equality with the specified elements. Two collections are
     * sequence-equals if every element in each collection is in the other collection at the same position.
     * This makes sequence equality an order-enforcing version of set equality.
     *
     * @param otherOrderedCollection the other collection to test for sequence equality against
     * @return <tt>true</tt> if this queryable is sequence equivalent to the specified
     * <code>otherOrderedCollection</code>
     */
    boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection);

    /**
     * Determines if this queryable has sequence equality with the specified elements using the values
     * provided by <code>equatableSelector</code> with each element to determine equality. Two collections are
     * sequence-equals if every element in each collection is in the other collection at the same position.
     * This makes sequence equality an order-enforcing version of set equality.
     *
     * @param otherOrderedCollection the other collection to test for sequence equality against
     * @param comparableSelector     a transform that will provide an equatable value for each element
     * @param <TCompared>            the type returned by the transform and the type used to determine equality
     * @return <tt>true</tt> if this equeryable is sequence-equivalent to the specified
     * <code>otherOrderedCollection</code>
     */
    <TCompared> boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection,
                                       Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Determines if this queryable has sequence equalitywith the specified elements
     * using the specified <code>equalityComparer</code> to determine equality. Two collections are
     * sequence-equals if every element in each collection is in the other collection at the same position.
     * This makes sequence equality an order-enforcing version of set equality.
     *
     * @param otherOrderedCollection the other collection to test for sequence equality against
     * @param equalityComparer       the method of equality to use
     * @return <tt>true</tt> if this equeryable is sequence-equivalent to the specified
     * <code>otherOrderedCollection</code>
     */
    boolean sequenceEquals(Iterable<? extends TElement> otherOrderedCollection,
                           EqualityComparer<? super TElement> equalityComparer);


    /**
     * Gets a queryable that doesn't contain the elements at the front of this queryable that pass the
     * specified <code>toExclude</code> condition. The elements given in the return value contain the first
     * value (as per the order given by the iterator) to fail the <code>toExclude</code> condition, and all
     * remaining elements given by the iterator. The resulting queryable is the empty set if the condition
     * holds true for all elements in this queryable.
     *
     * @param toExclude a condition that indicates which elements at the front are to be excluded
     *                  from the result
     * @return a queryable containing the first element to fail the spcified <code>toExclude</code> condition,
     * and all subsequent elements (regardless of whether or not they pass the condition).
     */
    Queryable<TElement> skipWhile(Condition<? super TElement> toExclude);

    /**
     * Gets a queryable containing the first <code>numberToSkip</code> elements in this queryable, or
     * the empty set the number to skip is greater than the number of elements contained in this queryable.
     * element order is determined by the iterator.
     *
     * @param numberToSkip the maximum number of elements to skip
     * @return the elements in the collection that are after the first <code>numberToSkip</code> elements,
     * or the empty set if there are to few elements.
     */
    Queryable<TElement> skip(int numberToSkip);


    /**
     * Gets the sum of the scalar value provided for each element in this queryable,
     * or 0.0 if <tt>this</tt> queryable is empty
     *
     * @param valueSelector a transform that gets a scalar value from each element in this queryable
     * @return the sum of the scalar values from each element in this queryable
     */
    double sum(Func1<? super TElement, Double> valueSelector);


    /**
     * @return a readonly List containing all the elements currently in this queryable.
     */
    ReadonlyLinqingList<TElement> toReadOnly();
    /**
     * @return a readonly Set containing all the slements curreently in this queryable,
     * de-duplicated by taking the first of each duplicates.
     */
    ReadonlyLinqingSet<TElement> toReadOnlySet();

    /**
     * Creates a mutable list containing all the elements currently in this queryable. Modifications
     * to the resulting list are <i>not</i> propagated to the source of this queryable. The
     * returned list has the same order as this queryable at the time of calling.
     *
     * @return a mutable list containing all the elements currently in this queryable
     */
    LinqingList<TElement> toList();

    /**
     * Creates a mutable set containing all the elements currently in this queryable. Modifications
     * to the resulting set are <i>not</i> propagated to the source of this queryable.
     * The returned set will have the same iteration order as <code>this.distinct().toList()</code> would.
     *
     * @return a mutable list containing all the elements currently in this queryable
     */
    LinqingSet<TElement> toSet();

    /**
     * Creates an input stream that streams the contens of this queryable, as converted into a byte-integer
     * by the supplied converter.
     * <p>The returned stream does not need to be closed.
     *
     * @param converter the method to convert an element into a byte-integer
     * @return an input stream that will stream through the source data
     * @see java.io.InputStream
     */
    InputStream toInputStream(Func1<? super TElement, Integer> converter);

    /**
     * Creates a queryable that contains all the elements currently in this queryable. This allows the caller
     * to avoid the lazy evaluation typically associated with Queryable's.
     * <p>
     * <p>For example, if the caller
     * wishes to modify this queryable's source elements while iterating through this queryable
     * s/he may call <code>immediately()</code> on this queryable to safely avoid the
     * {@link java.util.ConcurrentModificationException} typically associated with such behaviour.
     * <p>
     * <p>more specifically:
     * <pre>{@code
     * LinqingList<String> allSides = Factories.asList("FirstModel-Front", "SecondModel-Front");
     *
     * for(String modelFront : sourceElements.immediately()){
     * allSides.add(modelFront.replace("-Front", "-Back"));
     * }
     * }</pre>
     * which gives the list {"FirstModel-Front", "SecondModel-Front", "FirstModel-Back", "SecondModel-Back"}
     *
     * @return a queryable containing all the elements currently in this queryable.
     */
    Queryable<TElement> immediately();


    /**
     * Creates a mutable map using elements in the specified <code>keys</code> collection as keys for each element
     * in this queryable. A member of keys at any given index will map to the element in this queryable at the same index.
     * Entries created that have duplicate keys will be overwritten by the entry created last, as defined by the order
     * of the iterator.
     *
     * @param keys   an ordered collection of keys to give each element in this queryable for the resulting map
     * @param <TKey> the type of key
     * @return a map that maps each element in <code>keys</code> to the index-corresponding element in this queryable
     */
    <TKey> LinqingMap<TKey, TElement> toMap(Iterable<TKey> keys);

    /**
     * Creates a mutable map of this queryable using the result of the specified <code>keySelector</code> applied against
     * each element as that elements key.
     * Entries created that have duplicate keys will be overwritten by the entry created last, as defined by the order
     * of the iterator.
     *
     * @param keySelector the transform to use to get the key from each element
     * @param <TKey>      the type of key
     * @return a map that maps the result of the transform on each element to that element.
     */
    <TKey> LinqingMap<TKey, TElement> toMap(Func1<? super TElement, TKey> keySelector);

    /**
     * Creates a mutable map of this queryable using the values provided by the <code>keySelector</code> for keys
     * and the <code>valueSelector</code> for values, as applied to each element in this queryable.
     * Entries created that have duplicate keys will be overwritten by the entry created last, as defined by the order
     * of the iterator.
     *
     * @param keySelector   a transform to apply to each element to get its key
     * @param valueSelector a transform to apply to each element to get its value
     * @param <TKey>        the type of key
     * @param <TValue>      the type of value
     * @return a map consisting of entries where each entry corresponds to the keySelector
     * applied to the element and the value selector applied to the element.
     */
    <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func1<? super TElement, TKey> keySelector,
                                                  Func1<? super TElement, TValue> valueSelector);

    /**
     * Creates an (or uses the existing) array containing the elements in this queryable. If the supplied
     * array is of sufficient size then the elements in this queryable will be copied into the supplied
     * array and returned. If it is not, a new array of the same type will be instantiated, the elements
     * in this queryable loaded into it, and the resulting array returned.
     * <p>
     * <p>Note that the old convention of creating arrays by instantiating an array of size 0 purely for its type information
     * (eg <code>someList.toArray(new OtherType[0])</code>) can be largely
     * replaced by the more elegant <code>someQuery.toArray(OtherType[]::new)</code> provided by the overload
     * {@link #toArray(com.empowerops.linqalike.delegate.Func1)}
     *
     * @param typedArray an array to use if its space is sufficient
     * @param <TDesired> the type of the array element
     * @return an array of type TDesired[], either the one supplied if it has enough capacity, or a new array if it does not
     */
    <TDesired> TDesired[] toArray(TDesired[] typedArray);

    /**
     * Creates an array using the provided factory containing the elements in this queryable.
     *
     * @param arrayFactory the factory to create typed arrays with
     * @param <TDesired>   the element type of the array returned by the factory
     * @return an array created by the specified factory containing the elements in this queryable
     */
    <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory);

    /**
     * @return an object-array containing the elements in this queryable
     */
    Object[] toArray();

    /**
     * converts this queryable into an array of (the primitive) <code>boolean</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>boolean[thisQueryable.size()]</code>.
     */
    boolean[] toBooleanArray(Func1<? super TElement, Boolean> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>byte</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>byte[thisQueryable.size()]</code>.
     */
    byte[] toByteArray(Func1<? super TElement, Byte> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>char</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>char[thisQueryable.size()]</code>.
     */
    char[] toCharArray(Func1<? super TElement, Character> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>short</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>short[thisQueryable.size()]</code>.
     */
    short[] toShortArray(Func1<? super TElement, Short> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>int</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>int[thisQueryable.size()]</code>.
     */
    int[] toIntArray(Func1<? super TElement, Integer> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>long</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>long[thisQueryable.size()]</code>.
     */
    long[] toLongArray(Func1<? super TElement, Long> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>float</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>float[thisQueryable.size()]</code>.
     */
    float[] toFloatArray(Func1<? super TElement, Float> converter);

    /**
     * converts this queryable into an array of (the primitive) <code>double</code>
     * <p>
     * <p>the <code>converter</code> is applied to each element and the result is stored in a
     * <code>double[thisQueryable.size()]</code>.
     */
    double[] toDoubleArray(Func1<? super TElement, Double> converter);

    //TODO docs, smartly.
    Queryable<TElement> union(TElement toInclude);

    Queryable<TElement> union(TElement toInclude0, TElement toInclude1);

    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2);

    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3);

    Queryable<TElement> union(TElement toInclude0, TElement toInclude1, TElement toInclude2, TElement toInclude3, TElement toInclude4);

    /**
     * Gets a queryable that will iterate first through <code>this</code> queryable, then through
     * the elements in <code>toInclude</code>, including only the first instance of any duplicates.
     *
     * @param toInclude the elements to be included in the resulting union
     * @return the union of this queryable with the specified elements
     */
    @SuppressWarnings("unchecked")
    Queryable<TElement> union(TElement... toInclude);

    /**
     * Gets a queryable that will iterate first through <code>this</code> queryable, then through
     * the elements in <code>toInclude</code>, including only the first instance of any duplicates.
     *
     * @param toInclude the elements to be included in the resulting union
     * @return the union of this queryable with the specified elements
     */
    Queryable<TElement> union(Iterable<? extends TElement> toInclude);

    /**
     * Gets a queryable that will iterate first through <code>this</code> queryable, then through
     * the elements in <code>toInclude</code>, including only the first instance of any duplicates
     * found by comparing elements with the supplied <code>comparableSelector</code>
     *
     * @param toInclude          the elements to be included in the resulting union
     * @param comparableSelector a transform that will provide an equatable value for each element
     * @return the union of this queryable with the specified elements using equality (for duplicate detection)
     * on the value provided by <code>comparableSelector</code> for each element
     */
    <TCompared> Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                                          Func1<? super TElement, TCompared> comparableSelector);

    /**
     * Gets a queryable that will iterate first through <code>this</code> queryable, then through
     * the elements in <code>toInclude</code>, including only the first instance of any duplicates found by comparing
     * elements with the supplied <code>equalityComparator</code>
     *
     * @param toInclude          the the elements to be included in the resulting union
     * @param equalityComparator the method of equality used to determine duplicates
     * @return the union of this queryable ant the specified elements to include
     */
    Queryable<TElement> union(Iterable<? extends TElement> toInclude,
                              EqualityComparer<? super TElement> equalityComparator);


    /**
     * Gets a queryable containing only elements that pass the provided condition.
     * <p>
     * <p>akin to {@link java.util.stream.Stream#filter(java.util.function.Predicate)}
     * <p>
     * <p>For example:<pre>{@code
     * Factories.from(new Person(){{ age = 30;}}, new Person(){{ age = 65; }}, new Person(){{ age = 71;}})
     * .where(person -> person.age >= 65);
     * }</pre>
     * will return the collection {[Person age=65], [Person age=71]}
     *
     * @param condition the condition that each element the resulting queryable will pass
     * @return a queryable containing only elements that pass the specified condition
     * @see #ofType(Class)
     */
    Queryable<TElement> where(Condition<? super TElement> condition);

    //TODO docs, smartly.
    Queryable<TElement> with(TElement toInclude);

    Queryable<TElement> with(TElement another0, TElement another1);

    Queryable<TElement> with(TElement another0, TElement another1, TElement another2);

    Queryable<TElement> with(TElement another0, TElement another1, TElement another2, TElement another3);

    Queryable<TElement> with(TElement another0, TElement another1, TElement another2, TElement another3, TElement another4);

    Queryable<TElement> with(TElement... toInclude);

    /**
     * Gets a queryable that will iterate first through <code>this</code> queryable, then through
     * the elements in <code>toInclude</code>, including only the first instance of any duplicates.
     *
     * @param toInclude the elements to be included in the resulting union
     * @return the union of this queryable with the specified elements
     */
    Queryable<TElement> with(Iterable<? extends TElement> toInclude);

    /**
     * @return the number of elements in this queryable
     */
    int size();


    /**
     * @return true if this queryable contains exactly one element
     */
    boolean isSingle();

    /**
     * @return true if this queryable contains two or more elements
     */
    boolean isMany();

    /**
     * @return true if this queryable contains no elements, ie <code>size() == 0</code>
     */
    boolean isEmpty();

    /**
     * Determines if this queryable is a subset of the supplied set.
     * <p>
     * <p>A is a subset of B if all of the elements in A are contained in B.
     * <ul>
     * <li>All sets are subsets of themselves.</li>
     * <li>The empty set is a subset of any set</li>
     * <li>Duplicates are ignored, meaning one bag may be a subset of another bag.</li>
     * </ul>
     *
     * @param possibleSuperset a candidate superset
     * @return true if this queryable is a subset of <code>possibleSuperset</code>
     */
    boolean isSubsetOf(Iterable<? extends TElement> possibleSuperset);

    /**
     * Determines if this queryable is a superset of the supplied collection
     * <p>
     * <p>A is a superset of B if all of the elements in B are contained in A.
     * <ul>
     * <li>All sets are supersets of themselves.</li>
     * <li>The empty set is not a superset of any set except the empty set</li>
     * <li>Duplicates are ignored, meaning one bag may be a superset of another bag.</li>
     * </ul>
     *
     * @param possibleSubset a candidate subset
     * @return true if this queryable is a superset of <code>possibleSubset</code>
     */
    boolean isSupersetOf(Iterable<? extends TElement> possibleSubset);

    /**
     * Determines if this queryable is a subsequence of the supplied sequence
     * <p>
     * <p>'A' is a subsequence of 'B' if there is some portion of 'B' that is contained in-order in 'A'.
     * for example the sequence {3, 4, 7} is a subsequence of the sequence {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
     * but it is <i>not</i> a subsequence of the sequence {2, 4, 6, 8, 10}. Sequences also allow for
     * duplicates, meaning the sequence {3, 3, 4, 5} is <i>not</i> a subsequence of {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
     * (since the latter only has the one '3').
     *
     * @param possibleSupersequence the sequence to test as a supersequence
     * @return true if this queryable is a subsequence of <code>possibleSupersequence</code>
     */
    boolean isSubsequenceOf(Iterable<? extends TElement> possibleSupersequence);

    /**
     * Determines if this queryable is a subsequence of the supplied sequence
     * <p>
     * <p>'A' is a subsequence of 'B' if there is some portion of 'B' that is contained in-order in 'A'.
     * for example the sequence {3, 4, 7} is a subsequence of the sequence {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
     * but it is <i>not</i> a subsequence of the sequence {2, 4, 6, 8, 10}. Sequences also allow for
     * duplicates, meaning the sequence {3, 3, 4, 5} is <i>not</i> a subsequence of {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
     * (since the latter only has the one '3').
     *
     * @param possibleSupersequence the sequence to test as a supersequence
     * @param equalityComparer      the method of equality to use
     * @return true if this queryable is a subsequence of <code>possibleSupersequence</code>
     */
    boolean isSubsequenceOf(Iterable<? extends TElement> possibleSupersequence,
                            EqualityComparer<? super TElement> equalityComparer);

    /**
     * Determines if this queryable is a supersequence of the supplied sequence
     * <p>
     * <p>'A' supersequence of 'B' if if there is a some portion of 'A' that sequence-equals 'B'.
     * for example the sequence {a, b, c, d, e} is supersequence of the sequence {b, c, d, e},
     * but it is <i>not</i> a supersequence of the sequence {a, b, c, d, e, f, g, h, i, j, k}.
     * Sequences allow for duplicates, meaning the sequence {a, a, b, c, d, d, e} is <i>not</i> a supersequence of
     * {b, c, d, e} (since the former has two 'd's).
     *
     * @param possibleSubsequence the sequence to test as a supersequence
     * @return true if this queryable is a subsequence of <code>possibleSubsequence</code>
     */
    boolean isSupersequenceOf(Iterable<? extends TElement> possibleSubsequence);

    /**
     * Determines if this queryable is free of any duplicate items, using default equality for comparison.
     * <p>
     * <p>a queryable that is distinct is known as a set, and a queryable that is not distinct is
     * known as a bag. All known queryables that derive from the {@link Set} interface will
     * return true immediately.
     *
     * @return <tt>true</tt> if this queryable contains no duplicates
     */
    boolean isDistinct();

    /**
     * Determines if this queryable is free of any duplicate items, determining equality by applying the
     * supplied <code>equatableSelector</code> against each element in this queryable.
     * <p>
     * <p>Once a single duplicate is found no further evaluations of the <code>equatableSelector</code>
     * will be made.
     * <p>
     * <p>by hashing the result of the <code>equatableSelector</code>, all known implementations
     * will run in linear time.
     *
     * @param equatableSelector the transform that provides an equatable item for each element.
     * @param <TCompared>       the type of the compared item
     * @return <tt>true</tt> if this queryable contains no duplicates, as per the equality provided by the
     * <code>equatableSelector</code>
     */
    <TCompared> boolean isDistinct(Func1<? super TElement, TCompared> equatableSelector);

    /**
     * Determines if this queryable is free of any duplicate items
     * using the equality method provided to find duplicates.
     *
     * @param equalityComparer the the method of equality used to determine duplicates
     * @return <tt>true</tt> if this queryable contains no duplicates, as per the equality provided by the
     * <code>equalityComparer</code>
     */
    boolean isDistinct(EqualityComparer<? super TElement> equalityComparer);


    /**
     * Performs an inner join on this queryable with the elements in <code>right</code>
     * using each index as they key.
     * <p>
     * <p>this queryable and <code>rightElements</code> must have the same number of elements.
     *
     * @param rightElements        the elements to join with
     * @param joinedElementFactory the factory to use to produce the resulting objects of the join
     * @param <TRight>             the type of the lements being joined to
     * @param <TJoined>            the type of the result of each joined element
     * @return a queryable containing 1 joined element for each element in this and in <code>rightElements</code>.
     */
    <TRight, TJoined>
    Queryable<TJoined> zip(Iterable<TRight> rightElements,
                           Func2<? super TElement, ? super TRight, TJoined> joinedElementFactory);

    /**
     * Performs an inner join on this queryable with the elements in <code>right</code>,
     * using the index of each object is the key, (asserting that the sizes are the same)
     * and using the {@link com.empowerops.linqalike.common.Tuple} as the result type.
     * <p>
     * <p>this queryable and <code>rights</code> must have the same elements.
     *
     * @return a queryable of tuples, with each tuple's left value mapping to an element in this queryable,
     * and each tuple's right value mapping to an element in <code>rightElements</code>.
     */
    <TRight>
    BiQueryable<TElement, TRight> zip(Iterable<TRight> rightElements);

    /**
     * performs the specified action on each pair of elements in this queryable and the supplied
     * <code>rightElements</code>.
     *
     * <p>this queryable and <code>rights</code> must have the same number of elements.
     *
     * @param rightElements  another collection of elements, with the same size as this queryable.
     * @param zippedConsumer a consumer to apply use once for each pair of elements in this queryable
     *                       and <code>rightElements</code>
     * @param <TRight>       the type of element in <code>rightElements</code>
     */
    <TRight>
    void forEachWith(Iterable<TRight> rightElements, Action2<? super TElement, ? super TRight> zippedConsumer);

    /**
     * {@inheritDoc}
     *
     * <p>consider using {@link #inlineForEach(Action1)} or {@link #select(Func1)}
     */
    @Override
    //TODO this is annoying, hopefully the compiler figures this out,
    //do i really have to provide an implementation just so I can add documentation?
    default void forEach(Consumer<? super TElement> action){
        Iterable.super.forEach(action);
    }

    /**
     * Applies <code>sideEffectTransform</code> against each element in <tt>this</tt> queryable,
     * and then returns <tt>this</tt> queryable.
     *
     * <p>This method is similar to {@link #forEach(Consumer)} in that each element will have the
     * specified element applied against it, but different in that it can be pipelined.
     * Because this flow is intrinsically impure, care must be made to ensure that the lazy nature of
     * linq-a-like does not interfere with the expected behaviour when using this method.
     * It is often appropriate to call {@link #immediately()} after this method so that
     * <code>sideEffectTransform</code> will be executed exactly once for each element in <tt>this</tt>.
     *
     * <p><b>This method is antithetical to the referentially transparent ('pure') paradigm
     * that drives LinqALike's design.</b> This has a number of interesting reprocussions.
     *
     * <p> consider
     * <pre>{@code
     *
     * LinqingList<Customer> allCustomers = Factories.asList(
     *   new Customer("Bob", 0),
     *   new Customer("Sally", 5)
     * );
     *
     * //...
     *
     * Queryable<Customer> incrementedCustomers = allCustomers.apply(cust -> cust.visits += 1);
     *
     * //...
     *
     * for(Customer customer : incrementedCustomers){
     *   ui.registerNewComponent(makeControllerFor(customer));
     * }
     *
     * //...
     *
     * incrementedCustomers.forEach(cust -> println(cust));
     * }</pre>
     *
     * the printed result would be
     *
     * <pre>
     * Customer[name=Bob, visits=2]
     * Customer[name=Sally, visits=7]
     * </pre>
     *
     * <p>This is expected behaviour because of the lazyness of Linq-a-Like.
     * the sideEffectTransform is only applied at iteration time, which in this example is
     * as part of the first for-each loop and the later forEach method.
     * To achieve a behaviour wherein the visits are only incremented once,
     * call {@link #immediately()} after <code>apply(cust -&gt; cust.visits += 1)</code>
     *
     * <p>Most mutating transforms can be refactored into referentially transparent (pure) transforms.
     * Consider refactoring <code>sideEffectTransform</code> into something
     * that doesn't modify the elements or environment. In such a case, {@link #select(Func1)} or
     * {@link #pushSelect(Func1)} should be used.
     *
     * <p>Some common use cases for this method would be
     * if two different and disjoint mutators need to be applied against the same list
     * (such as a environment.modify and an element.modify methods),
     * or if the exact time of iteration is known trivially
     * (such as in the case where the queryable is a parameter to a for-each loop),
     * or in the case of technically-impure (eg logging) or idempotent stateful transforms.
     *
     * @param sideEffectTransform a function which will apply some side effect
     * @return <tt>this</tt> queryable, with each element updated as per <code>sideEffectTransform</code>
     */
    Queryable<TElement> inlineForEach(Action1<? super TElement> sideEffectTransform);
}


