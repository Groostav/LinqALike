package UsageExamples;

import LinqALike.Factories;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import LinqALike.QueryableMultiMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class SimpleExamples {

    /**
     * Below is a simple example of why you would want to use Linq-A-Like, and not streams
     * or a for-each loop. This example does not get too technical. Please see the {@link ArchetectureExamples}
     * for more detailed differences between Linq-A-Like and the Java-8 {@link Stream}s API.
     */
    @Test
    public void using_where_reduces_code_waste(){

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

        //here we're creating some simple sample data. Neither Linq nor Streams
        //is limited to such small data-sets.
        LinqingList<CustomerWithAge> sampleData = Factories.asList(
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

        // - result availability,   Streams are difficult to convert to the familiar List and Map forms.
        //                          Queryable's have convenient toList(), toMap(), and toArray(). Wherever
        //                          possible, Linq-A-Like integrates with the existing Collections framework,
        //                          where the Streams API remains largely separate.
        // - memory profile,        Linq (and thus Linq-A-Like) is lazy. This means that while Streams
        //                          will execute the delegates as they're supplied, Linq-A-Like will simple enqueue
        //                          those delegates, and will only execute them when the query is actually iterated.
        // - syntax and             Linq is more familiar to those from a SQL, C# and .net backgrounds than Streams.
        //   familiarity            A Queryable supports select, where, join, groupBy, etc. If you're a Linq
        //                          addict looking for something familiar from Java, Linq-A-Like is for you.
        //                          Streams support the more mathy-friendly Filter, Map, and Collect operations.
        //                          Though the two accomplish the same functions, the SQL like methods are simpler.
        // - Interface Size         The Linq-A-Like Interface classes contain a lot of methods. Many of them are
        //                          over-loads, but there are more unique methods on the Linq-A-Like interfaces
        //                          than there are on the Streams objects. This means programmers can rely more on
        //                          more specific methods, rather than generic methods with static method helpers,
        //                          making Linq-A-Like more friendlier to Object-Oriented developers.
        // - data latency           The lazy nature of Linq means that the resulting Queryable is best
        //                          described as a view into the list the query came from. This is very
        //                          powerful as it means you can pass references of a read-only view into
        //                          a list around. When that list is updated, the data "appears" on the view.

        //This makes Linq more concise, easier to use and discover, and more useful than Streams.
    }


    //model class for the next example:
    static class Person{

        enum Sex {MALE, FEMALE}

        private final String name;
        private final Sex gender;

        public Person(String name, Sex gender){
            this.name = name;
            this.gender = gender;
        }

        public String getName(){
            return name;
        }

        public Sex getGender(){
            return gender;
        }
    }

    /**
     * Below is a demonstration of how Linq-A-Like's wider interface can simplify
     * a consumer's code. Here we're going to use an explicit method on linq, {@link Queryable#groupBy},
     * which can be done with the {@link java.util.stream.Collector} in a more complex mannor.
     */
    @Test
    public void increased_interface_size_results_in_simpler_calls(){

        //one of the reasons Linq-A-Like can be simpler to use than Streams is because it has
        //more methods available on its base interface, "Queryable". This does occasionally make implementing
        //the Queryable interface more cumbersome, but it does so in exchange for simpler consumption models.

        //given a data-set that looks like this:
        LinqingList<Person> roster = Factories.asList(
                new Person("Sue", Person.Sex.FEMALE), new Person("Bob", Person.Sex.MALE), new Person("Alice", Person.Sex.FEMALE),
                new Person("Eve", Person.Sex.FEMALE), new Person("Chris", Person.Sex.MALE), new Person("Chris", Person.Sex.MALE));

        //Lets say we want to do a grouping. Lets say we want the names of each person grouped by their sex.
        //so we want the map:
        // key      | names of that sex
        // MALE     | {"Bob", "Chris", "Chris"}
        // FEMALE   | {"Sue", "Alice", "Eve" }

        //from Oracle's usage examples
        // (here: http://docs.oracle.com/javase/tutorial/collections/streams/reduction.html)
        //we have this heavily-indented masterpiece:
        Map<Person.Sex, List<String>> namesByGenderFromStreams =
                roster
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        Person::getGender,
                                        Collectors.mapping(
                                                Person::getName,
                                                Collectors.toList())));

        //with a slightly more conservative indentation scheme, and liberal use of import-static:
        Map<Person.Sex, List<String>> namesByGenderFromStreams2 =
                roster.stream()
                      .collect(groupingBy(Person::getGender,
                               mapping(Person::getName, toList())));

        //with a (couple of) for-each loops:
        Map<Person.Sex, List<String>> namesByGender = new LinkedHashMap<>();

        for(Person.Sex gender : Person.Sex.values()){
            namesByGender.put(gender, new ArrayList<>());
        }

        for(Person person : roster){
            namesByGender.get(person.getGender()).add(person.getName());
        }

        //but if we use linq, we get:
        Queryable<Queryable<String>> namesByGenderFromLinq =
                roster.groupBy(Person::getGender)
                      .selectFromGroups(Person::getName);

        // You'll notice there isn't actually that much in code-savings,
        // but look at the nested-brackets and the use of static methods:
        //      - Linq-A-Like has no static method uses and no nested brackets
        //      - Streams has 3 static method calls and 3 nested brackets (3 nested calls).
        //
        // To an object oriented programmer whose used to "dot-tab programming"
        // (using an IDE's built-in method lookup functionality, and selecting
        // "that one" from a list of methods) Linq-A-Like is drastically easier to use.
    }
}
