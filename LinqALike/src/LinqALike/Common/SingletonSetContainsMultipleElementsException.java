package LinqALike.Common;

import LinqALike.Delegate.Condition;
import LinqALike.LinqingList;

import static LinqALike.LinqBehaviour.verticallyPrintMembers;

public class SingletonSetContainsMultipleElementsException extends QueryDelegateException {
    public <TElement> SingletonSetContainsMultipleElementsException(Iterable<TElement> problemSet, Iterable<TElement> problemMembers, Condition<? super TElement> condition){
        super("The provided set once filtered to what was supposed to be one elements contains more than one element!\n"
                + "the original set was:"
                + verticallyPrintMembers(problemSet)
                + "The condition that should have yielded one element was:"
                + verticallyPrintMembers(LinqingList.asList(condition))
                + "The multiple elements that passed the above condition (where only one should've) are:"
                + verticallyPrintMembers(problemMembers));
    }
}
