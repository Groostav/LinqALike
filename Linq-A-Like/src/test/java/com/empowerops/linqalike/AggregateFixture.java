package com.empowerops.linqalike;

import com.empowerops.common.documentation.FixMe;
import org.junit.Test;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
* Created by Geoff on 2014-10-13.
*/
public class AggregateFixture {


    @Test
    public void when_concatinating_a_nonempty_list_of_names_the_result_should_be_a_string_join(){
        //setup
        LinqingList<String> names = new LinqingList<String>("Jimmy", "Ken", "Bob", "Alice");

        //act
        String result = names.aggregate((left, right) -> left + " " + right);

        //assert
        assertThat(result).isEqualTo("Jimmy Ken Bob Alice");
    }


    @Test
    public void when_aggregating_an_empty_set_without_seed_should_throw(){
        //setup
        LinqingList<Integer> emptySet = new LinqingList<Integer>();

        //act & assert
        assertThrows(IllegalArgumentException.class, () -> emptySet.aggregate((left, right) -> {throw new IllegalStateException();}));
    }

    @Test
    @FixMe(description = "Regarding the explicit types on the lambda parameters:" +
            "http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8056038", issueNo = 8056038)
    public void when_performing_product_with_aggregate_and_seed(){
        //seutup
        LinqingList<Integer> primes = new LinqingList<>(5, 7, 11, 13, 17);

        //act
        double result = primes.aggregate(1.0, (Double left, Integer right) -> left * right);

        //assert
        assertThat(result).isEqualTo(5 * 7 * 11 * 13 * 17);
    }

    @Test
    @FixMe(description = "Regarding the explicit types on the lambda parameters:" +
            "http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8056038", issueNo = 8056038)
    public void when_aggregating_on_dispirate_type(){
        //setup
        LinqingList<String> zombieHunters = new LinqingList<>("Bill", "Francis", "Louis", "Zoey");

        //act & spoilers
        double livingMembers = zombieHunters.aggregate(0, (Integer aliveCount, String member) -> aliveCount += member.equals("Bill") ? 0 : 1);

        //assert
        assertThat(livingMembers).isEqualTo(3);
    }

    @Test
    @FixMe(description = "Regarding the explicit types on the lambda parameters:" +
            "http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8056038", issueNo = 8056038)
    public void when_aggregating_an_empty_set_with_seed_should_return_seed(){
        //setup
        LinqingList<Double> emptySet = new LinqingList<>();

        //act
        double result = emptySet.aggregate(42.0, (Double left, Double right) -> {throw new IllegalStateException();});

        //assert
        assertThat(result).isEqualTo(42.0);
    }
}
