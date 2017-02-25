package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.assists.FixtureBase;
import org.assertj.core.api.Assertions;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.empowerops.linqalike.Factories.asList;
import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(Theories.class)
public class GroupByIndexedQueryFixture extends FixtureBase {
    @Theory
    public void when_running_groupby_with_index_on_simple_data_set_should_group_correctly(Queryable<String> source){
        //setup
        Queryable<String> result = doAdd(source, "A-first", "A-second", "A-third", "B-first", "B-second", "B-third");

        //act
        Queryable<Queryable<String>> groups = result.groupByIndexed((member, index) -> Math.floor(index / 3.0));

        //assert
        assertThat(groups.size()).isEqualTo(2);
        Assertions.assertThat(groups.select(Queryable::toList).toList()).isEqualTo(
                asList(asList("A-first", "A-second", "A-third"), asList("B-first", "B-second", "B-third"))
        );
    }

    @Theory
    public void when_running_groupby_indexed_against_lazily_updated_set_should_properly_contain_new_element(
            WritableCollection<Integer> nums
    ){
        //setup
        nums.addAll(1, 2, 3, 4, 5);
        Queryable<Queryable<Integer>> grouped = nums.groupByIndexed((num, idx) -> {
            int result = (int) Math.floor(idx / 2.0);
            return result;
        });

        //act
        nums.add(6);

        //assert
        assertThat(grouped.size()).isEqualTo(3);
        Assertions.assertThat(grouped.select(Queryable::toList).toList()).isEqualTo(
                asList(
                        asList(1, 2),
                        asList(3, 4),
                        asList(5, 6)
                )
        );


    }

}
