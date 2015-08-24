package com.empowerops.linqalike.common;

import com.empowerops.common.ReflectionUtilities;
import com.empowerops.linqalike.delegate.Condition;

public class SetIsEmptyException extends QueryDelegateException {

    public <TElement> SetIsEmptyException(Iterable<TElement> problemSet, Condition<? super TElement> condition){
        super("The provided set once filtered by the given condition contains no elements!\n"
                + "The visible members were:\n\t"
                + Formatting.verticallyPrintMembers(problemSet)
                + "And the condition was:\n"
                + ReflectionUtilities.describeObjectByReflection(condition, 1)
                + "\n");
    }
    public SetIsEmptyException(){
        super("The provided set contains no elements!");
    }
}
