package UnitTests;

import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import static LinqALike.LinqingList.from;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class IntersectionFixture extends QueryFixtureBase {
    @Test
    public void when_intersecting_a_set_containing_2_members_from_another_set(){
        //setup
        LinqingList<String> original = from("A", "B", "C");

        //act
        Queryable<String> result = original.excluding(from("B", "C"));
        LinqingList<String> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly("A");
    }

    @Test
    public void when_intersecting_a_set_with_specific_comparer(){
        //setup
        LinqingList<NamedValue> left = from(NamedValue.makeWithEach("A", "B", "C"));
        LinqingList<NamedValue> right = from(NamedValue.makeWithEach("A"));
        CountingTransform<NamedValue, String> getName = NamedValue.GetName();

        //act
        Queryable<NamedValue> result = left.intersection(NamedValue.makeWithEach("B", "C"), getName);
        LinqingList<NamedValue> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).hasSize(2);
        assertThat(flattenedResults.first().name).isEqualTo("B");
        assertThat(flattenedResults.last().name).isEqualTo("C");
    }
}
