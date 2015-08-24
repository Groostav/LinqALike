package com.empowerops.linqalike;

import com.empowerops.assists.*;
import org.junit.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-20.
 */
public class SingleOrDefaultFixture extends QueryFixtureBase {

    //TODO

    @Test
    public void when_calling_single_or_default_with_a_condition_on_a_set_containing_no_elements_matching_that_condition_the_result_should_be_null(){
        //setup
        LinqingList<Integer> numbersGreaterThanOne = new LinqingList<>(2, 3, 4);
        CountingCondition<Integer> numbersEqualToOne = CountingCondition.track(x -> x == 1);

        //act
        Integer one = numbersGreaterThanOne.singleOrDefault(numbersEqualToOne);

        //assert
        assertThat(one).isNull();
    }
}
