package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    boolean equals(TCompared left, TCompared right);

    default public int hashCode(TCompared object){
        return 0;
    }

    public static interface Untyped extends EqualityComparer<Object>{}
}
