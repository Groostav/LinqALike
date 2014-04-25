package UnitTests.Queries;

import Assists.CountingTransform;
import Assists.QueryFixtureBase;
import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static Assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;


/**
 * @author Geoff on 31/10/13
 */
public class ExceptQueryFixture extends QueryFixtureBase {

    @Test
    public void when_excluding_elements_using_the_default_equality_comparer(){
        //setup
        LinqingList<Double> originalSet = Factories.asList(1.0, 3.0, 5.0, 7.0);

        //act
        Queryable<Double> result = originalSet.except(Factories.asList(1.0, 5.0));
        LinqingList<Double> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly(3.0, 7.0);
    }

    @Test
    public void when_excluding_some_elements_including_duplicates_using_a_selector(){
        //setup
        LinqingList<NumberValue> originalSet = Factories.asList(
                new NumberValue(1), new NumberValue(1), new NumberValue(2),
                new NumberValue(3), new NumberValue(4));
        CountingTransform<NumberValue, Integer> getValueTransform = CountingTransform.track(x -> x.number);
        LinqingList<NumberValue> exclusionList = Factories.asList(new NumberValue(1), new NumberValue(3));

        //act
        List<NumberValue> result = originalSet.except(exclusionList, getValueTransform).toList();

        //assert
        assertThat(result).containsExactly(originalSet.get(2), originalSet.get(4));
        getValueTransform.shouldHaveBeenInvoked(SEVEN_TIMES);
    }

    @Test
    public void when_excluding_elements_from_empty_set(){
        //setup
        LinqingList<NumberValue> originalSet = new LinqingList<>();
        CountingTransform<NumberValue, Integer> getValueTransform = NumberValue.GetValue();

        //act
        Queryable<NumberValue> result = originalSet.except(Factories.asList(new NumberValue(1), new NumberValue(3)), getValueTransform);
        LinqingList<NumberValue> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).isEmpty();
    }

    @Test
    public void when_excluding_empty_set_of_elements_from_valid_set(){
        //setup
        LinqingList<String> originalSet = new LinqingList<>("A", "B", "C");

        //act
        Queryable<String> result = originalSet.except(Factories.<String>empty());
        LinqingList<String> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly("A", "B", "C");
    }

	@Test
	public void when_excluding_null_set_should_exception(){
		//setup
		LinqingList<String> originalSet = new LinqingList<>("A", "B", "C");

		//act & assert
		assertThrows(IllegalArgumentException.class, () -> originalSet.except((Iterable<String>)null).toList());
	}

    @Test
    public void when_calling_excluding_prior_to_adding_values_to_the_left_list_excluding_query_should_see_newly_added_values(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(1, 2, 3);
        LinqingList<Integer> right = new LinqingList<>(3, 4);
        int newValue = 0;

        //act
        Queryable<Integer> result = left.except(right);
        left.add(newValue);

        //assert
        assertThat(result.contains(newValue));
    }

    @Test
    public void when_calling_excluding_prior_to_adding_values_to_the_right_list_except_query_should_see_newly_excluded_values(){
        //setup
        LinqingList<Integer> left = new LinqingList<>(7, 8, 9);
        LinqingList<Integer> right = new LinqingList<>(5, 6, 7);
        int newlyExcluded = 8;

        //act
        Queryable<Integer> result = left.except(right);
        right.add(newlyExcluded);

        //assert
        assertThat(result).excludes(newlyExcluded);
    }
}
