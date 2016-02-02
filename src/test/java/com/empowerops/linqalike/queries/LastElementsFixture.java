package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.assists.FixtureBase;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
@RunWith(Theories.class)
public class LastElementsFixture extends FixtureBase {

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() { return LastElementsQuery.class; }

    @Theory
    public void when_asking_for_the_last_3_elements_of_a_5_element_list(
            WritableCollection<String> alphabet
    ){
        //setup
        alphabet.addAll("A", "B", "C", "D", "E");

        //act
        LastElementsQuery<String> alphabetEnd = asTypeUnderTest(alphabet.last(3));
        List<String> lastFew = alphabetEnd.toList();

        //assert
        assertThat(lastFew).containsExactly("C", "D", "E");
        assertThat(lastFew.size()).isEqualTo(3);
    }

    @Theory
    public void when_asking_for_the_last_9_elements_in_a_6_element_list(
            WritableCollection<Double> geometricSeries
    ){
        //setup
        geometricSeries.addAll(1/2D, 1/4D, 1/8D, 1/16D, 1/32D);

        //act
        LastElementsQuery<Double> smallGeometry = asTypeUnderTest(geometricSeries.last(9));
        List<Double> geometricsList = smallGeometry.toList();

        //assert
        assertQueryResult(geometricsList).containsSmartly(1 / 2D, 1 / 4D, 1 / 8D, 1 / 16D, 1 / 32D);
        assertThat(smallGeometry.size()).isEqualTo(5);
    }

    @Theory
    public void when_asking_for_the_last_bunch_of_an_empty_set_should_get_the_empty_set_back(
            WritableCollection<String> goodPlayersOnLakers
    ){
        //setup
        goodPlayersOnLakers.clear();

        //act
        LastElementsQuery<String> lastGroupOfGoodPlayers = asTypeUnderTest(goodPlayersOnLakers.last(20));
        List<String> playersList = lastGroupOfGoodPlayers.toList();

        //assert
        assertThat(playersList).isEmpty();
        assertThat(lastGroupOfGoodPlayers.size()).isEqualTo(0);
    }

    @Theory
    public void when_asking_for_the_last_elements_the_result_should_be_lazy(
            WritableCollection<Integer> ints
    ){
        //setup
        ints.addAll(1, 2, 3, 4, 5, 6);

        //act
        LastElementsQuery<Integer> lastQuery = asTypeUnderTest(ints.last(3));
        LinqingList<Integer> lastElementsEagerList = lastQuery.toList();
        ints.addAll(7, 8, 9);
        LinqingList<Integer> lastElementsLateList = lastQuery.toList();

        //assert
        assertThat(lastElementsEagerList).containsExactly(4, 5, 6);
        assertThat(lastQuery.size()).isEqualTo(3);
        assertThat(lastElementsLateList).containsExactly(7, 8, 9);
    }
}

