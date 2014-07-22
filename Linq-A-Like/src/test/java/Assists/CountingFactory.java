package Assists;

import com.EmpowerOperations.LinqALike.Delegate.Func;

public abstract class CountingFactory<TProduced> extends CountingDelegate implements Func<TProduced> {

    public static <TProduced> CountingFactory<TProduced> track(Func<TProduced> actualTransform){

        return new CountingFactory<TProduced>() {
            @Override
            public final TProduced getValue() {
                TProduced value = actualTransform.getValue();
                inspectedElements.add(value);
                return value;
            }
        };
    }
}
