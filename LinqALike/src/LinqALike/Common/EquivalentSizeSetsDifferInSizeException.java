package LinqALike.Common;

import LinqALike.LinqBehaviour;

public class EquivalentSizeSetsDifferInSizeException extends QueryDelegateException {
    public <TLeft, TRight> EquivalentSizeSetsDifferInSizeException(Iterable<TLeft> left, Iterable<TRight> right) {
        super("The provided sets differ in length!\n"
                + "The left set set was:"
                + LinqBehaviour.verticallyPrintMembers(left)
                + "And the right set was:"
                + LinqBehaviour.verticallyPrintMembers(right));
    }
}
