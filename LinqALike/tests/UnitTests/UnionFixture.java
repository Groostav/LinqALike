package UnitTests;

import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static UnitTests.QueryFixtureBase.NamedValue;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class UnionFixture {

    @Test
    public void when_calling_union_on_two_sets_with_duplicates() throws Exception {
        //setup
        LinqingList<Integer> left = Factories.asList(1, 2, 3);
        LinqingList<Integer> right = Factories.asList(2, 3, 4, 6);

        //act
        LinqingList<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 2, 3, 4, 6);
    }

    @Test
    public void when_calling_union_on_two_sets_where_host_is_list_it_does_not_remove_duplicates(){
        //setup
        LinqingList<NamedValue> left = NamedValue.makeWithEach("one", "two", "three");
        LinqingList<NamedValue> right = NamedValue.makeWithEach("three", "four");

        //act
        LinqingList<NamedValue> result = left.union(right, NamedValue.GetName()).toList();

        //result
        assertThat(result).containsExactly(left.get(0), left.get(1), left.get(2), right.get(0), right.get(1));
    }

    @Test
    public void when_calling_union_on_one_filled_set_and_one_empty_set(){
        //setup
        LinqingList<Integer> left = Factories.asList();
        LinqingList<Integer> right = Factories.asList(2, 3, 4, 6);

        //act
        LinqingList<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(2, 3, 4, 6);
    }

    @Test
    public void when_calling_union_on_two_sets_both_with_nulls(){
        //setup
        LinqingList<String> left = Factories.asList("one", null, "two", "three");
        LinqingList<String> right = Factories.asList(null, "four", "five");

        //act
        LinqingList<String> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly("one", null, "two", "three", null, "four", "five");
    }

    @Test
    public void when_calling_union_on_two_valid_sets_should_return_valid_output() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4, 5, 6, 7, 8, 9);
        LinqingList<Integer> right = new LinqingList<>(4, 5, 6);

        //act
        List<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 4, 5, 6);
    }

    @Test
    public void when_calling_union_on_two_valid_sets_with_duplicate_elements_should_return_unique() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4, 5);
        LinqingList<Integer> right = new LinqingList<>(1, 2, 3);

        //act
        Queryable<Integer> result = left.union(right).distinct();

        //assert
        assertThat(result).containsOnly(1,2,3,4,5);
    }
}
