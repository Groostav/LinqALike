package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.CommonDelegates;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.QueryableList;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.FixtureBase;
import com.empowerops.linqalike.delegate.Func1;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.CountingTransform.track;
import static java.lang.Math.PI;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("Convert2MethodRef") // stick with lambda's for simplicity
@RunWith(Theories.class)
public class SelectQueryFixture extends FixtureBase {

    /* Note regarding syntax:
     *
     * A number of these tests are written like this:
     *
     *     something = source.select(transform = track(x -> x.doSomething())
     *
     * from a functional programming standpoint (ignoring side effects for a second),
     * this is functionally identical to
     *
     *     something = something.select(x -> x.doSomething())
     *
     * which is likely a much more common syntax. The reason for the in-line assignment statement
     * and static 'track()' method call is so that we can assert that the transform was executed
     * the correct number of times.
     */

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() {
        return SelectQuery.class;
    }

    @Theory
    public void when_calling_select_on_an_objects_field_result_is_projected(
            Queryable<NamedValue> source
    ){
        //setup
        source = doAdd(source, new NamedValue("First"), new NamedValue("Second"), new NamedValue("Third"));
        CountingTransform<NamedValue, String> transform;

        //act
        List<String> result = source.select(transform = track(x -> x.name)).toList();

        //assert
        assertQueryResult(result).containsSmartly("First", "Second", "Third");
        assertThat(transform.getNumberOfInvocations()).isEqualTo(3);
    }

    @Theory
    public void when_using_select_to_apply_a_transform_across_the_origin_set(
            Queryable<Integer> source
    ){
        //setup
        source = doAdd(source, 2, 4, 6, 8, 10);
        CountingTransform<Integer, Double> transform;

        //act
        List<Double> result = source.select(transform = track(diameter -> diameter * PI)).toList();

        //assert
        assertThat(result).containsExactly(2*PI, 4*PI, 6*PI, 8*PI, 10*PI);
        assertThat(transform.getNumberOfInvocations()).isEqualTo(source.size());
    }

    @Theory
    public void when_running_select_over_a_set_containing_null(
            Queryable.SupportsNull<NumberValue> source
    ){
        //setup
        source = doAdd(source, new NumberValue(20), null, new NumberValue(30), new NumberValue(40));
        CountingTransform<NumberValue, String> transform;

        //act
        List<String> result = source.select(transform = track(elemement -> elemement == null ? "<null>" : "" + elemement.number)).toList();

        //assert
        assertThat(result).containsExactly("20", "<null>", "30", "40");
        assertThat(transform.getNumberOfInvocations()).isEqualTo(4);
    }

    @Theory
    public void when_calling_select_prior_to_adding_values_to_the_source_list_select_query_should_see_newly_added_values(
            WritableCollection<Double> source
    ){
        //setup
        source.addAll(0.000, 1.333, 2.666, 4.000);
        double newValue = 5.333;

        //act
        Queryable<Double> result = source.select(num -> num * 1000);
        source.add(newValue);

        //assert
        assertThat(result).containsOnly(0.000d, 1333d, 2666d, 4000d, 5333d);
    }

    @Theory
    public void when_calling_select_on_empty_collection_should_get_empty_collection(
            Queryable<Double> source
    ){
        //setup
        doClear(source);
        CountingTransform<Double, Double> transform;

        //act
        Queryable<Double> result = source.select(transform = track(num -> num * 1000));

        //assert
        assertThat(result).isEmpty();
        assertThat(result.size()).isEqualTo(0);
        assertThat(transform.getNumberOfInvocations()).isEqualTo(0);
    }

    @Theory
    public void when_getting_the_first_member_of_projected_list_should_only_apply_transform_once(
            Queryable<Double> source
    ){
        //setup
        source = doAdd(source, 1.1, 2.2, 3.3, 4.4);
        CountingTransform<Double, Double> transform;

        //act
        Queryable<Double> result = source.select(transform = track(num -> num * 1000));
        int size = result.size(); //we can get size() without disrupting this.

        //assert
        assertThat(size).isEqualTo(4);
        assertThat(result.first()).isEqualTo(1100.0);
        assertThat(transform.getNumberOfInvocations()).isEqualTo(1);
    }

