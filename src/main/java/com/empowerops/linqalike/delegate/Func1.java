package com.empowerops.linqalike.delegate;

import com.empowerops.linqalike.common.Tuple;

import java.io.Serializable;
import java.util.Map;

@FunctionalInterface
public interface Func1<TParameter, TResult> {

    public TResult getFrom(TParameter source);

    public static class WithDescription<TParameter, TResult>
            implements Func1<TParameter, TResult>{

        public final String description;
        public final Func1<TParameter, TResult> func;

        public WithDescription(String description, Func1<TParameter, TResult> func) {
            this.description = description;
            this.func = func;
        }

        @Override
        public TResult getFrom(TParameter source) {
            return func.getFrom(source);
        }
    }

    @FunctionalInterface
    public static interface Array<TParameter, TResult> extends Func1<TParameter, TResult[]>{

        @Override
        public TResult[] getFrom(TParameter source);
    }

    default <TRightParam, TRightResult>
    Func1<Map.Entry<TParameter, TRightParam>, Map.Entry<TResult, TRightResult>>
    asFuncOnLeftTupleWithRight(Func1<? super TRightParam, TRightResult> rightSelector){
        return entry -> new Tuple<>(this.getFrom(entry.getKey()), rightSelector.getFrom(entry.getValue()));
    }
}

