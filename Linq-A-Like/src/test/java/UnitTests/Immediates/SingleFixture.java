
package UnitTests.Immediates;

import Assists.CountingCondition;
import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.Common.SetIsEmptyException;
import com.EmpowerOperations.LinqALike.Common.SingletonSetContainsMultipleElementsException;
import com.EmpowerOperations.LinqALike.Factories;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Test;

import static Assists.CountingCondition.track;
import static Assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-04-16.
 */
public class SingleFixture extends QueryFixtureBase{

    @Test
    public void when_called_on_a_list_containing_one_element_that_element_should_be_returned(){
        //setup
        LinqingList<Integer> theAnswer = new LinqingList<>(42);

        //act
        int result = theAnswer.single();

        //assert
        assertThat(result).isEqualTo(42);
    }

    @Test
    public void when_called_on_an_empty_list_single_should_throw(){
        //setup
        LinqingList<Integer> empty = new LinqingList<>();

        //act & assert
        assertThrows(SetIsEmptyException.class, empty::single);
    }

    @Test
    public void when_called_on_a_set_containing_multiple_elements_single_should_throw(){
        //setup
        LinqingList<Integer> empty = new LinqingList<>();

        //act & assert
        assertThrows(SetIsEmptyException.class, empty::single);
    }

    @Test
    public void when_called_with_a_condition_that_passes_no_elements_of_the_set(){
        //setup
        LinqingList<EquatableValue> candidates = new LinqingList<>(
                new EquatableValue("Starbucks"), new EquatableValue("Tim Hortans"), new EquatableValue("Waves"));

        CountingCondition<EquatableValue> petesCoffeeCondition = track(x -> x.equals(new EquatableValue("Peete's Coffee")));

        //act & assert
        assertThrows(SetIsEmptyException.class, () -> candidates.single(petesCoffeeCondition));
        petesCoffeeCondition.shouldHaveBeenInvoked(THRICE);
    }

    @Test
    public void when_called_with_a_condition_that_passes_two_elements_of_the_set(){
        //setup
        LinqingList<String> colours = new LinqingList<>("Red", "Green", "Blue", "Black");
        CountingCondition<String> startsWithBCondition = track(x -> x.startsWith("B"));

        //act & assert
        assertThrows(SingletonSetContainsMultipleElementsException.class, () -> colours.single(startsWithBCondition));
        startsWithBCondition.shouldHaveBeenInvoked(FOUR_TIMES);
    }

    @Test
    public void when_called_with_a_condition_that_passes_exactly_one_element(){
        //setup
        Queryable<Double> numbers = Factories.from(0.0, 1.0, 2.0, 2.64, 3.14);
        CountingCondition<Double> betweenHalfAndOneAndAHalf = track(x -> x > 0.5 && x < 1.5);

        //act
        double one = numbers.single(betweenHalfAndOneAndAHalf);

        //assert
        assertThat(one).isEqualTo(1.0);
    }
}

