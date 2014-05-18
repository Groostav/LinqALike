package com.EmpowerOperations.LinqALike.Common;

import com.EmpowerOperations.LinqALike.CommonDelegates;

/**
 * Created by Geoff on 2014-05-11.
 */
public class Reference<TReferenced> {

    private final EqualityComparer<? super TReferenced> equalityComparer;
    private final Class<? super TReferenced> equatingSupertype;

    public TReferenced value;

    public Reference(){
        this.value = null;
        this.equatingSupertype = Object.class;
        this.equalityComparer = CommonDelegates.DefaultEquality;
    }

    public Reference(TReferenced initialValue){
        this.value = initialValue;
        this.equatingSupertype = Object.class;
        this.equalityComparer = CommonDelegates.DefaultEquality;
    }

    public Reference(Class<TReferenced> referencedType, EqualityComparer<TReferenced> equalityComparer){
        this.value = null;
        this.equatingSupertype = referencedType;
        this.equalityComparer = equalityComparer;
    }

    public Reference(TReferenced initialValue, EqualityComparer<TReferenced> equalityComparer){
        Preconditions.notNull(initialValue, "initialValue");
        this.value = initialValue;
        this.equalityComparer = equalityComparer;
        //noinspection unchecked
        this.equatingSupertype = (Class) initialValue.getClass();
    }

    public Reference(EqualityComparer.Untyped equalityComparer){
        this.value = null;
        this.equalityComparer = equalityComparer;
        this.equatingSupertype = Object.class;
    }

    public Reference(TReferenced initialValue, EqualityComparer.Untyped equalityComparer){
        this.value = initialValue;
        this.equalityComparer = equalityComparer;
        this.equatingSupertype = Object.class;
    }

    /**
     * A gnarly static factory to allow the creation of a <tt>Reference</tt> with a precise equals behaviour.
     *
     * <p>The problem this method is trying to solve is this: what if you want the equals to be performed
     * on the interface or superclass of its component types? We cant use the simpler
     * constructors since they will always assume equals is being done on the exact type of its composing members.
     * Thanks to type erasure we also cannot determine the type from the objects provided at run time.
     * The only solution I have thought of is to force the caller to supply the equating super-class at
     * source time.</p>
     *
     * <p>an example, illustrating the problem and how this solves it:
     * <ul>
     *     <li>You have the usual inheritance tree Vehicle, Car extends Vehicle, Truck extends Vehicle</li>
     *     <li>You have an EqualityComparator&ltVehicle&gt that returns <tt>true</tt> if the vehicle has 2 doors.</li>
     *     <li>you want to create a Tuple&gtVehicle, Vehicle&lt, with two cars, and the above equality comparator.</li>
     * </ul></p>
     *
     * <p>So your code might look like this.
     * <pre>{@code
     *  EqualityComparer<Vehicle> equatableOnDoorCount = (left, right) -> left.doors.size() == right.doors.size();
     *  Reference<Vehicle> gtrRef = new Reference<>(nessanGTR, equatableOnDoorCount); //this vehicle has 2 doors
     *  Reference<Vehicle> f150Ref = new Reference<>(fordF150, equatableOnDoorCount); //as does this one
     *
     *  boolean areEqualTuples = gtrRef.equals(f150Ref);
     * }</pre>
     * </p>
     *
     * <p>we would expect <code>areEqualTuples</code> to be <tt>true</tt>, but thanks to the subtleties of equals,
     * its actually compile-time error (or <tt>false</tt> or a ClassCastException). Consider the code
     * <pre>{@code
     *  EqualityComparer<Vehicle> equatableOnDoorCount = (left, right) -> left.doors.size() == right.doors.size();
     *  Reference<Vehicle> gtrRef = new Reference<>(nessanGTR, equatableOnDoorCount); //this vehicle has 2 doors
     *  Reference<String> stringRef = new Reference<>("oops"); // no doors at all on this "car"...
     *
     *  boolean areEqualTuples = gtrRef.equals(stringRef);
     * }</pre></p>
     *
     * <p>the only way this library can detect that the equals call in the latter is using an illegally typed argument
     * and the equals call in the former is using a perfectly legitimate argument is if you supply us with the run-time
     * type of the objects accepted by your typed equality comparer. In other words, the only way to avoid casting a
     * String to a Vehicle (so that it can be used as the <tt>right</tt> argument for the lambda equality comparer)
     * is if you provide this class with enough information so it can avoid that cast at runtime.</p>
     *
     * @param initalValue the initial value to set the reference to. Can be null.
     * @param equatedSuperType the type that equality is performed on.
     * @param equalityComparer the typed equality comparer to use. All comparisons to types not allowed by this type
     *                         comparer will evaluate to <tt>false</tt>.
     * @param <TEquated> the static type that equality is performed on.
     * @param <TReferenced> the static type of the object being referenced by this object
     * @return a <tt>Reference</tt> to the <tt>initialValue</tt>
     */
    public static <TEquated, TReferenced extends TEquated>
    Reference<TReferenced> withSpecificEquals(TReferenced initalValue, Class<TEquated> equatedSuperType, EqualityComparer<TEquated> equalityComparer){
        return new Reference<>(initalValue, equatedSuperType, equalityComparer);
    }
    public static <TEquated, TReferenced extends TEquated>
    Reference<TReferenced> withSpecificEquals(Class<TEquated> equatedSuperType, EqualityComparer<TEquated> equalityComparer){
        return new Reference<>(null, equatedSuperType, equalityComparer);
    }

    private Reference(TReferenced initialValue, Class<? super TReferenced> equatingSupertype, EqualityComparer<? super TReferenced> equalityComparer){
        this.value = initialValue;
        this.equalityComparer = equalityComparer;
        this.equatingSupertype = equatingSupertype;
    }

    @Override
    public int hashCode() {
        return equalityComparer.hashCode(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (! (other instanceof Reference)) { return false; }

        Reference otherRef = (Reference) other;
        if(otherRef.value != null && this.value != null
                && ! equatingSupertype.isInstance(otherRef.value)){
            // type miss-match on the arguments.
            // Something like thisIntRef.equals(otherStringRef) has happened.
            return false;
        }

        //noinspection UnnecessaryLocalVariable
        Reference<TReferenced> typedOtherRef = otherRef;

        return equalityComparer.equals(value, typedOtherRef.value);
    }
}