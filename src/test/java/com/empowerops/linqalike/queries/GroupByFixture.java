package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.QueryableList;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.List;

import static com.empowerops.linqalike.CommonDelegates.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Created by Geoff on 14/04/2014.
 */
@RunWith(Theories.class)
public class GroupByFixture extends FixtureBase {

    @Theory
    public void when_grouping_a_set_containing_groups_should_group_correctly(
            Queryable.PreservingInsertionOrder<NamedValue> values
    ){
        //setup
        values = doAdd(values, NamedValue.forNames(
                "Sony", "Panasonic", "Microsoft", "Sony", "Toshiba", "Sony", "Microsoft")
        );

        //act
        List<Queryable<NamedValue>> groups = values.groupBy(x -> x.name).toList();

        //assert
        assertThat(groups).hasSize(4);
        assertNameGroupHas(groups, 0, "Sony", 3);
        assertNameGroupHas(groups, 1, "Panasonic", 1);
        assertNameGroupHas(groups, 2, "Microsoft", 2);
        assertNameGroupHas(groups, 3, "Toshiba", 1);
    }

    @Theory
    public void when_grouping_an_empty_set_should_get_empty_set(
            Queryable<String> values
    ){
        //setup
        values = doClear(values);

        //act
        List<Queryable<String>> groups = values.groupBy(identity()).toList();

        //assert
        assertThat(groups).isEmpty();
    }

    @Theory
    public void when_grouping_a_set_containing_no_groups(
            Queryable<String> values
    ){
        //setup
        values = doAdd(values,
                "The Light", "Always For You", "Shine", "Writings On The Wall", "Red-Eye",
                "See In You", "into The Sea", "Wherever I Go", "Wishful Thinking", "Broken Arrow");

        //act
        List<Queryable<String>> groups = values.groupBy(x -> x).toList();

        //assert
        Iterator<String> expectedValue = values.iterator();
        for(Queryable<String> actualGroup : groups){
            LinqingList<String> actual = actualGroup.toList();
            assertThat(actual).hasSize(1);
            assertThat(actual.single()).isEqualTo(expectedValue.next());
        }
    }

    // set containing duplicates
    // test with group containing a duplicate
    @Theory
    public void when_grouping_a_bag_containing_multiple_ref_equals_entries(
            QueryableList<NumberValue> values
    ){
        //setup
        NumberValue duplicate = new NumberValue(5);
        values = doAdd(values,
                new NumberValue(839), duplicate, new NumberValue(857),
                new NumberValue(859), new NumberValue(863), duplicate);

        //act
        List<Queryable<NumberValue>> groups = values.groupBy(x -> x.number).toList();

        //assert
        assertThat(groups).hasSize(5);
        assertNumbedGroupHas(groups, 0, 839, 1);
        assertNumbedGroupHas(groups, 1, 5, 2);
        assertNumbedGroupHas(groups, 2, 857, 1);
        assertNumbedGroupHas(groups, 3, 859, 1);
        assertNumbedGroupHas(groups, 4, 863, 1);
    }

    @Theory
    public void when_groups_are_found_prior_to_adding_a_new_ungrouped_value_to_the_source_list_query_should_see_newly_added_group(
            WritableCollection<Double> sourceNums
    ){
        //setup
        sourceNums.addAll(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Queryable<Double>> groups = sourceNums.groupBy(x -> x);
        sourceNums.add(newValue);

        //assert
        LinqingList<Queryable<Double>> result = groups.toList();
        assertThat(result).hasSize(4);
        assertThat(result.where(group -> group.size() == 2).single()).containsOnly(2.0);
    }

    @Theory
    public void when_groups_are_found_prior_to_adding_a_wouldbe_group_member_to_the_source_list_query_should_see_new_group_member(
            //collection must support duplicates + be mutable = LinqingList
            LinqingList<Double> sourceNums
    ){
        //setup
        sourceNums.addAll(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.0;

        //act
        Queryable<Queryable<Double>> groups = sourceNums.groupBy(x -> x);
        sourceNums.add(newValue);

        //assert
        List<Queryable<Double>> result = groups.toList();
        assertThat(result.get(1).toList()).containsExactly(2.0, 2.0, newValue);
    }

    @Theory
    public void when_adding_a_member_to_a_group_already_resolved_by_a_group_by_query_group_should_contain_new_element(
            LinqingList<Integer> sourceNums
    ){
        //setup
        sourceNums.addAll(1, 2);
        Queryable<Integer> firstNumGroup = sourceNums.groupBy(x -> x).first();
        assumeTrue(firstNumGroup.containsElement(1) && firstNumGroup.isSingle());

        //act
        sourceNums.add(1);

        //assert
        assertThat(firstNumGroup).containsOnly(1, 1);
    }

    @Theory
    public void when_using_group_by_to_find_duplicates(
            QueryableList<String> names
    ){
        //setup
        names = doAdd(names, "Brian", "Justin", "Jeff", "Vincent", "Jeff", "George");

        //act
        Queryable<String> duplicates = names.groupBy(name -> name).where(group -> group.count() > 1).selectMany(x -> x).distinct();

        //assert
        assertThat(duplicates.toList()).containsExactly("Jeff");
    }

    @Theory
    public void when_looking_for_groups_over_things_not_reference_equal_should_behave_as_expected(
            WritableCollection<String> sourceNums
    ){
        //setup
        Queryable<String> renewingNums = doAdd(sourceNums, "Hello", "groupBy").select(String::new);

        //act
        Queryable<Queryable<String>> groups = renewingNums.groupBy(identity()).where(group -> group.size() >= 2);

        //assert
        assertThat(groups.size()).isEqualTo(0);
        assertThat(groups).isEmpty();
    }

    private void assertNameGroupHas(List<Queryable<NamedValue>> groups, int groupIndex, String expectedName, int expectedSize) {
        LinqingList<NamedValue> group = groups.get(groupIndex).toList();
        assertThat(group).hasSize(expectedSize);
        for(NamedValue namedValue : group){
            assertThat(namedValue.name).isEqualTo(expectedName);
        }
    }

    private void assertNumbedGroupHas(List<Queryable<NumberValue>> groups, int groupIndex, int expectedValue, int expectedSize) {
        LinqingList<NumberValue> group = groups.get(groupIndex).toList();
        assertThat(group).hasSize(expectedSize);
        for(NumberValue numberValue : group){
            assertThat(numberValue.number).isEqualTo(expectedValue);
        }
    }
}
