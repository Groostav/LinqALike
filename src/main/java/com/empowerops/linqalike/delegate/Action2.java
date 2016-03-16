package com.empowerops.linqalike.delegate;

import com.empowerops.linqalike.common.Tuple;

@FunctionalInterface
public interface Action2<TFirstParameter, TSecondParameter>{

    void doUsing(TFirstParameter firstArgument, TSecondParameter secondArgument);

    default Action1<Tuple<? extends TFirstParameter, ? extends TSecondParameter>> toActionOnTuple(){
        return tuple -> this.doUsing(tuple.left, tuple.right);
    }

    class WithDescription<TFirstParameter, TSecondParameter>
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

