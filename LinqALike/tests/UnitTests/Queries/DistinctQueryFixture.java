
package UnitTests.Queries;

import Assists.QueryFixtureBase;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


/**
 * Created by Edwin on 2014-04-02.
 */
public class DistinctQueryFixture extends QueryFixtureBase {

    @Test
    public void when_calling_distinct_on_a_list_containing_several_integer_duplicates() {
        //setup
        LinqingList<Integer> list = new LinqingList<>(1,2,2,2,3,4,5,6,1,2,4,5,3);

        //act
        List<Integer> result = list.distinct().toList();

        //assert
        assertThat(result).containsOnly(1,2,3,4,5,6);
    }

    @Test
    public void when_calling_distinct_on_a_list_containing_string_duplicates() {
        //setup
        LinqingList<String> left = new LinqingList<>(
                "Seoul", "Nagasaki", "Mumbai", "Amsterdam", "Shanghai", "Dubai", "Dubai",
                "Nagasaki", "Anchorage", "Mumbai", "Rio de Janeiro", "Cairo");

        //act
        List<String> resultLeft = left.distinct().toList();

        //assert
        assertThat(resultLeft).doesNotHaveDuplicates();
	    assertThat(resultLeft).containsExactly(
                "Seoul", "Nagasaki", "Mumbai", "Amsterdam", "Shanghai", "Dubai",
                "Anchorage", "Rio de Janeiro", "Cairo");
    }

    @Test
    public void when_calling_distinct_on_a_list_containing_objects_of_dispirate_types() {
        //setup
        LinqingList<Object> list = new LinqingList<Object>("Starbucks", 5L, 5, 5.0d, 5);

        //act
        List<Object> result = list.distinct().toList();

        //assert
        assertThat(result).doesNotHaveDuplicates();
        assertThat(result).containsOnly("Starbucks", 5L, 5, 5.0d);
	    // This should not be true.
        assertThat(5.0d).isEqualTo(5);
    }

    @Test
    public void when_calling_distinct_prior_to_adding_values_to_the_source_list_distinct_should_see_newly_added_values(){
        //setup
        LinqingList<Double> sourceList = new LinqingList<>(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Double> distinctResult = sourceList.distinct();
        sourceList.add(newValue);

        //assert
        assertThat(distinctResult).contains(newValue);
    }
}
