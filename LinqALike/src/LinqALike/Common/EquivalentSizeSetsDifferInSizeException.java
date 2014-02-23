package LinqALike.Common;

public class EquivalentSizeSetsDifferInSizeException extends QueryDelegateException {
    public <TLeft, TRight> EquivalentSizeSetsDifferInSizeException(Iterable<TLeft> left, Iterable<TRight> right) {
        super("The provided sets differ in length!\n"
                + "The left set set was:"
                + Formatting.verticallyPrintMembers(left)
                + "And the right set was:"
                + Formatting.verticallyPrintMembers(right));
    }
}
