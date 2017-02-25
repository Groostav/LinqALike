package com.empowerops.linqalike.assists;

import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.BiCondition;

/**
 * Created by Geoff on 2017-01-19.
 */
public abstract class CountingBiCondition<TFirst, TSecond> extends CountingDelegate implements BiCondition<TFirst, TSecond> {

    public static <TFirst, TSecond> CountingBiCondition<TFirst, TSecond> track(BiCondition<TFirst, TSecond> actualCondition){
        return new CountingBiCondition<TFirst, TSecond>() {
            @Override
            protected boolean passesForImpl(TFirst first, TSecond second) {
                return actualCondition.passesFor(first, second);
            }
        };
    }

    @Override
    public final boolean passesFor(TFirst left, TSecond right) {
        inspectedElements.add(new Tuple<>(left, right));
        return passesForImpl(left, right);
    }

    protected abstract boolean passesForImpl(TFirst left, TSecond right);
}
