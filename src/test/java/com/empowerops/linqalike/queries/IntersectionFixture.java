package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
@RunWith(Theories.class)
public class IntersectionFixture extends FixtureBase {

    @Theory
    public void when_intersecting_a_set_containing_2_members_from_another_set(
            Queryable<String> original
    ){
        //setup
        original = doAdd(original, "A", "B", "C");

        //act
        Queryable<String> result = original.intersect("B", "C");
        LinqingList<String> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly("B", "C");
    }

    @Theory
    public void when_intersecting_a_set_with_specific_comparer(
            Queryable<NamedValue> left,
            Queryable<NamedValue> right
    ){
        //setup
        left = doAdd(left, NamedValue.forNames("A", "B", "C"));
        right = doAdd(right, NamedValue.forNames("B", "C"));
        CountingTransform<NamedValue, String> getName = CountingTransform.track(x -> x.name);

        //act
        Queryable<NamedValue> result = left.intersect(right, getName);
        LinqingList<NamedValue> flattenedResults = result.toList();

        //assert
        assertThat(result).hasSize(2);
        assertThat(flattenedResults.first().name).isEqualTo("B");
        assertThat(flattenedResults.last().name).isEqualTo("C");
    }

    @Theory
    public void when_intersecting_two_sets_with_overlap_should_return_overlapping_elements(
            Queryable<Integer> left,
            Queryable<Integer> right
    ) {
        //setup
        left = doAdd(left, 1,2,3,4,5);
        right = doAdd(right, 3,4,6);

        //act
        List<Integer> result = left.intersect(right).toList();

        //assert
        assertThat(result).containsOnly(3,4);
        assertThat(result).doesNotHaveDuplicates();
    }

    @Theory
    public void when_intersecting_two_empty_sets_should_return_empty_set(
            Queryable<Integer> left,
            Queryable<Integer> right
    ) {
        //setup
        left = doClear(left);
        right = doClear(right);

        //act
        List<Integer> result = left.intersect(right).toList();

        //assert
        assertThat(result).isEmpty();
    }

    @Theory
    public void when_intersecting_two_sets_should_not_matter_order_by_which_intersect_is_applied(
            Queryable<Integer> left,
            Queryable<Integer> right
    ) {
        //setup
        left = doAdd(left, 1, 2, 3, 4, 5);
        right = doAdd(right, 4, 5);

        //act
        List<Integer> resultLeft = left.intersect(right).toList();
        List<Integer> resultRight = right.intersect(left).toList();

        //assert
        assertThat(resultLeft.equals(resultRight));
    }

    @Theory
    public void when_intersecting_two_non_intersecting_sets_should_return_empty_result(
            Queryable<String> left,
            Queryable<String> right
    ) {
        //setup
        left = doAdd(left, "Copenhagen", "Santiago", "Tokyo", "Tashkent", "Kabul");
        right = doAdd(right, "Sydney", "Toronto", "Barcelona");

        //act
        List<String> result = left.intersect(right).toList();

        //assert
        assertThat(result).isEmpty();
    }

    @Theory
    public void when_intersecting_two_sets_containing_different_typed_content(
            WritableCollection<Object> left,
            WritableCollection<Object> right
    ) {
        //setup
        left = doAdd(left, "1", 1, 4.3d, 2f, null);
        right = doAdd(right, 4.3d, 2, 1L);

        //act
        List<Object> result = left.intersect(right).toList();

        //assert
        assertThat(result).containsExactly(4.3d);
    }

    @Theory
    public void when_intersecting_three_sets_with_overlapping_elements_in_two_should_return_empty_set(
            Queryable<String> count,
            Queryable<String> evens,
            Queryable<String> odds
    ) {
        //setup
        count = doAdd(count, "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");
        evens = doAdd(evens, "two", "four", "six", "eight", "ten");
	    odds = doAdd(odds, "one", "three", "five", "seven", "nine");

        //act
	    List<String> result = count.intersect(evens).intersect(odds).toList();

        //assert
		assertThat(result).isEmpty();
    }

	@Theory
	public void when_intersecting_three_sets_with_overlapping_elements_in_all_should_return_valid_set(
            Queryable<String> left,
            Queryable<String> right,
            Queryable<String> bottom
    ) {
		//setup
		left = doAdd(left, "one", "two", "three", "four", "five", "six");
		right = doAdd(right, "two", "four", "eight", "ten");
		bottom = doAdd(bottom, "one", "two", "three", "five", "seven");

		//act
		List<String> result = left.intersect(right).intersect(bottom).toList();

		//assert
		assertThat(result).containsExactly("two");
	}

	@Theory
	public void when_intersecting_null_set_should_throw_exception(
            Queryable<String> left
    ) {
		//setup
		Queryable<String> left2 = doAdd(left, "one", "two", "three", "four", "five", "six");

		//act
		assertThrows(IllegalArgumentException.class, () -> left2.intersect((Iterable<String>) null));
	}

	@Theory
	public void when_intersecting_one_valid_set_one_empty_set_should_return_empty_set(
            Queryable<String> left,
            Queryable<String> right
    ) {
		//setup
		left = doAdd(left, "one", "two", "three", "four", "five", "six");
		right = doAdd(right);

		//act
		Queryable<String> result = left.intersect(right);

		//assert
		assertThat(result).isEmpty();
	}
}
