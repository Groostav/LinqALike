package UnitTests;

import Assists.QueryFixtureBase;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static LinqALike.CommonDelegates.identity;
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
        List<Queryable<NumberValue>> groups = values.groupBy((x, y) -> false).toList();

        assertThat(groups).hasSize(6);
    }

    //lazy is updated
        //new group
        //new member of existing group

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
