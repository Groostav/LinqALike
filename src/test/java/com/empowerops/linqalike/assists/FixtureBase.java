package com.empowerops.linqalike.assists;

import com.empowerops.linqalike.*;
import com.empowerops.linqalike.experimental.IndexableLinqingSet;
import com.empowerops.linqalike.queries.DefaultedCollection;
import com.empowerops.linqalike.queries.UnionQuery;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.api.IterableAssert;
import org.junit.After;
import org.junit.experimental.theories.DataPoint;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Geoff on 13/10/13
 */
public abstract class FixtureBase {

    protected static final int NEVER = 0;
    protected static final int ONCE = 1;
    protected static final int TWICE = 2;
    protected static final int THRICE = 3; //couldn't resist.
    protected static final int SIX_TIMES = 6;
    protected static final int FOUR_TIMES = 4;
    protected static final int FIVE_TIMES = 5;
    protected static final int SEVEN_TIMES = 7;

    static Boolean isTestingOrderedObject = null;

    public @DataPoint static LinqingSet usingHashSet(){
        setTestingOrderedQuery();
        return new LinqingSet();
    }
    public @DataPoint static LinqingList usingList(){
        setTestingOrderedQuery();
        return new LinqingList();
    }
    public @DataPoint static DefaultedCollection usingWrapper() {
        setTestingOrderedQuery();
        return new DefaultedCollection();
    }
    public @DataPoint static ISet usingImmutableSet() {
        setTestingOrderedQuery();
        return new ISet();
    }
    public @DataPoint static IList usingImmutableList() {
        setTestingOrderedQuery();
        return new IList();
    }
    @SuppressWarnings("unchecked")
    public @DataPoint static IndexableLinqingSet usingIndexableSet(){
        setTestingOrderedQuery();
        return new IndexableLinqingSet(Object.class);
    }
    public @DataPoint static SortedLinqingSet usingSortedSet(){
        isTestingOrderedObject = false;
        return SortedLinqingSet.createFor(comparing(Object::hashCode));
    }

    private static void setTestingOrderedQuery() {
        isTestingOrderedObject = isTestingOrderedObject == null ? true : isTestingOrderedObject;
    }

    @After
    public final void reset_ordered_ness_after(){
        isTestingOrderedObject = null;
    }
    protected boolean isTestingOrderedQuery(){
        return isTestingOrderedObject;
    }

    public class SmartIterableAssert<T> extends IterableAssert<T>{

        private final Iterable<? extends T> actual;

        protected SmartIterableAssert(Iterable<? extends T> actual) {
            super(actual);

            this.actual = actual;
        }

        public AbstractIterableAssert<?, ?, T> containsSmartly(T... values){
            if(isTestingOrderedObject) {
                return assertThat(actual).containsExactly(values);
            }
            else{
                return assertThat(actual).containsOnly(values);
            }
        }
    }

    protected <T> SmartIterableAssert<T> assertQueryResult(Iterable<T> result){
        return new SmartIterableAssert<T>(result);
    }

    /**
     * This method provides indirection for immutable and mutable collections.
     * This means that, while this method appears functional, it very much is not!
     * If you pass it a java.util.collection object as TSource, that object will be modified,
     * but if you pass it an ISet or an IList, a new ISet or IList will be returned
     * containing the new elements (as per the immutable paradigm)
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected final <T extends Queryable<E>, E> T doAdd(T source, E... elements){
        if(source instanceof WritableCollection){
            ((WritableCollection) source).addAll(elements);
            return source;
        }
        else if (source instanceof ISet){
            return (T) ((ISet) source).union((Object[])elements);
        }
        else if (source instanceof IList){
            return (T) ((IList) source).with((Object[])elements);
        }
        else throw new UnsupportedOperationException();
    }

    /**
     * {@link FixtureBase#doAdd(Queryable, Object[])}
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Queryable<E>, E> T doAdd(T source, Iterable<E> elements){
        if(source instanceof WritableCollection){
            ((WritableCollection) source).addAll(elements);
            return source;
        }
        else if (source instanceof ISet){
            return (T) ((ISet) source).union(elements);
        }
        else if (source instanceof IList){
            return (T) ((IList) source).with(elements);
        }
        else throw new UnsupportedOperationException();
    }
    /**
     * {@link FixtureBase#doAdd(Queryable, Object[])}
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Queryable<?>> T doClear(T source){
        if(source instanceof WritableCollection){
            ((WritableCollection) source).clear();
            return source;
        }
        else if (source instanceof ISet){
            return (T) ISet.empty();
        }
        else if (source instanceof IList){
            return (T) IList.empty();
        }
        else throw new UnsupportedOperationException();
    }

    protected static class NamedValue {
        public String name;

        public NamedValue(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public static LinqingList<NamedValue> forNames(String... values) {
            LinqingList<NamedValue> returnable = new LinqingList<>();
            for(String value : values){
                NamedValue namedValue = new NamedValue(value);
                returnable.add(namedValue);
            }
            return returnable;
        }

        public static CountingTransform<NamedValue, String> GetName() {
            return new CountingTransform<NamedValue, String>() {
                @Override
                protected String getFromImpl(NamedValue cause) {
                    return cause.name;
                }
            };
        }

        @Override
        public String toString(){
            return "NamedValue:" + name;
        }
    }

    protected static class NumberValue{
        public int number;

        public NumberValue(int number){
            this.number = number;
        }

        public static CountingTransform<NumberValue, Integer> GetValue(){
            return new CountingTransform<NumberValue, Integer>() {
                @Override
                public Integer getFromImpl(NumberValue cause) {
                    return cause.number;
                }
            };
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("number", number)
                    .toString();
        }
    }

    protected static class EquatableValue{
        public String value;

        public EquatableValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EquatableValue)) return false;

            EquatableValue that = (EquatableValue) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "EquatableValue{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    /**
     * Method to conveniently cast the object under test to a specific type,
     * so that any further assertions or method calls are more obvious
     * (for example, if you have an UnionQuery and you call its size method,
     * {@link UnionQuery#size()}, if you statically know its a UnionQuery
     * you dont have to jump through interfaces)
     */
    //TODO: I would like to make this class generic on tthe type under test, so that this method can return
    // a more specific type (=> less error prone) of query, but in my first implementation
    // doing so casues
    // A) every existing fixute to use a raw type in its extends clause
    // B) every use of this method to cast the _raw type_ of the query to the specific type
    //     - eg CountSkipQuery to CountSkipQuery<String>
    @SuppressWarnings("unchecked") //purpose of the method
    protected <TElement, TQuery extends Queryable<TElement>>
    TQuery asTypeUnderTest(Queryable<TElement> cast){
        return (TQuery) (getTypeUnderTest() == null ? cast : getTypeUnderTest().cast(cast));
    }

    protected Class<? extends Queryable> getTypeUnderTest(){ return null; }
}
