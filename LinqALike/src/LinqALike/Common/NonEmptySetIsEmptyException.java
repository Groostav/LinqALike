package LinqALike.Common;

import LinqALike.Delegate.Condition;
import LinqALike.Factories;

public class NonEmptySetIsEmptyException extends QueryDelegateException {

    public <TElement> NonEmptySetIsEmptyException(Iterable<TElement> problemSet, Condition<? super TElement> condition){
        super("The provided set once filtered by the given condition contains no elements!\n"
                + "The original set was:"
                + Formatting.verticallyPrintMembers(problemSet)
                + "And the condition was:"
                + Formatting.verticallyPrintMembers(Factories.asList(condition)));
    }
    public NonEmptySetIsEmptyException(){
        super("The provided set contains no elements!");
    }
}
