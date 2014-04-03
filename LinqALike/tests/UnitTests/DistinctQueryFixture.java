package UnitTests;

import junit.framework.TestCase;
import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


/**
 * Created by Edwin on 2014-04-02.
 */
public class DistinctQueryFixture extends QueryFixtureBase {

    @Test
    public void eliminate_integer_duplicates_in_list() {
        //setup
        LinqingList<Integer> list = new LinqingList<>(1,2,2,2,3,4,5,6,1,2,4,5,3);

        //act
        List<Integer> result = list.distinct().toList();

        //assert
        assertThat(result).containsOnly(1,2,3,4,5,6);
    }

    @Test
    public void eliminate_string_duplicates_in_list() {
        //setup
        LinqingList<String> left = new LinqingList<>("Seoul", "Nagasaki",
                                                    "Mumbai", "Mumbai", "Amsterdam", "Shaghai", "Dubai", "Dubai");
        LinqingList<String> right = new LinqingList<>("Nagasaki", "Anchorage", "Rio de Janeiro", "Cairo");

        //act
        Queryable<String> resultLeft = left.distinct();
        Queryable<String> resultRight = right.distinct();


        //assert
        assertThat(resultLeft).doesNotHaveDuplicates();
        assertThat(resultRight).doesNotHaveDuplicates();
    }

    @Test
    public void eliminate_duplicates_in_disparate_objects_list() {
        //setup
        LinqingList<Object> list = new LinqingList<>("Starbucks", 5L, 5, 5.0d, Integer.parseInt("5"));

        //act
        Queryable<Object> result = list.distinct();

        //assert

        //This part is iffy. I don't think 5L and 5 shouldn't be identical
        //containsOnly is distinguishing between 5L and 5
        //  but it complains when I remove any of the 5's from the expected output.
        //Take a look.
        assertThat(result).doesNotHaveDuplicates();
        assertThat(result).containsOnly("Starbucks", 5L, 5, 5.0d);
        assertThat(5.0d).isEqualTo(5);
    }
}
