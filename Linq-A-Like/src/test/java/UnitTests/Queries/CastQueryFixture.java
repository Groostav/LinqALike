package UnitTests.Queries;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;
import org.junit.Test;

import java.util.List;

import static Assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-10.
 */
public class CastQueryFixture extends QueryFixtureBase{

    @Test
    public void when_performing_a_safe_cast_with_a_dynamically_specified_type(){
        //setup
        LinqingList<Number> numbers = new LinqingList<Number>(1.0d, 2.0d, 3.0d, 4.0d);

        //act
        List<Double> castNumbers = numbers.<Double>cast().toList();

        //assert
        assertThat(castNumbers).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
    }

    @Test
    public void when_performing_a_cast_on_a_list_containing_one_bad_member(){
        //setup
        LinqingList<Number> numbers = new LinqingList<Number>(1.0d, 2.0, 3.0f);

        //act
        Queryable<Double> castNumbers = numbers.<Double>cast();
        //polluted heap :(

        //closing act & assert
        assertThrows(ClassCastException.class, castNumbers::toList);
    }
}
