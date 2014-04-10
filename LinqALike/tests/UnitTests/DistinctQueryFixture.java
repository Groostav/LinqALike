
package UnitTests;

import junit.framework.TestCase;
import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


/**
 * Created by Edwin on 2014-04-02.
 */
public class DistinctQueryFixture extends QueryFixtureBase {

    @Test
    public void eliminate_integer_duplicates_in_list() {
        //setup
        LinqingList<Integer> list = new LinqingList<>(1,2,2,2,3,4,5,6,1,2,4,5,3);

        //act
        List<Integer> result = list.distinct().toList();

        //assert
        assertThat(result).containsOnly(1,2,3,4,5,6);
    }

    @Test
    public void eliminate_string_duplicates_in_list() {
        //setup
        LinqingList<String> left = new LinqingList<>("Seoul", "Nagasaki",
                                                    "Mumbai", "Mumbai", "Amsterdam", "Shanghai", "Dubai", "Dubai");
        LinqingList<String> right = new LinqingList<>("Nagasaki", "Anchorage", "Rio de Janeiro", "Cairo");

        //act
        List<String> resultLeft = left.distinct().toList();
        List<String> resultRight = right.distinct().toList();

        //assert
        assertThat(resultLeft).doesNotHaveDuplicates();
        assertThat(resultRight).doesNotHaveDuplicates();
	    assertThat(resultLeft).containsExactly("Seoul", "Nagasaki", "Mumbai", "Amsterdam", "Shanghai", "Dubai");
	    assertThat(resultRight).containsExactly("Nagasaki", "Anchorage", "Rio de Janeiro", "Cairo");
    }

    @Test
    public void eliminate_duplicates_in_disparate_objects_list() {
        //setup
        LinqingList<Object> list = new LinqingList<>("Starbucks", 5L, 5, 5.0d, Integer.parseInt("5"));

        //act
        List<Object> result = list.distinct().toList();

        //assert
        assertThat(result).doesNotHaveDuplicates();
        assertThat(result).containsOnly("Starbucks", 5L, 5, 5.0d);
	    // This should not be true.
        assertThat(5.0d).isEqualTo(5);
    }

	@Test
	public void eliminate_duplicates_detects_string_case() {
		//setup
		LinqingList<String> cities = new LinqingList<>("Mumbai", "mumbai", "Osaka", "Seattle", "Seattle", "Bavaria", "sEaTTLE");

		//act
		List<String> result = cities.distinct().toList();

		//assert
		assertThat(result).containsExactly("Mumbai", "mumbai", "Osaka", "Seattle", "Bavaria", "sEaTTLE");
	}

    @Test
    public void when_calling_distinct_prior_to_adding_values_to_the_source_list_distonct_should_see_newly_added_values(){
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
