package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.CommonDelegates;

import java.util.Map;

import static com.EmpowerOperations.LinqALike.CommonDelegates.nullSafeToString;

public class Tuple<TLeftMember, TRightMember> implements Map.Entry<TLeftMember, TRightMember> {

    private final EqualityComparer<? super TLeftMember> leftEqualityComparator;
    private final EqualityComparer<? super TRightMember> rightEqualityComparator;

    private final Class<? super TLeftMember> leftEquatingSuperClass;
    private final Class<? super TRightMember> rightEquatingSuperClass;

    public final TLeftMember left;
    public final TRightMember right;

    public Tuple(TLeftMember left, TRightMember right){
        this(
                left,
                Object.class,
                CommonDelegates.DefaultEquality,
                right,
                Object.class,
                CommonDelegates.DefaultEquality);
    }

    public Tuple(TLeftMember left, EqualityComparer<TLeftMember> leftEqualityComparator,
                 TRightMember right, EqualityComparer<TRightMember> rightEqualityComparator){

        Preconditions.notNull(left, "left");
        Preconditions.notNull(right, "right");

        this.left = left;
        this.leftEquatingSuperClass = (Class) left.getClass();
        this.leftEqualityComparator = leftEqualityComparator;
        this.right = right;
        this.rightEquatingSuperClass = (Class) right.getClass();
        this.rightEqualityComparator = rightEqualityComparator;

    }

    public Tuple(TLeftMember left, EqualityComparer.Untyped leftEqualityComparator,
                 TRightMember right, EqualityComparer.Untyped rightEqualityComparator){
        this(
                left,
                Object.class,
                leftEqualityComparator,
                right,
                Object.class,
                rightEqualityComparator);
    }

    public Tuple(TLeftMember left, TRightMember right, EqualityComparer.Untyped universalEqualityComparer) {
        this(left, Object.class, universalEqualityComparer, right, Object.class, universalEqualityComparer);
    }


    //thank god for java type inference...
    public static <TLeftEquated, TLeftMember extends TLeftEquated, TRightEquated, TRightMember extends TRightEquated>
    Tuple<TLeftMember, TRightMember> withPreciseEquality(TLeftMember left,
                                                         Class<TLeftEquated> leftEquatingSuperClass,
                                                         EqualityComparer<TLeftEquated> leftEqualityComparator,
                                                         TRightMember right,
                                                         Class<TRightEquated> rightEquatingSuperClass,
                                                         EqualityComparer<TRightEquated> rightEqualityComparator){
        return new Tuple<>(left,
                leftEquatingSuperClass,
                leftEqualityComparator,
                right,
                rightEquatingSuperClass,
                rightEqualityComparator);
    }

    private Tuple(TLeftMember left,
                  Class<? super TLeftMember> leftEquatingSuperClass,
                  EqualityComparer<? super TLeftMember> leftEqualityComparator,
                  TRightMember right,
                  Class<? super TRightMember> rightEquatingSuperClass,
                  EqualityComparer<? super TRightMember> rightEqualityComparator){

        this.left = left;
        this.leftEquatingSuperClass = leftEquatingSuperClass;
        this.leftEqualityComparator = leftEqualityComparator;
        this.right = right;
        this.rightEquatingSuperClass = rightEquatingSuperClass;
        this.rightEqualityComparator = rightEqualityComparator;

    }

    @Override
    public String toString() {
        return "Tuple [left=" + nullSafeToString(left) + ",right=" + nullSafeToString(right) + "]";
    }

    @Override
    public TLeftMember getKey() {
        return left;
    }

    @Override
    public TRightMember getValue() {
        return right;
    }

    @Override
    public TRightMember setValue(TRightMember value) {
        throw new UnsupportedOperationException();
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
        if(otherComponent != null && thisComponent != null
                && ! thisComponentEquatingType.isInstance(otherComponent)){
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
