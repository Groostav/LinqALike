package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.CountingTransform.track;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Created by Geoff on 09/04/14.
 */
@RunWith(Theories.class)
public class OrderByQueryFixture extends FixtureBase {

    @Theory
    public void when_ordering_an_unordered_set_of_integers_by_their_value(
            Queryable<Integer> values
    ){
        //setup
        values = doAdd(values, 1, 4, 43, 2, 7, 19, 0, 42);

        //act
        CountingTransform<Integer, Integer> comparableSelector = track(element -> element);
        List<Integer> orderedValues = values.orderBy(comparableSelector).toList();

        //assert
        int elementCount = values.size();
        assertThat(orderedValues).containsExactly(0, 1, 2, 4, 7, 19, 42, 43);
        assertThat(comparableSelector.getNumberOfInvocations()).isLessThanOrEqualTo(elementCount * elementCount);
        assertThat(comparableSelector.getNumberOfInvocations()).isGreaterThan(0);
    }

    @Theory
    public void when_ordering_an_empty_set(
            Queryable<Integer> values
    ){
        //setup
        values = doAdd(values);

        //act
        CountingTransform<Integer, Integer> comparableSelector = track(x -> x);
        List<Integer> ordered = values.orderBy(comparableSelector).toList();

        //assert
        assertThat(ordered).isEmpty();
        assertThat(comparableSelector.getNumberOfInvocations()).isEqualTo(0);
    }

    @Theory
    public void when_ordering_a_bag(
            LinqingList<EquatableValue> values
    ){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("Sedin");
        EquatableValue secondDuplicate = new EquatableValue("Sedin");
        values = doAdd(values, new EquatableValue("Burrows"), firstDuplicate, new EquatableValue("Hamhuis"), secondDuplicate);

        //act
        List<EquatableValue> ordered = values.orderBy(x -> x.value).toList();

        //assert
        assertThat(ordered).containsExactly(values.first(), values.first(3).last(), firstDuplicate, firstDuplicate);
        assertThat(ordered.get(2)).isSameAs(firstDuplicate);
        assertThat(ordered.get(3)).isSameAs(secondDuplicate);
    }

    @Theory
    public void when_comparing_two_custom_objects_by_a_string_field(
            Queryable<NamedValue> values
    ){
        //setup
        List<NamedValue> origin = NamedValue.forNames(
                "Uranium", "Einsteinium", "Manganese", "Silicon", "Francium");
        values = doAdd(values, origin);
        //act
        List<NamedValue> ordered = values.orderBy(x -> x.name).toList();

        //assert
        assertThat(ordered).containsExactly(origin.get(1), origin.get(4), origin.get(2), origin.get(3), origin.get(0));
    }

    @Theory
    public void when_ordering_by_a_non_sequitur(
            Queryable<Integer> source
    ){
        //setup
        source = doAdd(source, 2, 3, 6, 1, 5, 4);

        //act
        List<Integer> result = source.orderBy(x -> 42).toList();

        //assert
        assertThat(result).containsExactly(2, 3, 6, 1, 5, 4);
    }

    @Theory
    public void when_ordering_by_a_nonintuitive_comparison(
            Queryable<Integer> source
    ){
        //setup
        source = doAdd(source, 2, 3, 6, 1, 5, 4);

        //act
        List<Integer> result = source.orderBy(x -> x < 4).toList();

        //assert
        // interestingly, when we sort by a boolean, "true" is greater than "false",
        // so the "true" elements are at the end of the list.
        assertThat(result).containsExactly(6, 5, 4, 2, 3, 1);
    }

    @Theory
    public void when_calling_order_by_prior_to_adding_values_to_the_source_list_order_by_should_see_newly_added_values(
            WritableCollection<Double> sourceList
    ){
        //setup
        sourceList = doAdd(sourceList, 1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Double> distinctResult = sourceList.orderBy(x -> x);
        sourceList.add(newValue);

        //assert
        assertThat(distinctResult.toList()).contains(newValue);
    }

    @Theory
    public void when_calling_order_by_on_a_set_containing_a_duplicate_by_default_equality_but_not_by_a_specified_comparable_selector(
            Queryable<EquatableValue> cityAndState
    ){
        //setup
        EquatableValue city = new EquatableValue("New York");
        EquatableValue state = new EquatableValue("New York");
        cityAndState = doAdd(cityAndState, city, state);
        assumeTrue(cityAndState.first() == city && cityAndState.last() == state);

        //act
        List<EquatableValue> stateFirst = cityAndState.orderBy(x -> x == state ? 0 : 1).toList();

        //assert
        //cant use containsExactly since EquatableValue overrides Equals
        assertThat(stateFirst.get(0)).isSameAs(state);
        assertThat(stateFirst.get(1)).isSameAs(city);
    }

}
