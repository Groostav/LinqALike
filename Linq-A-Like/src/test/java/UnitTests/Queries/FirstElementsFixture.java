package UnitTests.Queries;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
public class FirstElementsFixture extends QueryFixtureBase {

    @Test
    public void when_asking_for_the_first_three_letters_in_the_alphabet(){
        //setup
        LinqingList<String> alphabet = new LinqingList<>("A", "B", "C", "D", "E");

        //act
        List<String> abcs = alphabet.first(3).toList();

        //assert
        assertThat(abcs).containsExactly("A", "B", "C");
        //assertThat(abcs).isEasyAs(1, 2, 3);
    }

    @Test
    public void when_asking_for_the_first_4_elements_of_a_3_element_list(){
        //setup
        LinqingList<Integer> countDown = new LinqingList<>(3, 2, 1);

        //act
        List<Integer> theFinalCountDown = countDown.first(5).toList();

        //assert
        assertThat(theFinalCountDown).containsExactly(3, 2, 1);
    }
}
