package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-07-26.
 */
public class IsDistinctFixture extends QueryFixtureBase{

    @Test
    public void when_asking_if_set_is_distinct(){
        //setup
        LinqingList<Double> heights = new LinqingList<>(6.1, 5.4, 6.4);

        //act
        boolean result = heights.isDistinct();

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_if_bag_is_distinct(){
        //setup
        LinqingList<Integer> friendsAreaCodes = new LinqingList<>(604, 604, 778, 508);

        //act
        boolean result = friendsAreaCodes.isDistinct();

        //assert
        assertThat(result).isFalse();
    }

    @Test
    public void when_asking_if_empty_set_is_distinct(){
        //setup
        LinqingList<Integer> emptySet = new LinqingList<>();

        //act
        boolean result = emptySet.isDistinct();

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_if_set_with_comparable_is_distinct(){
        //setup
        LinqingList<NamedValue> flowers = new LinqingList<>(
                new NamedValue("Rose"), new NamedValue("Mum"), new NamedValue("Daisy"),
                new NamedValue("Tulip"), new NamedValue("Carnation"), new NamedValue("Hydrangea"),
                new NamedValue("Sun flower"), new NamedValue("Dandelion"), new NamedValue("Geranium"),
                new NamedValue("Orchid"));

        //act
        boolean result = flowers.isDistinct(NamedValue::getName);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_if_bag_with_comparator_is_distinct(){
        //setup
        LinqingList<NamedValue> flowers = new LinqingList<>(
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
