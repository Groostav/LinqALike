package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 2014-05-18.
 */
public class DescribedUntypedEqualityComparer implements EqualityComparer.Untyped{

    public final String description;
    private final Untyped base;

    public DescribedUntypedEqualityComparer(String description, Untyped baseComparer){
        this.description = description;
        this.base = baseComparer;
    }

    @Override
    public boolean equals(Object left, Object right) {
        return base.equals(left, right);
    }

    @Override
    public int hashCode(Object object) {
        return base.hashCode(object);
    }

    @Override
    public String toString() {
        return description;
    }
}
