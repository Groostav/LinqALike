package UnitTests;

import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


/**
 * @author Geoff on 31/10/13
 */
public class ExcludingFixture extends QueryFixtureBase {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
        LinqingList<NumberValue> originalSet = Factories.asList(new NumberValue(1), new NumberValue(1), new NumberValue(2), new NumberValue(3), new NumberValue(4));
        CountingTransform<NumberValue, Integer> getValueTransform = NumberValue.GetValue();

        //act
        Queryable<NumberValue> result = originalSet.except(Factories.asList(new NumberValue(1), new NumberValue(3)), getValueTransform);
        LinqingList<NumberValue> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).containsExactly(originalSet.get(2), originalSet.get(4));
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
	// Reference to except is ambiguous with null, compilation error
	public void when_excluding_null_set_should_exception(){
/*		//setup
		thrown.expect(IllegalArgumentException.class);
		LinqingList<String> originalSet = new LinqingList<>("A", "B", "C");

		//act
		List<String> result = originalSet.except(null).toList();*/
	}
}
