package UnitTests.Queries;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-11.
 */
public class LastElementsFixture extends QueryFixtureBase {

    @Test
    public void when_asking_for_the_last_3_elements_of_a_5_element_list(){
        //setup
        LinqingList<String> alphabet = new LinqingList<>("A", "B", "C", "D", "E");

        //act
        List<String> last3 = alphabet.last(3).toList();

        //assert
        assertThat(last3).containsExactly("C", "D", "E");
    }

    @Test
    public void when_asking_for_the_last_9_elements_in_a_6_element_list(){
        //setup
        LinqingList<Double> geometricSeries = new LinqingList<>(1/2D, 1/4D, 1/8D, 1/16D, 1/32D);

        //act
        List<Double> lastGeometricSeriesMembers = geometricSeries.last(9).toList();

        //assert
        assertThat(lastGeometricSeriesMembers).containsExactly(1/2D, 1/4D, 1/8D, 1/16D, 1/32D);
    }
}
