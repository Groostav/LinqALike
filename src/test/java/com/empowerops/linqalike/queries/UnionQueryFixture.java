package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
@RunWith(Theories.class)
public class UnionQueryFixture extends QueryFixtureBase {

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() { return UnionQuery.class; }

    @Theory
    public void when_calling_union_on_two_disjoint_sets_the_result_should_be_the_simple_sum(
            WritableCollection<String> goodTeams,
            WritableCollection<String> badTeams){

        //setup
        goodTeams.addAll("Colorado", "Minnesota", "St. Louis", "Chicago",
                         "Anaheim", "Dallas", "San Jose", "Los Angeles");
        badTeams.addAll("Phoenix", "Nashville", "Winnipeg", "Vancouver", // T_T
                "Calgary", "Edmonton");

        //act
        UnionQuery<String> westernConference = asTypeUnderTest(goodTeams.union(badTeams));
        LinqingList<String> resultList = westernConference.toList();

        //assert
        assertThat(resultList).containsExactly(
                "Colorado", "Minnesota", "St. Louis", "Chicago",
                "Anaheim", "Dallas", "San Jose", "Los Angeles",
                "Phoenix", "Nashville", "Winnipeg", "Vancouver",
                "Calgary", "Edmonton");
        assertThat(westernConference.size()).isEqualTo(14);
    }

    @Theory
    public void when_calling_union_on_two_sets_where_the_summation_contains_a_duplicate_the_dupliace_should_be_removed(
            WritableCollection<NamedValue> left,
            WritableCollection<NamedValue> right
    ){
        //setup
        left.addAll(NamedValue.forNames("one", "two", "three"));
        right.addAll(NamedValue.forNames("three", "four"));

        //act
        UnionQuery<NamedValue> result = asTypeUnderTest(left.union(right, NamedValue.GetName()));
        LinqingList<NamedValue> resultList = result.toList();

        //result
        assertThat(resultList).containsExactly(left.first(), left.second(), left.first(3).last(), right.second());
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_one_filled_set_and_one_empty_set(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left.clear();
        right.addAll(2, 3, 4, 6);

        //act
        UnionQuery<Integer> result = asTypeUnderTest(left.union(right));
        LinqingList<Integer> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly(2, 3, 4, 6);
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_two_set_identical_sets_the_result_should_be_identical_to_the_arguments(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left.addAll(1, 2, 3, 4);
        right.addAll(4, 3, 2, 1);

        //act
        UnionQuery<Integer> result = asTypeUnderTest(left.union(right));
        LinqingList<Integer> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly(1, 2, 3, 4);
        assertThat(result.size()).isEqualTo(4);
    }

    @Theory
    public void when_calling_union_on_two_sets_both_with_nulls(
            WritableCollection<String> left,
            WritableCollection<String> right
    ){
        //setup
        left.addAll("one", null, "two", "three");
        right.addAll(null, "four", "five");

        //act
        Queryable<String> result = left.union(right);
        LinqingList<String> resultList = result.toList();

        //assert
        assertThat(resultList).containsExactly("one", null, "two", "three", "four", "five");
        assertThat(result.size()).isEqualTo(6);
    }

    @Theory
    public void when_calling_union_on_a_subset_result_should_appear_identical(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
        right.addAll(4, 5, 6);

        //act
        Queryable<Integer> result = left.union(right);

        //assert
        assertThat(result.toList()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertThat(result.size()).isEqualTo(9);
    }

    @Theory
    public void when_calling_union_on_a_set_already_containing_duplicates_the_duplicates_should_be_removed(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left.addAll(1, 2, 2, 3);
        right.addAll(1, 4);

        //act
        UnionQuery<Integer> result = asTypeUnderTest(left.union(right));

        //assert
        assertThat(result.toList()).containsExactly(1, 2, 3, 4);
        assertThat(result.size()).isEqualTo(4);
    }
}
