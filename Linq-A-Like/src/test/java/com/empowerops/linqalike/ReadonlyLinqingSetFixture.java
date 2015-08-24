package com.empowerops.linqalike;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ReadonlyLinqingSetFixture {

    @Test
    public void when_constructed_elements_are_retained(){
        //setup & act
        ReadonlyLinqingSet<Integer> set = new ReadonlyLinqingSet<>(1, 2, 3, 4);

        //assert
        assertThat(set).containsOnly(1, 2, 3, 4);
    }

}