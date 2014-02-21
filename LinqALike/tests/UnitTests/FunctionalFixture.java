package UnitTests;

import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.fest.util.VisibleForTesting;
import org.junit.Test;

import static Assists.Exceptions.verifyThrows;
import static LinqALike.CommonDelegates.Is;
import static org.fest.assertions.Assertions.assertThat;

public class FunctionalFixture extends QueryFixtureBase{

    @Test
    public void when_calling_select() throws Exception {
        //setup
        LinqingList<NamedValue> testableSet = new LinqingList<NamedValue>(NamedValue.class, new Object[]
                {new NamedValue("One"), null, new NamedValue("Two"), new NamedValue("Three")});

        //act
        LinqingList<String> results = testableSet.select(namedValue -> namedValue == null ? "[null]" : namedValue.name).toList();

        //assert
        assertThat(results).containsExactly("One", "[null]", "Two", "Three");
    }

    @Test
    public void when_calling_single() throws Exception {
        //setup
        LinqingList<String> testableSet = new LinqingList<>(null, "one", "two", "two", "three", "three", "three");
        CountingCondition<String> condition = new CountingCondition<String>() {
            @Override public boolean passesForImpl(String cause) {
                return cause != null && cause.equals("one");
            }
        };

        //act
        String result = testableSet.single(condition);

        //assert
        assertThat(result).isNotNull().isEqualTo("one");
        condition.shouldHaveBeenInvoked(SEVEN_TIMES);
    }

    @Test
    public void when_calling_singleOrDefault_with_existing_value() throws Exception {
        //setup
        CountingCondition<String> condition = new CountingCondition<String>() {
            @Override public boolean passesForImpl(String cause) {
                return cause.equals("one");
            }
        };

        //act
        testSingleOrDefault("one", condition);

        //assert
        condition.shouldHaveBeenInvoked(SIX_TIMES);
    }
    @Test
    public void when_calling_singleOrDefault_with_unavailable_value() throws Exception {
        //setup
        CountingCondition<String> condition = new CountingCondition<String>() {
            @Override public boolean passesForImpl(String cause) {
                return cause.equals("four");
            }
        };

        //act
        verifyThrows(RuntimeException.class, () -> testSingleOrDefault("four", condition));

        //assert
        condition.shouldHaveBeenInvoked(SIX_TIMES);
    }

    @VisibleForTesting //if you make this method private, the verifyException wont be able to intercept this call.
    public void testSingleOrDefault(final String expected, CountingCondition<String> uniqueConstraint){
        //we bit more setup
        LinqingList<String> testableSet = new LinqingList<>("one", "two", "two", "three", "three", "three");

        //actual act
        String result = testableSet.single(uniqueConstraint);

        //assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void when_calling_first_with_existing_value() throws Exception {
        CountingCondition<NamedValue> condition = testFirstExpectingValue("Two");
        condition.shouldHaveBeenInvoked(TWICE);
    }

    @Test
    public void when_calling_first_with_unavailable_value() throws Exception{
        verifyThrows(RuntimeException.class, () -> testFirstExpectingValue("Seven"));
        conditionUsedForFirstTest.shouldHaveBeenInvoked(FIVE_TIMES);
    }

    //because of the nature of the exception flow, if first() is supposed to throw the return value is never given.
    //so we must use a side-channel.
    private CountingCondition<NamedValue> conditionUsedForFirstTest;
    @VisibleForTesting //visible for verifyException, which throws if this method is private.
    public CountingCondition<NamedValue> testFirstExpectingValue(final String expected) {
        //setup
        LinqingList<NamedValue> testableSet = new LinqingList<>(NamedValue.class, new Object[]
                {new NamedValue("One"), new NamedValue("Two"), null, new NamedValue("Two"), new NamedValue("Three")});

        //act
        conditionUsedForFirstTest = new CountingCondition<NamedValue>() {
            public boolean passesForImpl(NamedValue cause) {
                return cause != null && cause.name.equals(expected);
            }
        };
        NamedValue result = testableSet.first(conditionUsedForFirstTest);

        //assert
        assertThat(result.name).isEqualTo(expected);
        return conditionUsedForFirstTest;
    }


