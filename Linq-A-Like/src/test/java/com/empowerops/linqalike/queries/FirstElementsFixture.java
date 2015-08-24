package com.empowerops.linqalike.queries;

import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
public class FirstElementsFixture extends QueryFixtureBase {

    @Test
    public void when_asking_for_the_first_three_letters_in_the_alphabet(){
        //setup
        LinqingList<String> alphabet = new LinqingList<String>("A", "B", "C", "D", "E");

        //act
        List<String> abcs = alphabet.first(3).toList();

        //assert
        assertThat(abcs).containsExactly("A", "B", "C");
        //assertThat(abcs).isEasyAs(1, 2, 3);
    }

    @Test
    public void when_asking_for_the_first_4_elements_of_a_3_element_list(){
        //setup
        LinqingList<Integer> countDown = new LinqingList<>(3, 2, 1);

        //act
        List<Integer> theFinalCountDown = countDown.first(5).toList();

        //assert
        assertThat(theFinalCountDown).containsExactly(3, 2, 1);
    }

    @Test
    public void when_asking_for_the_first_elements_the_result_should_be_lazy(){
        //setup
        LinqingList<Integer> ints = new LinqingList<>(1, 2, 3, 4, 5, 6);

        //act
        Queryable<Integer> firstQuery = ints.first(3);
        ints.add(0, 0);
        ints.add(0, -1);

        //assert
        assertThat(firstQuery.toList()).containsExactly(- 1, 0, 1);
    }
}
