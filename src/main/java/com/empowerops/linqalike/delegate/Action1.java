package com.empowerops.linqalike.delegate;

import java.io.FileNotFoundException;
import java.io.Serializable;

@FunctionalInterface
public interface Action1<TParameter> extends Serializable {

    public void doUsing(TParameter argument);

    public static class WithDescription<TParameter> implements Action1<TParameter>{
        public final String name;
        public final Action1<TParameter> action;

        public WithDescription(String name, Action1<TParameter> action){
            this.name = name;
            this.action = action;
        }

        @Override
        public void doUsing(TParameter argument) {
            action.doUsing(argument);
        }
    }
}
