package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import org.junit.*;

import java.util.*;

import static com.empowerops.linqalike.Factories.asSet;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-19.
 */
public class SelectManyQueryFixture extends QueryFixtureBase {

    private class NumberGroup{

        public NumberGroup(){}
        public NumberGroup(Integer... initialvalues){ for(int val : initialvalues){numbers.add(val);}  }

        public final List<Integer> numbers = new ArrayList<Integer>();
    }

    @Test
    public void when_selecting_on_a_group_of_multi_child_elements_select_many_should_simply_flatten(){
        //setup
        LinqingList<NumberGroup> groups = new LinqingList<>(new NumberGroup(1, 2, 3, 4), new NumberGroup(5, 6, 7, 8));

        //act
        List<Integer> numbers = groups.selectMany(x -> x.numbers).toList();

        //assert
        assertThat(numbers).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Test
    public void when_selecting_many_on_groups_containing_no_elements_they_should_simply_be_skipped(){
        //setup
        LinqingList<NumberGroup> groups = new LinqingList<>(new NumberGroup(1, 2, 3, 4), new NumberGroup(), new NumberGroup(5, 6, 7, 8));

        //act
        List<Integer> numbers = groups.selectMany(x -> x.numbers).toList();

        //assert
        assertThat(numbers).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Test
    public void when_selecting_many_on_only_empty_groups(){
        LinqingList<NumberGroup> groups = new LinqingList<>(new NumberGroup(), new NumberGroup());

        //act
        List<Integer> numbers = groups.selectMany(x -> x.numbers).toList();

        //assert
        assertThat(numbers).isEmpty();
    }

    public static class Parent {

        public Parent(Set<Integer> children){
            this.children = children;
        }

        public Set<Integer> children;
    }

    @Test
    public void when_selecting_many_and_selector_returns_null_should_get_nice_exception(){
        //setup
        LinqingList<Parent> groups = new LinqingList<>(new Parent(new HashSet<>()), new Parent(asSet(1, 2, 3)), new Parent(null));

        //act
        assertThrows(IllegalArgumentException.class, () -> groups.selectMany(parent -> parent.children).toList());
    }
}
