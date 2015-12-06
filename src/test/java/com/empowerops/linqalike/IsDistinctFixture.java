package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-07-26.
 */
@RunWith(Theories.class)
public class IsDistinctFixture extends FixtureBase {

    @Theory
    public void when_asking_if_set_is_distinct(
            Queryable<Double> heights
    ){
        //setup
        heights = doAdd(heights, 6.1, 5.4, 6.4);

        //act
        boolean result = heights.isDistinct();

        //assert
        assertThat(result).isTrue();
    }

    @Theory
    public void when_asking_if_bag_is_distinct(
            QueryableList<Integer> friendsAreaCodes
    ){
        //setup
        friendsAreaCodes = doAdd(friendsAreaCodes, 604, 604, 778, 508);

        //act
        boolean result = friendsAreaCodes.isDistinct();

        //assert
        assertThat(result).isFalse();
    }

    @Theory
    public void when_asking_if_empty_set_is_distinct(
            Queryable<Integer> emptySet
    ){
        //setup
        emptySet = doAdd(emptySet);

        //act
        boolean result = emptySet.isDistinct();

        //assert
        assertThat(result).isTrue();
    }

    @Theory
    public void when_asking_if_set_with_comparable_is_distinct(
            Queryable<NamedValue> flowers
    ){
        //setup
        flowers = doAdd(flowers,
                new NamedValue("Rose"), new NamedValue("Mum"), new NamedValue("Daisy"),
                new NamedValue("Tulip"), new NamedValue("Carnation"), new NamedValue("Hydrangea"),
                new NamedValue("Sun flower"), new NamedValue("Dandelion"), new NamedValue("Geranium"),
                new NamedValue("Orchid"));

        //act
        boolean result = flowers.isDistinct(NamedValue::getName);

        //assert
        assertThat(result).isTrue();
    }

    @Theory
    public void when_asking_if_bag_with_comparator_is_distinct(
            QueryableList<NamedValue> flowers
    ){
        //setup
        flowers = doAdd(flowers,
                new NamedValue("Rose"), new NamedValue("Mum"), new NamedValue("Daisy"),
                new NamedValue("Tulip"), new NamedValue("Carnation"), new NamedValue("Hydrangea"),
                new NamedValue("Sun flower"), new NamedValue("Dandelion"), new NamedValue("Geranium"),
                new NamedValue("Orchid"));

        //act
        boolean result = flowers.isDistinct((left, right) -> left.getName().length() == right.getName().length());

        //assert
        assertThat(result).isFalse();
    }
}
