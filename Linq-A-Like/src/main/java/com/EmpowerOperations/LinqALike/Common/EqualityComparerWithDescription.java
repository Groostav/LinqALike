package com.EmpowerOperations.LinqALike.Common;

/**
* Created by Geoff on 2014-05-11.
*/
public class EqualityComparerWithDescription<TCompared> implements EqualityComparer<TCompared>{

    private final EqualityComparer<TCompared> base;
    private final String description;

    public EqualityComparerWithDescription(String description, EqualityComparer<TCompared> base){
        this.base = base;
        this.description = description;
    }

    @Override
    public boolean equals(TCompared left, TCompared right) {
        return base.equals(left, right);
    }

    @Override
    public String toString() {
        return description;
    }

    public static class Untyped extends EqualityComparerWithDescription<Object> implements EqualityComparer.Untyped{

        public Untyped(String description, EqualityComparer<Object> base) {
            super(description, base);
        }
    }
}
