package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.Exceptions;
import com.empowerops.linqalike.common.Tuple;
import org.junit.Test;

import java.util.Map;

import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.common.Tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

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
        Queryable<Double> result = values.getAll(from("x1", "x2", "x3", "x5"));
        values.put("x5", 5.0);

        //assert
        assertThat(result).containsExactly(1.0, 2.0, 3.0, 5.0);
    }

    @Test
    public void when_constructing_map_with_iterator_should_only_iterate_through_once(){

        //setup
        CountingTransform<String, Integer> transform = CountingTransform.track(String::length);
        Queryable<Integer> stringLengths = from("1", "22", "333").select(transform);

        //act
        Map<Integer, Double> lenghtsToDoubleLenghts = new LinqingMap<>(stringLengths, from(1.0, 2.0, 3.0));

        //assert
        transform.shouldHaveBeenInvoked(3);
        assertThat(lenghtsToDoubleLenghts).isEqualTo(new LinqingMap<>(from(1, 2, 3), from(1.0, 2.0, 3.0)));
    }

    @Test
    public void when_constructing_map_with_biqueryable_should_only_apply_transforms_once(){
        //setup                                                               '
        CountingTransform<String, Integer> initialTransform = CountingTransform.track(Integer::parseInt);
        CountingTransform<Integer, Integer> rightTransform = CountingTransform.track(CommonDelegates.identity());
        BiQueryable<String, Integer> biQuery = from("1", "2", "3", "4")
                .pushSelect(initialTransform)
                .selectRight(rightTransform);

        //act
        Map<String, Integer> map = biQuery.toMap();

        //assert
        initialTransform.shouldHaveBeenInvoked(4);
        rightTransform.shouldHaveBeenInvoked(4);
        assertThat(map).isEqualTo(Factories.asMap(Pair("1", 1), Pair("2", 2), Pair("3", 3), Pair("4", 4)));
    }

    @Test
    public void when_calling_constructor_with_non_distinct_keys_should_throw(){

        Exceptions.assertThrows(IllegalArgumentException.class,
            () -> new LinqingMap<String, Double>(from("dupe", "dupe"), from(1.0, 2.0))
        );

    }
}