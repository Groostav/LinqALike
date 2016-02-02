package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.common.Tuple;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Geoff on 2/1/2016.
 */
public class LambdaComprehention{

    //TODO need an either
    public static Tuple<Method, RuntimeException> getReferredProperty(Serializable serializableLambda, Class expectedHostClass){

        Method writeReplace;
        try { writeReplace = serializableLambda.getClass().getDeclaredMethod("writeReplace"); }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        writeReplace.setAccessible(true);
        SerializedLambda serialized;

        try { serialized = (SerializedLambda)writeReplace.invoke(serializableLambda);}
        catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }

        if(serialized.getImplMethodName().contains("$")){
            return new Tuple<>(
                    null,
                    new IllegalArgumentException(
                            "serializableLambda must be a method reference, "
                                + "but it was a lambda:" + serialized.getImplMethodName()
                    )
            );
        }

        if(Descriptor.getParameterTypes(serialized.getImplMethodSignature()).length >= 1){
            return new Tuple<>(
                    null,
                    new IllegalArgumentException(
                    "serializableLambda must be a property (getter), "
                            + "but it had arguments:" + serialized.getImplMethodSignature()
                    )
            );
        }

        Class<?> actualHostClass = Descriptor.toClass(serialized.getImplClass());

        if(actualHostClass != expectedHostClass){
            return new Tuple<>(
                    null,
                    new IllegalArgumentException(
                    "expected serialized lambda to be a getter (or other property) on '" + expectedHostClass + "', "
                        + "but it was a property on '" + actualHostClass + "'"
                    )
            );
        }

        Method targetMethod;
        try {targetMethod = actualHostClass.getMethod(serialized.getImplMethodName()); }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        return new Tuple<>(targetMethod, null);
    }

    @FunctionalInterface
    public interface PropertyGetter<THost, TResult> extends Serializable{
        TResult get(THost liftedHost);
    }
}
