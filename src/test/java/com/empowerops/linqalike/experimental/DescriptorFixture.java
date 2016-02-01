package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.delegate.Action3;
import org.junit.Test;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 1/31/2016.
 */
public class DescriptorFixture {

    //see https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html

    @Test
    public void when_asking_for_signature_and_return_type_of_one_arg_method_should_get_appropriate(){
        //setup
        Function<Integer, String> transform = (Serializable & Function<Integer, String>) this::doSomethingWithAndInt;
        SerializedLambda sl = getWriteReplace((Serializable)transform);

        //act
        Class returnType = Descriptor.getReturnType(sl.getImplMethodSignature());
        Class[] paramTypes = Descriptor.getParameterTypes(sl.getImplMethodSignature());
        Class implementingClass = Descriptor.toClass(sl.getImplClass());

        //assert
        assertThat(implementingClass).isEqualTo(DescriptorFixture.class);
        assertThat(returnType).isEqualTo(String.class);
        assertThat(paramTypes).containsExactly(int.class);
        assertThat(paramTypes).doesNotContain(Integer.class);
    }

    @Test
    public void when_asking_for_signature_and_return_type_of_three_arg_method_should_get_appropriate(){
        //setup
        Action3<Integer, Double, Double> transform = (Serializable & Action3<Integer, Double, Double>) this::consumeManyThings;
        SerializedLambda sl = getWriteReplace((Serializable)transform);

        //act
        Class returnType = Descriptor.getReturnType(sl.getImplMethodSignature());
        Class[] paramTypes = Descriptor.getParameterTypes(sl.getImplMethodSignature());
        Class implementingClass = Descriptor.toClass(sl.getImplClass());

        //assert
        assertThat(returnType).isEqualTo(void.class);
        assertThat(paramTypes).containsExactly(int.class, double.class, Double.class);
        assertThat(implementingClass).isEqualTo(DescriptorFixture.class);
    }


    private SerializedLambda getWriteReplace(Serializable serializableLambda){

        Method m;
        try { m = serializableLambda.getClass().getDeclaredMethod("writeReplace"); }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        m.setAccessible(true);
        SerializedLambda serialized;

        try { serialized = (SerializedLambda)m.invoke(serializableLambda);}
        catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }

        return serialized;
    }

    private String doSomethingWithAndInt(int value){
        return String.valueOf(value);
    }

    private void consumeManyThings(int x, double y, Double z){

    }
}

