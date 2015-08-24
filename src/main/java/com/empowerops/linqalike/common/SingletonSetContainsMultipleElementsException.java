package com.empowerops.linqalike.common;

import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.delegate.Condition;

public class SingletonSetContainsMultipleElementsException extends QueryDelegateException {
    public <TElement> SingletonSetContainsMultipleElementsException(Iterable<TElement> problemSet, Iterable<TElement> problemMembers, Condition<? super TElement> condition){
        super("The provided set once filtered to what was supposed to be one elements contains more than one element!\n"
                + "the original set was:\n\t"
                + Formatting.verticallyPrintMembers(problemSet)
                + "The condition that should have yielded one element was:\n\t"
                + Formatting.verticallyPrintMembers(Factories.asList(condition))
                + "The multiple elements that passed the above condition (where only one should've) are:\n\t"
                + Formatting.verticallyPrintMembers(problemMembers));
    }
}
