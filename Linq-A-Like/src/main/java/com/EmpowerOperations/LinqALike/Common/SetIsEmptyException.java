package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Factories;

public class SetIsEmptyException extends QueryDelegateException {

    public <TElement> SetIsEmptyException(Iterable<TElement> problemSet, Condition<? super TElement> condition){
        super("The provided set once filtered by the given condition contains no elements!\n"
                + "The visible members were:"
                + Formatting.verticallyPrintMembers(problemSet)
                + "And the condition was:"
                + Formatting.verticallyPrintMembers(Factories.asList(condition)));
    }
    public SetIsEmptyException(){
        super("The provided set contains no elements!");
    }
}
