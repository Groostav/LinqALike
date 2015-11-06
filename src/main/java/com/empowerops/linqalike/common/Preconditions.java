package com.empowerops.linqalike.common;

import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Linq;

import static com.empowerops.linqalike.ImmediateInspections.fastSizeIfAvailable;
import static com.empowerops.linqalike.ImmediateInspections.hasFastSize;

/**
 * Created by Geoff on 13/04/2014.
 */
public class Preconditions {

    public static <TElement> void cannotBeEmpty(Iterable<TElement> set, String paramName) {
        if(Linq.isEmpty(set)){
            throw new IllegalArgumentException(paramName);
        }
    }

    public static void notNull(Object parameter, String parameterName) {
        if(parameter == null){
            throw new IllegalArgumentException(parameterName);
        }
    }

    public static <TElement> void contains(Iterable<TElement> sourceElements,
                                           TElement element,
                                           String paramName) {
        if ( ! Factories.from(sourceElements).containsElement(element)) {
            throw new IllegalArgumentException(paramName);
        }
    }

    public static <TLeft, TRight> void fastSameSize(Iterable<TLeft> leftSourceElements,
                                                    Iterable<TRight> rightSourceElements) {

       if (hasFastSize(leftSourceElements) && hasFastSize(rightSourceElements)
               && ! fastSizeIfAvailable(leftSourceElements).equals(fastSizeIfAvailable(rightSourceElements))){

           throw makeNotSameSizeException(leftSourceElements, rightSourceElements);
       }
    }

    public static IllegalArgumentException makeNotSameSizeException(Iterable<?> leftSourceElements,
                                                                    Iterable<?> rightSourceElements) {
        return new IllegalArgumentException(
                "left, right: must be same size, but they were" + "\n" +
                "\tleft (size " + Linq.size(leftSourceElements) + "):" + Formatting.csv(leftSourceElements) + "\n" +
                "\tright (size " + Linq.size(rightSourceElements) + "):" + Formatting.csv(rightSourceElements)
        );
    }

    public static void notNegative(int number, String argName) {
        if(number < 0) { throw new IllegalArgumentException(argName); }
    }
}
