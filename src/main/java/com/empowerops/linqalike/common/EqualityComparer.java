package com.empowerops.linqalike.common;

import com.empowerops.linqalike.delegate.Func1;
import com.empowerops.linqalike.delegate.Func2;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    /**
     * Determines equality from some particular context of two objects.
     *
     * <p>This method follows many of the same rule sas the {@link Object#equals(Object)}, namely
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code comparator.equals(x, x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code comparator.equals(x, y)}
     *     should return {@code true} if and only if
     *     {@code comparator.equals(y, x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code comparator.equals(x, y)} returns {@code true} and
     *     {@code comparator.equals(y, z)} returns {@code true}, then
     *     {@code comparator.equals(x, z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code comaprator.equals(x, y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     *
     *
     * <p>Note that it is <b>not</b> strictly the case that if {@code a.equals(b)}
     * then {@code anyComparator.equals(a, b} must return true.
     * This allows for a useful abstraction of equals to be defined
     * from different contexts.
     *
     * <!-- TODO: example -->
     *
     * @param left the first element to determine equality for
     * @param right the second argument to determine equality for
     * @return <tt>true</tt> iff the two objects are the same from this comparators perspective.
     *
     * @see Object#equals(Object)
     */
    boolean equals(TCompared left, TCompared right);

    /**
     * Hash code corresponding to the provided equals method.
     *
     * <p>defaults to return 0 for everything, which is am acceptable behaviour
     * as defined by {@link Object#hashCode()}, but means object using this
     * <code>EqualityCopmarer</code> will not get any of the benefits of
     * an effective hashing system, such as constant time access from a
     * Hashing based map
     *
     * <!-- TODO better integrate with {@link Object#hashCode()}-->
     *
     * @param object the object to determine a hashcode for, adhereing to the
     * @return the hash code corresponding to <tt>object</tt> under this context.
     *
     * @see Object#hashCode()
     * @see System#identityHashCode(Object)
     */
    default public int hashCode(TCompared object){
        //unfortunately for us the only satisfactory way to ensure the equals <=> hashCode contract is satisfied,
        //is to render hashCode completely useless.
        //more on this in a future update, I hope. This is a tough challenge.
        return 0;
    }

    public static interface Untyped extends EqualityComparer<Object>{

        public static <TLowestEquatable> Untyped make(Class<TLowestEquatable> upperBoundType,
                                                      Func2<? super TLowestEquatable, ? super TLowestEquatable, Boolean> comparator,
                                                      Func1<? super TLowestEquatable, Integer> hasher){
            return new Untyped() {
                @Override
                public boolean equals(Object left, Object right) {
                    if ( ! upperBoundType.isInstance(left)) { return false; }
                    if ( ! upperBoundType.isInstance(right)) { return false; }

                    return comparator.getFrom(upperBoundType.cast(left), upperBoundType.cast(right));
                }

                @Override
                public int hashCode(Object object) {
                    if ( ! upperBoundType.isInstance(object)){
                        return 0;
                    }
                    else {
                        return hasher.getFrom(upperBoundType.cast(object));
                    }
                }
            };
        }

        static Untyped make(Func2<Object, Object, Boolean> comparator, Func1<Object, Integer> hasher){
            return new Untyped(){
                @Override
                public boolean equals(Object left, Object right) {
                    return comparator.getFrom(left, right);
                }

                @Override
                public int hashCode(Object toHash) {
                    return hasher.getFrom(toHash);
                }
            };
        }
    }
}

