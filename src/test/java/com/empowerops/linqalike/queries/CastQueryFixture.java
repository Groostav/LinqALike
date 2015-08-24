package com.empowerops.linqalike.queries;

import com.empowerops.assists.QueryFixtureBase;
import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.delegate.Action;
import org.junit.Test;

import java.util.List;

import static com.empowerops.assists.Exceptions.assertDoesNotThrow;
import static com.empowerops.assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-10.
 */
@SuppressWarnings({"Convert2MethodRef", "UnusedDeclaration"})
//method references not used because lambda's are clearer in the case of throw-away return values
//unused declarations because its only in assignment that we have an exception, => its clearer to have unused vars.
public class CastQueryFixture extends QueryFixtureBase{

    @Test
    public void when_performing_a_safe_cast_with_a_inferred_type(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0d, 2.0d, 3.0d, 4.0d);

        //act
        Queryable<Double> castNumbers = numbers.<Double>cast();
        List<Double> castNumbersList = castNumbers.toList();

        //assert
        assertThat(castNumbersList).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
    }

    @Test
    public void when_performing_a_safe_cast_with_a_specific_supplied_runtime_type(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0d, 2.0d, 3.0d, 4.0d);

        //act
        List<Double> castNumbers = numbers.cast(Double.class).toList();

        //assert
        assertThat(castNumbers).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
    }

    @Test
    public void when_using_cast_with_explicit_type_cast_exception_should_be_raised_earlier_than_assignment_but_still_lazily(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0, 2.0, 3.0, 4L);

        //act
        Queryable<Double> polluted = numbers.cast(Double.class);

        //assert
        assertDoesNotThrow(() -> { double first = polluted.first(); });
        assertDoesNotThrow(() -> polluted.first());

        assertThrows(ClassCastException.class, () -> {Object ignored = polluted.last();});
        assertThrows(ClassCastException.class, () -> polluted.last());
        assertThrows(ClassCastException.class, () -> {double ignored = polluted.last();});
        //with the explicit type, we get an exception with or without the assignment
    }

    @Test
    public void when_retrieving_a_value_from_a_polluted_list(){
        //setup
        LinqingList<Number> numbers = new LinqingList<Number>(1.0d, 2.0, 3.0f);

        //act
        Queryable<Double> polluted = numbers.cast();
        //polluted heap :(

        //assert
        assertDoesNotThrow(() -> {double first = polluted.first();});
        assertDoesNotThrow(() -> polluted.first());

        assertDoesNotThrow(() -> {Object ignored = polluted.last();}); //we can even call "last", and not get an exception! yikes!!
        assertThrows(ClassCastException.class, () -> polluted.last()); //interestingly when we dont have an assignment it _does_ throw...
        assertDoesNotThrow(new Action(){
               @Override
               public void run() {
                   polluted.last();  //hold the phone. this looks like a bug in java's lambdas.
               }
           });
        assertThrows(ClassCastException.class, () -> { double ignored = polluted.last(); });
    }

    @Test
    public void when_performing_operations_on_a_polluted_list_as_long_as_those_operations_are_generic_there_is_no_problem(){
        //setup
        LinqingList<Number> numbers = new LinqingList<>(1.0d, 2.0d, 3.0d, 4L);

        //act
        Queryable<Double> polluted = numbers.cast();
        LinqingList<Double> reallyPolluted = polluted.toList();
        //polluted heap

        //assert
        assertDoesNotThrow(() -> { double first = reallyPolluted.first(); });
        assertDoesNotThrow(() -> { double secondLast = reallyPolluted.reversed().skip(1).first(); });
        assertDoesNotThrow(() -> { Object problem = reallyPolluted.reversed().first(); });
        assertThrows(ClassCastException.class, () -> { double problem = reallyPolluted.reversed().first(); });
    }
}
