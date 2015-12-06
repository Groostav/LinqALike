package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.empowerops.linqalike.assists.CountingCondition.track;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
@RunWith(Theories.class)
public class AnyFixture extends FixtureBase {

    @Theory
    public void when_calling_any_on_set_with_one_element_passing(
            Queryable<String> countries
    ){
        //setup
        countries = doAdd(countries, "Canada", "United States of America", "Mexico");
        CountingCondition<String> startsWithC = track(x -> x.startsWith("C"));

        //act
        boolean result = countries.any(startsWithC);

        //assert
        assertThat(result).isTrue();
        startsWithC.shouldHaveBeenInvoked(ONCE);
    }

    @Theory
    public void when_calling_any_on_set_with_no_elements_passing(
		Queryable<Integer> bigNums
	){
        //setup
        bigNums = doAdd(bigNums, 1000, 1000000, 1000000000);
        CountingCondition<Integer> isSmallNum = track(x -> x < 10);

        //act
        boolean result = bigNums.any(isSmallNum);

        //assert
        assertThat(result).isFalse();
        isSmallNum.shouldHaveBeenInvoked(THRICE);
    }

    @Theory
    public void when_calling_any_with_condition_on_empty_set_result_should_be_false(
		Queryable<Double> noNums
	){
        //setup
        CountingCondition<Double> isSmallNum = track(x -> x < 10.0);

        //act
        boolean result = noNums.any(isSmallNum);

        //assert
        assertThat(result).isFalse();
        isSmallNum.shouldHaveBeenInvoked(NEVER);
    }

    @Theory
    public void when_calling_conditionless_any_on_empty_set_result_should_be_false(
		Queryable<String> noContent
	){

        //act
        boolean result = noContent.any();

        //assert
        assertThat(result).isFalse();
    }

    @Theory
    public void when_calling_conditionless_any_on_set_containing_one_element_result_should_be_true(
		Queryable<String> content
	){
        //setup
        content = doAdd(content, "data!");

        //act
        boolean result = content.any();

        //assert
        assertThat(result).isTrue();
    }
}
