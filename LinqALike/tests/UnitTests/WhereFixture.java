package UnitTests;

import LinqALike.LinqingList;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 31/10/13
 */
public class WhereFixture extends QueryFixtureBase {

    @Test
    public void when_calling_where_on_number_values_with_several_values_passing_the_condition() throws Exception {
        //setup
        LinqingList<NumberValue> testableSet = new LinqingList<NumberValue>(NumberValue.class, new Object[]
                {new NumberValue(1), null, new NumberValue(2), new NumberValue(3)});
        CountingCondition<NumberValue> condition = new CountingCondition<NumberValue>() {
            @Override public boolean passesForImpl(NumberValue cause) {
                return cause != null && cause.number >= 2;
            }
        };

        //act
        LinqingList<NumberValue> results = testableSet.where(condition).toList();

        //assert
        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(testableSet.get(2), testableSet.get(3));
        condition.shouldHaveBeenInvoked(FOUR_TIMES);
    }

    @Test
    public void when_calling_whereTypeIs() throws Exception {
        //setup
        LinqingList<Object> testableSet = new LinqingList<Object>(5.0d, new NumberValue(20), null, new NamedValue("hi"), new NumberValue(5){{number = 6;}});
        //the last element is an anonymous subclass, make sure that its class is not EQUAL to (but rather inherits from) NumberValue.class
        Class classOfAnonymousSubclass = testableSet.get(4).getClass();
        assert( ! classOfAnonymousSubclass.equals(NumberValue.class) && NumberValue.class.isAssignableFrom(classOfAnonymousSubclass));

        //act
        LinqingList<NumberValue> results = testableSet.ofType(NumberValue.class).toList();

        //assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).number).isEqualTo(20);
        assertThat(results.get(1).number).isEqualTo(QueryFixtureBase.SIX_TIMES);
    }
}
