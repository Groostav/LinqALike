package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.common.Tuple;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.assists.CountingCondition.track;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-20.
 */
@RunWith(Theories.class)
public class SingleOrDefaultFixture extends QueryFixtureBase {

    //TODO

    @Theory
    public void when_calling_single_or_default_with_a_condition_on_a_set_containing_no_elements_matching_that_condition_the_result_should_be_null(
            Queryable<Integer> numbersGreaterThanOne
    ){
        //setup
        numbersGreaterThanOne = doAdd(numbersGreaterThanOne, 2, 3, 4);
        CountingCondition<Integer> condition;

        //act
        Integer one = numbersGreaterThanOne.singleOrDefault(condition = track(x -> x == 1));

        //assert
        assertThat(one).isNull();
        assertThat(condition.getNumberOfInvocations()).isEqualTo(3);
    }

    @Test
    public void when_calling_single_or_default_on_biqueryable_with_one_entry_should_get_that_entry(){
        //setup
        BiQueryable<String, Integer> nums = from("1").zip(from(1));

        //act
        Tuple<String, Integer> one = nums.singleOrDefault((left, right) -> left.equals("1") && right.equals(1));

        //assert
        assertThat(one).isNotNull();
        assertThat(one.left).isEqualTo("1");
        assertThat(one.right).isEqualTo(1);
    }
}
