package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
@RunWith(Theories.class)
public class FirstElementsFixture extends FixtureBase {

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() { return FirstElementsQuery.class; }

    @Theory
    public void when_asking_for_the_first_three_letters_in_the_alphabet(
            Queryable<String> alphabet
    ){
        //setup
        alphabet = doAdd(alphabet, "A", "B", "C", "D", "E");

        //act
        FirstElementsQuery<String> abcs = asTypeUnderTest(alphabet.first(3));
        List<String> abcsList = abcs.toList();

        //assert
        assertThat(abcsList).containsExactly("A", "B", "C");
        assertThat(abcs.size()).isEqualTo(3);
        //assertThat(abcs).isEasyAs(1, 2, 3);
    }

    @Theory
    public void when_asking_for_the_first_4_elements_of_a_3_element_list(
            Queryable.PreservingInsertionOrder<Integer> countDown
    ){
        //setup
        countDown = doAdd(countDown, 3, 2, 1);

        //act
        FirstElementsQuery<Integer> first = asTypeUnderTest(countDown.first(5));
        List<Integer> theFinalCountDown = first.toList();

        //assert
        assertThat(first.size()).isEqualTo(3);
        assertThat(theFinalCountDown).containsExactly(3, 2, 1);
    }

    @Theory
    public void when_asking_for_the_first_elements_the_result_should_be_lazy(
            // since we're testing specifically at indexes and lazyness,
            // that restricts our candidate types to mutable collections supporting indexes.
            // Only 1 of those.
            LinqingList<Integer> ints
    ){
        //setup
        ints.addAll(1, 2, 3, 4, 5, 6);

        //act
        Queryable<Integer> firstQuery = ints.first(3);
        ints.add(0, 0);
        ints.add(0, -1);

        //assert
        assertThat(firstQuery.toList()).containsExactly(- 1, 0, 1);
    }

    @Theory
    public void when_asking_for_the_first_elements_the_result_should_be_lazy(
            WritableCollection<Integer> ints
    ){
        //setup
        ints.addAll(1, 2, 3);

        //act
        FirstElementsQuery<Integer> firstQuery = asTypeUnderTest(ints.first(4));
        LinqingList<Integer> firstElementsEagerList = firstQuery.toList();
        ints.addAll(4, 5);
        LinqingList<Integer> firstElementsLateList = firstQuery.toList();

        //assert
        assertThat(firstElementsEagerList).containsExactly(1, 2, 3);
        assertThat(firstQuery.size()).isEqualTo(4);
        assertThat(firstElementsLateList).containsExactly(1, 2, 3, 4);
    }

    @Test
    public void when_asking_for_the_first_elements_of_infinite_sequence_should_terminate_properly(){
        //setup
        Queryable<Double> theAnswer = Factories.repeat(42.0);

        //act
        FirstElementsQuery<Double> firstQuery = asTypeUnderTest(theAnswer.first(4));
        List<Double> firstList = firstQuery.toList();

        //assert
        assertThat(firstList).containsExactly(42.0, 42.0, 42.0, 42.0);
        assertThat(firstQuery.size()).isEqualTo(4);
    }

}
