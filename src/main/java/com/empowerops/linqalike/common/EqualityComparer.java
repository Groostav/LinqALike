package com.empowerops.linqalike.common;

import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    boolean equals(TCompared left, TCompared right);

    default public int hashCode(TCompared object){
        //unfortunately for us the only satisfactory way to ensure the equals <=> hashCode contract is satisfied,
        //is to render hashCode completely useless.
        //more on this in a future update, I hope. This is a tough challenge.
        return 0;
    }

    public static interface Untyped extends EqualityComparer<Object>{

        public static <TLowestEquatable> Untyped make(Class<TLowestEquatable> upperBoundType,
                                                      Func2<? super TLowestEquatable, ? super TLowestEquatable, Boolean> comparator,
                                                      Func1<? super TLowestEquatable, Integer> hasher){
            return new Untyped() {
                @Override
                public boolean equals(Object left, Object right) {
                    if ( ! upperBoundType.isInstance(left)) { return false; }
                    if ( ! upperBoundType.isInstance(right)) { return false; }

                    return comparator.getFrom(upperBoundType.cast(left), upperBoundType.cast(right));
                }

                @Override
                public int hashCode(Object object) {
                    if ( ! upperBoundType.isInstance(object)){
                        return 0;
                    }
                    else {
                        return hasher.getFrom(upperBoundType.cast(object));
                    }
                }
            };
        }

        public static Untyped make(Func2<Object, Object, Boolean> comparator, Func1<Object, Integer> hasher){
            return new Untyped(){
                @Override
                public boolean equals(Object left, Object right) {
                    return comparator.getFrom(left, right);
                }

                @Override
                public int hashCode(Object toHash) {
                    return hasher.getFrom(toHash);
                }
            };
        }
    }
}

