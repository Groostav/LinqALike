package com.empowerops.linqalike.delegate;

@FunctionalInterface
public interface Action2<TFirstParameter, TSecondParameter>{

    public void doUsing(TFirstParameter firstArgument, TSecondParameter secondArgument);

    public static class WithDescription<TFirstParameter, TSecondParameter>
            implements Action2<TFirstParameter, TSecondParameter>{

        public final String description;
        public final Action2<TFirstParameter, TSecondParameter> action;

        public WithDescription(String description, Action2<TFirstParameter, TSecondParameter> action) {
            this.description = description;
            this.action = action;
        }

        @Override
        public void doUsing(TFirstParameter firstArgument, TSecondParameter secondArgument) {
            action.doUsing(firstArgument, secondArgument);
        }
    }
}

