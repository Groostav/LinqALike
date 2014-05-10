package com.EmpowerOperations.LinqALike.Delegate;

@FunctionalInterface
public interface Action {

    public void run();

    public static class WithDescription implements Action{
        public final String name;
        public final Action action;

        public WithDescription(String name, Action action){
            this.name = name;
            this.action = action;
        }

        @Override
        public void run() {
            action.run();
        }
    }
}


