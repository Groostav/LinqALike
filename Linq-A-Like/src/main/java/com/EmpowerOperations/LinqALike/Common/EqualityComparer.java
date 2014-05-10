package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    boolean equals(TCompared left, TCompared right);


    public static class WithDescription<TCompared> implements EqualityComparer<TCompared>{

        private final EqualityComparer<TCompared> base;
        private final String description;

        public WithDescription(String description, EqualityComparer<TCompared> base){
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
    }


}
