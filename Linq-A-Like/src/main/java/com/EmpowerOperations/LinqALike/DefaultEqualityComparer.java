package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.EqualityComparer;

/**
* Created by Geoff on 2014-05-11.
*/
public class DefaultEqualityComparer implements EqualityComparer.Untyped{

    @Override
    public int hashCode(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    @Override
    public boolean equals(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }

    @Override
    public String toString() {
        return "Default Equality: " +
                "equals(left, right) -> left == null ? right == null : left.equals(right), " +
                "hashCode(object) -> object == null ? 0 : object.hashCode()";
    }
}

