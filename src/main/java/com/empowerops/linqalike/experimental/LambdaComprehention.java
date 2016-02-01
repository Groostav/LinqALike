package com.empowerops.linqalike.experimental;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Geoff on 2/1/2016.
 */
public class LambdaComprehention{

    public static Method getReferredProperty(Serializable serializableLambda){

        Method writeReplace;
        try { writeReplace = serializableLambda.getClass().getDeclaredMethod("writeReplace"); }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        writeReplace.setAccessible(true);
        SerializedLambda serialized;

        try { serialized = (SerializedLambda)writeReplace.invoke(serializableLambda);}
        catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }

        if(serialized.getImplMethodName().contains("$")){
            throw new IllegalArgumentException(
                    "serializableLambda must be a method reference, "
                        + "but it was a lambda:" + serialized.getImplMethodName()
            );
        }

        if(Descriptor.getParameterTypes(serialized.getImplMethodSignature()).length >= 1){
            throw new IllegalArgumentException(
                    "serializableLambda must be a property (getter), "
                        + "but it had arguments:" + serialized.getImplMethodSignature()
            );
        }

        Method targetMethod;
        try { targetMethod = Descriptor.toClass(serialized.getImplClass()).getMethod(serialized.getImplMethodName()); }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        return targetMethod;
    }
}
