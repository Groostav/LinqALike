package com.empowerops.linqalike;

import com.empowerops.assists.CountingTransform;
import com.empowerops.assists.QueryFixtureBase;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-09.
 */
public class AverageFixture extends QueryFixtureBase {

    @Test
    public void when_averaging_a_set_of_doubles_the_result_should_be_the_average(){
        //setup
        LinqingList<Double> divingBoardHeights = new LinqingList<>(2.5, 5.0, 7.5, 10.0, 12.5);
        CountingTransform<Double, Double> identity = CountingTransform.track(x -> x);

        //act
        double average = divingBoardHeights.average(identity);

        //assert
        assertThat(average).isEqualTo(7.5);
        identity.shouldHaveBeenInvoked(FIVE_TIMES);
    }

    @Test
    public void when_averaging_an_empty_set_average_call_should_give_NaN(){
        //setup
        LinqingList<AverageFixture> fixtures = new LinqingList<>();

        //act & assert
        double result = fixtures.average(x -> 10.0d);

        //assert
        assertThat(result).isNaN();
    }
}
