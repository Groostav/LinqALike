package LinqALike.Delegate;

@FunctionalInterface
public interface Func<TResult>{

    public TResult getValue();

    public static class WithDescription<TResult> implements Func<TResult>{
        public final String description;
        public final Func<TResult> func;

        public WithDescription(String description, Func<TResult> func) {
            this.description = description;
            this.func = func;
        }

        @Override
        public TResult getValue() {
            return func.getValue();
        }
    }
}
