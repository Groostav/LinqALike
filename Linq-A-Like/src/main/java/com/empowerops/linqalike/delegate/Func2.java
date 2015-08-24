package com.empowerops.linqalike.delegate;

@FunctionalInterface
public interface Func2<TFirstParam, TSecondParam, TResult> {

    public TResult getFrom(TFirstParam firstArgument, TSecondParam secondArgument);

    public static class WithDescription<TFirstParam, TSecondParam, TResult>
            implements Func2<TFirstParam, TSecondParam, TResult>{

        public final String description;
        public final Func2<TFirstParam, TSecondParam, TResult>func;

        public WithDescription(String description, Func2<TFirstParam, TSecondParam, TResult> func) {
            this.description = description;
            this.func = func;
        }

        @Override
        public TResult getFrom(TFirstParam firstArgument, TSecondParam secondArgument) {
            return func.getFrom(firstArgument, secondArgument);
        }
    }
}

