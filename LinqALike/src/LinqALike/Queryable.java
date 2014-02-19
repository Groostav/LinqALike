package LinqALike;

import LinqALike.Delegate.Action1;
import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.Delegate.Func2;

/**
 * @author Geoff on 06/09/13
 */
@SuppressWarnings("SpellCheckingInspection")
public interface Queryable<TElement> extends Iterable<TElement> {

    /**
     * <P>Yields a list that contains an element for every element in this list, after
     * having the transform applied to it.</P>
     *
     * <P>Consider the relatively simple and common situation:
     * <ul>
     *     <li>You have a List&lt;{@link PDOL.DataObject.Optimization}&gt;</li>
     *     <li>You want a List&lt;String&gt;, with each entry in this list being the
     *     {@link PDOL.DataObject.Optimization#name} of the optimization in the above list</li>
     * </ul>
     * These two pieces of code <i>functionally identical</i> ways of doing it:
     * <pre>{@code
     * //using a for-each loop
     * List<String> algNames = new ArrayList<String>();
     * for(Optimization optimization : listOfOptimizations){
     *     algNames.add(optimization.algName);
     * }
     *
     * //using the select method
     * List<String> algNames = linqingListOfOptimizations.select(new Func1<Optimization, String>(){
     *     public String getFrom(Optimization optimization){return optimization.algName;}
     * });
     *
     * //if you click the negative sign on the left,
     * //intelliJ will shorten this to the following which is almost legal code in the upcoming Java 8
     * List<String> algNames = linqingListOfOptimizations.select((optimization) -> { return optimization.algName; });
     *
     * //as a convention, we sometimes put Transforms as fields on the "from" object of the transform
     * //so in this case, we would put a field on the Optimization obect as such
     * public static final Func1<Optimization, String> GetAlgName = new Func1<Optimization, String>(){
     *     @Override public String getFrom(Optimization source){
     *         return source.getAlgName();
     *     }
     * }
     * //which turns our above select statement into
     * List<String> algNames = linqingListOfOptimizations.select(Optimization.GetAlgName);
     * //which is pretty neat looking, and reads much better than that big nasty for loop!
     * }</pre></P>
     *
     * <P>You do not have to simply use a <code>return source.someProperty()</code> as the body of your transform,
     * you can, if you wish, get much more complicated in your selector function bodies, but a simple property-getter-call
     * is one of the most common usages.</P>
     *
     * <P>The name <code>select</code> is SQL inspired, take a look at an SQL SELECT if you want to know more about
     * the mentality behind this method.</P>
     *
     * <P>In an attempt to try and lower the code-expense of anonymous classes as delegate objects, a series of
     * common set transforms has been added to {@link PDOL.Common.Delegate.CommonDelegates}.</P>
     *
     * @param selector the transform that will take each element in this list, and produce an element to be put in the returned list
     * @param <TTransformed> the type of the object yielded by the selector transform
     * @return a list of exactly the same size as this, containing the result of the transform applied to each element in this.
     * @see PDOL.Common.Delegate.CommonDelegates
     */
    <TTransformed>
    Queryable<TTransformed> select(Func1<? super TElement, TTransformed> selector);

    /**
     * Performs a selection where the selected member it itself a set, rather than a single member, and aggregates the results.
     *
     * <p>This is very intuitive if demonstrated by example. Consider the class
     * <pre>{@code
     * public class Customer{
     *     //... other members
     *     public String firstName;
     *     public List<String> aliases;
     * }
     * }</pre></p>
     *
     * If you had a list of customers, each with some number of aliases like this:
     * <pre>{@code
     * {firstName: mark, Aliases:{Mark, mark, MARK, MinuteMan}}
     * {firstName: ben, Aliases:{Ben, ben, BEN, benjimen, frank1en, benn}}
     * {firstName: alice, Aliases:{Alice, alice, ALICE, Alicia}}
     * }</pre>
     *
     * And you wanted a complete list of all the aliases you had of your customers, you would use the following code
     * <pre>{@code
     * //the delegate
     * public static Func1<Customer, Iterable<String>> GetAliases = new Func1<Customer, Iterable<String>>(){
     *     {@literal @}Override
     *     public Iterable<String> getFrom(Customer customer){
     *         return customer.aliases;
     *     }
     * }
     *
     * //the selectMany usage that will give you a complete list of aliases
     * Queryable<String> allKnownAliases = aboveMentionedMarkBenAliceList.selectMany(GetAliases);
     * //the result will be:
     * //{Mark, mark, MARK, MinuteMan, Ben, ben, BEN, benjimen, frank1en, benn, Alice, alice, ALICE, alicia}
     * }</pre>
     *
     * @param selector a selector that yields a set of items from an object
     * @param <TTransformed> The type of the element in the selectors return value, used to give a properly typed Queryable in return.
     * @return an aggregated set composed, in order, of the result from the <tt>selector</tt> applied to each member in this Queryable.
     */
    <TTransformed> LinqingList<TTransformed> selectMany(Func1<? super TElement, ? extends Iterable<TTransformed>> selector);

