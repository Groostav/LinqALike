package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-04.
 */
public class ToArrayFixture extends QueryFixtureBase{

    @Test
    public void when_calling_to_array_on_a_set_of_objects(){
        //setup
        LinqingList<EquatableValue> woodTypes = new LinqingList<EquatableValue>(
                new EquatableValue("Elm"), new EquatableValue("Birch"), new EquatableValue("Pine"),
                new EquatableValue("Hemlock"), new EquatableValue("Mahogony"), new EquatableValue("Cypress"),
                new EquatableValue("Oak"));

        //act
        EquatableValue[] result = woodTypes.toArray(EquatableValue[]::new);

        //assert
        assertThat(result.getClass().isArray()).isTrue();
        assertThat(result.getClass()).isEqualTo(EquatableValue[].class);
        assertThat(result).containsOnly(
                new EquatableValue("Elm"), new EquatableValue("Birch"), new EquatableValue("Pine"),
                new EquatableValue("Hemlock"), new EquatableValue("Mahogony"), new EquatableValue("Cypress"),
                new EquatableValue("Oak"));
    }

    @Test
    public void when_calling_to_array_on_a_set_of_ints(){
        //setup
        LinqingList<NumberValue> bettysRandomNumbers = new LinqingList<NumberValue>(
                new NumberValue(42),
                new NumberValue(64),
                new NumberValue(97),
                new NumberValue(271),
                new NumberValue(126),
                new NumberValue(1072),
                new NumberValue(587));

        //act
        int[] primativeResult = bettysRandomNumbers.toIntArray(elem -> elem.number);

        //assert
        assertThat(primativeResult.getClass().isArray()).isTrue();
        assertThat(primativeResult.getClass()).isEqualTo(int[].class);
        assertThat(primativeResult).isEqualTo(new int[]{42, 64, 97, 271, 126, 1072, 587});
    }
}
