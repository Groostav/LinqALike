package LinqALike.Delegate;

@FunctionalInterface
public interface Action3<TFirstParameter, TSecondParameter, TThirdParameter>{

    public void doUsing(TFirstParameter firstArgument, TSecondParameter secondArgument, TThirdParameter thirdArgument);

    public static class WithDescription<TFirstParameter, TSecondParameter, TThirdParameter>
            implements Action3<TFirstParameter, TSecondParameter, TThirdParameter>{

        public final String description;
        public final Action3<TFirstParameter, TSecondParameter, TThirdParameter> action;

        public WithDescription(String description, Action3<TFirstParameter, TSecondParameter, TThirdParameter> action) {
            this.description = description;
            this.action = action;
        }

        @Override
        public void doUsing(TFirstParameter firstArgument, TSecondParameter secondArgument, TThirdParameter thirdArgument) {
            action.doUsing(firstArgument, secondArgument, thirdArgument);
        }
    }
}
