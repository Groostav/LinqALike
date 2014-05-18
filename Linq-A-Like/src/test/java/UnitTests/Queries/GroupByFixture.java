package UnitTests.Queries;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static com.EmpowerOperations.LinqALike.CommonDelegates.identity;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 14/04/2014.
 */
public class GroupByFixture extends QueryFixtureBase {

    @Test
    public void when_grouping_a_set_containing_groups(){
        //setup
        LinqingList<NamedValue> values = new LinqingList<>(NamedValue.forNames(
                "Sony", "Panasonic", "Microsoft", "Sony", "Toshiba", "Sony", "Microsoft"));

        //act
        List<Queryable<NamedValue>> groups = values.groupBy(x -> x.name).toList();

        //assert
        assertThat(groups).hasSize(4);
        assertNameGroupHas(groups, 0, "Sony", 3);
        assertNameGroupHas(groups, 1, "Panasonic", 1);
        assertNameGroupHas(groups, 2, "Microsoft", 2);
        assertNameGroupHas(groups, 3, "Toshiba", 1);
    }

    @Test
    public void when_grouping_an_empty_set(){
        //setup
        LinqingList<String> values = new LinqingList<>();

        //act
        List<Queryable<String>> groups = values.groupBy(identity()).toList();

        //assert
        assertThat(groups).isEmpty();
    }

    @Test
    public void when_grouping_a_set_containing_no_groups(){
        //setup
        LinqingList<String> values = new LinqingList<>(
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
    @Test
    public void when_grouping_a_bag_containing_multiple_ref_equals_entries(){
        //setup
        NumberValue duplicate = new NumberValue(5);
        LinqingList<NumberValue> values = new LinqingList<>(
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

    @Test
    public void when_grouping_a_bag_containing_ref_equals_entries_that_are_in_different_groups(){
        //setup
        NumberValue duplicate = new NumberValue(1);
        LinqingList<NumberValue> values = new LinqingList<>(
                new NumberValue(877), duplicate, new NumberValue(881),
                duplicate, new NumberValue(883), new NumberValue(887));

        //act
        Queryable<Queryable<NumberValue>> groups = values.groupBy((x, y) -> false);
        Object flattened = fetch(groups);

        assertThat(groups).hasSize(6);
    }

    @Test
    public void when_groups_are_found_prior_to_adding_a_new_ungrouped_value_to_the_source_list_query_should_see_newly_added_group(){
        //setup
        LinqingList<Double> sourceList = new LinqingList<>(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.5;

        //act
        Queryable<Queryable<Double>> groups = sourceList.groupBy(x -> x);
        Object beforeAddition = fetch(groups);
        sourceList.add(newValue);
        Object afterAddition = fetch(groups);

        //assert
        LinqingList<Queryable<Double>> result = groups.toList();
        assertThat(result).hasSize(4);
        assertThat(result.get(3)).contains(newValue);
    }

    @Test
    public void when_groups_are_found_prior_to_adding_a_wouldbe_group_member_to_the_source_list_query_should_see_new_group_member(){
        //setup
        LinqingList<Double> sourceList = new LinqingList<>(1.0, 2.0, 2.0, 3.0);
        double newValue = 2.0;

        //act
        Queryable<Queryable<Double>> groups = sourceList.groupBy(x -> x);
        sourceList.add(newValue);

        //assert
        List<Queryable<Double>> result = groups.toList();
        assertThat(result.get(1).toList()).containsExactly(2.0, 2.0, newValue);
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

    private <TElement> LinqingList<LinqingList<TElement>> fetch(Queryable<Queryable<TElement>> queries){
        return queries.select(Queryable::toList).toList();
    }
}
