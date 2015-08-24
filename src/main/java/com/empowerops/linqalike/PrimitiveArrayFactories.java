package com.empowerops.linqalike;

import com.empowerops.linqalike.delegate.Func1;

/**
 * Created by Geoff on 2014-08-04.
 */
public class PrimitiveArrayFactories{

    public static <TElement> boolean[] asBooleanArray(Iterable<TElement> sourceElements,
                                                      Func1<? super TElement, Boolean> converter) {
        boolean[] result = new boolean[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            boolean unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> char[] asCharArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Character> converter) {

        char[] result = new char[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            char unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> byte[] asByteArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Byte> converter) {

        byte[] result = new byte[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            byte unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }


    public static <TElement> short[] asShortArray(Iterable<TElement> sourceElements,
                                                  Func1<? super TElement, Short> converter){

        short[] result = new short[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            short unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> int[] asIntArray(Iterable<TElement> sourceElements,
                                              Func1<? super TElement, Integer> converter){

        int[] result = new int[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            int unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> long[] asLongArray(Iterable<TElement> sourceElements,
                                                Func1<? super TElement, Long> converter){

        long[] result = new long[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            long unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> float[] asFloatArray(Iterable<TElement> sourceElements,
                                                  Func1<? super TElement, Float> converter){

        float[] result = new float[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            float unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }

    public static <TElement> double[] asDoubleArray(Iterable<TElement> sourceElements,
                                                    Func1<? super TElement, Double> converter){

        double[] result = new double[ImmediateInspections.size(sourceElements)];
        int index = 0;
        for(TElement element : sourceElements){
            double unboxedValue = converter.getFrom(element);
            result[index] = unboxedValue;
            index += 1;
        }
        return result;
    }
}