    @Theory
    public void when_running_select_over_a_bag_should_include_duplicates_in_source_for_transform(
            QueryableList<Integer> bag
    ){
        //setup
        bag = doAdd(bag, -1, 0, -1, 42, 43, 44, 42);

        //act
        List<Integer> result = bag.select(member -> member * -1).toList();

        //assert
        assertThat(result).containsExactly(1, 0, 1, -42, -43, -44, -42);
    }

    public static class Customer{
        public final String firstName;
        public final String lastName;

        Customer(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @Theory
    public void when_selector_returns_item_that_will_be_duplicated_should_get_queryable_containing_duplicates(
            Queryable<Customer> customers
    ){

        //setup
        customers = doAdd(customers,
                new Customer("John", "Stewart"), new Customer("John", "Oliver"),
                new Customer("John", "Wick"), new Customer("John", "Snow")
        );

        //act
        Queryable<String> firstNames = customers.select(cust -> cust.firstName);

        //assert
        assertThat(firstNames).hasSize(4);
        assertThat(firstNames.toList()).containsExactly("John", "John", "John", "John");
    }

    @Theory
    public void when_asking_for_size_of_select_query_should_inline_call_to_source_size(
            Queryable<Integer> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1, 2, 3, 4);
        CountingTransform<Integer, Integer> transform;

        //act
        Queryable<Integer> doubler = numbers.select(transform = track(num -> num * 2));
        int size = doubler.size();

        //assert
        assertThat(size).isEqualTo(4);
        assertThat(transform.getNumberOfInvocations()).isEqualTo(0);
    }

    /**
     * This test is critical to using <code>select</code>.
     * If you project into new memory, the consequences are suble and usually quite frustrating!
     *
     * <p><code>source.select(elem -> new ...)</code> <b>Is almost always an error.</b>
     * do not select-new.
     */
    @Theory
    public void when_asking_if_the_first_element_of_a_select_query_is_equal_to_itself_should_get_false(
            Queryable<String> names
    ){
        //setup
        names = doAdd(names, "Bob", "Ken", "Susan");

        //act
        Queryable<NamedValue> result = names.select(name -> new NamedValue(name));

        //assert
        assertThat(result.first()).isNotEqualTo(result.first()); //!!!
        // the reason for this is that each call to 'first()' gets a new instance, because of select's lazyness
        // and NamedValue does not overload equals,
        // -> each call to `result.first()` gives us a different instance,
        // -> with reference equality, that means they're not equal!
        assertThat(result.first().getName()).isEqualTo(result.first().getName());
    }

    /**
     * to prevent the issue described in the above test, the standard practice is to enumerate the queryable
     * immediately after creating it. This means you loose the lazy-updating feature of the select query,
     * but it does mean the transform will only be applied once for each element.
     */
    @Theory
    public void when_using_immediately_should_only_invoke_the_transform_once_per_element(
            Queryable<String> names
    ){
        //setup
        names = doAdd(names, "Vincent", "Justin");
        CountingTransform<String, NamedValue> transform;

        //act
        Queryable<NamedValue> namedValues = names.select(transform = track(name -> new NamedValue(name))).immediately();

        //assert
        assertThat(namedValues.first()).isEqualTo(namedValues.first());
        assertThat(namedValues.last()).isEqualTo(namedValues.last());
        assertThat(transform.getNumberOfInvocations()).isEqualTo(2);
    }

    /**
     * alternatively, you can memoize the selector
     */
    @Theory
    public void when_using_memoized_selector_should_only_invoke_the_transform_once_per_element(
            Queryable<String> names
    ){
        //setup
        names = doAdd(names, "Vincent", "Brian", "Geoff", "Justin");

        Func1<String, NamedValue> rawTransform = name -> new NamedValue(name);
        CountingTransform<String, NamedValue> trackedTransform = track(rawTransform);
        Func1<String, NamedValue> memoizedTrackedTransform = CommonDelegates.memoizedSelector(trackedTransform);

        //act
        Queryable<NamedValue> namedValues = names.select(memoizedTrackedTransform);
        //more commonly these lambda-wrapping lines would be in-lined (and the second skipped entirely), like this:
        //Queryable<NamedValue> namedValues = names.select(CommonDelegates.memoizedSelector(name -> new NamedValue(name));

        //assert
        assertThat(namedValues.first()).isEqualTo(namedValues.first());
        assertThat(namedValues.last()).isEqualTo(namedValues.last());
        assertThat(trackedTransform.getNumberOfInvocations()).isEqualTo(4);
    }

}
