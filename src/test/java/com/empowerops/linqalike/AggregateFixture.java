package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Created by Geoff on 2014-10-13.
*/
@RunWith(Theories.class)
public class AggregateFixture extends FixtureBase {

    @Theory
    public void when_concatinating_a_nonempty_list_of_names_the_result_should_be_a_string_join(
            Queryable.PreservingInsertionOrder<String> names
    ){
        //setup
        names = doAdd(names, "Jimmy", "Ken", "Bob", "Alice");

        //act
        String result = names.aggregate((left, right) -> left + " " + right).get();

        //assert
        assertThat(result).isEqualTo("Jimmy Ken Bob Alice");
    }


    @Theory
    public void when_aggregating_an_empty_set_without_seed_should_give_optional_empty(
            Queryable<Integer> emptySet
    ){
        //setup
        emptySet = doClear(emptySet);

        //act
        Optional<Integer> result = emptySet.aggregate((left, right) -> {throw new IllegalStateException();});

        //assert
        assertThat(result).isEmpty();
    }

    @Theory
    public void when_performing_product_with_aggregate_and_seed(
            Queryable<Integer> primes
    ){
        //setup
        primes = doAdd(primes, 5, 7, 11, 13, 17);

        //act
        double result = primes.aggregate(1.0, (Double left, Integer right) -> left * right);

        //assert
        assertThat(result).isEqualTo(5 * 7 * 11 * 13 * 17);
    }

    @Theory
    public void when_aggregating_on_dispirate_type(
            Queryable<String> zombieHunters
    ){
        //setup
        zombieHunters = doAdd(zombieHunters, "Bill", "Francis", "Louis", "Zoey");

        //act & spoilers
        double livingMembers = zombieHunters.aggregate(0, (aliveCount, member) -> aliveCount += member.equals("Bill") ? 0 : 1);

        //assert
        assertThat(livingMembers).isEqualTo(3);
    }

    @Theory
    public void when_aggregating_an_empty_set_with_seed_should_return_seed(
            Queryable<Double> emptySet
    ){
        //setup
        emptySet = doClear(emptySet);

        //act
        double result = emptySet.aggregate(42.0, (Double left, Double right) -> {throw new IllegalStateException();});

        //assert
        assertThat(result).isEqualTo(42.0);
    }
}
