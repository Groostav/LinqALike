package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
@RunWith(Theories.class)
public class AllFixture extends QueryFixtureBase {

    @Theory
    public void when_calling_all_on_a_set_of_passing_objects_all_should_return_true(
            Queryable<String> countriesStartingWithN
    ){
        //setup
        countriesStartingWithN = doAdd(countriesStartingWithN, "New Zealand", "Nigera", "Norway");
        CountingCondition<String> startsWithN = CountingCondition.track(x -> x.startsWith("N"));

        //act
        boolean result = countriesStartingWithN.all(startsWithN);

        //assert
        assertThat(result).isTrue();
        startsWithN.shouldHaveBeenInvoked(THRICE);
    }

    @Theory
    public void when_calling_all_on_a_group_containing_one_failing_element_all_should_return_false(
            Queryable<Object> differentTypes
    ){
        //setup
        differentTypes = doAdd(differentTypes, 1.0d, 2.0f, "3.0", 4L);
        CountingCondition<Object> isNumeric = CountingCondition.track(x -> x instanceof Number);

        //act
        boolean result = differentTypes.all(isNumeric);

        //assert
        assertThat(result).isFalse();
        isNumeric.shouldHaveBeenInvoked(THRICE);
    }

    @Theory
    public void when_calling_all_on_the_empty_set_result_should_be_true(
            Queryable<Integer> emptyNumbersSet
    ){
        //setup
        CountingCondition<Integer> falsehood = CountingCondition.track(x -> false);

        //act
        boolean result = emptyNumbersSet.all(falsehood);

        //assert
        assertThat(result).isTrue();
        falsehood.shouldHaveBeenInvoked(NEVER);
    }
}
