package com.empowerops.linqalike;

import org.junit.Test;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-04-24.
 */
public class IsSetEquivalentFixture {

    @Test
    public void when_asking_if_two_non_equal_sets_are_equivalent(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4);
        LinqingList<Integer> right = new LinqingList<>(2, 3, 4);

        //act
        boolean result = left.setEquals(right);

        //assert
        assertThat(result).isFalse();
    }

    @Test
    public void when_asking_if_two_equal_sets_are_equivalent(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4);
        LinqingList<Integer> right = new LinqingList<>(1, 2, 3, 4);

        //act
        boolean result = left.setEquals(right);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable") // necessary for the test
    public void when_asking_if_the_same_sets_are_set_equivalent(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4);
        LinqingList<Integer> right = left;

        //act
        boolean result = left.setEquals(right);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_if_the_null_is_equivalent_the_query_should_throw(){
        //setup
        LinqingList<Integer> left = new LinqingList<Integer>();

        //act & assert
        assertThrows(IllegalArgumentException.class, () -> left.setEquals(null));
    }
}
