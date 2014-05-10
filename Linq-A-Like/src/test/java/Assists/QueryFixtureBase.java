package Assists;

import com.EmpowerOperations.LinqALike.LinqingList;

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

        public static LinqingList<NamedValue> forNames(String... values) {
            LinqingList<NamedValue> returnable = new LinqingList<>();
            for(String value : values){
                NamedValue namedValue = new NamedValue(value);
                returnable.add(namedValue);
            }
            return returnable;
        }

        public static CountingTransform<NamedValue, String> GetName() {
            return new CountingTransform<NamedValue, String>() {
                @Override
                protected String getFromImpl(NamedValue cause) {
                    return cause.name;
                }
            };
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

        public static CountingTransform<NumberValue, Integer> GetValue(){
            return new CountingTransform<NumberValue, Integer>() {
                @Override
                public Integer getFromImpl(NumberValue cause) {
                    return cause.number;
                }
            };
        }
    }

    protected static class EquatableValue{
        public String value;

        public EquatableValue(String value) {
            this.value = value;
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

        @Override
        public String toString() {
            return "EquatableValue{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }


}
