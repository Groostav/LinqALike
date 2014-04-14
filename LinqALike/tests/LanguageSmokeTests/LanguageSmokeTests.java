package LanguageSmokeTests;


import org.junit.Test;

import java.util.function.Supplier;

import static org.fest.assertions.Assertions.assertThat;

public class LanguageSmokeTests {

    interface FirstInterface{
        default public String DoSomething(){
            return "first";
        }
    }
    interface SecondInterface{
        default public String DoSomething(){
            return "second";
        }
    }

    class FirstAndSecondImpl implements FirstInterface, SecondInterface{
//        compile-time exception. Ok.

        @Override
        public String DoSomething() {
            return FirstInterface.super.DoSomething();
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void when_comparing_a_double_to_a_long(){
        double doubleValue = 25.0;
        long longValue = 25L;

        boolean x = doubleValue != longValue;

        assertThat(x).isTrue();
    }

    @Test
    public void when_comparing_doubles(){
        int result = Boolean.TRUE.compareTo(Boolean.FALSE);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void when_getting_class_of_a_lambda(){
        Supplier<Integer> function = () -> 15;

        Class foundType = function.getClass();
    }

}
