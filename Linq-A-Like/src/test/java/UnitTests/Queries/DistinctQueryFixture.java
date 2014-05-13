
package UnitTests.Queries;

import Assists.CountingEqualityComparator;
import Assists.CountingTransform;
import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static Assists.CountingEqualityComparator.track;
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
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6);
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
        assertThat(result).containsExactly("Starbucks", 5L, 5, 5.0d);
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

    @Test
    public void when_calling_distinct_with_a_comapared_value_selector(){
        //setup
        LinqingList<Object> list = new LinqingList<>(1,"1", 2, 2, "3", 3);
        CountingTransform<Object, String> getStringValue = CountingTransform.track(Object::toString);

        //act
        List<Object> result = list.distinct(getStringValue).toList();

        //assert
        assertThat(result).containsExactly(1, 2, "3");
        assertThat(getStringValue.getNumberOfInvocations()).isEqualTo(list.size());
    }

    @Test
    public void when_calling_distinct_with_a_comparator(){
        //setup
        LinqingList<Integer> list = new LinqingList<>(1,2,2,2,3,4,5,6,1,2,4,5,3);
        CountingEqualityComparator<Integer> leftIsSaveEvennessAsRight = track((left, right) -> left%2 == right%2);

        //act
        List<Integer> result = list.distinct(leftIsSaveEvennessAsRight).toList();

        //assert
        assertThat(result).containsExactly(1, 2);
        assertThat(leftIsSaveEvennessAsRight.getNumberOfInvocations()).isGreaterThanOrEqualTo(list.size()).isLessThanOrEqualTo(list.size() * list.size());
    }
}
