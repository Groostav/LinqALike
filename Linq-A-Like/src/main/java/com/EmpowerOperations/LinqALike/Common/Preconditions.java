package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Linq;

/**
 * Created by Geoff on 13/04/2014.
 */
public class Preconditions {

    public static <TElement> void cannotBeEmpty(Iterable<TElement> set) {
        if(Linq.isEmpty(set)){
            throw new SetIsEmptyException();
        }
    }

    public static void notNull(Object parameter, String parameterName) {
        if(parameter == null){
            throw new IllegalArgumentException(parameterName);
        }
    }

}
