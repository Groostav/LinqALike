package com.empowerops.linqalike;

import com.empowerops.linqalike.common.*;
import com.empowerops.linqalike.delegate.*;
import com.sun.javafx.binding.ObjectConstant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.empowerops.linqalike.Factories.from;


public class CommonDelegates {

    public static final Func1.WithDescription<Object, Object> Identity = new Func1.WithDescription<>("identity function: object -> object", object -> object);
    public static final Condition.WithDescription<Iterable> IsEmpty           = new Condition.WithDescription<>(
            "set is empty",
            candidate -> ! candidate.iterator().hasNext()
    );
    public static final EqualityComparer.Untyped DefaultEquality   = new DefaultEqualityComparer();
    public static final EqualityComparer.Untyped ReferenceEquality = new ReferenceEqualityComparer();
    public static final EqualityComparer.Untyped FalsehoodEquality = new DescribedUntypedEqualityComparer(
            "never-true equaity, equals(left, right) -> false, hashcode(object) -> counter++",
            new EqualityComparer.Untyped() {
                @Override
                public boolean equals(Object left, Object right) {
                    return false;
                }

                @Override
                public int hashCode(Object object) {
                    return System.identityHashCode(object);
                }
            });

    public static <T> Func<? extends T> NullFactory() {
        return () -> null;
    }

    public static final Comparator<Object> HashCodeComparator = new Comparator.WithDescription<>(
            "hashcode Comparison: (left, right) -> Integer.compare(nullSafeHashCode(left), nullSafeHashCode(right))",
            (left, right) -> Integer.compare(nullSafeHashCode(left), nullSafeHashCode(right)));

    public static final EqualityComparer.Untyped ComparedByClass = EqualityComparer.Untyped.make(
            (left, right) -> left == null ? right == null : left.getClass().equals(right.getClass()),
            obj -> obj.getClass().hashCode()
    );

    @SuppressWarnings("unchecked")
    public static <TObject> Func1<TObject, TObject> identity() {
        return (Func1) Identity;
    }

    public static <TObject> Func1<TObject, TObject> elementsAsIs() {
        return identity();
    }

    public static <TInspected> Condition.WithDescription<TInspected> Not(final Condition<TInspected> condition) {
        return new Condition.WithDescription<>("Not:" + condition, candidate -> ! condition.passesFor(candidate));
    }

    public static final Condition.WithDescription<Object> IsNull  = new Condition.WithDescription<>("Is Null: can -> can == null", can -> can == null);
    public static final Condition.WithDescription<Object> NotNull = Not(IsNull);
    public static final Condition<Number> NotNaN  = can -> ! Double.isNaN(can.doubleValue());

    public static final Condition.WithDescription<Object> Tautology = new Condition.WithDescription<>("Tautology: can -> true", candidate -> true);
    public static final Condition.WithDescription<Object> Falsehood = new Condition.WithDescription<>("Falsehoold: can -> false", candidate -> false);


    public static final Func1<Object, String> NullSafeToString = new Func1.WithDescription<>(
            "Null Safe ToString: source -> source == null ? \"<null>\" : source.toString()",
            CommonDelegates::nullSafeToString);

    public static Condition<Object> IsInstanceOf(final Class<?> allowed) {
        return new Condition.WithDescription<>(
                "is instance of " + allowed + ": candidate -> allowed.isAssignableFrom(candidate.getClass())",
                allowed::isInstance
        );
    }


    public static Condition<Object> IsInstanceOfAny(final Class... allowedTypes) {
        return IsInstanceOfAny(Factories.asList(allowedTypes));
    }

    public static Condition<Object> IsInstanceOfAny(final Iterable<? extends Class> allowedTypes) {
        return new Condition.WithDescription<>(
                "is instance of any " + Formatting.join(from(allowedTypes).select(Class::getSimpleName), ","),
                actual -> isInstanceOfAny(actual, allowedTypes)
        );
    }

    public static boolean isInstanceOfAny(Object inspected, Iterable<? extends Class> allowedTypes) {
        if (inspected == null) {
            return false;
        }

        return from(allowedTypes).any(
                allowed -> inspected instanceof Class
                        ? allowed.isAssignableFrom((Class) inspected)
                        : allowed.isInstance(inspected)
        );
    }

    public static <TLeft, TRight> Condition<Tuple<TLeft, TRight>> EntryIsIn(final DefaultedQueryableMap<TLeft, ? extends Queryable<TRight>> other) {
        return candidate -> other.getValueFor(candidate.left).containsElement(candidate.right);
    }

    /**
     * Oxy-moron method designed to aide in ober
     */
    public static <TSource, TObserved> Func1<? super TSource, ObjectConstant<TObserved>> observableConst(Func1<? super TSource, TObserved> selector) {
        return x -> ObjectConstant.valueOf(selector.getFrom(x));
    }

    public static Condition<File> FileExists = new Condition.WithDescription<>("The File exists: File::exists", File::exists);

    public static <TElement> boolean nullSafeEquals(TElement left, TElement right) {
        return left == null ? right == null :
                right != null && left.equals(right);
    }

