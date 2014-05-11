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
public class CastQuery extends QueryFixtureBase{

    @Test
    public void when_performing_a_safe_cast_with_a_dynamically_specified_type(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0d, 2.0d, 3.0d, 4.0d);

        //act
        List<Double> castNumbers = numbers.cast(Double.class).toList();

        //assert
        assertThat(castNumbers).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
    }

    @Test
    public void when_performing_a_cast_on_a_list_containing_one_bad_member(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0d, 2.0, 3.0f);

        //act
        Queryable<Double> castNumbers = numbers.cast(Double.class);
        //so is this heap pollution? Its not if you understand intrinsically Queryable's lazy nature.

        //closing act & assert
        assertThrows(ClassCastException.class, () -> castNumbers.toList());
    }
}
