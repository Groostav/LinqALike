package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.CountingBiCondition;
import com.empowerops.linqalike.assists.CountingCondition;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.CountingBiCondition.track;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
@RunWith(Theories.class)
public class WhereIndexedQueryFixture extends FixtureBase {


    @Theory
    public void when_filtering_on_number_list_by_evenness_it_should_return_even_values(
            Queryable<Integer> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1, 2, 3, 4, 5, 6, 7, 8);
        CountingBiCondition<Integer, Integer> condition = track((number, idx) -> idx % 2 == 1);

        //act
        List<Integer> evens = numbers.whereIndexed(condition).toList();

        //Assert
        assertThat(evens).containsExactly(2, 4, 6, 8);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());

    }

    @Theory
    public void when_filtering_on_number_list_by_null_it_should_throw_argumentexception(
            Queryable<Integer> numbers
    ){
        //setup
        Queryable<Integer> numbers2 = doAdd(numbers, 1, 2, 3, 4, 5, 6, 7, 8);

        //act
        assertThrows(IllegalArgumentException.class, () -> numbers2.whereIndexed(null));
    }

    @Theory
    public void when_filtering_on_number_list_by_non_matching_condition_should_return_empty_list(
            Queryable<Integer> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1, 2, 3, 4, 5, 6, 7, 8);
        CountingBiCondition<Integer, Integer> condition = track((number, index) -> index < 0);

        //act
        List<Integer> emptyList = numbers.whereIndexed(condition).toList();

        //assert
        assertThat(emptyList).isEmpty();
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Theory
    public void when_filtering_on_number_list_by_tautology_should_return_all_members_of_list(
            Queryable<Integer> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1, 2, 3, 4, 5, 6 ,7, 8);
        CountingBiCondition<Integer, Integer> condition = track((number, idx) -> true);

        //act
        List<Integer> completeList = numbers.whereIndexed(condition).toList();

        //assert
        assertThat(completeList).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Theory
    public void when_filtering_an_empty_list_it_should_return_empty_list(
            Queryable<Integer> numbers
    ){
        //setup
        CountingBiCondition<Integer, Integer> condition = track((number, idx) -> true);

        //act
        List<Integer> filteredList = numbers.whereIndexed(condition).toList();

        //assert
        assertThat(filteredList).isEmpty();
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Theory
    public void when_filtering_on_a_list_of_desperate_types_it_should_apply_the_condition_to_each(
            WritableCollection<Object> desperateList
    ){
        //setup
        desperateList = doAdd(desperateList,
                null,
                new NamedValue("hi"),
                "Hi",
                -1d,
                1L,
                2,
                new Integer[]{1, 2, 3, 4, 5},
                new NumberValue(5)
        );
        CountingBiCondition<Object, Integer> condition = track((element, idx) -> element instanceof Number && idx >= 4);

        //act
        List<Object> typedList = desperateList.whereIndexed(condition).toList();

        //assert
        assertThat(typedList).containsExactly(1L, 2);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(desperateList.size());
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Theory
    public void when_filtering_on_a_list_of_country_namedvalues_it_should_return_proper_countries(
            Queryable<NamedValue> countries
    ) {
        //setup
        countries = doAdd(countries, new NamedValue("Uganda"), new NamedValue("Zimbabwe"),
                                     new NamedValue("Denmark"), new NamedValue("Deutschland"));
        CountingBiCondition<NamedValue, Integer> condition
                = track((country, idx) -> country.name.startsWith("D") || idx == 0);

        //act
        List<NamedValue> dCountries = countries.whereIndexed(condition).toList();

        //assert
        assertThat(dCountries).containsExactly(
                countries.first(),
                countries.first(3).last(),
                countries.first(4).last()
        );
        assertThat(condition.getNumberOfInvocations()).isEqualTo(countries.size());
    }
}

