package UnitTests.Immediates;

import Assists.CountingTransform;
import Assists.QueryFixtureBase;
import LinqALike.Common.SetIsEmptyException;
import LinqALike.LinqingList;
import org.junit.Test;

import static Assists.Exceptions.assertThrows;
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
    public void when_averaging_an_empty_set_average_call_should_throw(){
        //setup
        LinqingList<AverageFixture> fixtures = new LinqingList<>();

        //act & assert
        assertThrows(SetIsEmptyException.class, () -> fixtures.average(x -> 10.0d));
    }
}
