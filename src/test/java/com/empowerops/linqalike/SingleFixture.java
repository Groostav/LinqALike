
package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.common.SetIsEmptyException;
import com.empowerops.linqalike.common.SingletonSetContainsMultipleElementsException;
import com.empowerops.linqalike.common.Tuple;
import javafx.beans.property.StringProperty;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.empowerops.linqalike.assists.CountingCondition.track;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-04-16.
 */
@RunWith(Theories.class)
public class SingleFixture extends QueryFixtureBase{

    @Theory
    public void when_called_on_a_list_containing_one_element_that_element_should_be_returned(
            Queryable<Integer> theAnswer
    ){
        //setup
        theAnswer = doAdd(theAnswer, 42);

        //act
        int result = theAnswer.single();

        //assert
        assertThat(result).isEqualTo(42);
    }

    @Theory
    public void when_called_on_an_empty_list_single_should_throw(
            Queryable<Integer> empty
    ){
        //setup
        empty = doAdd(empty);

        //act & assert
        assertThrows(SetIsEmptyException.class, empty::single);
    }

    @Theory
    public void when_called_on_a_set_containing_multiple_elements_single_should_throw(
            Queryable<Integer> empty
    ){
        //setup
        empty = doAdd(empty);

        //act & assert
        assertThrows(SetIsEmptyException.class, empty::single);
    }

    @Theory
    public void when_called_with_a_condition_that_passes_no_elements_of_the_set(
            Queryable<EquatableValue> candidates
    ){
        //setup
        Queryable<EquatableValue> candidates2 = doAdd(candidates,
                new EquatableValue("Starbucks"), new EquatableValue("Tim Hortans"), new EquatableValue("Waves"));

        CountingCondition<EquatableValue> petesCoffeeCondition = track(x -> x.equals(new EquatableValue("Peete's Coffee")));

        //act & assert
        assertThrows(SetIsEmptyException.class, () -> candidates2.single(petesCoffeeCondition));
        petesCoffeeCondition.shouldHaveBeenInvoked(THRICE);
    }

    @Theory
    public void when_called_with_a_condition_that_passes_two_elements_of_the_set(
            Queryable<String> colours
    ){
        //setup
        Queryable<String> colours2 = doAdd(colours, "Red", "Green", "Blue", "Black");
        CountingCondition<String> startsWithBCondition = track(x -> x.startsWith("B"));

        //act & assert
        assertThrows(SingletonSetContainsMultipleElementsException.class, () -> colours2.single(startsWithBCondition));
        startsWithBCondition.shouldHaveBeenInvoked(FOUR_TIMES);
    }

    @Theory
    public void when_called_with_a_condition_that_passes_exactly_one_element(
            Queryable<Double> numbers
    ){
        //setup
        numbers = doAdd(numbers, 0.0, 1.0, 2.0, 2.64, 3.14);
        CountingCondition<Double> betweenHalfAndOneAndAHalf = track(x -> x > 0.5 && x < 1.5);

        //act
        double one = numbers.single(betweenHalfAndOneAndAHalf);

        //assert
        assertThat(one).isEqualTo(1.0);
    }
}

