package com.empowerops.linqalike;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.empowerops.linqalike.Factories.from;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-10-02.
 */
public class QueryableUsageExamples {

    @Test
    public void when_youve_got_an_obnoxious_grid_you_want_to_convert_to_a_readonly_queryable(){
        //setup
        List<List<Integer>> integerGrid = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5), Arrays.asList(6));

        //act
        Queryable<Queryable<Integer>> queryableGrid = from(integerGrid).select(Factories::from);

        //assert
        assertThat(queryableGrid.first().toList())          .containsExactly(1, 2, 3);
        assertThat(queryableGrid.skip(1).first().toList())  .containsExactly(4, 5);
        assertThat(queryableGrid.last().toList())           .containsExactly(6);
    }
}