    <TRight>
    Queryable<Tuple<TElement, TRight>> join(TRight[] right);

    <TRight>
    Queryable<Tuple<TElement, TRight>> join(Iterable<TRight> right);

    /**
     * <p>gets the result of a set-subtraction.</p>
     *
     * <p>for example:</p>
     * <p><code>from(A, B, D, C).excluding(from(B, D, E))</code> returns <code>{A, C}</code></p>
     *
     * @param toExclude the elements to exlude from the resulting set
     * @return all elements contained in <tt>this</tt> set that <i>are not</i> contained in
     * the <tt>toExclude</tt> set.
     */
    Queryable<TElement> excluding(Iterable<? extends TElement> toExclude);

    Queryable<TElement> excluding(TElement... toExclude);

    /**
     * <p>gets the result of a set-subtraction, using equality on the objects supplied by the selector.
     * This allows you to subvert the normal equals process by supplying a specific member (or whole other object)
     * you wish to perform equals on. </p>
     *
     * @param toExclude the values to exclude
     * @param comparableSelector a selector that gets a comparable value from each element in both <tt>this</tt>
     *                           and <tt>toExclude</tt>.
     * @param <TCompared>
     * @return
     */
    <TCompared>
    Queryable<TElement> excluding(Iterable<? extends TElement> toExclude,
                                  Func1<? super TElement, TCompared> comparableSelector);

    /**
     * <p>gets the union of two querable sets.</p>
     *
     * <p><i>this is not strictly a union, as members from one set that exist in the other set
     * will both be in the resulting set as two elements.</i></p>
     *
     * TODO correct the order of the resulting set.
     * @param toInclude the other set to include in the union
     * @return the resulting union between <tt>this</tt> set and the <tt>toInlcude</tt> set.
     */
    Queryable<TElement> union(Iterable<? extends TElement> toInclude);

    <TCompared>
    Queryable<TElement> union(Iterable<? extends TElement> toInclude, Func1<? super TElement, TCompared> comparableSelector);

    Queryable<TElement> union(TElement... elements);

    Queryable<TElement> intersection(Iterable<? extends TElement> toInclude);

    <TCompared>
    Queryable<TElement> intersection(Iterable<? extends TElement> toInclude, Func1<? super TElement, TCompared> comparableSelector);

    Queryable<TElement> intersection(TElement... toIntersect);

    /**
     * <p>in order of iteration, this method gets the <i>right-subset</i> of elements that
     * <i>do not</i> pass the given condition, <tt>toExclude</tt>.</p>
     *
     * @param toExclude the condition to apply to each element, only taking elemnts after
     *                  the first passing element.
     * @return elements <i>past</i> and inlcuding the first element to fail the <tt>toExclude</tt>
     *         condition.
     */
    Queryable<TElement> skipWhile(Condition<? super TElement> toExclude);

    Queryable<TElement> skipUntil(Condition<? super TElement> toInclude);

    Queryable<TElement> skip(int numberToSkip);

    <TComparable>
    QueryableMultiMap<TComparable, TElement> groupBy(Func1<TElement, TComparable> comparableSelector);

    <TDerived>
    Queryable<TDerived> selectCast();

