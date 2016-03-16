package com.empowerops.linqalike.delegate;

import com.empowerops.linqalike.common.Tuple;

@FunctionalInterface
public interface BiCondition<TFirstInspected, TSecondInspected> {

    boolean passesFor(TFirstInspected left, TSecondInspected right);

    default Condition<Tuple<? extends TFirstInspected, ? extends TSecondInspected>> toConditionOnTuple(){
        return tuple -> this.passesFor(tuple.left, tuple.right);
    }
}
