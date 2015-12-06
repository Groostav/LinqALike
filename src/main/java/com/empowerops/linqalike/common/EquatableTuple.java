package com.empowerops.linqalike.common;

import com.empowerops.linqalike.CommonDelegates;

/**
* Created by Geoff on 2015-10-18.
*/
public class EquatableTuple<TLeftMember, TRightMember> extends Tuple<TLeftMember, TRightMember>{

    private final EqualityComparer<? super TLeftMember>  leftEqualityComparator;
    private final EqualityComparer<? super TRightMember> rightEqualityComparator;
    private final Class<? super TLeftMember>             leftEquatingSuperClass;
    private final Class<? super TRightMember>            rightEquatingSuperClass;

    public EquatableTuple(TLeftMember left, TRightMember right){
        this(
                left,
                Object.class,
                CommonDelegates.DefaultEquality,
                right,
                Object.class,
                CommonDelegates.DefaultEquality);
    }

    public EquatableTuple(TLeftMember left, EqualityComparer.Untyped leftEqualityComparator,
                          TRightMember right, EqualityComparer.Untyped rightEqualityComparator){
        this(
                left,
                Object.class,
                leftEqualityComparator,
                right,
                Object.class,
                rightEqualityComparator);
    }

    public EquatableTuple(TLeftMember left, TRightMember right, EqualityComparer.Untyped universalEqualityComparer) {
        this(left, Object.class, universalEqualityComparer, right, Object.class, universalEqualityComparer);
    }

    public EquatableTuple(TLeftMember left,
                          Class<? super TLeftMember> leftEquatingSuperClass,
                          EqualityComparer<? super TLeftMember> leftEqualityComparator,
                          TRightMember right,
                          Class<? super TRightMember> rightEquatingSuperClass,
                          EqualityComparer<? super TRightMember> rightEqualityComparator){

        super(left, right);

        Preconditions.notNull(leftEquatingSuperClass, "leftEquatingSuperClass");
        Preconditions.notNull(leftEqualityComparator, "leftEqualityComparator");
        Preconditions.notNull(rightEquatingSuperClass, "rightEquatingSuperClass");
        Preconditions.notNull(rightEqualityComparator, "rightEqualityComparator");

        this.leftEquatingSuperClass = leftEquatingSuperClass;
        this.leftEqualityComparator = leftEqualityComparator;
        this.rightEquatingSuperClass = rightEquatingSuperClass;
        this.rightEqualityComparator = rightEqualityComparator;

    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple otherTuple = (Tuple) o;

        if ( ! isCompatableType(this.left, leftEquatingSuperClass, otherTuple.left)) return false;
        if ( ! isCompatableType(this.right, rightEquatingSuperClass, otherTuple.right)) return false;

        Tuple<TLeftMember, TRightMember> typedOther = otherTuple;

        return leftEqualityComparator.equals(left, typedOther.left)
                && rightEqualityComparator.equals(right, typedOther.right);
    }
    private boolean isCompatableType(Object thisComponent, Class thisComponentEquatingType, Object otherComponent) {
        if (otherComponent != null && thisComponent != null
                && !thisComponentEquatingType.isInstance(otherComponent)) {
            // type miss-match on the arguments.
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
