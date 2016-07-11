package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Tuple;
import org.junit.Test;

import static com.empowerops.linqalike.common.Tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2016-07-09.
 */
public class BiQueryableSetEqualsFixture {

    @Test
    public void when_calling_set_equals_on_reversably_equals_sets_should_yield_true(){

        BiQueryable<String, Double> ascendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));
        BiQueryable<String, Double> descendingPairs = Factories.pairs(Pair("second", 2.0), Pair("first", 1.0));

        assertThat(ascendingPairs.setEquals(descendingPairs)).isTrue();
        assertThat(ascendingPairs.setEquals(descendingPairs, String::equals, Double::equals)).isTrue();
        assertThat(ascendingPairs.setEquals(descendingPairs, (a, b) -> a + "_" + b.toString())).isTrue();
        assertThat(ascendingPairs.setEquals(descendingPairs, a -> a, b -> b + 5)).isTrue();
    }

    @Test
    public void when_calling_sequence_equals_on_two_identical_sequences_should_get_true(){

        BiQueryable<String, Double> ascendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));
        BiQueryable<String, Double> descendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));

        assertThat(ascendingPairs.sequenceEquals(descendingPairs)).isTrue();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, String::equals, Double::equals)).isTrue();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, (a, b) -> a + "_" + b.toString())).isTrue();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, a -> a, b -> b + 5)).isTrue();
    }

    @Test
    public void when_calling_set_equals_on_different_sets_should_yield_false(){

        BiQueryable<String, Double> ascendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));
        BiQueryable<String, Double> descendingPairs = Factories.pairs(Pair("second", 2.0), Pair("first", 1.0), Pair("third", 3.0));

        assertThat(ascendingPairs.setEquals(descendingPairs)).isFalse();
        assertThat(ascendingPairs.setEquals(descendingPairs, String::equals, Double::equals)).isFalse();
        assertThat(ascendingPairs.setEquals(descendingPairs, (a, b) -> a + "_" + b.toString())).isFalse();
        assertThat(ascendingPairs.setEquals(descendingPairs, a -> a, b -> b + 5)).isFalse();
    }

    @Test
    public void when_calling_sequence_equals_on_two_different_sequences_should_get_false(){

        BiQueryable<String, Double> ascendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));
        BiQueryable<String, Double> descendingPairs = Factories.pairs(Pair("second", 2.0));

        assertThat(ascendingPairs.sequenceEquals(descendingPairs)).isFalse();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, String::equals, Double::equals)).isFalse();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, (a, b) -> a + "_" + b.toString())).isFalse();
        assertThat(ascendingPairs.sequenceEquals(descendingPairs, a -> a, b -> b + 5)).isFalse();
    }

    @Test
    public void when_converting_from_biqueryable_to_iterable_with_subclass_should_not_cause_problems(){
        BiQueryable<String, Double> ascendingPairs = Factories.pairs(Pair("first", 1.0), Pair("second", 2.0));
        BiQueryable<String, Number> descendingPairs = Factories.pairs(Pair("second", 2.0), Pair("first", 1.0));

        assertThat(ascendingPairs.setEquals(ascendingPairs)).isTrue();
    }
}
