
package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.common.SetIsEmptyException;
import com.empowerops.linqalike.common.SingletonSetContainsMultipleElementsException;
import org.junit.Test;

import static com.empowerops.linqalike.assists.CountingCondition.track;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-04-16.
 */
public class SingleFixture extends QueryFixtureBase{

    @Test
    public void when_called_on_a_list_containing_one_element_that_element_should_be_returned(){
        //setup
        LinqingList<Integer> theAnswer = new LinqingList<>(new Integer[]{42});

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

