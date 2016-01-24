package com.empowerops.linqalike;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 1/23/2016.
 */
public class ThroughFixture {

    @Test
    public void when_calling_through_a_simple_three_element_generator_should_get_proper_three_elements(){
        // setup & act
        Queryable<Integer> generator = Factories.through(0, i -> i + 1, i -> i < 3).immediately();

        //assert
        assertThat(generator).containsExactly(0, 1, 2);
    }

    @Test
    public void when_calling_through_super_classes_should_get_as_expected(){
        //setup & act
        Queryable<Class> intSupers = Factories.through((Class) Integer.class, Class::getSuperclass).immediately();

        //assert
        assertThat(intSupers).containsExactly(Integer.class, Number.class, Object.class);
    }

    @Test
    public void when_calling_through_set_with_only_seed_should_get_set_of_one(){
        // setup & act
        Queryable<Integer> generator = Factories.through(0, i -> i + 1, i -> i <= 0).immediately();

        //assert
        assertThat(generator).containsExactly(0);
    }

    @Test
    public void when_calling_on_seed_that_doesnt_pass_should_get_set_of_zero(){
        // setup & act
        Queryable<Integer> generator = Factories.through(0, i -> i + 1, i -> i < 0).immediately();

        //assert
        assertThat(generator).isEmpty();
    }

    @Test
    public void when_calling_on_null_seed_with_passing_condition_should_get_null(){
        //setup
        Queryable<Object> objects = Factories.through(null, x -> new Object(), x -> x == null);

        //assert
        assertThat(objects).containsOnly(new Object[]{null});
    }
}
