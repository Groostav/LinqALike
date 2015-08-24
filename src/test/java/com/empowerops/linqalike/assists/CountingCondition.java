package com.empowerops.linqalike.assists;

import com.empowerops.linqalike.delegate.Condition;

/**
 * the purpose of this class is to assert that side effects have also been satisfied.
 * eg: if you call <code>new LinqingList&lt;Integer&gt;(1, 2, 3).any((elem) -> {elem == 1;});</code>
 * you would expected any to <i>only invoke the condition once.</i>. If it invokes the condition multiple times,
 * then you might cause a side effect error. This is an extension of short-circuiting.
 */
public abstract class CountingCondition<TInspected> extends CountingDelegate implements Condition<TInspected> {

    public static <TInspected> CountingCondition<TInspected> track(Condition<TInspected> actualCondition){
        return new CountingCondition<TInspected>() {
            @Override
            protected boolean passesForImpl(TInspected cause) {
                return actualCondition.passesFor(cause);
            }
        };
    }

    /**
     * note: the final is simply to make sure forgetful programmers don't override {@link com.empowerops.linqalike.delegate.Condition#passesFor(Object)}
     * instead of {@link #passesForImpl(Object)} (as they should).
     */
    @Override
    public final boolean passesFor(TInspected cause) {
        inspectedElements.add(cause);
        return passesForImpl(cause);
    }

    protected abstract boolean passesForImpl(TInspected cause);
}
