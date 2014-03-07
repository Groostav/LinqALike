package LinqALike;

import LinqALike.Delegate.Condition;
import LinqALike.Delegate.Func1;

import java.io.File;
import java.lang.invoke.SerializedLambda;

import static org.apache.commons.lang.StringUtils.join;

public class CommonDelegates {

    public static Condition.WithDescription<Iterable> IsEmpty = new Condition.WithDescription<>(
            "set is not empty",
            (Condition<Iterable>) candidate -> ! candidate.iterator().hasNext()
    );

    public static <TObject> Func1.WithDescription<TObject, TObject> identity(){
        return new Func1.WithDescription<>("identity function: object -> object", object -> object);
    };

    public static <TInspected> Condition.WithDescription<TInspected> Not(final Condition<TInspected> condition){
        return new Condition.WithDescription<>("Not:" + condition, candidate -> ! condition.passesFor(candidate));
    }

    public static final Condition.WithDescription<Object> IsNull = new Condition.WithDescription<>("Is Null: can -> can == null", can -> can == null);
    public static final Condition.WithDescription<Object> NotNull = Not(IsNull);

    public static final Condition.WithDescription<Object> Tautology = new Condition.WithDescription<>("Tautology: can -> true", candidate -> true);
    public static final Condition.WithDescription<Object> Falsehood = new Condition.WithDescription<>("Falsehoold: can -> false", candidate -> false);

    public static final Func1<Object, String> NullSafeToString = new Func1.WithDescription<>(
            "Null Safe ToString: source -> source == null ? \"<null>\" : source.toString()",
            CommonDelegates::nullSafeToString);

    public static Condition<Object> IsInstanceOf(final Class<?> allowed){
        return new Condition.WithDescription<>(
                "is instance of " + allowed + ": candidate -> allowed.isAssignableFrom(candidate.getClass())",
                (Object candidate) -> allowed.isAssignableFrom(candidate.getClass())
        );
    }


    public static Condition<Object> IsInstanceOfAny(final Class... allowedTypes){
        return IsInstanceOfAny(Factories.asList(allowedTypes));
    }

    public static Condition<Object> IsInstanceOfAny(final Iterable<Class> allowedTypes){
        return new Condition.WithDescription<>(
                "is instance of any " + join(allowedTypes.iterator(), ","),
                actual -> actual != null &&
                        Factories.asList(allowedTypes).any((Class allowed) -> {
                            Class actualType = actual instanceof Class ? (Class) actual : actual.getClass();
                            return allowed.isAssignableFrom(actualType);
                        })
        );
    }

    public static <TLeft, TRight> Condition<Tuple<TLeft, TRight>> EntryIsIn(final QueryableMap<TLeft, ? extends Queryable<TRight>> other) {
        return new Condition<Tuple<TLeft, TRight>>() {
            public boolean passesFor(Tuple<TLeft, TRight> candidate) {
                return other.get(candidate.left).contains(candidate.right);
            }
        };
    }

    public static Condition<File> FileExists = new Condition.WithDescription<>("The File exists: File::exists", File::exists);

    public static <TElement> boolean nullSafeEquals(TElement left, TElement right) {

        return left == null ? right == null : left.equals(right);
    }
    public static String nullSafeToString(Object source){
        return source == null ? "<null>" : source.toString();
    }

    public static Condition.WithDescription<Object> Is(String desired) {
        return new Condition.WithDescription<>(
                "Is: object -> object.equals(desired)",
                object -> object.equals(desired)
        );
    }
}
