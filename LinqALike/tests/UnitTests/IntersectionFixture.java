package UnitTests;

import Assists.CountingTransform;
import Assists.QueryFixtureBase;
import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static Assists.Exceptions.assertThrows;
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
        CountingTransform<NamedValue, String> getName = CountingTransform.track(x -> x.name);

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
        List<Integer> result = left.intersect(right).toList();

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
        List<Integer> result = left.intersect(right).toList();

        //assert
        assertThat(result).isEmpty();
    }

    @Test
    public void when_intersecting_two_sets_should_not_matter_order_by_which_intersect_is_applied() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4, 5);
        LinqingList<Integer> right = new LinqingList<>(4, 5);

        //act
        List<Integer> resultLeft = left.intersect(right).toList();
        List<Integer> resultRight = right.intersect(left).toList();

        //assert
        assertThat(resultLeft.equals(resultRight));
    }

    @Test
    public void when_intersecting_two_non_intersecting_sets_should_return_empty_result() {
        //setup
        LinqingList<String> left = new LinqingList<>("Copenhagen", "Santiago", "Tokyo", "Tashkent", "Kabul");
        LinqingList<String> right = new LinqingList<>("Sydney", "Toronto", "Barcelona");

        //act
        List<String> result = left.intersect(right).toList();

        //assert
        assertThat(result).isEmpty();
    }

    @Test
    public void when_intersecting_two_sets_containing_different_typed_content() {
        //setup
        LinqingList<Object> left  = new LinqingList<Object>("1", 1, 4.3d, 2f, null);
        LinqingList<Object> right = new LinqingList<Object>(4.3d, 2, 1L);

        //act
        List<Object> result = left.intersect(right).toList();

        //assert
        assertThat(result).containsExactly(4.3d);
    }

    @Test
    public void when_intersecting_three_sets_with_overlapping_elements_in_two_should_return_empty_set() {
        //setup
        LinqingList<String> left = new LinqingList<>("one", "two", "three", "four", "five", "six");
        LinqingList<String> right = new LinqingList<>("two", "four", "eight", "ten");
	    LinqingList<String> bottom = new LinqingList<>("one", "three", "five", "seven");

        //act
	    List<String> result = left.intersect(right.intersect(bottom)).toList();

        //assert
		assertThat(result).isEmpty();
    }

	@Test
	public void when_intersecting_three_sets_with_overlapping_elements_in_all_should_return_valid_set() {
		//setup
		LinqingList<String> left = new LinqingList<>("one", "two", "three", "four", "five", "six");
		LinqingList<String> right = new LinqingList<>("two", "four", "eight", "ten");
		LinqingList<String> bottom = new LinqingList<>("one", "two", "three", "five", "seven");

		//act
		List<String> result = left.intersect(right.intersect(bottom)).toList();

		//assert
		assertThat(result).containsExactly("two");
	}

	@Test
	public void when_intersecting_null_set_should_throw_exception() {
		//setup
		LinqingList<String> left = new LinqingList<>("one", "two", "three", "four", "five", "six");

		//act
		assertThrows(IllegalArgumentException.class, () -> left.intersect((Iterable<String>) null));
	}

	@Test
	public void when_intersecting_one_valid_set_one_empty_set_should_return_empty_set() {
		//setup
		LinqingList<String> left = new LinqingList<>("one", "two", "three", "four", "five", "six");
		LinqingList<String> right = new LinqingList<>();

		//act
		List<String> result = left.intersect(right).toList();

		//assert
		assertThat(result).isEmpty();
	}
}
