package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.WritableCollection;
import com.empowerops.linqalike.delegate.Action1;
import com.sun.javafx.application.PlatformImpl;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.core.api.ListAssert;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import com.empowerops.linqalike.Factories;

import java.util.List;
import java.util.Set;

import static com.empowerops.linqalike.experimental.LinqFXFixture.Customer.*;

@RunWith(Theories.class)
public class LinqFXFixture{

    // @formatter:off

    static{
        PlatformImpl.startup(() -> {});
    }

    @DataPoint public static WritableObservableQueryable<Customer> list(){ return new ObservableLinqingList<>(createAdult("Adam")); }
    @DataPoint public static WritableObservableQueryable<Customer> set(){ return new ObservableLinqingSet<>(createAdult("Adam"));}
    @DataPoint public static WritableObservableQueryable<Customer> emptyList(){ return new ObservableLinqingList<>(); }
    @DataPoint public static WritableObservableQueryable<Customer> emptySet(){ return new ObservableLinqingSet<>(); }
    @DataPoint public static WritableObservableQueryable<Customer> bigList(){ return new ObservableLinqingSet<>(createSenior("Marnie"), createAdult("Henrick"), createAdult("Daniel"), createChild("Sidney")); }
    @DataPoint public static WritableObservableQueryable<Customer> bigSet(){ return new ObservableLinqingSet<>(createSenior("Marnie"), createAdult("Henrick"), createAdult("Daniel"), createChild("Sidney")); }

    @DataPoint public static final Action1<WritableCollection<Customer>> addChild         = source -> source.add(createChild("Mike"));
    @DataPoint public static final Action1<WritableCollection<Customer>> addAllSeniors    = source -> source.addAll(createSenior("Billy"), createSenior("Jackson"));
    @DataPoint public static final Action1<WritableCollection<Customer>> remove           = source -> source.removeElement(forEquality("Adam"));
    @DataPoint public static final Action1<WritableCollection<Customer>> addAllThenRemove = source -> { source.addAll(createSenior("Johnson")); source.removeElement(forEquality("Adam")); };
    @DataPoint public static final Action1<WritableCollection<Customer>> retainAll        = source -> source.retainAll(Factories.from(createSenior("Adam")));
    @DataPoint public static final Action1<WritableCollection<Customer>> clear            = WritableCollection::clear;
    @DataPoint public static final Action1<WritableCollection<Customer>> addThenSet       = source -> {
        source.addAll(createSenior("Johnson"), createAdult("Michael"), createChild("Bobby"));
        if(source instanceof List){ ((List<Customer>)source).set(1, createAdult("Justice"));}
        else{ /* not a list, so no Set() method is available, so just add */ source.add(createAdult("Justice")); }
    };


    // @formatter:on

    @Theory
    public void when_binding_two_collections_together_they_are_initially_identical(
            WritableObservableQueryable<Customer> sourceCustomers,
            WritableObservableQueryable<Customer> destinationCustomers) {

        //act
        destinationCustomers.bindToContentOf(sourceCustomers);

        //assert
        assertThat(sourceCustomers, destinationCustomers).containsAppropriately(sourceCustomers);
    }

    @Theory
    public void when_source_bound_list_is_modified_that_change_surfaces_correspondingly_in_any_derrived_collections(
            WritableObservableQueryable<Customer> sourceCustomers,
            WritableObservableQueryable<Customer> destinationCustomers,
            Action1<WritableCollection<Customer>> mutationToTest) {

        //setup
        destinationCustomers.bindToContentOf(sourceCustomers);

        //act
        mutationToTest.doUsing(sourceCustomers);

        //assert
        assertThat(sourceCustomers, destinationCustomers).containsAppropriately(sourceCustomers);
    }

    public static class Customer {

        private final String  name;
        private final boolean isSenior;
        private final Customer child;

        public static Customer createChild(String name){
            return new Customer(name, false, null);
        }
        public static Customer forEquality(String name) {
            return new Customer(name, false, null);
        }
        public static Customer createSenior(String name, Customer... child){
            assert child.length == 0 || child.length == 1;
            return new Customer(name, true, child.length == 0 ? null : child[0]);
        }
        public static Customer createAdult(String name, Customer... child){
            assert child.length == 0 || child.length == 1;
            return new Customer(name, false, child.length == 0 ? null : child[0]);
        }

        private Customer(String name, boolean isSenior, Customer child) {
            this.name = name;
            this.isSenior = isSenior;
            this.child = child;
        }

        public boolean isSenior() {
            return isSenior;
        }

        public Customer child() {
            return child;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            Customer customer = (Customer) o;

            if (! name.equals(customer.name)) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override public String toString() {
            return new ToStringBuilder(this)
                    .append("name", name)
                    .append("isSenior", isSenior)
                    .append("child", child)
                    .toString();
        }
    }

    private static class AppropriateCollectionAssert extends ListAssert {

        private final Queryable<?> source;
        private final WritableCollection<?> actual;

        public AppropriateCollectionAssert(Queryable<?> source, WritableCollection<?> actual) {
            super(actual.toList());
            this.source = source;
            this.actual = actual;
        }

        public AbstractIterableAssert containsAppropriately(Queryable<Customer> expected){
            if(actual instanceof Set || source instanceof Set){
                return containsOnly(expected.toArray());
            }
            else if (actual instanceof List && source instanceof List){
                return containsExactly(expected.toArray());
            }
            else{
                throw new UnsupportedOperationException();
            }
        }
    }

    private AppropriateCollectionAssert assertThat(Queryable<Customer> source, WritableObservableQueryable<Customer> actual) {
        return new AppropriateCollectionAssert(source, actual);
    }

}
