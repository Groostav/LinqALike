package com.empowerops.linqalike;

import com.empowerops.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Theories.class)
public class ContainsFixture extends QueryFixtureBase {

    @Theory
    public void when_asking_if_set_of_strings_contains_another_string_ignoring_case_should_get_true(WritableCollection<String> collection){
        //setup
        collection.addAll("true", "yes");

        //act
        boolean result = collection.containsElement("TRUE", String::equalsIgnoreCase);

        //assert
        assertThat(result).describedAs("{'true', 'yes'}.containsElement('TRUE', String::equalsIgnoreCase)").isTrue();
    }
}
