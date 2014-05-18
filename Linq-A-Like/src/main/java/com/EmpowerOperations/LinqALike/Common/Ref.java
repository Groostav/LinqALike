package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.CommonDelegates;

/**
 * This class is effectively a double pointer, allowing you to ether modify
 * something by reference, or escape javas 'members in a closure must be final'
 * semantics.
 *
 * @author Geoff on 24/07/13
 */
public final class Ref<TValue>{

    public final EqualityComparer<? super TValue> equalityComparer;

    public TValue val;

    public Ref() {
        equalityComparer = CommonDelegates.DefaultEquality;
    }
    public Ref(TValue value){
        this.val = value;
        equalityComparer = CommonDelegates.DefaultEquality;
    }
    public Ref(TValue value, EqualityComparer<? super TValue> equalityComparer){
        this.val = value;
        this.equalityComparer = equalityComparer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object right) {
        if (this == right) { return true; }
        if (right == null || getClass() != right.getClass()) { return false; }

        Ref rightRef = (Ref) right;
        if(rightRef.val != null && val != null
                && ! val.getClass().isInstance(rightRef.val)){
            //type param miss-match, you've called stringRef.equals(intRef).
            return false;
        }

        return equalityComparer.equals(val, (TValue) rightRef.val);
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }
}
