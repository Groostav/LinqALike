package com.empowerops.linqalike.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-11-11.
 */
public class ComparingLinkedHashSetFixture {

    @Test
    public void when_using_a_bad_uniqueness_comparator_set_maintains_most_recent_values(){
        //setup
        EqualityComparer<Integer> sameSign = (left, right) -> left / Math.abs(left) == right / Math.abs(right);
        ComparingLinkedHashSet<Integer> linkedHashSet = new ComparingLinkedHashSet<Integer>(sameSign, -1, 1);

        //act
        boolean madeChange = linkedHashSet.addAll(-2, 2);

        //assert
        assertThat(madeChange).describedAs("a change was made to the set").isFalse();
        assertThat(linkedHashSet.toList()).containsExactly(-1, 1);
    }

}
