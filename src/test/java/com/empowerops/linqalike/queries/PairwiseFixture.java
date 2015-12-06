package com.empowerops.linqalike.queries;
import com.empowerops.linqalike.BiQueryable;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.assists.CountingFactory;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.common.Tuple;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;

import java.util.List;
import org.junit.runner.RunWith;

import javax.management.Query;

import static com.empowerops.linqalike.Factories.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Justin on 7/21/2014.
 */
@RunWith(Theories.class)
public class PairwiseFixture extends QueryFixtureBase{

    @Theory
    public void when_calling_pairwise_on_some_sensible_data(
            Queryable<String> zeldaCharacters
    ) {
        //Setup
        zeldaCharacters = doAdd(zeldaCharacters,
                "Link",
                "Zelda",
                "Ganon", //Do you even LoZ bro?
                "Impa",
                "Shiek",
                "Big Goron"
        );

        //act
        BiQueryable<String, String> zeldaPairs = zeldaCharacters.pairwise();

        //assert
        assertThat(zeldaPairs.toList()).containsExactly(
                new Tuple<>(null, "Link"),
                new Tuple<>("Link", "Zelda"),
                new Tuple<>("Zelda", "Ganon"),
                new Tuple<>("Ganon", "Impa"),
                new Tuple<>("Impa", "Shiek"),
                new Tuple<>("Shiek", "Big Goron"),
                new Tuple<>("Big Goron", null));
    }

    @Theory
    public void when_calling_pairwise_on_an_empty_list_should_get_a_single_null_null_pair(
            Queryable<NamedValue> emptySet
    ) {
        //setup
        emptySet = doAdd(emptySet);

        //act
        BiQueryable<NamedValue, NamedValue> pairs = emptySet.pairwise();

        //assert
        assertThat(pairs.toList()).containsExactly(new Tuple<>(null, null));
    }

    @Theory
    public void when_calling_pairwise_on_a_singleton_list_should_get_two_pairs_each_with_a_null(
            Queryable<EquatableValue> emptySet
    ) {
        //setup
        emptySet = doAdd(emptySet, new EquatableValue("IM SO ALONE"));

        //act
        BiQueryable<EquatableValue, EquatableValue> pairs = emptySet.pairwise();

        //assert
        assertThat(pairs.toList()).containsExactly(
                new Tuple<>(null, new EquatableValue("IM SO ALONE")),
                new Tuple<>(new EquatableValue("IM SO ALONE"), null)
        );
    }

    @Theory
    public void when_calling_pairwise_on_a_singleton_list_of_null_it_should_get_two_empty_pairs(
            WritableCollection<EquatableValue> singletonSet
    ){
        //setup
        singletonSet = doAdd(singletonSet, (EquatableValue)null);

        //act
        BiQueryable<EquatableValue, EquatableValue> pairs = singletonSet.pairwise();

        //assert
        assertThat(pairs.toList()).containsExactly(
                new Tuple<>(null, null),
                new Tuple<>(null, null)
        );
    }

    @Theory
    public void when_calling_pairwise_with_a_default_factory_the_factory_should_be_called_twice(
            Queryable<EquatableValue> cafes
    ){
        //setup
        cafes = doAdd(cafes,
                new EquatableValue("Starbucks"),
                new EquatableValue("Blenz"),
                new EquatableValue("Woods")
        );
        CountingFactory<EquatableValue> factory = CountingFactory.track(() -> new EquatableValue("Tim Hortans"));

        //act
        List<Tuple<EquatableValue, EquatableValue>> pairs = cafes.pairwise(factory).toList();

        //assert
        factory.shouldHaveBeenInvoked(TWICE);
        assertThat(pairs).containsExactly(
                new Tuple<>(new EquatableValue("Tim Hortans"),  new EquatableValue("Starbucks")),
                new Tuple<>(new EquatableValue("Starbucks"),    new EquatableValue("Blenz")),
                new Tuple<>(new EquatableValue("Blenz"),        new EquatableValue("Woods")),
                new Tuple<>(new EquatableValue("Woods"),        new EquatableValue("Tim Hortans"))
        );
    }
}

