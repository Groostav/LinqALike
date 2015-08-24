package com.empowerops.linqalike;

import com.empowerops.assists.CountingCondition;
import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
public class AnyFixture extends QueryFixtureBase {

    @Test
    public void when_calling_any_on_set_with_one_element_passing(){
        //setup
        LinqingList<String> countriesWithC = new LinqingList<>("Canada", "United States of America", "Mexico");
        CountingCondition<String> startsWithC = CountingCondition.track(x -> x.startsWith("C"));

        //act
        boolean result = countriesWithC.any(startsWithC);

        //assert
        assertThat(result).isTrue();
        startsWithC.shouldHaveBeenInvoked(ONCE);
    }

    @Test
    public void when_calling_any_on_set_with_no_elements_passing(){
        //setup
        LinqingList<Integer> bigNums = new LinqingList<>(1000, 1000000, 1000000000);
        CountingCondition<Integer> isSmallNum = CountingCondition.track(x -> x < 10);

        //act
        boolean result = bigNums.any(isSmallNum);

        //assert
        assertThat(result).isFalse();
        isSmallNum.shouldHaveBeenInvoked(THRICE);
    }

    @Test
    public void when_calling_any_with_condition_on_empty_set_result_should_be_false(){
        //setup
        LinqingList<Double> noNums = new LinqingList<>();
        CountingCondition<Double> isSmallNum = CountingCondition.track(x -> x < 10.0);

        //act
        boolean result = noNums.any(isSmallNum);

        //assert
        assertThat(result).isFalse();
        isSmallNum.shouldHaveBeenInvoked(NEVER);
    }

    @Test
    public void when_calling_conditionless_any_on_empty_set_result_should_be_false(){
        //setup
        LinqingList<String> noContent = new LinqingList<>();

        //act
        boolean result = noContent.any();

        //assert
        assertThat(result).isFalse();
    }

    @Test
    public void when_calling_conditionless_any_on_set_containing_one_element_result_should_be_true(){
        //setup
        LinqingList<String> content = new LinqingList<>("data!");

        //act
        boolean result = content.any();

        //assert
        assertThat(result).isTrue();
    }
}
