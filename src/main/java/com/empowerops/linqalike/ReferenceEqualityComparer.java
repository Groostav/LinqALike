package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;

public class ReferenceEqualityComparer implements EqualityComparer.Untyped{

    @Override
    public boolean equals(Object left, Object right) {
        return left == right;
    }

    @Override
    public int hashCode(Object object) {
        return System.identityHashCode(object);
    }

    @Override
    public String toString() {
        return "Reference Equality: " +
                "equals(left, right) -> left == right, " +
                "hashCode(object) -> System.identityHashCode(object)";
    }
}
