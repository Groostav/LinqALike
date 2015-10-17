package com.empowerops.linqalike;

import com.empowerops.linqalike.delegate.Func1;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Created by Geoff on 2015-07-03.
 */
public class LinqingTreeSet<TElement> extends TreeSet<TElement> implements WritableCollection<TElement>, DefaultedQueryable<TElement>{

    private static final long serialVersionUID = - 5269998965732966028L;
    public static enum NullComparablePolicy {
        /**
         * Directive to the LinqingTreeSet to throw an {@link java.lang.IllegalArgumentException}
         * when constructed using a comparableSelector
         * and subsequently asked to include an element for which the comparableSelector returns <tt>null</tt>
         *
         * <p>For example: if we had a <code>Person</code> class with a <code>name</code> field that was of type
         * <code>String</code>, and our comparable selector was simply <code>Person::getName</code>, when a call
         * is made to insert a <code>person</code> instance with a <tt>null</tt> name the set will throw an
         * {@link java.lang.IllegalArgumentException}.
         */
        Throw,
        /**
         * Directive to the LinqingTreeSet to shuffle elements forward
         * when constructed using a comparableSelector and subsequently
         * asked to include an element for which the comparableSelector returns <tt>null</tt>
         *
         * <p>For example: if we had a <code>Person</code> class with a <code>name</code> field that was of type
         * <code>String</code>, and our comparable selector was simply <code>Person::getName</code>, when a call
         * is made to insert a <code>person</code> instance with a <tt>null</tt> name the set will place that
         * element in the front of the set.
         */
        NullsFirst,
        /**
         * Directive to the LinqingTreeSet to shuffle elements backward
         * when constructed using a comparableSelector and subsequently
         * asked to include an element for which the comparableSelector returns <tt>null</tt>
         *
         * <p>For example: if we had a <code>Person</code> class with a <code>name</code> field that was of type
         * <code>String</code>, and our comparable selector was simply <code>Person::getName</code>, when a call
         * is made to insert a <code>person</code> instance with a <tt>null</tt> name the set will place that
         * element in the back of the set.
         */
        NullsLast,;

        public int leftIsNull() {
            switch (this) {
                case NullsFirst:
                    return - 1;
                case NullsLast:
                    return + 1;
                default:
                case Throw: { throw new IllegalArgumentException("left --selector returned null"); }
            }
        }
        public int rightIsNull() {
            switch (this) {
                case NullsFirst:
                    return + 1;
                case NullsLast:
                    return - 1;
                default:
                case Throw: { throw new IllegalArgumentException("right --selector returned null"); }
            }
        }
        public int bothAreNull(){
            switch(this) {
                case NullsFirst: case NullsLast: return 0;
                default: case Throw: { throw new IllegalArgumentException("left --selector returned null"); }
            }
        }
    }


    private static <TElement, TCompared extends Comparable<TCompared>>

    Comparator<TElement> makeComparatorFor(Func1<? super TElement, TCompared> comparableSelector, NullComparablePolicy nullComparablePolicy) {
        return (left, right) -> {
            try {
                TCompared leftActual = comparableSelector.getFrom(left);
                TCompared rightActual = comparableSelector.getFrom(right);

                return leftActual != null && rightActual != null ? leftActual.compareTo(rightActual) :
                        leftActual == null && rightActual == null ? nullComparablePolicy.bothAreNull() :
                                leftActual != null ? nullComparablePolicy.rightIsNull() :
                                        nullComparablePolicy.leftIsNull();
            }
            catch(IllegalArgumentException exception){
                throw new IllegalArgumentException(
                        "error while comparing '" + left + "' to '" + right + "' with '" + comparableSelector + "'.",
                        exception
                );
            }

        };
    }

    public LinqingTreeSet() {
    }
    public LinqingTreeSet(Comparator<? super TElement> comparator) {
        super(comparator);
    }
    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector){
        this(comparableSelector,  NullComparablePolicy.Throw);
    }
    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector, NullComparablePolicy nullComparablePolicy){
        super(makeComparatorFor(comparableSelector, nullComparablePolicy));
    }
    public LinqingTreeSet(Iterable<? extends TElement> initialElements) {
        super();
        addAll(initialElements);
    }
    public LinqingTreeSet(TElement... initialElements){
        super();
        addAll(initialElements);
    }

    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector, Iterable<? extends TElement> initialElements){
        this(comparableSelector, NullComparablePolicy.Throw, initialElements);
    }
    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector, TElement... initialElements){
        this(comparableSelector, NullComparablePolicy.Throw, initialElements);
    }

    public LinqingTreeSet(Comparator<? super TElement> comparator, Iterable<? extends TElement> initialElements){
        super(comparator);
        addAll(initialElements);
    }
    public LinqingTreeSet(Comparator<? super TElement> comparator, TElement... initialElements){
        super(comparator);
        addAll(initialElements);
    }
    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector, NullComparablePolicy nullComparablePolicy, Iterable<? extends TElement> initialElements){
        super(makeComparatorFor(comparableSelector, nullComparablePolicy));
        addAll(initialElements);
    }
    public <TCompared extends Comparable<TCompared>>
    LinqingTreeSet(Func1<? super TElement, TCompared> comparableSelector, NullComparablePolicy nullComparablePolicy, TElement... initialElements){
        super(makeComparatorFor(comparableSelector, nullComparablePolicy));
        addAll(initialElements);
    }
    @Override
    public boolean removeElement(TElement toRemove) {
        return super.remove(toRemove);
    }

    @Override
    public boolean removeIf(Predicate<? super TElement> filter) {
        return super.removeIf(filter);
    }
}
