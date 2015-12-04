package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.assists.QueryFixtureBase;
import com.empowerops.linqalike.delegate.Action;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.assists.Exceptions.assertDoesNotThrow;
import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Geoff on 2014-05-10.
 */
@SuppressWarnings({"Convert2MethodRef", "UnusedDeclaration"})
//method references not used because lambda's are clearer in the case of throw-away return values
//unused declarations because its only in assignment that we have an exception, => its clearer to have unused vars.
@RunWith(Theories.class)
public class CastQueryFixture extends QueryFixtureBase{

    @Override
    protected Class<? extends Queryable> getTypeUnderTest() { return CastQuery.class; }

    @Theory
    public void when_performing_a_safe_cast_with_a_inferred_type(
            Queryable<Number> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1.0d, 2.0d, 3.0d, 4.0d);

        //act
        Queryable<Double> castNumbers = numbers.<Double>cast();
        List<Double> castNumbersList = castNumbers.toList();

        //assert
        assertThat(castNumbersList).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
        assertThat(castNumbers.size()).isEqualTo(4);
    }

    @Theory
    public void when_performing_a_safe_cast_with_a_specific_supplied_runtime_type(
            Queryable<Number> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1.0d, 2.0d, 3.0d, 4.0d);

        //act
        CastQuery<Number, Double> castNumbers = asTypeUnderTest(numbers.cast(Double.class));
        LinqingList<Double> castList = castNumbers.toList();

        //assert
        assertThat(castList).containsExactly(1.0d, 2.0d, 3.0d, 4.0d);
        assertThat(castNumbers.size()).isEqualTo(4);
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "CodeBlock2Expr", "Convert2Lambda", "Anonymous2MethodRef"}) //purpose of test
    @Theory
    public void when_using_cast_with_explicit_type_cast_exception_should_be_raised_earlier_than_assignment_but_still_lazily(
            Queryable<Number> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1.0D, 2.0D, 3.0D, 4L);

        //act
        CastQuery<Number, Double> polluted = asTypeUnderTest(numbers.cast(Double.class));

        //assert
        assertThat(polluted.size()).isEqualTo(4);

        assertDoesNotThrow(() -> { double first = polluted.first(); });
        assertDoesNotThrow(() -> { Queryable<Double> first = polluted.first(3); });
        assertDoesNotThrow(() -> { List<Double> first = polluted.first(3).toList(); });

        assertThrows(ClassCastException.class, () -> polluted.last());
        assertThrows(ClassCastException.class, () -> { polluted.last(); });
        assertThrows(ClassCastException.class, new Action() { @Override  public void run() { polluted.last(); } });
        assertThrows(ClassCastException.class, () -> { Object ignored = polluted.last(); });
        assertThrows(ClassCastException.class, () -> { double ignored = polluted.last(); });
        assertThrows(ClassCastException.class, () -> polluted.toList());
        //with the explicit type, we get an exception with or without the assignment
    }

    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef", "CodeBlock2Expr"})
    @Theory
    public void when_retrieving_a_value_from_a_polluted_list_should_act_strangely(
            Queryable<Number> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1.0d, 2.0, 3.0f);

        //act
        Queryable<Double> polluted = numbers.cast();
        //polluted heap :(

        //assert
        assertThat(polluted.size()).isEqualTo(3);

        assertDoesNotThrow(() -> { double first = polluted.first(); });
        assertDoesNotThrow(() -> { Queryable<Double> first = polluted.first(3); });
        assertDoesNotThrow(() -> { List<Double> first = polluted.first(3).toList(); });
        assertDoesNotThrow(() -> { polluted.last(); });
        assertDoesNotThrow(() -> { Object ignored = polluted.last(); });
        assertDoesNotThrow(new Action() { @Override public void run() { polluted.last(); } });

        assertThrows(ClassCastException.class, () -> polluted.last()); //um... what?
        //somehow the lambda meta factory is doing a strong type check here?

        assertThrows(ClassCastException.class, () -> { double ignored = polluted.last(); });
        assertThrows(ClassCastException.class, () -> { Double ignored = polluted.last(); });
    }

    @Theory
    public void when_performing_operations_on_a_polluted_list_as_long_as_those_operations_are_generic_there_is_no_problem(
            Queryable<Number> numbers
    ){
        //setup
        numbers = doAdd(numbers, 1.0d, 2.0d, 3.0d, 4L);

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
