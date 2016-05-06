package com.empowerops.linqalike.common;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

import static com.empowerops.linqalike.CommonDelegates.nullSafeToString;

@Immutable
public class Tuple<TLeftMember, TRightMember> implements Map.Entry<TLeftMember, TRightMember>{

    public final TLeftMember left;
    public final TRightMember right;

    public static <TL, TR> Tuple<TL, TR> Pair(TL l, TR r){
        return new Tuple<>(l, r);
    }

    public Tuple(TLeftMember left, TRightMember right){
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Tuple [left=" + nullSafeToString(left) + ",right=" + nullSafeToString(right) + "]";
    }

    @Override
    public TLeftMember getKey() {
        return left;
    }
    public TLeftMember getLeft(){ return left; }

    @Override
    public TRightMember getValue() {
        return right;
    }
    public TRightMember getRight(){ return right; }

    @Override
    public TRightMember setValue(TRightMember value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Tuple)) { return false; }

        Tuple tuple = (Tuple) o;

        if (left != null ? !left.equals(tuple.left) : tuple.left != null) { return false; }
        if (right != null ? !right.equals(tuple.right) : tuple.right != null) { return false; }

        return true;
    }
    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}

