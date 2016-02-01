package com.empowerops.linqalike.experimental;

import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Method;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Geoff on 2/1/2016.
 */
public class LambdaComprehentionFixture {


    @Test
    public void when_asking_for_method_associated_with_local_method_ref_should_get_as_expected() throws NoSuchMethodException {
        //setup
        Runnable runnable = (Runnable & Serializable) this::localRun;

        //act
        Method target = LambdaComprehention.getReferredProperty((Serializable) runnable);

        //assert
        assertThat(target.getName()).isEqualTo("localRun");
        assertThat(target).isEqualTo(getClass().getMethod("localRun"));
    }

    public void localRun(){}

    @Test
    @SuppressWarnings("Convert2MethodRef") //purpose of the test
    public void when_asking_for_method_associated_with_local_lambda_should_throw(){
        //setup
        Runnable runnable = (Runnable & Serializable) () -> localRun();

        //act
        assertThrows(IllegalArgumentException.class, () -> LambdaComprehention.getReferredProperty((Serializable) runnable));
    }

    public static class NestedClass{

        private String simple;

        public String getSimpleString(){
            return simple;
        }
    }

}