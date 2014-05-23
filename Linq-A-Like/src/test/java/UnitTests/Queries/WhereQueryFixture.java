package UnitTests.Queries;

import Assists.CountingCondition;
import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import org.junit.Test;

import java.util.List;

import static Assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class WhereQueryFixture extends QueryFixtureBase {

    @Test
    public void when_filtering_on_number_list_by_evenness_it_should_return_even_values(){
        //setup
        LinqingList<Integer> numbers = new LinqingList<>(1, 2, 3, 4, 5, 6, 7, 8);
        CountingCondition<Integer> condition = CountingCondition.track(number -> number % 2 == 0);

        //act
        List<Integer> evens = numbers.where(condition).toList();

        //Assert
        assertThat(evens).containsExactly(2, 4, 6, 8);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());

    }

    @Test
    public void when_filtering_on_number_list_by_null_it_should_throw_argumentexception() {
        //setup
        LinqingList<Integer> numbers = new LinqingList<>(1, 2, 3, 4, 5, 6, 7, 8);

        //act
        assertThrows(IllegalArgumentException.class, () -> numbers.where(null));
    }

    @Test
    public void when_filtering_on_number_list_by_non_matching_condition_should_return_empty_list() {
        //setup
        LinqingList<Integer> numbers = new LinqingList<>(1, 2, 3, 4, 5, 6, 7, 8);
        CountingCondition<Integer> condition = CountingCondition.track(number -> number < 0);

        //act
        List<Integer> emptyList = numbers.where(condition).toList();

        //assert
        assertThat(emptyList).isEmpty();
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Test
    public void when_filtering_on_number_list_by_tautology_should_return_all_members_of_list() {
        //setup
        LinqingList<Integer> numbers = new LinqingList<>(1, 2, 3, 4, 5, 6 ,7, 8);
        CountingCondition<Integer> condition = CountingCondition.track(number -> true);

        //act
        List<Integer> completeList = numbers.where(condition).toList();

        //assert
        assertThat(completeList).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Test
    public void when_filtering_an_empty_list_it_should_return_empty_list(){
        //setup
        LinqingList<Integer> numbers = new LinqingList<>();
        CountingCondition<Integer> condition = CountingCondition.track(number -> true);

        //act
        List<Integer> filteredList = numbers.where(condition).toList();

        //assert
        assertThat(filteredList).isEmpty();
        assertThat(condition.getNumberOfInvocations()).isEqualTo(numbers.size());
    }

    @Test
    public void when_filtering_on_a_list_of_desperate_types_it_should_apply_the_condition_to_each(){
        //setup
        LinqingList<Object> desperateList = new LinqingList<>(null, new NamedValue("hi"), "Hi", -1d, 1L, 2, new Integer[]{1, 2, 3, 4, 5}, new NumberValue(5));
        CountingCondition<Object> condition = CountingCondition.track(element -> element instanceof Number);

        //act
        List<Object> typedList = desperateList.where(condition).toList();

        //assert
        assertThat(typedList).containsExactly(-1d, 1L, 2);
        assertThat(condition.getNumberOfInvocations()).isEqualTo(desperateList.size());
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    public void when_filtering_on_a_list_of_country_namedvalues_it_should_return_proper_countries() {
        //setup
        LinqingList<NamedValue> countries = new LinqingList<>(new NamedValue("Uganda"), new NamedValue("Zimbabwe"),
                                                              new NamedValue("Denmark"), new NamedValue("Deutschland"));
        CountingCondition<NamedValue> condition = CountingCondition.track(country -> country.name.startsWith("D"));

        //act
        List<NamedValue> dCountries = countries.where(condition).toList();

        //assert
        assertThat(dCountries).containsExactly(countries.get(2), countries.get(3));
        assertThat(condition.getNumberOfInvocations()).isEqualTo(countries.size());
    }
}
