package com.empowerops.linqalike.delegate;

import com.empowerops.linqalike.common.Tuple;

@FunctionalInterface
public interface Func2<TFirstParam, TSecondParam, TResult> {

    TResult getFrom(TFirstParam firstArgument,TSecondParam secondArgument);


    default Func1<Tuple<? extends TFirstParam, ? extends TSecondParam>, TResult> asFuncOnTuple(){
        return tuple -> getFrom(tuple.left, tuple.right);
    }

    class WithDescription<TFirstParam, TSecondParam, TResult>
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

