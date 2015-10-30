package com.empowerops.linqalike.queries;

import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Theories.class)
public class CountSkipQueryFixture extends QueryFixtureBase {

    // Because of the nature of SkipQuery and SkipQuery.ForList,
    // I cant use the Type-under-test stuff here.

    @Theory
    public void when_skipping_first_three_elements_in_seven_element_set_should_get_last_four(
            WritableCollection<String> names
    ){
        //setup
        names.addAll("Joe", "Johnson", "giorgio", "Jack", "Justin", "Giovanni", "Giorgio");

        //act
        Queryable<String> latterNames =names.skip(3);
        List<String> latterNamesList = latterNames.toList();

        //assert
        assertThat(latterNamesList).containsExactly("Jack", "Justin", "Giovanni", "Giorgio");
        assertThat(latterNames.size()).isEqualTo(4);
    }

    @Theory
    public void when_skipping_no_elements_in_empty_set_should_get_empty_set(
            WritableCollection<Integer> numbers
    ){
        //setup
        numbers.clear();

        //act
        Queryable<Integer> skippedNums = numbers.skip(0);
        List<Integer> skippedNumsList = skippedNums.toList();

        //assert
        assertThat(skippedNumsList).isEmpty();
        assertThat(skippedNums.size()).isEqualTo(0);
    }

    @Theory
    public void when_skipping_negative_elements_should_get_exception(
            WritableCollection<Integer> numbers
    ){
        //setup
        numbers.clear();

        //act & assert
        assertThrows(IllegalArgumentException.class, () -> numbers.skip(-4));
    }

    @Theory
    public void when_skipping_more_elements_than_are_in_the_set_should_get_empty_set(
            WritableCollection<String> names
    ){
        //setup
        names.addAll("Joe", "Johnson", "giorgio", "Jack", "Justin", "Giovanni", "Giorgio");

        //act
        Queryable<String> latterNames = names.skip(10);
        List<String> latterNamesList = latterNames.toList();

        //assert
        assertThat(latterNamesList).isEmpty();
        assertThat(latterNames.size()).isEqualTo(0);
    }

    @Theory
    public void when_skipping_as_many_elements_as_are_in_the_collection_should_get_empty_queryable(
            WritableCollection<String> names
    ){
        //setup
        names.addAll("Joe", "Johnson", "giorgio", "Jack", "Justin", "Giovanni", "Giorgio");

        //act
        Queryable<String> latterNames = names.skip(names.count());
        List<String> latterNamesList = latterNames.toList();

        //assert
        assertThat(latterNamesList).isEmpty();
        assertThat(latterNames.size()).isEqualTo(0);
    }

    @Theory
    public void when_adding_elements_after_query_is_made_should_add_elements_lazily(
            WritableCollection<String> names
    ){
        //setup
        names.addAll("Joe", "Johnson", "giorgio", "Jack", "Justin", "Giovanni", "Giorgio");

        //act
        Queryable<String> latterNames = names.skip(names.count() - 1);
        names.addAll("Gregor", "Jay");
        List<String> lateLatterNamesList = latterNames.toList();

        //assert
        assertThat(lateLatterNamesList).containsExactly("Giorgio", "Gregor", "Jay");
        assertThat(latterNames.size()).isEqualTo(3);
    }


}