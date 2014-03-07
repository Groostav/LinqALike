package LinqALike;

import java.util.Map;

import static LinqALike.CommonDelegates.nullSafeToString;

public class Tuple<TLeftMember, TRightMember> implements Map.Entry<TLeftMember, TRightMember> {

    public Tuple(TLeftMember left, TRightMember right){
        this.left = left;
        this.right = right;
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

    // this code was auto-generated by intelliJ, and is safe because of this classes immutability

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        if (left != null ? !left.equals(tuple.left) : tuple.left != null) return false;
        if (right != null ? !right.equals(tuple.right) : tuple.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
