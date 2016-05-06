package com.empowerops.linqalike;

import org.junit.Test;

import static com.empowerops.linqalike.common.Tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 5/6/2016.
 */
public class LinqingMapFixture {

    @Test
    public void when_calling_getAll_resulting_query_should_be_ordered_by_supplied_keys(){
        //setup
        LinqingMap<String, Double> values = new LinqingMap<>(
                Pair("x1", 1.0),
                Pair("x3", 3.0),
                Pair("x4", 4.0),
                Pair("x2", 2.0)
        );
        LinqingList<String> keys = new LinqingList<>("x1", "x2", "x3");

        //act
        Queryable<Double> result = values.getAll(keys);

        //assert
        assertThat(result).containsExactly(1.0, 2.0, 3.0);
    }

    @Test
    public void when_calling_getAll_resulting_query_should_be_lazily_updated(){
        //setup
        LinqingMap<String, Double> values = new LinqingMap<>(
                Pair("x1", 1.0),
                Pair("x2", 2.0),
                Pair("x3", 3.0),
                Pair("x4", 4.0)
        );

        //act
        Queryable<Double> result = values.getAll(Factories.from("x1", "x2", "x3", "x5"));
        values.put("x5", 5.0);

        //assert
        assertThat(result).containsExactly(1.0, 2.0, 3.0, 5.0);

    }
}