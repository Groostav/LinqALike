package UnitTests;

import LinqALike.LinqingList;
import org.junit.Test;

import static LinqALike.LinqingList.from;
import static UnitTests.QueryFixtureBase.NamedValue;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class UnionFixture {

    @Test
    public void when_calling_union_on_two_sets_with_duplicates() throws Exception {
        //setup
        LinqingList<Integer> left = from(1, 2, 3);
        LinqingList<Integer> right = from(2, 3, 4, 6);

        //act
        LinqingList<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4, 6);
    }

    @Test
    public void when_calling_union_on_two_sets_where_selector_reveals_duplicates(){
        //setup
        LinqingList<NamedValue> left = NamedValue.makeWithEach("one", "two", "three");
        LinqingList<NamedValue> right = NamedValue.makeWithEach("three", "four");

        //act
        LinqingList<NamedValue> result = left.union(right, NamedValue.GetName()).toList();

        //result
        assertThat(result).containsExactly(left.get(0), left.get(1), left.get(2), right.get(1));
    }

    @Test
    public void when_calling_union_on_one_filled_set_and_one_empty_set(){
        //setup
        LinqingList<Integer> left = from();
        LinqingList<Integer> right = from(2, 3, 4, 6);

        //act
        LinqingList<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(2, 3, 4, 6);
    }

    @Test
    public void when_calling_union_on_two_sets_both_with_nulls(){
        //setup
        LinqingList<String> left = from("one", null, "two", "three");
        LinqingList<String> right = from(null, "four", "five");

        //act
        LinqingList<String> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly("one", null, "two", "three", "four", "five");
    }

}
