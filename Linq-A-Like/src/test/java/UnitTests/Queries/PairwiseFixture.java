package UnitTests.Queries;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Test;
import org.omg.CORBA.NamedValue;

import static com.EmpowerOperations.LinqALike.Factories.asList;
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
}