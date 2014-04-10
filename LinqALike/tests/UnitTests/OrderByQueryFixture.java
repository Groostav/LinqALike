package UnitTests;

import LinqALike.Delegate.Func1;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static UnitTests.QueryFixtureBase.CountingTransform.track;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 09/04/14.         b
 */
public class OrderByQueryFixture extends QueryFixtureBase{

    @Test
    public void when_ordering_an_unordered_set_of_integers_by_their_value(){
        //seutp
        LinqingList<Integer> values = new LinqingList<>(1, 4, 43, 2, 7, 19, 0, 42);

        //act
        CountingTransform<Integer, Integer> comparableSelector = track(element -> element);
        List<Integer> orderedValues = values.orderBy(comparableSelector).toList();

        //assert
        int elementCount = values.size();
        assertThat(orderedValues).containsExactly(0, 1, 2, 4, 7, 19, 42, 43);
        assertThat(comparableSelector.getNumberOfInvocations()).isLessThanOrEqualTo(elementCount * elementCount);
        assertThat(comparableSelector.getNumberOfInvocations()).isGreaterThan(0);
    }

    @Test
    public void when_ordering_an_empty_set(){
        //setup
        LinqingList<Integer> values = new LinqingList<>();

        //act
        CountingTransform<Integer, Integer> comparableSelector = track(x -> x);
        List<Integer> ordered = values.orderBy(comparableSelector).toList();

        //assert
        assertThat(ordered).isEmpty();
        assertThat(comparableSelector.getNumberOfInvocations()).isEqualTo(0);
    }

    //duplicates


    //negatives

    @Test
    public void when_comparing_two_custom_objects_by_a_string_field(){
        //setup
        List<NamedValue> origin = NamedValue.makeWithEach(
                "Uranium", "Einsteinium", "Manganese", "Silicon", "Francium");
        LinqingList<NamedValue> values = new LinqingList<>(origin);
        //act
        List<NamedValue> ordered = values.orderBy(x -> x.name).toList();

        //assert
        assertThat(ordered).containsExactly(origin.get(1), origin.get(4), origin.get(2), origin.get(3), origin.get(0));
    }

    @Test
    public void when_ordering_by_a_non_sequitur(){
        //setup
        LinqingList<Integer> source = new LinqingList<>(2, 3, 6, 1, 5, 4);

        //act
        List<Integer> result = source.orderBy(x -> 42).toList();

        //assert
        assertThat(result).containsExactly(2, 3, 6, 1, 5, 4);
    }

    @Test
    public void when_ordering_by_a_nonintuitive_comparison(){
        //setup
        LinqingList<Integer> source = new LinqingList<>(2, 3, 6, 1, 5, 4);

        //act
        List<Integer> result = source.orderBy(x -> x < 4).toList();

        //assert
        assertThat(result).containsExactly(6, 5, 4, 2, 3, 1);
    }

    @Test
    public void when_calling_order_by_prior_to_adding_values_to_the_source_list_order_by_should_see_newly_added_values(){
        //setup
        LinqingList<Double> sourceList = new LinqingList<>(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Double> distinctResult = sourceList.orderBy(x -> x);
        sourceList.add(newValue);

        //assert
        assertThat(distinctResult).contains(newValue);
    }
}