    @Test
    public void when_calling_firstOrDefault_with_expected_value() throws Exception {

        //setup
        CountingCondition<NamedValue> condition = new CountingCondition<NamedValue>() {
            public boolean passesForImpl(NamedValue cause) {
                return cause == null ? false : cause.name.equals("Two");
            }
        };

        //act
        NamedValue result = testFirstOrDefaultExpectingValue(condition);

        //assert
        assertThat(result.name).isEqualTo("Two");
        condition.shouldHaveBeenInvoked(TWICE);
    }

    @Test
    public void when_calling_firstOrDefault_with_unavailable_value(){
        //setup
        CountingCondition<NamedValue> condition = new CountingCondition<NamedValue>() {
            @Override
            public boolean passesForImpl(NamedValue cause) {
                return cause == null ? false : cause.name.equals("Six");
            }
        };

        //act
        NamedValue result = testFirstOrDefaultExpectingValue(condition);

        //assert
        assertThat(result).isNull();
        condition.shouldHaveBeenInvoked(FIVE_TIMES);
    }

    private NamedValue testFirstOrDefaultExpectingValue(CountingCondition<NamedValue> condition) {
        //a little more setup
        LinqingList<NamedValue> testableSet = new LinqingList<NamedValue>(NamedValue.class, new Object[]
                {new NamedValue("One"), new NamedValue("Two"), null, new NamedValue("Two"), new NamedValue("Three")});

        //(actual) act
        NamedValue result = testableSet.firstOrDefault(condition);

        return result;
    }

    @Test
    public void when_calling_any_with_passing_condition() throws Exception {
        //setup
        LinqingList<NamedValue> testableSet = new LinqingList<NamedValue>(NamedValue.class, new Object[]
                {new NamedValue("One"), new NamedValue("Two"), new NamedValue("Two"), null, new NamedValue("Three")});

        //act
        CountingCondition<NamedValue> condition = new CountingCondition<NamedValue>() {
            public boolean passesForImpl(NamedValue cause) {
                return cause.name.equals("Two");
            }
        };
        boolean setContainsTwo = testableSet.any(condition);

        //assert
        assertThat(setContainsTwo).isTrue();
        condition.shouldHaveBeenInvoked(TWICE);
    }

    @Test
    public void when_calling_any_with_failing_condition(){
        //setup
        LinqingList<NumberValue> testableSet = new LinqingList<NumberValue>(NumberValue.class, new Object[]
                {new NumberValue(1), new NumberValue(2), new NumberValue(2), null, new NumberValue(3)});
        CountingCondition<NumberValue> condition = new CountingCondition<NumberValue>() {
            public boolean passesForImpl(NumberValue cause) {
                return cause == null ? false : cause.number == 20;
            }
        };

        //act
        boolean setContainsTwenty = testableSet.any(condition);

        //assert
        assertThat(setContainsTwenty).isFalse();
        condition.shouldHaveBeenInvoked(FIVE_TIMES);
    }

    @Test
    public void when_asking_if_two_equivalent_sets_are_equivalent(){
        //setup
        LinqingList<EquatableValue> leftSet = LinqingList.asList(new EquatableValue("one"), new EquatableValue("two"), new EquatableValue("3"));
        LinqingList<EquatableValue> rightSet = LinqingList.asList(new EquatableValue("3"), new EquatableValue("one"), new EquatableValue("two"));

        //act
        boolean areEquivalentSets = leftSet.isSetEquivalentOf(rightSet);

        //assert
        assertThat(areEquivalentSets).isTrue();
    }

    @Test
    public void when_skipping_until_a_condition_queryable_should_get_the_correct_result(){
        //setup
        LinqingList<String> strings = LinqingList.asList("one", "one", "one", "two", "three");

        //act
        Queryable<String> result = strings.skipUntil(Is("two"));

        //assert
        assertThat(result.toList()).containsExactly("two", "three");
    }

    @Test
    public void when_skipping_while_a_condition_queryable_should_get_the_correct_result(){
        //setup
        LinqingList<String> strings = LinqingList.asList("one", "one", "one", "two", "three");

        //act
        Queryable<String> result = strings.skipWhile(Is("one"));

        //assert
        assertThat(result.toList()).containsExactly("two", "three");
    }

    @Test
    public void when_skipping_three_elements(){
        //setup
        LinqingList<String> strings = LinqingList.asList("one", "one", "one", "two", "three");

        //act
        Queryable<String> result = strings.skip(3);

        //assert
        assertThat(result.toList()).containsExactly("two", "three");
    }
}
