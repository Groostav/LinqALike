package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2016-12-08.
 */
@RunWith(Theories.class)
public class TakeWhileQueryFixture extends FixtureBase {

    @Theory
    public void when_calling_take_while_on_simple_set_should_get_back_appropriately(Queryable<Integer> source){
        //setup
        doAdd(source, 1, 3, 5, 7, 2, 4);

        //act
        Queryable<Integer> result = source.takeWhile(it -> it < 6);

        //assert
        assertThat(result).containsExactly(1, 3, 5);
    }

    @Theory
    public void when_taking_elements_the_result_should_be_lazy(
            WritableCollection<Integer> ints
    ){
        //setup
        ints.addAll(1, 2, 3);

        //act
        TakeWhileQuery<Integer> firstQuery = asTypeUnderTest(ints.takeWhile(it -> it < 5));
        LinqingList<Integer> firstElementsEagerList = firstQuery.toList();
        ints.addAll(4, 5);
        LinqingList<Integer> firstElementsLateList = firstQuery.toList();

        //assert
        assertThat(firstElementsEagerList).containsExactly(1, 2, 3);
        assertThat(firstQuery.size()).isEqualTo(4);
        assertThat(firstElementsLateList).containsExactly(1, 2, 3, 4);
    }

}

