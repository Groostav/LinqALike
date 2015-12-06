package com.empowerops.linqalike;

import org.junit.Test;

import static com.empowerops.linqalike.Factories.empty;
import static com.empowerops.linqalike.Factories.from;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-06-18.
 */
public class IsSubsetOfFixture_ {

    @Test
    public void when_asking_a_simple_subset_if_its_a_subset_linq_should_return_true(){
        //setup
        Queryable<String> subset = from("A", "B", "C");
        Queryable<String> superset = from("A", "1", "B", "2", "C");

        //act
        boolean result = subset.isSubsetOf(superset);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_empty_set_if_its_a_subset_of_a_set_linq_should_return_true(){
        //setup
        Queryable<String> subset = empty();
        Queryable<String> superSet = from("A", "B", "C");

        //act
        boolean result = subset.isSubsetOf(superSet);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_superset_if_its_a_subset_of_a_smaller_set_linq_should_return_false(){
        //setup
        Queryable<String> subset = from("A", "B", "C");
        Queryable<String> superset = from("A", "1", "B", "2", "C");

        //act
        boolean result = superset.isSubsetOf(subset);

        //assert
        assertThat(result).isFalse();
    }

    @Test
    public void when_asking_if_two_refEquals_set_are_subsets_linq_should_return_true(){
        //setup
        Queryable<Integer> set = Factories.from(1, 2, 3);

        //act
        boolean result = set.isSubsetOf(set);

        //assert
        assertThat(result).isTrue();
    }
}

