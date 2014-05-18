package UnitTests.Queries;

import Assists.CountingEqualityComparator;
import Assists.CountingTransform;
import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.Common.Ref;
import com.EmpowerOperations.LinqALike.CommonDelegates;
import com.EmpowerOperations.LinqALike.Factories;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static Assists.Exceptions.assertThrows;
import static com.EmpowerOperations.LinqALike.Factories.from;
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
	public void when_excluding_null_call_should_throw_exception(){
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
    public void when_calling_except_with_comparable_selector(){
        //setup
        LinqingList<NumberValue> primes = new LinqingList<>(new NumberValue(7), new NumberValue(11), new NumberValue(13));
        LinqingList<NumberValue> palindromes = new LinqingList<>(new NumberValue(11), new NumberValue(1001), new NumberValue(1331));
        CountingTransform<NumberValue, Integer> getNumberValue = CountingTransform.track(x -> x.number);

        //act
        List<NumberValue> result = primes.except(palindromes, getNumberValue).toList();

        //assert
        assertThat(result).containsExactly(primes.first(), primes.last());
        getNumberValue.shouldHaveBeenInvoked(primes.size() + palindromes.size());
    }

    @Test
    public void when_calling_except_with_equality_comparor(){
        //setup
        LinqingList<String> boys = new LinqingList<>("Ed", "Ken", "Jesse");
        LinqingList<String> girls = new LinqingList<>("Ellen", "Eireen");
        CountingEqualityComparator<String> haveEsInSamePlace = CountingEqualityComparator.track(
                (left, right) -> left.toLowerCase().indexOf("e") == right.toLowerCase().indexOf("e")
        );

        //act
        List<String> result = boys.except(girls, haveEsInSamePlace).toList();

        //assert
        assertThat(result).containsExactly("Ken", "Jesse");
        haveEsInSamePlace.shouldHaveBeenInvoked(1 + 2 + 2);
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

    @Test
    public void when_excluding_something_that_is_a_duplicate_by_default_equality_but_not_by_a_specified_equality_comparator(){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("Sedin");
        EquatableValue secondDuplicate = new EquatableValue("Sedin");
        LinqingList<EquatableValue> brothers = new LinqingList<>(firstDuplicate, secondDuplicate);

        //act
        List<EquatableValue> results = brothers.except(from(firstDuplicate), CommonDelegates.ReferenceEquality).toList();

        //assert
        assertThat(results).containsExactly(secondDuplicate);
    }

    @Test
    public void when_excluding_something_that_is_a_duplicate_by_default_equality_but_not_by_a_specified_comparable_selector(){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("Sedin");
        EquatableValue secondDuplicate = new EquatableValue("Sedin");
        LinqingList<EquatableValue> brothers = new LinqingList<>(firstDuplicate, secondDuplicate);
        Ref<Integer> counter = new Ref<>(0);

        //act
        List<EquatableValue> results = brothers.except(from(firstDuplicate), System::identityHashCode).toList();

        //assert
        assertThat(results).containsExactly(secondDuplicate);
    }
}
