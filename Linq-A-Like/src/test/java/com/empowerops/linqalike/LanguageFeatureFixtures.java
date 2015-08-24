package com.empowerops.linqalike;


import com.empowerops.common.BootstrappingUtilities;
import com.empowerops.linqalike.delegate.Func1;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import static com.empowerops.assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class LanguageFeatureFixtures {

    interface FirstInterface{ default public String doSomething(){ return "first"; } }
    interface SecondInterface{ default public String doSomething(){ return "second"; } }

    @Test
    public void when_two_interfaces_have_signature_collision_java_resolves_an_impl(){

        class FirstAndSecondImpl implements FirstInterface, SecondInterface{

            //note the compile-time exception if you try to remove this override
            // (which should be possible since its defaulted... twice!)
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

    @Test
    public void when_casting_list_from_Number_to_Double_then_add_a_float_should_unbox_properly(){
        //setup
        ArrayList<Number> numberList = Factories.asList(1.0d, 2.0d, 3.0d);
        numberList.add(4.0f);

        //act
        ArrayList<Double> doubleList = ((ArrayList) numberList);

        //assert
        assertThat(((Object) doubleList.get(3))).isInstanceOf(Float.class);
        assertThrows(ClassCastException.class, () -> { double result = doubleList.get(3); });
        assertThrows(ClassCastException.class, () -> { Double result = doubleList.get(3); });
    }

    @Test
    public void when_using_array_constructor_as_factory(){
        //no setup

        //act
        Func1<Integer, String[][]> matrixFactory = String[][]::new;
        String[][] matrix = matrixFactory.getFrom(3);

        //assert
        assertThat(matrix).hasSize(3);
        assertThat(matrix).containsOnly(new Object[]{null});
    }

    @Test
    public void when_asking_java_for_the_environment_variables(){
        //setup
        assumeTrue(BootstrappingUtilities.isWindows());
        Map<String, String> environmentVariables = System.getenv();

        //act
        String processorArch = environmentVariables.get("PROCESSOR_ARCHITECTURE");

        //assert
        assertThat(processorArch).isNotNull();
    }

}
