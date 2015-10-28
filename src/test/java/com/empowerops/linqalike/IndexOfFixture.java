package com.empowerops.linqalike;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2015-10-27.
 */
public class IndexOfFixture {

    @Test
    public void when_asking_for_index_using_specific_equality_comparer_should_get_expected_element(){
        //setup
        QueryableList<String> names = makeList("Geoff", "Justin", "Vincent");

        //act
        int index = names.indexOfElement("G", (l, r) -> l.charAt(0) == r.charAt(0));

        //assert
        assertThat(index).isEqualTo(0);
    }

    @Test
    public void when_getting_index_of_element_with_many_matches_should_get_first_match(){
        //setup
        QueryableList<String> names = makeList("Geoff", "Justin", "Justice");

        //act
        int index = names.indexOfElement("J", (l, r) -> l.charAt(0) == r.charAt(0));

        //assert
        assertThat(index).isEqualTo(1);
    }

    @SuppressWarnings("RedundantStringConstructorCall") //no it isn't, since we're looking for reference equality.
    @Test
    public void when_looking_for_specific_element_amongst_many_with_reference_equality_should_get_correct_element(){
        //setup
        QueryableList<String> names = makeList(new String("a"), new String("a"), new String("a"));

        //act
        int index = names.indexOfElement(names.second(), CommonDelegates.ReferenceEquality);

        //assert
        assertThat(index).isEqualTo(1);
    }

    private <T> QueryableList<T> makeList(T... elements) {
        return new LinqingList<>(elements);
    }
}