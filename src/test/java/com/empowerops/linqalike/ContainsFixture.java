package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class ContainsFixture extends QueryFixtureBase {

    @Theory
    public void when_asking_if_set_of_strings_contains_another_string_ignoring_case_should_get_true(
            Queryable<String> collection
    ){
        //setup
        collection = doAdd(collection, "true", "yes");

        //act
        boolean result = collection.containsElement("TRUE", String::equalsIgnoreCase);

        //assert
        assertThat(result).describedAs("{'true', 'yes'}.containsElement('TRUE', String::equalsIgnoreCase)").isTrue();
    }
}
