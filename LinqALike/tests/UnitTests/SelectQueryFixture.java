package UnitTests;

import Assists.QueryFixtureBase;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static java.lang.Math.PI;
import static org.fest.assertions.Assertions.assertThat;

public class SelectQueryFixture extends QueryFixtureBase {

    @Test
    public void when_calling_select_on_an_objects_field_result_is_projected(){
        //setup
        LinqingList<NamedValue> source = new LinqingList<>(new NamedValue("First"), new NamedValue("Second"), new NamedValue("Third"));

        //act
        List<String> result = source.select(x -> x.name).toList();

        //assert
        assertThat(result).containsExactly("First", "Second", "Third");
    }

    @Test
    public void when_using_select_to_apply_a_transform_across_the_origin_set(){
        //setup
        LinqingList<Integer> source = new LinqingList<>(2, 4, 6, 8, 10);

        //act
        List<Double> result = source.select(diameter -> diameter * PI).toList();

        //assert
        assertThat(result).containsExactly(2*PI, 4*PI, 6*PI, 8*PI, 10*PI);
    }

    @Test
    public void when_running_select_over_a_set_containing_null(){
        //setup
        LinqingList<NumberValue> source = new LinqingList<>(new NumberValue(20), null, new NumberValue(30), new NumberValue(40));

        //act
        List<String> result = source.select(elemement -> elemement == null ? "<null>" : "" + elemement.number).toList();

        //assert
        assertThat(result).containsExactly("20", "<null>", "30", "40");
    }

    @Test
    public void when_running_select_over_a_bag(){
        //setup
        LinqingList<Integer> bag = new LinqingList<>(-1, 0, -1, 42, 43, 44, 42);

        //act
        List<Integer> result = bag.select(member -> member * -1).toList();

        //assert
        assertThat(result).containsExactly(1, 0, 1, -42, -43, -44, -42);
    }

    @Test
    public void when_calling_select_prior_to_adding_values_to_the_source_list_select_query_should_see_newly_added_values(){
        //setup
        LinqingList<Double> source = new LinqingList<>(0.000, 1.333, 2.666, 4.000);
        double newValue = 5.333;

        //act
        Queryable<Double> result = source.select(num -> num * 1000);
        source.add(newValue);

        //assert
        assertThat(result).containsOnly(0.000d, 1333d, 2666d, 4000d, 5333d);
    }
}
