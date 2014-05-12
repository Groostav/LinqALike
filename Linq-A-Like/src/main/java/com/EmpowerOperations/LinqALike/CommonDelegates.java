package com.EmpowerOperations.LinqALike;

import com.EmpowerOperations.LinqALike.Common.DescribedEqualityComparer;
import com.EmpowerOperations.LinqALike.Common.EqualityComparer;
import com.EmpowerOperations.LinqALike.Common.EqualityComparerWithDescription;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.Delegate.Condition;
import com.EmpowerOperations.LinqALike.Delegate.Func1;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.join;

public class CommonDelegates {

    public static Condition.WithDescription<Iterable> IsEmpty = new Condition.WithDescription<>(
            "set is not empty",
            (Condition<Iterable>) candidate -> !candidate.iterator().hasNext()
    );
    public static final EqualityComparer.Untyped DefaultEquals = new DefaultEqualityComparer();
    public static final EqualityComparer.Untyped ReferenceEquals = new ReferenceEqualityComparer();

    public static <TObject> Func1<TObject, TObject> identity() {
        return new Func1.WithDescription<>("identity function: object -> object", object -> object);
    }

    public static <TInspected> Condition.WithDescription<TInspected> Not(final Condition<TInspected> condition) {
        return new Condition.WithDescription<>("Not:" + condition, candidate -> !condition.passesFor(candidate));
    }

    public static final Condition.WithDescription<Object> IsNull = new Condition.WithDescription<>("Is Null: can -> can == null", can -> can == null);
    public static final Condition.WithDescription<Object> NotNull = Not(IsNull);

    public static final Condition.WithDescription<Object> Tautology = new Condition.WithDescription<>("Tautology: can -> true", candidate -> true);
    public static final Condition.WithDescription<Object> Falsehood = new Condition.WithDescription<>("Falsehoold: can -> false", candidate -> false);

    public static final Func1<Object, String> NullSafeToString = new Func1.WithDescription<>(
            "Null Safe ToString: source -> source == null ? \"<null>\" : source.toString()",
            CommonDelegates::nullSafeToString);

    public static Condition<Object> IsInstanceOf(final Class<?> allowed) {
        return new Condition.WithDescription<>(
                "is instance of " + allowed + ": candidate -> allowed.isAssignableFrom(candidate.getClass())",
                (Object candidate) -> allowed.isAssignableFrom(candidate.getClass())
        );
    }


    public static Condition<Object> IsInstanceOfAny(final Class... allowedTypes) {
        return IsInstanceOfAny(Factories.asList(allowedTypes));
    }

    public static Condition<Object> IsInstanceOfAny(final Iterable<Class> allowedTypes) {
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
        return candidate -> other.get(candidate.left).contains(candidate.right);
    }

    public static Condition<File> FileExists = new Condition.WithDescription<>("The File exists: File::exists", File::exists);

    public static <TElement> boolean nullSafeEquals(TElement left, TElement right) {

        return left == null ? right == null : left.equals(right);
    }

    public static String nullSafeToString(Object source) {
        return source == null ? "<null>" : source.toString();
    }


    public static <TSource, TResult> Func1<TSource, TResult> memoized(Func1<TSource, TResult> valueRetrieval){
        Map<TSource, TResult> cache = new HashMap<>();
        return new Func1.WithDescription<>(
                "memoized { " + valueRetrieval + " }",
                source -> {
                    if (cache.containsKey(source)) {
                        return cache.get(source);
                    }
                    else{
                        TResult value = valueRetrieval.getFrom(source);
                        cache.put(source, value);
                        return value;
                    }
                }
        );
    }

    public static <TEquated> EqualityComparer<TEquated> memoized(EqualityComparer<TEquated> valueRetreval){
        Map<Tuple<TEquated, TEquated>, Boolean> cache = new HashMap<>();
        return new EqualityComparerWithDescription<>(
                "memoized { " + valueRetreval + " }",
                (left, right) -> {
                    Tuple<TEquated, TEquated> key = Tuple.withEqualityComparator(left, right, CommonDelegates.ReferenceEquals);
                    if(cache.containsKey(key)){
                        return cache.get(key);
                    }
                    else{
                        boolean value = valueRetreval.equals(left, right);
                        cache.put(key, value);
                        return value;
                    }
                }
        );
    }

    public static <TArgument, TEquated>
    DescribedEqualityComparer<TArgument> equalsBySelector(final Func1<TArgument, TEquated> comparableSelector){
        return new DescribedEqualityComparer<>(
                "default equality on values provided by: " + comparableSelector,
                (left, right) -> {
                    TEquated leftComparable = comparableSelector.getFrom(left);
                    TEquated rightComparable = comparableSelector.getFrom(right);

                    return leftComparable.equals(rightComparable);
                }
        );
    }

    public static <TArgument, TCompared extends Comparable<TCompared>>
    Comparator<TArgument> performComparisonUsing(final Func1<TArgument, TCompared> comparableSelector){
        return (left, right) -> {
            TCompared leftComparable = comparableSelector.getFrom(left);
            TCompared rightComparable = comparableSelector.getFrom(right);

            return leftComparable.compareTo(rightComparable);
        };
    }
}
