package LinqALike.Common;

import LinqALike.Delegate.Condition;
import LinqALike.LinqBehaviour;
import LinqALike.LinqingList;

public class NonEmptySetIsEmptyException extends QueryDelegateException {

    public <TElement> NonEmptySetIsEmptyException(Iterable<TElement> problemSet, Condition<? super TElement> condition){
        super("The provided set once filtered by the given condition contains no elements!\n"
                + "The original set was:"
                + LinqBehaviour.verticallyPrintMembers(problemSet)
                + "And the condition was:"
                + LinqBehaviour.verticallyPrintMembers(LinqingList.asList(condition)));
    }
    public NonEmptySetIsEmptyException(){
        super("The provided set contains no elements!");
    }
}
