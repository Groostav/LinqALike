package UnitTests;

import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class IntersectionFixture extends QueryFixtureBase {
    @Test
    public void when_intersecting_a_set_containing_2_members_from_another_set(){
        //setup
        LinqingList<String> original = Factories.asList("A", "B", "C");

        //act
        Queryable<String> result = original.except(Factories.asList("B", "C"));
        LinqingList<String> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly("A");
    }

    @Test
    public void when_intersecting_a_set_with_specific_comparer(){
        //setup
        LinqingList<NamedValue> left = Factories.asList(NamedValue.makeWithEach("A", "B", "C"));
        LinqingList<NamedValue> right = Factories.asList(NamedValue.makeWithEach("A"));
        CountingTransform<NamedValue, String> getName = NamedValue.GetName();

        //act
        Queryable<NamedValue> result = left.intersect(NamedValue.makeWithEach("B", "C"), getName);
        LinqingList<NamedValue> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).hasSize(2);
        assertThat(flattenedResults.first().name).isEqualTo("B");
        assertThat(flattenedResults.last().name).isEqualTo("C");
    }

    @Test
    public void when_intersecting_two_sets_with_overlap_should_return_overlapping_elements() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1,2,3,4,5);
        LinqingList<Integer> right = new LinqingList<>(3,4);

        //act
        Queryable<Integer> result = left.intersect(right).toList();

        //assert
        assertThat(result).containsOnly(3,4);
        assertThat(result).doesNotHaveDuplicates();
    }

    @Test
    public void when_intersecting_two_empty_sets_should_return_empty_set() {
        //setup
        LinqingList<Integer> left = new LinqingList<>();
        LinqingList<Integer> right = new LinqingList<>();

        //act
        Queryable<Integer> result = left.intersect(right).toList();

        //assert
        assertThat(result).isEmpty();
    }

    @Test
    public void when_intersecting_two_sets_should_not_matter_order_by_which_intersect_is_applied() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4, 5);
        LinqingList<Integer> right = new LinqingList<>(4, 5);

        //act
        Queryable<Integer> resultLeft = left.intersect(right).toList();
        Queryable<Integer> resultRight = right.intersect(left).toList();

        //assert
        assertThat(resultLeft.equals(resultRight));
    }
}
