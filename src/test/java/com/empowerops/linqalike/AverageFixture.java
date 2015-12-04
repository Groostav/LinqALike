package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
@RunWith(Theories.class)
public class AverageFixture extends QueryFixtureBase {

    @Theory
    public void when_averaging_a_set_of_doubles_the_result_should_be_the_average(
            Queryable<Double> divingBoardHeights
    ){
        //setup
        divingBoardHeights = doAdd(divingBoardHeights, 2.5, 5.0, 7.5, 10.0, 12.5);
        CountingTransform<Double, Double> identity = CountingTransform.track(x -> x);

        //act
        double average = divingBoardHeights.average(identity);

        //assert
        assertThat(average).isEqualTo(7.5);
        identity.shouldHaveBeenInvoked(FIVE_TIMES);
    }

    @Theory
    public void when_averaging_an_empty_set_average_call_should_give_NaN(
            Queryable<Double> fixtures
    ){

        //act & assert
        double result = fixtures.average(x -> 10.0d);

        //assert
        assertThat(result).isNaN();
    }
}