    /**
     * <P>reduces a set by a condition, giving back the subset of this list that passes the given condition</P>
     * <P>
     *     Consider:
     *     <ul>
     *         <li>You have a List&lt;{@link PDOL.DataObject.OptimizationRun}&gt; that contains <i>both</i> MPS and PSP optimization runs</li>
     *         <li>You want only that lists PSP optimization runs.
     *     </ul>
     *     These two pieces of code <i>functionally identical</i> ways of doing it:
     *     <pre>{@code
     * //using a for-each loop
     * List<OptimizationRun> pspRuns = new ArrayList<OptimizationRun>();
     * for(OptimizationRun run : listOfORuns){
     *     if(run.getOptimization().getAlgorithmName().equals(AlgorithmNames.PSP)){
     *         pspRuns.add(run);
     *     }
     * }
     *
     * //using the where method
     * List<OptimizationRun> pspRuns = linqingListOfRuns.where(new condition<OptimizationRun>(){
     *     @Override public boolean passesFor(OptimizationRun run){
     *         return run.getOptimization.getAlgorithmName.equals(AlgorithmNames.PSP);
     *     }
     * })
     *
     * //if you click the negative sign on the left,
     * //intelliJ will shorten this to the following which is almost legal code in the upcoming Java 8
     * List<String> algNames = linqingListOfRuns.where((run) -> { return run.getOptimization().getAlgorithmName().equals(AlgorithmNames.PSP); });
     *
     * //As a convention on this project, we also typically add parametrized methods like this as members to the class that
     * //the condition is on. For example, if we go to OptimizationRun and add this field:
     * public static final Condition<OptimizationRun> IsPSP = new Condition<OptimizationRun>(){
     *     @Override public boolean passesFor(OptimizationRun run){
     *         return run.getOptimization().getAlgorithmName().equals(AlgorithmNames.PSP);
     *     }
     * }
     *
     * //which means our where statement now looks like this
     * List<String> algNames = linqingListOfRuns.where(OptimizationRun.IsPSP);
     * //which looks way better than that big nasty for-if loop!
     *     }</pre>
     * </P>
     * <P>These subset restrictions are quite powerful, as they allow us to in-line reduce the amount of data we're viewing. </P>
     * <P>The name <code>where</code> is SQL inspired, along with most of this class, so take a look at an SQL WHERE
     * if you want to know about when and where (pun completely intended) to use a <code>linqingList.where()</code></P>
     * <P>This method bears many similarities with the methods listed in the <i>see also</i> section. If you want something
     * other than a simple subset, take a look at those methods</P>
     *
     * @param condition the condition to restrict each element on, returning <code>true</code> if the element should
     *                  be in the resulting subset list, false if not
     * @return the subset that passes the given condition.
     *
     * @see LinqingList#first(Condition)
     * @see LinqingList#firstOrDefault(Condition)
     * @see LinqingList#single(Condition)
     * @see LinqingList#singleOrDefault(Condition)
     * @see LinqingList#any(Condition)
     */
    Queryable<TElement> where(Condition<? super TElement> condition);

    /**
     * <P>Gets a subset of this list containing only objects that implement the specified class. This operation can be
     * done with the more generic {@link Common#where(Condition)} method in conjunction with the
     * {@link Common#selectCast()} method but is provided here for brevity. </P>
     *
     * <P>
     *     Consider the following:
     *     <ul>
     *         <li>You have a List&lt;Object&gt; that contains the following
     *         <ul>
     *             <li>a string, the name of the block object</li>
     *             <li>null</li>
     *             <li>an ETBlockObject</li>
     *             <li>an OptimizationObject </li>
     *             <li>a string saying "Hello world"</li>
     *             <li>a FileInputObject</li>
     *             <li>a HashMap of Booleans that have something to do with verification (total nonsense)</li>
     *         </ul>
     *         </li>
     *         <li>You want the subset that is:
     *         <ul>
     *             <li>the ETBlockObject</li>
     *             <li>the OptimizationObject</li>
     *             <li>the FileInputObject</li>
     *         </ul>
     *         </li>
     *     </ul>
     *     <i>please note, this list is hypothetical. though we do have nonsense hashmaps like that, and lists that have
     *     null in them, we certainly don't have one this nonsensical. <b>don't you dare add one to
     *     our project.</b></i>
     *     <p>These two pieces of code <i>functionally identical:</i></p>
     *     <pre>{@code
    //using a for-each loop
    List<WorkspaceObject> workspaceObjects = new ArrayList<WorkspaceObject>();
    for(Object candidate : nonsenseListOfObjects){
        if(candidate != null && WorkspaceObject.class.isAssignableFrom(candidate.getClass())){
            workspaceObjects.add((WorkspaceObject)object);
        }
    }

    //using the whereTypeIs method
    List<WorkspaceObject> workspaceObjects = nonsenseLinqingListOfObjects.whereTypeIs(WorkspaceObject.class);
     *     }</pre>
     *     <P>no need for IntelliJ or Java 8 tricks here, this just looks <i>way better!</i></P>
     * </P>
     *
     * @param   desiredClass the type of the elements that should be added to the returned subset
     * @param   <TDesired> same as desired class (but for static-typing reasons, Java wont let me just use a Class<T> object.)
     * @return  a subset that only contains objects that extend the provided class
     * @see     LinqingList#where(Condition)
     */
    <TDesired extends TElement>
    Queryable<TDesired> whereTypeIs(Class<TDesired> desiredClass);

