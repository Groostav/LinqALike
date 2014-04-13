package UnitTests;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;
import LinqALike.LinqingList;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Geoff on 13/10/13
 */
public class QueryFixtureBase {
    protected static final int NEVER = 0;
    protected static final int ONCE = 1;
    protected static final int TWICE = 2;
    protected static final int THRICE = 3; //couldn't resist.
    protected static final int SIX_TIMES = 6;
    protected static final int FOUR_TIMES = 4;
    protected static final int FIVE_TIMES = 5;
    protected static final int SEVEN_TIMES = 7;

    protected static class NamedValue {
        public String name;

        public NamedValue(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public static LinqingList<NamedValue> makeWithEach(String... values) {
            LinqingList<NamedValue> returnable = new LinqingList<>();
            for(String value : values){
                NamedValue namedValue = new NamedValue(value);
                returnable.add(namedValue);
            }
            return returnable;
        }

        @Override
        public String toString(){
            return "NamedValue:" + name;
        }
    }

    protected static class NumberValue{
        public int number;

        public NumberValue(int number){
            this.number = number;
        }
    }

    protected static class EquatableValue{
        public String value;

        protected EquatableValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "EquatableValue{" +
                    "value='" + value + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EquatableValue)) return false;

            EquatableValue that = (EquatableValue) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    /**
     * the purpose of this class is to assert that side effects have also been satisfied.
     * eg: if you call <code>new LinqingList&lt;Integer&gt;(1, 2, 3).any((elem) -> {elem == 1;});</code>
     * you would expected any to <i>only invoke the condition once.</i>. If it invokes the condition multiple times,
     * then you might cause a side effect error. This is an extension of short-circuiting.
     */
    protected static abstract class CountingCondition<TInspected> extends CountingDelegate implements Condition<TInspected> {

        public static <TInspected> CountingCondition<TInspected> track(Condition<TInspected> actualCondition){
            return new CountingCondition<TInspected>() {
                @Override
                public boolean passesFor(TInspected candidate) {
                    inspectedElements.add(candidate);
                    return actualCondition.passesFor(candidate);
                }
            };
        }
    }


    /**
     * serves the same purpose as {@link CountingCondition}
     *
     * @see CountingCondition
     */
    protected static abstract class CountingTransform<TInspected, TResult> extends CountingDelegate implements Func1<TInspected, TResult> {

        public static <TInspected, TResult> CountingTransform<TInspected, TResult> track(Func1<TInspected, TResult> actualTransform){
            return new CountingTransform<TInspected, TResult>() {
                @Override
                public TResult getFrom(TInspected cause) {
                    inspectedElements.add(cause);
                    return actualTransform.getFrom(cause);
                }
            };
        }
    }

    public static abstract class CountingDelegate{
        protected List<Object> inspectedElements = new ArrayList<>();

        public void shouldHaveBeenInvoked(int numberOfTimes){
            assertThat(inspectedElements.size()).describedAs("the elements for which the delegate " + this + " was invoked.").isEqualTo(numberOfTimes);
        }

        public int getNumberOfInvocations(){
            return inspectedElements.size();
        }
    }
}
