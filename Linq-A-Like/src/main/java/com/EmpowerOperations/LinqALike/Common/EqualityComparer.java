package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    boolean equals(TCompared left, TCompared right);

    default public int hashCode(TCompared object){
        //unfortunately for us the cheapest and fastest way on the implementation side
        //to ensure the equals <> hashCode contract is satisfied, is to render hashCode completely useless.
        //more on this in a future update, I hope. This is a tough challenge.
        return 0;
    }

    public static interface Untyped extends EqualityComparer<Object>{}
}
