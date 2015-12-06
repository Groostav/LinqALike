package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.common.SingletonSetContainsMultipleElementsException;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import com.empowerops.linqalike.common.Tuple;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Optional;
import org.junit.Test;

import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Geoff on 2014-05-20.
 */
@RunWith(Theories.class)
public class SingleOrDefaultFixture extends FixtureBase {

    @Theory
    public void when_calling_singleOrDefault_empty_set_should_get_empty(
            WritableCollection<Integer> singletonSet
    ) {
        //setup
        singletonSet.clear();

        //act
        Optional<Integer> one = singletonSet.singleOrDefault();

        //assert
        assertThat(one).isEqualTo(Optional.empty());
    }

    @Theory
    public void when_calling_singleOrDefault_on_a_set_with_one_element_should_get_that_one_element(
            WritableCollection<Integer> singletonSet
    ) {
        //setup
        singletonSet.add(1);

        //act
        Optional<Integer> one = singletonSet.singleOrDefault();

        //assert
        assertThat(one.get()).isEqualTo(1);
    }

    @Theory
    public void when_calling_singleOrDefault_on_a_set_with_many_elements_should_throw(
            WritableCollection<Integer> singletonSet
    ) {
        //setup
        singletonSet.addAll(1, 2, 3, 4, 5, 6);

        //act & assert
        assertThrows(SingletonSetContainsMultipleElementsException.class, singletonSet::singleOrDefault);
    }

    @Theory
    public void when_calling_singleOrDefault_on_a_set_with_one_element_and_condition_to_match_should_get_that_one_element(
            WritableCollection<Integer> singletonSet
    ) {
        //setup
        singletonSet.add(1);
        CountingCondition<Integer> numbersEqualToOne = CountingCondition.track(x -> x == 1);

        //act
        Optional<Integer> one = singletonSet.singleOrDefault(numbersEqualToOne);

        //assert
        assertThat(one.get()).isEqualTo(1);
        assertThat(numbersEqualToOne.getNumberOfInvocations()).isEqualTo(singletonSet.size());
    }

    @Theory
    public void when_calling_singleOrDefault_with_a_condition_on_a_set_containing_no_elements_matching_that_condition_the_result_should_be_empty(
            WritableCollection<Integer> numbersGreaterThanOne
    ) {
        //setup
        numbersGreaterThanOne.addAll(2, 3, 4);
        CountingCondition<Integer> numbersEqualToOne = CountingCondition.track(x -> x == 1);

        //act
        Optional<Integer> one = numbersGreaterThanOne.singleOrDefault(numbersEqualToOne);

        //assert
        assertThat(one).isEqualTo(Optional.empty());
        assertThat(numbersEqualToOne.getNumberOfInvocations()).isEqualTo(numbersGreaterThanOne.size());
    }

    @Test
    public void when_calling_single_or_default_on_biqueryable_with_one_entry_should_get_that_entry() {
        //setup
        BiQueryable<String, Integer> nums = from("1").zip(from(1));

        //act
        Optional<Tuple<String, Integer>> one = nums.singleOrDefault((left, right) -> left.equals("1") && right.equals(1));

        //assert
        assertThat(one).isNotNull().isPresent();
        assertThat(one.get().left).isEqualTo("1");
        assertThat(one.get().right).isEqualTo(1);
    }
}