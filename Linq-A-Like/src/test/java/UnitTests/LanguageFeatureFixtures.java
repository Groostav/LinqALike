package UnitTests;


import com.EmpowerOperations.LinqALike.Delegate.Func1;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import static org.fest.assertions.Assertions.assertThat;

public class LanguageFeatureFixtures {

    interface FirstInterface{ default public String doSomething(){ return "first"; } }
    interface SecondInterface{ default public String doSomething(){ return "second"; } }

    @Test
    public void when_two_interfaces_have_signature_collision_java_resolves_an_impl(){

        class FirstAndSecondImpl implements FirstInterface, SecondInterface{
//        compile-time exception. Ok.

            @Override
            public String doSomething() {
                return FirstInterface.super.doSomething();
            }
        }

        assertThat(new FirstAndSecondImpl().doSomething()).isEqualTo("first");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void when_comparing_a_double_to_a_long(){
        double doubleValue = 25.0;
        long longValue = 25L;

        boolean x = doubleValue == longValue;

        assertThat(x).isTrue();
    }

    @Test
    public void when_comparing_booleans(){
        int result = Boolean.TRUE.compareTo(Boolean.FALSE);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void when_getting_class_of_a_lambda() throws NoSuchMethodException {
        Supplier<Integer> function = () -> 15;

        Class foundType = function.getClass();

        assertThat(foundType.getMethod("get").getReturnType()).isEqualTo(Object.class);
    }

    @Test
    public void when_asking_for_the_return_type_of_a_generic_instance() throws NoSuchMethodException {
        Func1<Integer, Double> doubleArrayMaker = Double::new;
        Class arrayConstructorContainer = doubleArrayMaker.getClass();

        Method arrayConstructor = arrayConstructorContainer.getMethod("getFrom", Object.class);

        //erasure is getting pretty old.
        assertThat(arrayConstructor.getReturnType()).isEqualTo(Object.class);
    }

}
