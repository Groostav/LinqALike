package UsageExamples;

import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleExamples {

    @Test
    /**
     * Below is a simple example of why you would want to use Linq-A-Like, and not streams
     * or a for-each loop. This example does not get too technical please see the {@link ArchetectureExamples}
     * for more detailed differences between Linq-A-Like and the Java-8 {@link Stream}s API.
     */
    public void why_you_would_want_to_use_a_where_call(){

        //Lets say you've got a nice simple customer class
        //that looks something like this.
        class CustomerWithAge {
            private final int age;

            public CustomerWithAge(int age){
                this.age = age;
            }

            public boolean CanClaimSeniorsDiscount(){
                return age >= 60;
            }
        }
        //Though you may not have a model this simple, you probably have a model
        //thats got some boolean properties/getters on it.

        //here we're creating some simple sample data. Neither Linq nor the Streams API
        //is limited to such small data-sets.
        LinqingList<CustomerWithAge> sampleData = LinqingList.from(
                new CustomerWithAge(20), new CustomerWithAge(34),
                new CustomerWithAge(66), new CustomerWithAge(72),
                new CustomerWithAge(45)
        );

        //now lets say we want to get the sub-set of customers who can claim a seniors discount.

        //using Linq
        Queryable<CustomerWithAge> seniorsViaLinq = sampleData.where(customer -> customer.CanClaimSeniorsDiscount());

        //using Filters
        Stream<CustomerWithAge> seniorsViaFilter = sampleData.stream().filter(customer -> customer.CanClaimSeniorsDiscount());

        //using for-each loop
        List<CustomerWithAge> seniorsViaLoop = new ArrayList<>();
        for(CustomerWithAge customer : sampleData){
            if(customer.CanClaimSeniorsDiscount()){
                seniorsViaLoop.add(customer);
            }
        }

        //we can see that both the new Streams API and Linq-A-Like provide a much more
        //concise means of filtering that data than the old-school for-each loop.

        //but which is better, Linq-A-Like or Streams?
        //there are a number of key architectural differences
        //that broadly break down as:

        // - result availability,   Streams are difficult to convert to the familiar List and Map forms
        //                          Queryable's have convienient  toList(), toMap(), and toArray(). Wherever
        //                          possible, Linq-A-Like integrates with the existing Collections framework,
        //                          where the Streams API remains largely separate.
        // - memory profile,        Linq (and thus Linq-a-like) is lazy. This means that while streams
        //                          will execute the delegates as they're supplied, linq will simple enqueue
        //                          them, and will only execute them when the query is actually iterated.
        // - syntax and             Linq is more familar to those from a SQL, C# and .net backgrounds.
        //   familiarity            A Queryable supports Select, Where, Join, GroupBy, etc. If youre a Linq
        //                          addict looking for something familiar from Java, Linq-A-Like is for you.
        //                          A Stream supports the more mathy Filter, Map, and Collect concepts.
        // - data latency           The lazy nature of Linq means that the resulting queryable is best
        //                          described as a view into the list the query came from. This is very
        //                          powerful as it means you can pass references of a read-only view into
        //                          a list around. When that list is updated, the data "appears" on the view.

        // this undoutably makes Linq both more sophisticated than streams and more complex. If you use this
        // library, you will likely have to solve bugs around the delegate-queing nature of linq. That being
        // said, if you overcome those bugs, Linq will offer a variety of features that can be very difficult
        // to emulate with the Streams API.
    }

}