    public static <TElement> boolean nullSafeEquals(TElement left, TElement right, Comparator<? super TElement> comparator){
        return left == null ? right == null : comparator.compare(left, right) == 0;
    }

    public static <TElement> boolean nullSafeEquals(TElement left, TElement right, EqualityComparer<? super TElement> comparer){
        return left == null ? right == null : right != null && comparer.equals(left, right);
    }

    public static int nullSafeHashCode(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    public static String nullSafeToString(Object source) {
        return source == null ? "<null>" : source.toString();
    }

    public static <TSource> Condition<TSource> memoized(Condition<TSource> condition) {
        Map<TSource, Boolean> cache = new HashMap<>();
        return new Condition.WithDescription<>(
                "memoized { " + condition + " }",
                source -> {
                    if (cache.containsKey(source)) {
                        return cache.get(source);
                    }
                    else{
                        Boolean value = condition.passesFor(source);
                        cache.put(source, value);
                        return value;
                    }
                }
        );
    }
    public static <TSource, TResult> Func1<TSource, TResult> memoizedSelector(Func1<TSource, TResult> valueRetrieval){
        Map<Reference<TSource>, TResult> cache = new HashMap<>();
        return new Func1.WithDescription<>(
                "memoized { " + valueRetrieval + " }",
                source -> {
                    Reference<TSource> key = new Reference<>(source, CommonDelegates.ReferenceEquality);
                    if (cache.containsKey(key)) {
                        return cache.get(key);
                    }
                    else{
                        TResult value = valueRetrieval.getFrom(source);
                        cache.put(key, value);
                        return value;
                    }
                }
        );
    }

    public static <TEquated> EqualityComparer<TEquated> memoized(EqualityComparer<TEquated> valueRetrieval){
        Map<Tuple<TEquated, TEquated>, Boolean> cache = new HashMap<>();
        return new EqualityComparerWithDescription<>(
                "memoized { " + valueRetrieval + " }",
                (left, right) -> {
                    Tuple<TEquated, TEquated> key = new EquatableTuple<>(left, right, CommonDelegates.ReferenceEquality);
                    if(cache.containsKey(key)){
                        return cache.get(key);
                    }
                    else{
                        boolean value = valueRetrieval.equals(left, right);
                        cache.put(key, value);
                        return value;
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public static <TEquated> EqualityComparer.Untyped typedEquality(Class<TEquated> miniumAllowedType, EqualityComparer<TEquated> equalityComparerToBeFiltered){
        return new EqualityComparer.Untyped() {
            @Override
            public boolean equals(Object left, Object right) {
                return miniumAllowedType.isInstance(left)
                        && miniumAllowedType.isInstance(right)
                        && equalityComparerToBeFiltered.equals((TEquated) left, (TEquated) right);
            }
        };
    }

    public static <TArgument, TEquated>
    EqualityComparer.Untyped performEqualsUsing(final Func1<TArgument, TEquated> comparableSelector){
        return new DescribedUntypedEqualityComparer(
                "default equality on values provided by: " + comparableSelector,
                EqualityComparer.Untyped.make(
                    (left, right) -> {
                        //TODO investigate, maybe split Queryable and Collections further?
                        TEquated leftComparable = comparableSelector.getFrom((TArgument)left);
                        TEquated rightComparable = comparableSelector.getFrom((TArgument)right);

                        return leftComparable == null
                                ? rightComparable == null
                                : leftComparable.equals(rightComparable);
                    },
                    argToHash -> {
                        TEquated hashable = comparableSelector.getFrom((TArgument) argToHash);
                        return hashable == null ? 0 : hashable.hashCode();
                    }
                )
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


    public static <TElement>
    Condition<TElement> isEqualTo(TElement desired, EqualityComparer<? super TElement> equalityComparer){
        return actual -> equalityComparer.equals(actual, desired);
    }

    public static <TSource, TTransformedMember>
    Func1<TSource, Queryable<TTransformedMember>> asList(Func1<TSource, TTransformedMember[]> arraySelector){
        return source -> Factories.from(arraySelector.getFrom(source));
    }

    public static boolean XOR(Boolean... args){
        return from(args).where(condiition -> condiition).isSingle();
    }

    public static boolean roughlyEquals(int left, int right, int plusMinus){
        return Math.abs(left - right) <= plusMinus;
    }

    public static boolean isBetween(double lower, double suspect, double upper){
        return lower <= suspect && suspect <= upper;
    }
    public static boolean notNullOrNan(Double aDouble) {
        return aDouble != null && ! aDouble.isNaN();
    }
    public static boolean isNull(Object obj){
        return obj == null;
    }
    public static boolean isNullOrNan(Double aDouble){
        return aDouble == null || aDouble.isNaN();
    }

    public static <T> Optional<Boolean> ifBothPresent(Optional<T> left, Optional<T> right, Func2<T, T, Boolean> condition){
        if(left.isPresent() && right.isPresent()){
            return Optional.of(condition.getFrom(left.get(), right.get()));
        }
        else{
            return Optional.empty();
        }
    }
    public static boolean HasBeenGarbageCollected(java.lang.ref.Reference<?> reference) {
        return reference.get() == null;
    }
}