    Queryable<TElement> reversed();

    //element selectors

    /**
     * <p>gets the <i>one</i> element in this set. If this set contains no elements or more than one, an exception is thrown</p>
     * <p>this can be useful if you believe that you have sufficiently reduced (or <i>constrained</i>) to the point
     * where <i>there should only be one element left.</i> If there are more than one element in such sets, its best to
     * throw an exception right away, since we're inevitably headed toward an error state (we just havn't quite had it
     * surface yet)</p>
     *
     * @return  the single element in this set
     * @see     LinqingList#first
     */
    TElement single();

    /**
     * <p>gets the <i>one</i> element <i>that passes the given constraint</i> in this set. It is OK to invoke this method
     * on a set with more than one element, as long as there is only one elemen tin this set that will pass the provided
     * condition. If there are no elements in the set, or more than pass the provided condition, an exception is thrown.</p>
     * <p> invoking <code>set.single(condition)</code> is functionally identical* to <code>set.where(condition).single()</code></p>
     *
     * @param   uniqueConstraint a condition that only one element in this set will pass.
     * @return  the single element in the set that passed the given constraint.
     * @see     LinqingList#first(Condition)
     */
    TElement single(Condition<? super TElement> uniqueConstraint);

    /**
     * <p>gets the <i>one</i> element in this set <i>if this set contains elements,</i> null if it does not. If this set
     * contains more than one element, an exception is thrown.</p>
     *
     * @return  the single element that this set contains if this set contains elements, null if the set is empty
     * @see     LinqingList#firstOrDefault()
     */
    TElement singleOrDefault();

    /**
     * <p>gets the <i>one</i> element in this set that passes the provided condition <i>if this set contains elements,</i>
     * null if it does not. If this set contains more than one element that passes the provided condition,
     * an exception is thrown. ie:
     * <ul>
     *     <li>if <i>the set contains no elements</i>, then <code>null</code> is returned</li>
     *     <li>if <i>the set contains one element</i>, then that element is returned</li>
     *     <li>if <i>the set contains more than one element</i>, then an exception is thrown </li>
     * </ul></p>
     * <p> invoking <code>set.singleOrDefault(condition)</code> is functionally identical* to
     * <code>set.where(condition).singleOrDefault()</code></p>
     *
     * @param   uniqueConstraint a condition that will identify a single element in the set, which will be the returned value
     * @return  the single element in this set that passes the condition if this set contains elements, null if the
     *          set is empty
     * @see     LinqingList#firstOrDefault(Condition)
     */
    TElement singleOrDefault(Condition<? super TElement> uniqueConstraint);

    /**
     * <p>gets the <i>first</i> element in this set if this set contains elements. An exception is thrown if it does not.</p>
     * <P>Note: Users of this method should have a good reason why they're not using the method
     * {@link LinqingList#single()}. Chances are, if you want the first element in the list, you might
     * want to assert also that the list contains <i>only one element anyways</i></P>
     *
     * @return  the first element in this set. Identical to <code>set.get(0)</code> or <code>arraySet[0]</code>, but,
     *          again, less ugly.
     * @see     LinqingList#single()
     */
    TElement first();

    /**
     * <p>gets the <i>first</i> element in this set that passes the provided condition. An exception is thrown if the
     * set contains no elements.</p>
     * <P>Note: Users of this method should have a good reason why they're not using the method
     * {@link LinqingList#single(Condition)}. Chances are, if you want the first element in the list, you might
     * want to assert also that the list contains <i>only one element anyways</i></P>
     *
     * @param   condition a condition which will be applied in-order against all elements in the set until one element
     *          passes. That element will be returned.
     * @return  the first element in this set. Identical to <code>set.get(0)</code> or <code>arraySet[0]</code>, but,
     *          again, less ugly.
     * @see     LinqingList#single()
     */
    TElement first(Condition<? super TElement> condition);

    /**
     * <p>gets the <i>first</i> element in this set <i>if this set contains elements,</i> <code>null</code> if it does not.</p>
     *
     * @return  the first element that this set contains if this set contains elements, null if the set is empty
     * @see     LinqingList#singleOrDefault()
     */
    TElement firstOrDefault();

    /**
     * <p>gets the <i>first</i> element in this set to pass the given condition <i>if this set contains elements,</i>
     * <code>null</code> if it does not.</p>
     *
     * @return  the first element that this set contains that also passes the suppliued condition
     *          if this set contains elements, null if the set is empty
     * @see     LinqingList#singleOrDefault()
     */
    TElement firstOrDefault(Condition<? super TElement> condition);

