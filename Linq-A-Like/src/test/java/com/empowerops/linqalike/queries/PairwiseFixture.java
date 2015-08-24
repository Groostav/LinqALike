package com.empowerops.linqalike.queries;

import com.empowerops.assists.CountingFactory;
import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import org.junit.Test;

import java.util.List;

import static com.empowerops.linqalike.Factories.asList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Justin on 7/21/2014.
 */
public class PairwiseFixture extends QueryFixtureBase{

    @Test
    public void when_calling_pairwise_on_some_sensible_data() {
        //Setup
        LinqingList<String> zeldaCharacters = asList(
                "Link",
                "Zelda",
                "Gannon",
                "Impa",
                "Shiek",
                "Big Goron"
        );

        //act
        Queryable<Tuple<String, String>> zeldaPairs = zeldaCharacters.pairwise();

        //assert
        assertThat(zeldaPairs.toList()).containsExactly(new Tuple<>(null, "Link"),
                new Tuple<>("Link", "Zelda"),
                new Tuple<>("Zelda", "Gannon"),
                new Tuple<>("Gannon", "Impa"),
                new Tuple<>("Impa", "Shiek"),
                new Tuple<>("Shiek", "Big Goron"),
                new Tuple<>("Big Goron", null));
    }

    @Test
    public void when_calling_pairwise_on_an_empty_list_should_get_a_single_null_null_pair() {
        //setup
        LinqingList<NamedValue> emptySet = new LinqingList<>();

        //act
        Queryable<Tuple<NamedValue, NamedValue>> pairs = emptySet.pairwise();

        //assert
        assertThat(pairs.toList()).containsExactly(new Tuple<>(null, null));
    }

    @Test
    public void when_calling_pairwise_on_a_singleton_list_should_get_two_pairs_each_with_a_null() {
        //setup
        LinqingList<EquatableValue> emptySet = new LinqingList<>(new EquatableValue("IM SO ALONE"));

        //act
        Queryable<Tuple<EquatableValue, EquatableValue>> pairs = emptySet.pairwise().toList();

        //assert
        assertThat(pairs.toList()).containsExactly(
                new Tuple<>(null, new EquatableValue("IM SO ALONE")),
                new Tuple<>(new EquatableValue("IM SO ALONE"), null)
        );
    }

    @Test
    public void when_calling_pairwise_on_a_singleton_list_of_null_it_should_get_two_empty_pairs(){
        //setup
        LinqingList<EquatableValue> emptySet = new LinqingList<>((EquatableValue)null);

        //act
        Queryable<Tuple<EquatableValue, EquatableValue>> pairs = emptySet.pairwise();

        //assert
        assertThat(pairs.toList()).containsExactly(
                new Tuple<>(null, null),
                new Tuple<>(null, null)
        );
    }

    @Test
    public void when_calling_pairwise_with_a_default_factory_the_factory_should_be_called_twice(){
        //setup
        LinqingList<EquatableValue> cafes = new LinqingList<>(
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

