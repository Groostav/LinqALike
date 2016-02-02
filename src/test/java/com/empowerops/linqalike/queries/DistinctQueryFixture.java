
package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.CountingEqualityComparator;
import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.CountingEqualityComparator.track;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Edwin on 2014-04-02.
 */
@RunWith(Theories.class)
public class DistinctQueryFixture extends FixtureBase {

    @Theory
    public void when_calling_distinct_on_a_list_containing_several_integer_duplicates(
            Queryable<Integer> numbersWithDupes
    ) {
        //setup
        numbersWithDupes = doAdd(numbersWithDupes, 1,2,2,2,3,4,5,6,1,2,4,5,3);

        //act
        List<Integer> result = numbersWithDupes.distinct().toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Theory
    public void when_calling_distinct_on_a_list_containing_string_duplicates(
            Queryable<String> cities
    ) {
        //setup
        cities = doAdd(cities,
                "Seoul", "Nagasaki", "Mumbai", "Amsterdam", "Shanghai", "Dubai", "Dubai",
                "Nagasaki", "Anchorage", "Mumbai", "Rio de Janeiro", "Cairo");

        //act
        List<String> resultLeft = cities.distinct().toList();

        //assert
        assertThat(resultLeft).doesNotHaveDuplicates();
	    assertQueryResult(resultLeft).containsSmartly(
                "Seoul", "Nagasaki", "Mumbai", "Amsterdam", "Shanghai", "Dubai",
                "Anchorage", "Rio de Janeiro", "Cairo");
    }

    @Theory
    public void when_calling_distinct_on_a_list_containing_distinct_objects_of_dispirate_types_should_get_back_original_list(
            Queryable<Object> starbucksNumbers
    ) {
        //setup
        starbucksNumbers = doAdd(starbucksNumbers, "Starbucks", 5L, 4, 3.0d);

        //act
        List<Object> result = starbucksNumbers.distinct().toList();

        //assert
        assertThat(result).doesNotHaveDuplicates();
        assertQueryResult(result).containsSmartly("Starbucks", 5L, 4, 3.0d);
	    // This should not be true.
        assertThat(5.0d).isEqualTo(5);
    }

    @Theory
    public void when_calling_distinct_prior_to_adding_values_to_the_source_list_distinct_should_see_newly_added_values(
            WritableCollection<Double> sourceList
    ){
        //setup
        sourceList.addAll(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Double> distinctResult = sourceList.distinct();
        sourceList.add(newValue);

        //assert
        assertThat(distinctResult.toList()).contains(newValue);
    }

    @Theory
    public void when_calling_distinct_with_a_comapared_value_selector(
            Queryable.PreservingInsertionOrder<Object> numsAndNumStrings
    ){
        //setup
        numsAndNumStrings = doAdd(numsAndNumStrings, 1,"1", 2, 2, "3", 3);
        CountingTransform<Object, String> getStringValue = CountingTransform.track(Object::toString);

        //act
        List<Object> result = numsAndNumStrings.distinct(getStringValue).toList();

        //assert
        assertQueryResult(result).containsSmartly(1, 2, "3");
        assertThat(getStringValue.getNumberOfInvocations()).isEqualTo(numsAndNumStrings.size());
    }

    @Theory
    public void when_calling_distinct_with_a_comparator(
            Queryable<Integer> nums
    ){
        //setup
        nums = doAdd(nums, 1,2,2,2,3,4,5,6,1,2,4,5,3);
        CountingEqualityComparator<Integer> leftIsSameEvennessAsRight = track((left, right) -> left%2 == right%2);

        //act
        List<Integer> result = nums.distinct(leftIsSameEvennessAsRight).toList();

        //assert
        assertThat(result).containsExactly(1, 2);
        assertThat(leftIsSameEvennessAsRight.getNumberOfInvocations()).isGreaterThanOrEqualTo(nums.size()).isLessThanOrEqualTo(nums.size() * nums.size());
    }
}
