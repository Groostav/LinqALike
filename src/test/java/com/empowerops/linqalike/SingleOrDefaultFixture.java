package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

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
        CountingCondition<Integer> numbersEqualToOne = CountingCondition.track(x -> x == 1);

        //act
        Integer one = numbersGreaterThanOne.singleOrDefault(numbersEqualToOne);

        //assert
        assertThat(one).isNull();
    }
}
