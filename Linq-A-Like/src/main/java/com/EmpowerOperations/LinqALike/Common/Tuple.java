package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.CommonDelegates;

import java.util.Map;

import static com.EmpowerOperations.LinqALike.CommonDelegates.nullSafeToString;

public class Tuple<TLeftMember, TRightMember> implements Map.Entry<TLeftMember, TRightMember> {

    private final EqualityComparer<Object> equalityComparator;

    public Tuple(TLeftMember left, TRightMember right){
        this.left = left;
        this.right = right;
        this.equalityComparator = CommonDelegates.DefaultEquality;
    }

    public static <TCompared, TLeftMember extends TCompared, TRightMember extends TCompared>
    Tuple<TLeftMember, TRightMember> withEqualityComparator(TLeftMember left, TRightMember right, EqualityComparer<TCompared> equalityComparator){
        return new Tuple<>(left, right, equalityComparator);
    }

    @SuppressWarnings("unchecked")
    // We've statically satisfied these constraints one,
    // continuing to satisfy them would yield no benefit to the consumer and would require us to keep another type parameter.
    // Since we've already got problems inferring the two types, I'm not going to compound the problem further,
    // so we're just going to use the raw type of the equality comparer.
    private Tuple(TLeftMember left, TRightMember right, EqualityComparer<?> equalityComparator){
        this.left = left;
        this.right = right;
        this.equalityComparator = (EqualityComparer) equalityComparator;
    }

    public final TLeftMember left;
    public final TRightMember right;

    //static factories:
    public static <TLeft, TRight>
    Tuple<TLeft, TRight> pair(TLeft left, TRight right){
        return new Tuple<>(left, right);
    }

    //equals and toString

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        return equalityComparator.equals(left, right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
