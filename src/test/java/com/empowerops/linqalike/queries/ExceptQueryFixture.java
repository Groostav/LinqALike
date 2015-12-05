package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.assists.CountingEqualityComparator;
import com.empowerops.linqalike.assists.CountingTransform;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Geoff on 31/10/13
 */
@RunWith(Theories.class)
public class ExceptQueryFixture extends QueryFixtureBase {

    @Theory
    public void when_excluding_elements_using_the_default_equality_comparer(
            Queryable<Double> originalSet
    ){
        //setup
        originalSet = doAdd(originalSet, 1.0, 3.0, 5.0, 7.0);

        //act
        List<Double> result = originalSet.except(1.0, 5.0).toList();

        //assert
        assertThat(result).containsExactly(3.0, 7.0);
    }

    @Theory
    public void when_excluding_some_elements_including_duplicates_using_a_selector(
            Queryable<NumberValue> originalSet
    ){
        //setup
        originalSet = doAdd(originalSet,
                new NumberValue(1), new NumberValue(1), new NumberValue(2),
                new NumberValue(3), new NumberValue(4));
        CountingTransform<NumberValue, Integer> getValueTransform = CountingTransform.track(x -> x.number);
        LinqingList<NumberValue> exclusionList = Factories.asList(new NumberValue(1), new NumberValue(3));

        //act
        List<NumberValue> result = originalSet.except(exclusionList, getValueTransform).toList();

        //assert
        assertThat(result).containsExactly(originalSet.first(3).last(), originalSet.first(5).last());
        getValueTransform.shouldHaveBeenInvoked(SEVEN_TIMES);
    }

    @Theory
    public void when_excluding_elements_from_empty_set(
            Queryable<NumberValue> originalSet
    ){
        //setup
        originalSet = doClear(originalSet);
        CountingTransform<NumberValue, Integer> getValueTransform = NumberValue.GetValue();

        //act
        Queryable<NumberValue> result = originalSet.except(Factories.asList(new NumberValue(1), new NumberValue(3)), getValueTransform);
        LinqingList<NumberValue> flattenedResults = result.toList();

        //assert
        assertThat(flattenedResults).isEmpty();
    }

    @Theory
    public void when_excluding_empty_set_of_elements_from_valid_set(
            Queryable<String> originalSet
    ){
        //setup
        originalSet = doAdd(originalSet, "A", "B", "C");

        //act
        List<String> result = originalSet.except(Factories.<String>empty()).toList();

        //assert
        assertThat(result).containsExactly("A", "B", "C");
    }

	@Theory
	public void when_excluding_null_call_should_throw_exception(
            Queryable<String> originalSet
    ){
		//setup
        Queryable<String> originalSet2 = doAdd(originalSet, "A", "B", "C");

		//act & assert
		assertThrows(IllegalArgumentException.class, () -> originalSet2.except((Iterable<String>)null));
	}

    @Theory
    public void when_calling_excluding_prior_to_adding_values_to_the_left_list_excluding_query_should_see_newly_added_values(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left = doAdd(left, 1, 2, 3);
        right = doAdd(right, 3, 4);
        int newValue = 0;

        //act
        Queryable<Integer> result = left.except(right);
        left.add(newValue);

        //assert
        assertThat(result.toList()).contains(newValue);
    }

    @Theory
    public void when_calling_except_with_comparable_selector_should_call_selector_appropriately(
            Queryable<NumberValue> primes,
            Queryable<NumberValue> palindromes
    ){
        //setup
        primes = doAdd(primes, new NumberValue(7), new NumberValue(11), new NumberValue(13));
        palindromes = doAdd(palindromes, new NumberValue(11), new NumberValue(1001), new NumberValue(1331));
        CountingTransform<NumberValue, Integer> getNumberValue = CountingTransform.track(x -> x.number);

        //act
        List<NumberValue> result = primes.except(palindromes, getNumberValue).toList();

        //assert
        assertThat(result).containsExactly(primes.first(), primes.last());
        getNumberValue.shouldHaveBeenInvoked(primes.size() + palindromes.size());
    }

    @Theory
    public void when_calling_except_with_equality_comparor_should_strip_correct_member_from_source(
            Queryable<String> boys,
            Queryable<String> girls
    ){
        //setup
        boys = doAdd(boys, "Ed", "Ken", "Jesse");
        girls = doAdd(girls, "Ellen", "Eireen");
        CountingEqualityComparator<String> haveEsInSamePlace = CountingEqualityComparator.track(
                (left, right) -> left.toLowerCase().indexOf("e") == right.toLowerCase().indexOf("e")
        );

        //act
        List<String> result = boys.except(girls, haveEsInSamePlace).toList();

        //assert
        assertThat(result).containsExactly("Ken", "Jesse");
        haveEsInSamePlace.shouldHaveBeenInvoked(1/*Ed to Ellen*/ + 2/*Ken to X*/ + 2/*Jesse to X*/);
    }

    @Theory
    public void when_calling_excluding_prior_to_adding_values_to_the_right_list_except_query_should_see_newly_excluded_values(
            WritableCollection<Integer> left,
            WritableCollection<Integer> right
    ){
        //setup
        left = doAdd(left, 7, 8, 9);
        right = doAdd(right, 5, 6, 7);
        int newlyExcluded = 8;

        //act
        Queryable<Integer> result = left.except(right);
        right.add(newlyExcluded);

        //assert
        assertThat(result).doesNotContain(newlyExcluded);
    }

    @SuppressWarnings("unchecked")
    @Theory
    public void when_excluding_something_that_is_a_duplicate_by_default_equality_but_not_by_a_specified_equality_comparator(
            QueryableList<EquatableValue> brothers
    ){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("Sedin");
        EquatableValue secondDuplicate = new EquatableValue("Sedin");
        brothers = doAdd(brothers, firstDuplicate, secondDuplicate);

        //act
        List<EquatableValue> results = brothers.except(from(firstDuplicate), CommonDelegates.ReferenceEquality).toList();

        //assert
        assertThat(results).containsExactly(secondDuplicate);
    }

    @Theory
    public void when_excluding_something_that_is_a_duplicate_by_default_equality_but_not_by_a_specified_comparable_selector(
            QueryableList<EquatableValue> brothers
    ){
        //setup
        EquatableValue firstDuplicate = new EquatableValue("Sedin");
        EquatableValue secondDuplicate = new EquatableValue("Sedin");
        brothers = doAdd(brothers, firstDuplicate, secondDuplicate);

        //act
        List<EquatableValue> results = brothers.except(from(firstDuplicate), System::identityHashCode).toList();

        //assert
        assertThat(results).containsExactly(secondDuplicate);
    }
}
