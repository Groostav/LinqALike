package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 30/04/2014.
 */
public class ReversedQueryFixture extends QueryFixtureBase{

    @Test
    public void when_reversing_a_set_of_numbers(){
        //setup
        LinqingList<Integer> list = new LinqingList<>(1, 2, 3);

        //act
        List<Integer> reversed = list.reversed().toList();

        //assert
        assertThat(reversed).containsExactly(3, 2, 1);
    }

    @Test
    public void when_a_bag_is_reversed(){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("454");
        EquatableValue secondDuplicate = new EquatableValue("454");
        LinqingList<EquatableValue> list = new LinqingList<>(new EquatableValue("354"), firstDuplicate, secondDuplicate);

        //act
        List<EquatableValue> reversed = list.reversed().toList();

        //assert
        assertThat(reversed).containsExactly(firstDuplicate, firstDuplicate, new EquatableValue("354"));
        assertThat(reversed.get(0)).isSameAs(secondDuplicate);
        assertThat(reversed.get(1)).isSameAs(firstDuplicate);
    }

    @Test
    public void when_empty_set_is_reversed(){
        //setup
        LinqingList<Double> emptySet = new LinqingList<>();

        //act
        List<Double> reversedEmptySet = emptySet.reversed().toList();

        //assert
        assertThat(reversedEmptySet).isEmpty();
    }

    @Test
    public void when_given_source_is_reversed_prior_to_adding_a_value_the_added_item_should_appear_in_reversed_list(){
        //setup
        LinqingList<Integer> source = new LinqingList<>(1, 2, 3);
        Queryable<Integer> reversed = source.reversed();

        //act
        source.add(4);

        //assert
        assertThat(reversed.toList()).containsExactly(4, 3, 2, 1);
    }
}
