package Assists;

import LinqALike.Delegate.Func1;

/**
 * serves the same purpose as {@link CountingCondition}
 *
 * @see CountingCondition
 */
public abstract class CountingTransform<TInspected, TResult> extends CountingDelegate implements Func1<TInspected, TResult> {

    public static <TInspected, TResult> CountingTransform<TInspected, TResult> track(Func1<TInspected, TResult> actualTransform){
        return new CountingTransform<TInspected, TResult>() {
            @Override
            protected TResult getFromImpl(TInspected cause) {
                return actualTransform.getFrom(cause);
            }
        };
    }

    @Override
    public final TResult getFrom(TInspected cause) {
        inspectedElements.add(cause);
        return getFromImpl(cause);
    }

    protected abstract TResult getFromImpl(TInspected cause);
}

