package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.BiQueryable;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.assists.FixtureBase;
import com.empowerops.linqalike.common.Tuple;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.empowerops.linqalike.Factories.asSet;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 5/22/2016.
 */
@RunWith(Theories.class)
public class PushSelectManyQueryFixture extends FixtureBase{

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
        NumberGroup firstGroup = new NumberGroup(1, 2, 3, 4);
        NumberGroup secondGroup = new NumberGroup(5, 6, 7, 8);
        groups = doAdd(groups, firstGroup, secondGroup);

        //act
        BiQueryable<NumberGroup, Integer> numbers = groups.pushSelectMany(x -> x.numbers);

        //assert
        assertThat(numbers).containsExactly(
                new Tuple<>(firstGroup, 1),
                new Tuple<>(firstGroup, 2),
                new Tuple<>(firstGroup, 3),
                new Tuple<>(firstGroup, 4),
                new Tuple<>(secondGroup, 5),
                new Tuple<>(secondGroup, 6),
                new Tuple<>(secondGroup, 7),
                new Tuple<>(secondGroup, 8)
        );
    }

    @Theory
    public void when_selecting_many_on_groups_containing_no_elements_they_should_simply_be_skipped(
            Queryable<NumberGroup> groups
    ){
        //setup
        NumberGroup first = new NumberGroup(1, 2, 3, 4), second = new NumberGroup(), third = new NumberGroup(5,6,7,8);
        groups = doAdd(groups, first, second, third);

        //act
        BiQueryable<NumberGroup, Integer> numbers = groups.pushSelectMany(x -> x.numbers);

        //assert
        assertThat(numbers).containsExactly(
                new Tuple<>(first, 1),
                new Tuple<>(first, 2),
                new Tuple<>(first, 3),
                new Tuple<>(first, 4),
                //note: no second
                new Tuple<>(third, 5),
                new Tuple<>(third, 6),
                new Tuple<>(third, 7),
                new Tuple<>(third, 8)
        );
    }

    @Theory
    public void when_selecting_many_on_only_empty_groups(
            Queryable<NumberGroup> groups
    ){
        groups = doAdd(groups, new NumberGroup(), new NumberGroup());

        //act
        List<?> numbers = groups.pushSelectMany(x -> x.numbers).toList();

        //assert
        assertThat(numbers).isEmpty();
    }
}
