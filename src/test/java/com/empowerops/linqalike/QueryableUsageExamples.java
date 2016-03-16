package com.empowerops.linqalike;

import com.empowerops.linqalike.experimental.IList;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.empowerops.linqalike.Factories.from;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-10-02.
 */
public class QueryableUsageExamples {

    @Test
    public void when_attempting_to_get_a_list_of_similar_customers_should_involve_no_for_loops(){
        //setup
        Customer shopOwner = new Customer("Ken", "Johnson");
        IList<Customer> customers = new IList<>(
                new Customer("Robert", "Smith"),
                new Customer("Ken", "Donaldson"),
                new Customer("Sarah", "MacKenzie"),
                new Customer("Jim", "Patriarche"),

                new Customer("Jack", "Johnson"),
                new Customer("Samantha", "Johnson"),
                new Customer("Justice", "Johnson"),

                new Customer("Sandra", "Smith")
        );

        Queryable<String> johnsons = customers
                .with(shopOwner)
                .where(cust -> cust.getLastName().equals("Johnson"))
                .select(Customer::getFirstName);

        assertThat(johnsons).containsExactly("Jack", "Samantha", "Justice", "Ken");
    }

    @Test
    public void when_youve_got_an_obnoxious_grid_you_want_to_convert_to_a_readonly_queryable(){
        //setup
        List<List<Integer>> integerGrid = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5), Arrays.asList(6));

        //act
        Queryable<Queryable<Integer>> queryableGrid = from(integerGrid).select(Factories::from);

        //assert
        assertThat(queryableGrid.first().toList())   .containsExactly(1, 2, 3);
        assertThat(queryableGrid.second().toList())  .containsExactly(4, 5);
        assertThat(queryableGrid.last().toList())    .containsExactly(6);
    }

    public static class Customer{

        private final String firstName;
        private final String lastName;

        public Customer(@Nonnull String firstName, @Nonnull String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public @Nonnull String getFirstName() {
            return firstName;
        }

        public @Nonnull String getLastName() {
            return lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Customer)) return false;

            Customer customer = (Customer) o;

            return firstName.equals(customer.firstName)
                    && lastName.equals(customer.lastName);

        }

        @Override
        public int hashCode() {
            return 31 * firstName.hashCode() + lastName.hashCode();
        }

        @Override
        public String toString() {
            return "Customer{ firstName='" + firstName + "', lastName=" + lastName + "'}";
        }
    }
}
