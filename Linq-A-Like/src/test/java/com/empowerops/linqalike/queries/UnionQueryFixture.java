package com.empowerops.linqalike.queries;

import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.LinqingList;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class UnionQueryFixture extends QueryFixtureBase {

    @Test
    public void when_calling_union_on_two_disjoint_sets_the_result_should_be_the_simple_sum(){
        //setup
        LinqingList<String> goodWesternTeams = new LinqingList<>("Colorado", "Minnesota", "St. Louis", "Chicago",
                                                                 "Anaheim", "Dallas", "San Jose", "Los Angeles");
        LinqingList<String> badWesternTeams = new LinqingList<>("Phoenix", "Nashville", "Winnipeg", "Vancouver", // T_T
                                                                "Calgary", "Edmonton");

        //act
        LinqingList<String> westernConference = goodWesternTeams.union(badWesternTeams).toList();

        //assert
        assertThat(westernConference).containsExactly(
                "Colorado", "Minnesota", "St. Louis", "Chicago",
                "Anaheim", "Dallas", "San Jose", "Los Angeles",
                "Phoenix", "Nashville", "Winnipeg", "Vancouver",
                "Calgary", "Edmonton");
    }

    @Test
    public void when_calling_union_on_two_sets_where_the_summation_contains_a_duplicate_the_dupliace_should_be_removed(){
        //setup
        LinqingList<NamedValue> left = NamedValue.forNames("one", "two", "three");
        LinqingList<NamedValue> right = NamedValue.forNames("three", "four");

        //act
        LinqingList<NamedValue> result = left.union(right, NamedValue.GetName()).toList();

        //result
        assertThat(result).containsExactly(left.get(0), left.get(1), left.get(2), right.get(1));
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
    public void when_calling_union_on_two_set_identical_sets_the_result_should_be_identical_to_the_arguments(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4);
        LinqingList<Integer> right = new LinqingList<>(4, 3, 2, 1);

        //act
        List<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4);
    }

    @Test
    public void when_calling_union_on_two_sets_both_with_nulls(){
        //setup
        LinqingList<String> left = Factories.asList("one", null, "two", "three");
        LinqingList<String> right = Factories.asList(null, "four", "five");

        //act
        LinqingList<String> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly("one", null, "two", "three", "four", "five");
    }

    @Test
    public void when_calling_union_on_a_subset_result_should_appear_identical() {
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3, 4, 5, 6, 7, 8, 9);
        LinqingList<Integer> right = new LinqingList<>(4, 5, 6);

        //act
        List<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void when_calling_union_on_a_set_already_containing_duplicates_the_duplicates_should_be_removed(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 2, 3);
        LinqingList<Integer> right = new LinqingList<>(1, 4);

        //act
        List<Integer> result = left.union(right).toList();

        //assert
        assertThat(result).containsExactly(1, 2, 3, 4);
    }
}
