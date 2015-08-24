package com.empowerops.linqalike.delegate;

@FunctionalInterface
public interface Condition<TInspected> {

    public boolean passesFor(TInspected candidate);

    public static class WithDescription<TInspected> implements Condition<TInspected>{

        public final String description;
        public final Condition<TInspected> condition;

        public WithDescription(String description, Condition<TInspected> condition) {
            this.description = description;
            this.condition = condition;
        }

        @Override
        public boolean passesFor(TInspected candidate) {
            return condition.passesFor(candidate);
        }

        @Override
        public String toString() {
            return description;
        }
    }
}