    TElement last();

    TElement lastOrDefault();

    TElement withMinimum(Func1<TElement, Number> valueSelector);

    //set state queries

    int size();

    /**
     * <p>states whether or not the set contains one element.</p>
     *
     * @return true if the set contains one element, functionally identical to <code>list.size() == 1</code>
     */
    boolean isSingle();

    /**
     * <p>stats whether or not the set contains exactly one element and that the one element passes the
     * condition.</p>
     * @param condition true if the set contains a single element, and that the single element passes this
     *                  condition.
     * @return true if the set contains exactly one element and that one element passes the supplied condition.
     */
    boolean containsSingle(Condition<? super TElement> condition);

    /**
     * <p>Returns <tt>true</tt> if this list contains elements. Identical to <code> ! isEmpty()</code></p>
     *
     * @return <tt>true</tt> if this list contains elements
     */
    boolean any();

    /**
     * <p>Returns <tt>true</tt> if any of the elements in this set pass the supplied conidition, <tt>false</tt> if
     * none of them do, or if the set is empty. Application of the condition stops when a single element is found.</p>
     *
     * @param   condition the condition to run over each element in the set until a passing one is found.
     * @return  <tt>true</tt> if an element in the set passes the supplied condition.
     */
    boolean any(Condition<? super TElement> condition);

    /**
     * Returns <tt>true</tt> if this list contains no elements. Identical to <code> ! any()</code>
     *
     * @return <tt>true</tt> if this list does not contain any elements.
     */
    boolean isEmpty();

    int count(Condition<? super TElement> condition);

    /**
     * <p>returns true if all the elements withen the set pass the given condition, or if the set
     * is empty. This method is defined as <tt>true</tt> for <i>any</i> condition on an empty list as,
     * suprisingly (to me), and more-often-than-not, this is a convinient behaviour.</p>
     *
     * @param condition the condition each element has to pass for the method to return <tt>true</tt>
     * @return <tt>true</tt> if the set is empty, or all elements in the set pass for the condition.
     */
    boolean all(Condition<? super TElement> condition);

    /**
     * <p>return true if, in no order, each element in <tt>otherSet</tt> is contained withen
     * <tt>this</tt> set.</p>
     *
     * @param otherSet the set to compare this set to
     * @return <tt>true</tt> if both sets have the same number of elements and each element
     * exists in both sets.
     */
    boolean isSetEquivalentOf(Iterable<TElement> otherSet);

    boolean isSubsetOf(Iterable<TElement> otherSet);

    boolean contains(Object candidate);

    boolean contains(Condition<TElement> candidateRequirement);

    boolean containsDuplicates();

    Queryable<TElement> withoutDuplicates();

    //misc

    ReadonlyLinqingList<TElement> asReadOnly();

    LinqingList<TElement> toList();

    <TKey>
    LinqingMap<TKey, TElement> toMap(Func1<TElement, TKey> keySelector);

    Object[] toArray();

    <TDesired> TDesired[] toArray(TDesired[] arrayTypeIndicator);

    /**
     * <p></p>Runs the supplied Function over every set in this element. Does nothing if the set is empty. The two pieces of
     * code are identical:</p>
     * <pre>{@code
     *      //using a for-each loop
     *      for(String member : linqingList){
     *          System.out.println("found" + member);
     *      }
     *
     *      //using the forEach method
     *      linqingList.forEach(new Action1<TElement>() {
     *          @Override public void doUsing(TElement element) {
     *              System.out.println("found" + element);
     *          }
     *      });
     *
     *      //which intelliJ will shorten to
     *      linqingList.forEach((element) -> { System.out.println("found" + element; });
     * }</pre></p>
     * @param function
     */
    void forEach(Action1<? super TElement> function);

    <TOther> Queryable<Tuple<TElement,TOther>> cartesianProduct(Queryable<TOther> allKnownSubclasses);

    /**
     * Iterates through any iterator queue'd up queries (ie, it will pull the values through any
     * {@link #select(PDOL.Common.Delegate.Transform)}, {@link #whereTypeIs(Class)},
     * {@link #where(PDOL.Common.Delegate.Condition)}, {@link #union(Iterable)}, or {@link #excluding(Iterable)} calls.
     *
     * @return The values of this queryably as pulled forward, thus protected from
     */
    Queryable<TElement> fetch();

    TElement firstOr(TElement alternative);

    <TRight, TResult>
    Queryable<TResult> join(Iterable<TRight> right, Func2<TElement, TRight, TResult> makeResult);
}
