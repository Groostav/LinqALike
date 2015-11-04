package com.empowerops.linqalike;

import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2015-10-27.
 */
@RunWith(Theories.class)
public class ReplaceFixture extends QueryFixtureBase{

    @Theory
    public void when_replacing_first_element_should_correctly_be_replaced(WritableCollection<String> collection){
        //setup
        collection.addAll("x1 + x2", "345", "x3");

        //act
        collection.replace("x1 + x2", "x4 + x5");

        //assert
        if(collection instanceof List) {
            assertThat(collection.toList()).containsExactly("x4 + x5", "345", "x3");
        }
        else {
            assertThat(collection.toList()).containsOnly("x4 + x5", "345", "x3");
        }
    }

}