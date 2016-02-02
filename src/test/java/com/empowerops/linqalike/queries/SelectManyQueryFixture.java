package com.empowerops.linqalike.queries;
import com.empowerops.linqalike.*;
import com.empowerops.linqalike.assists.FixtureBase;

import java.util.*;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.experimental.theories.Theories;


import static com.empowerops.linqalike.Factories.asSet;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-19.
 */
@RunWith(Theories.class)
public class SelectManyQueryFixture extends FixtureBase {

    private class NumberGroup{

        public NumberGroup(){}
        public NumberGroup(Integer... initialvalues){ for(int val : initialvalues){numbers.add(val);}  }

        public final List<Integer> numbers = new ArrayList<Integer>();
    }

    @Theory
    public void when_selecting_on_a_group_of_multi_child_elements_select_many_should_simply_flatten(
            Queryable<NumberGroup> groups
    ){
        //setup
        groups = doAdd(groups, new NumberGroup(1, 2, 3, 4), new NumberGroup(5, 6, 7, 8));

        //act
        List<Integer> numbers = groups.selectMany(x -> x.numbers).toList();

        //assert
        assertQueryResult(numbers).containsSmartly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Theory
    public void when_selecting_many_on_groups_containing_no_elements_they_should_simply_be_skipped(
            Queryable<NumberGroup> groups
    ){
        //setup
        groups = doAdd(groups, new NumberGroup(1, 2, 3, 4), new NumberGroup(), new NumberGroup(5, 6, 7, 8));

        //act
        List<Integer> numbers = groups.selectMany(x -> x.numbers).toList();

        //assert
        assertQueryResult(numbers).containsSmartly(1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Theory
    public void when_selecting_many_on_only_empty_groups(
            Queryable<NumberGroup> groups
    ){
        groups = doAdd(groups, new NumberGroup(), new NumberGroup());

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

    @Theory
    public void when_selecting_many_and_selector_returns_null_should_get_nice_exception(
            Queryable<Parent> groups
    ){
        //setup
        Queryable<Parent> groups2 = doAdd(groups, new Parent(new HashSet<>()), new Parent(asSet(1, 2, 3)), new Parent(null));

        //act
        assertThrows(IllegalArgumentException.class, () -> groups2.selectMany(parent -> parent.children).toList());
    }
}
