package com.empowerops.linqalike.queries;

import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
@RunWith(Theories.class)
public class LastElementsFixture extends QueryFixtureBase {

    @Theory
    public void when_asking_for_the_last_3_elements_of_a_5_element_list(WritableCollection<String> alphabet){
        //setup
        alphabet.addAll("A", "B", "C", "D", "E");

        //act
        List<String> lastFew = alphabet.last(3).toList();

        //assert
        assertThat(lastFew).containsExactly("C", "D", "E");
    }

    @Theory
    public void when_asking_for_the_last_9_elements_in_a_6_element_list(WritableCollection<Double> geometricSeries ){
        //setup
        geometricSeries.addAll(1/2D, 1/4D, 1/8D, 1/16D, 1/32D);

        //act
        List<Double> lastGeometricSeriesMembers = geometricSeries.last(9).toList();

        //assert
        assertThat(lastGeometricSeriesMembers).containsExactly(1/2D, 1/4D, 1/8D, 1/16D, 1/32D);
    }

    @Theory
    public void when_asking_for_the_last_bunch_of_an_empty_set_should_get_the_empty_set_back(WritableCollection<String> goodPlayersOnLakers){
        //setup
        goodPlayersOnLakers.clear();

        //act
        Queryable<String> lastGroupOfGoodPlayers = goodPlayersOnLakers.last(20);

        //assert
        assertThat(lastGroupOfGoodPlayers).isEmpty();
    }

    @Theory
    public void when_asking_for_the_last_elements_the_result_should_be_lazy(WritableCollection<Integer> ints){
        //setup
        ints.addAll(1, 2, 3, 4, 5, 6);

        //act
        Queryable<Integer> lastQuery = ints.last(3);
        ints.addAll(7, 8, 9);

        //assert
        assertThat(lastQuery.toList()).containsExactly(7, 8, 9);
    }
}

