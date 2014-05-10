package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Factories;

public class SingletonSetContainsMultipleElementsException extends QueryDelegateException {
    public <TElement> SingletonSetContainsMultipleElementsException(Iterable<TElement> problemSet, Iterable<TElement> problemMembers, Condition<? super TElement> condition){
        super("The provided set once filtered to what was supposed to be one elements contains more than one element!\n"
                + "the original set was:"
                + Formatting.verticallyPrintMembers(problemSet)
                + "The condition that should have yielded one element was:"
                + Formatting.verticallyPrintMembers(Factories.asList(condition))
                + "The multiple elements that passed the above condition (where only one should've) are:"
                + Formatting.verticallyPrintMembers(problemMembers));
    }
}
