package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.FixtureBase;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 4/27/2016.
 */
@RunWith(Theories.class)
public class ForEachQueryFixture extends FixtureBase {

    @Theory
    public void when_calling_foreach_should_properly_invoke_with_each_member(Queryable<Integer> source){
        //setup
        source = doAdd(source, 1, 2, 3, 4);
        NumberValue closure = new NumberValue(0);

        //act
        source.inlineForEach(x -> closure.number += x).immediately();

        //assert
        assertThat(closure.number).isEqualTo(1 + 2 + 3 + 4);
    }

    @Theory
    public void when_calling_foreach_on_empty_list_should_noop(Queryable<Integer> source){
        //setup
        source = doClear(source);
        NumberValue closure = new NumberValue(0);

        //act
        source.inlineForEach(x -> closure.number = 1000);

        //assert
        assertThat(closure.number).isEqualTo(0);
    }

    @Theory
    public void when_adding_element_after_generation_of_query_should_properly_appear_in_result(WritableCollection<Integer> source){
        //setup
        source.addAll(1, 2, 3);
        NumberValue closure = new NumberValue(0);

        //act
        Queryable<Integer> result = source.inlineForEach(x -> closure.number += x);
        source.addAll(4, 5, 6);
        result.immediately();

        //assert
        assertThat(closure.number).isEqualTo(1 + 2 + 3 + 4 + 5 + 6);
    }
}
