package com.EmpowerOperations.LinqALike.Delegate;

@FunctionalInterface
public interface Func3<TFirstParam, TSecondParam, TThirdParam, TResult> {

    public TResult getFrom(TFirstParam firstArgument, TSecondParam secondArgument, TThirdParam thirdArgument);

    public static class WithDescription<TFirstParam, TSecondParam, TThirdParam, TResult>
            implements Func3<TFirstParam, TSecondParam, TThirdParam, TResult>{

        public final String description;
        public final Func3<TFirstParam, TSecondParam, TThirdParam, TResult> func;

        public WithDescription(String description, Func3<TFirstParam, TSecondParam, TThirdParam, TResult> func) {
            this.description = description;
            this.func = func;
        }

        @Override
        public TResult getFrom(TFirstParam firstArgument, TSecondParam secondArgument, TThirdParam thirdArgument) {
            return func.getFrom(firstArgument, secondArgument, thirdArgument);
        }
    }
}
