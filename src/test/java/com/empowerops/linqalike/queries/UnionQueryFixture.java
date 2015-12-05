package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
@RunWith(Theories.class)
public class UnionQueryFixture extends QueryFixtureBase {

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() { return UnionQuery.class; }

    @Theory
    public void when_calling_union_on_two_disjoint_sets_the_result_should_be_the_simple_sum(
            Queryable<String> goodTeams,
            Queryable<String> badTeams
    ){

        //setup
        goodTeams = doAdd(goodTeams, "Colorado", "Minnesota", "St. Louis", "Chicago",
                         "Anaheim", "Dallas", "San Jose", "Los Angeles");
        badTeams = doAdd(badTeams, "Phoenix", "Nashville", "Winnipeg", "Vancouver", // T_T
                        "Calgary", "Edmonton");

        //act
        Queryable<String> westernConference = goodTeams.union(badTeams);
        LinqingList<String> resultList = westernConference.toList();

        //assert
        assertThat(resultList).containsExactly(
                "Colorado", "Minnesota", "St. Louis", "Chicago",
                "Anaheim", "Dallas", "San Jose", "Los Angeles",
                "Phoenix", "Nashville", "Winnipeg", "Vancouver",
                "Calgary", "Edmonton"
        );
        assertThat(westernConference.size()).isEqualTo(14);
    }

    @Theory
    public void when_calling_union_on_two_sets_where_the_summation_contains_a_duplicate_the_dupliace_should_be_removed(
            Queryable<NamedValue> left,
            Queryable<NamedValue> right
    ){
        //setup
        left = doAdd(left, NamedValue.forNames("one", "two", "three"));
        right = doAdd(right, NamedValue.forNames("three", "four"));

        //act
        Queryable<NamedValue> result = left.union(right, NamedValue.GetName());
        LinqingList<NamedValue> resultList = result.toList();

        //result
        assertThat(resultList).containsExactly(left.first(), left.second(), left.first(3).last(), right.second());
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_one_filled_set_and_one_empty_set(
            Queryable<Integer> left,
            Queryable<Integer> right
    ){
        //setup
        right = doAdd(right, 2, 3, 4, 6);

        //act
        Queryable<Integer> result = left.union(right);
        LinqingList<Integer> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly(2, 3, 4, 6);
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_two_set_identical_sets_the_result_should_be_identical_to_the_arguments(
            Queryable<Integer> left,
            Queryable<Integer> right
    ){
        //setup
        left = doAdd(left, 1, 2, 3, 4);
        right = doAdd(right, 4, 3, 2, 1);

        //act
        Queryable<Integer> result = left.union(right);
        LinqingList<Integer> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly(1, 2, 3, 4);
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_two_sets_both_with_nulls(
            WritableCollection<String> left,
            WritableCollection<String> right
            //note: only testing writable collections because null is unsupported in pcollections.
    ){
        //setup
        left = doAdd(left, "one", null, "two", "three");
        right = doAdd(right, null, "four", "five");

        //act
        Queryable<String> result = left.union(right);
        LinqingList<String> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly("one", null, "two", "three", "four", "five");
        assertThat(result.size()).isEqualTo(6);
    }

    @Theory
    public void when_calling_union_on_a_subset_result_should_appear_identical(
            Queryable<Integer> left,
            Queryable<Integer> right
    ){
        //setup
        left = doAdd(left, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        right = doAdd(right, 4, 5, 6);

        //act
        Queryable<Integer> result = left.union(right);

        //assert
        assertThat(result.toList()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertThat(result.size()).isEqualTo(9);
    }

    @Theory
    public void when_calling_union_on_a_set_already_containing_duplicates_the_duplicates_should_be_removed(
            Queryable<Integer> left,
            Queryable<Integer> right
    ){
        //setup
        left = doAdd(left, 1, 2, 2, 3);
        right = doAdd(right, 1, 4);

        //act
        Queryable<Integer> result = left.union(right);

        //assert
        assertThat(result.toList()).containsExactly(1, 2, 3, 4);
        assertThat(result.size()).isEqualTo(4);
    }
}
