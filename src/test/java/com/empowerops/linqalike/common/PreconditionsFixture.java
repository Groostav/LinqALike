package com.empowerops.linqalike.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.empowerops.linqalike.assists.Exceptions.assertDoesNotThrow;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;

/**
 * Yes, I'm testing my test-validation stuff, deal with it.
 *
 * --I was foolish enough to do reference equality on an Optional
 * (clearly still getting the hang of this functional programming thing)
 * and if there's one thing I believe in more than functional programming
 * its prooving your bugs. So this is a proof/regression against that error.
 *
 * Created by Geoff on 2015-11-06.
 */
public class PreconditionsFixture {

    @Test
    public void when_asserting_precondition_same_size_on_two_similarly_sized_collections_should_get_no_errors(){
        //setup
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<String> strings = Arrays.asList("A", "B", "C");

        //act & assert
        assertDoesNotThrow(() -> Preconditions.fastSameSize(ints, strings));
    }

    @Test
    public void when_asserting_two_differently_sized_lists_have_same_size_should_get_no_errors(){
        //setup
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<String> strings = Arrays.asList("A", "B", "C", "D", "E", "F");

        //act & assert
        assertThrows(IllegalArgumentException.class, () -> Preconditions.fastSameSize(ints, strings));
    }
}