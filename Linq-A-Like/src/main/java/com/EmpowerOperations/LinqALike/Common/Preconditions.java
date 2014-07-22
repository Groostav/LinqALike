package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Factories;
import com.EmpowerOperations.LinqALike.Linq;
import com.EmpowerOperations.LinqALike.Queryable;

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

    public static <TElement> void contains(Iterable<TElement> sourceElements,
                                           TElement element,
                                           String paramName) {
        if ( ! Factories.from(sourceElements).containsElement(element)) {
            throw new IllegalArgumentException(paramName);
        }
    }

    public static <TElement> void hasExactlyOneMatching(Iterable<? extends TElement> sourceElements,
                                                        Condition<? super TElement> condition,
                                                        String parameterName) {
        Queryable<? extends TElement> where = Factories.from(sourceElements).where(condition).toList();
        if ( ! where.isSingle()) {
            throw new IllegalArgumentException(parameterName);
        }
    }

}
