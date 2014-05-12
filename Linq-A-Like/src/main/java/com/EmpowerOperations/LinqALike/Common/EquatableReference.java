package com.EmpowerOperations.LinqALike.Common;

/**
 * Created by Geoff on 2014-05-11.
 */
public class EquatableReference<TReferenced> {

    private final EqualityComparer<? super TReferenced> equalityComparer;

    public TReferenced value;

    public EquatableReference(EqualityComparer<? super TReferenced> equalityComparer){
        this.value = null;
        this.equalityComparer = equalityComparer;
    }
    public EquatableReference(TReferenced initialValue, EqualityComparer<? super TReferenced> equalityComparer){
        this.value = initialValue;
        this.equalityComparer = equalityComparer;
    }

    @Override
    public int hashCode() {
        return equalityComparer.hashCode(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (! (other instanceof EquatableReference)) { return false; }

        EquatableReference otherRef = (EquatableReference) other;
        if(otherRef.value != null && value != null
                && ! otherRef.value.getClass().isInstance(value.getClass())){ return false; }

        EquatableReference<TReferenced> typedOtherRef = otherRef;

        return equalityComparer.equals(value, typedOtherRef.value);
    }
}
