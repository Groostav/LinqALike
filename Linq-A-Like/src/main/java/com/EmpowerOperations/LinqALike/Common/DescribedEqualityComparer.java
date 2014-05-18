package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 14/04/14.
 */
public class DescribedEqualityComparer<TCompared> implements EqualityComparer<TCompared> {

    public final String description;
    private final EqualityComparer<TCompared> base;

    public DescribedEqualityComparer(String description, EqualityComparer<TCompared> baseComparer){
        this.description = description;
        this.base = baseComparer;
    }

    @Override
    public boolean equals(TCompared left, TCompared right) {
        return base.equals(left, right);
    }

    @Override
    public int hashCode(TCompared object) {
        return base.hashCode(object);
    }

    @Override
    public String toString() {
        return description;
    }
}
