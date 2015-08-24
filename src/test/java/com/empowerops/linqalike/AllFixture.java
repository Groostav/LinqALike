package com.empowerops.linqalike;

import com.empowerops.assists.CountingCondition;
import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
public class AllFixture extends QueryFixtureBase {

    @Test
    public void when_calling_all_on_a_set_of_passing_objects_all_should_return_true(){
        //setup
        LinqingList<String> countriesStaringWithN = new LinqingList<>("New Zealand", "Nigera", "Norway");
        CountingCondition<String> startsWithN = CountingCondition.track(x -> x.startsWith("N"));

        //act
        boolean result = countriesStaringWithN.all(startsWithN);

        //assert
        assertThat(result).isTrue();
        startsWithN.shouldHaveBeenInvoked(THRICE);
    }

    @Test
    public void when_calling_all_on_a_group_containing_one_failing_element_all_should_return_false(){
        //setup
        LinqingList<Object> differentTypes = new LinqingList<>(1.0d, 2.0f, "3.0", 4L);
        CountingCondition<Object> isNumeric = CountingCondition.track(x -> x instanceof Number);

        //act
        boolean result = differentTypes.all(isNumeric);

        //assert
        assertThat(result).isFalse();
        isNumeric.shouldHaveBeenInvoked(THRICE);
    }

    @Test
    public void when_calling_all_on_the_empty_set_result_should_be_true(){
        //setup
        LinqingList<Integer> emptyNumbersSet = new LinqingList<>();
        CountingCondition<Integer> falsehood = CountingCondition.track(x -> false);

        //act
        boolean result = emptyNumbersSet.all(falsehood);

        //assert
        assertThat(result).isTrue();
        falsehood.shouldHaveBeenInvoked(NEVER);
    }
}
