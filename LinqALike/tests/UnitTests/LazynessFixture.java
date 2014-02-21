package UnitTests;

import LinqALike.LinqBehaviour;
import LinqALike.LinqingList;
import LinqALike.Queryable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

/**
 * @author Geoff on 13/10/13
 */
public class LazynessFixture extends QueryFixtureBase {

    @Test
    public void when_running_a_select_query_it_only_invokes_delegate_when_set_is_iterated(){
        //setup
        List<String> numberValues = Arrays.asList("1", "5", "7", "0");
        CountingTransform<String, Double> transformToDouble = new CountingTransform<String, Double>() {
            @Override public Double getFromImpl(String cause) { return Double.parseDouble(cause); }
        };

        //act
        Queryable<Double> result = LinqBehaviour.select(numberValues, transformToDouble);

        //assert
        forceIterationAndAssertInvocationHappenedLazily(transformToDouble, result);
    }

    @Test
    public void when_running_a_where_query_it_only_invokes_delegate_when_set_is_iterated(){
        //setup
        List<Integer> numberValues = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        CountingCondition<Integer> greaterThanFive = new CountingCondition<Integer>() {
            @Override public boolean passesForImpl(Integer cause) { return cause > 5; }
        };
        //act
        Queryable<Integer> result = LinqBehaviour.where(numberValues, greaterThanFive);

        //assert
        forceIterationAndAssertInvocationHappenedLazily(greaterThanFive, result);
    }

    @Test
    public void when_running_a_excluding_query_it_only_invokes_delegate_when_set_is_iterated(){
        //setup
        LinqingList<NamedValue> elements = LinqingList.asList(new NamedValue("H"), new NamedValue("Hg"), new NamedValue("Li"),
                new NamedValue("Ne"), new NamedValue("Si"), new NamedValue("He"));
        List<NamedValue> scaryMetals = Arrays.asList(new NamedValue("Li"), new NamedValue("Hg"));
        CountingTransform<NamedValue, String> getName = new CountingTransform<NamedValue, String>() {
            public String getFromImpl(NamedValue cause) { return cause.name; }
        };

        //act
        Queryable<NamedValue> result = LinqBehaviour.except(elements, scaryMetals, getName);

        //assert
        forceIterationAndAssertInvocationHappenedLazily(getName, result);
    }

    @Test
    public void when_running_a_union_query_it_invokes_delegate_when_set_is_iterated(){
        //setup
        List<NamedValue> goodUDCourses = NamedValue.makeWithEach("373", "379", "479");
        List<NamedValue> badUDCourses = NamedValue.makeWithEach("307", "483");
        CountingTransform<NamedValue, String> getName = new CountingTransform<NamedValue, String>() {
            public String getFromImpl(NamedValue cause) { return cause.name; }
        };

        //act
        Queryable<NamedValue> goodAndBadResult = LinqBehaviour.union(goodUDCourses, badUDCourses, getName);

        //assert
        forceIterationAndAssertInvocationHappenedLazily(getName, goodAndBadResult);
    }

    @Test
    public void when_running_an_intersection_query_it_invokes_delegate_when_set_is_iterated(){
        //setup
        List<NamedValue> goodUDCourses = NamedValue.makeWithEach("373", "379", "479");
        List<NamedValue> hardUDCourses = NamedValue.makeWithEach("373", "379", "433");
        CountingTransform<NamedValue, String> getName = new CountingTransform<NamedValue, String>() {
            public String getFromImpl(NamedValue cause) { return cause.name; }
        };

        //act
        Queryable<NamedValue> goodAndBadResult = LinqBehaviour.intersect(goodUDCourses, hardUDCourses, getName);

        //assert
        forceIterationAndAssertInvocationHappenedLazily(getName, goodAndBadResult);
    }


    private <TResult> void forceIterationAndAssertInvocationHappenedLazily(CountingDelegate delegate,
                                                                           Queryable<TResult> result) {

        //when we're first called, the list hasn't been iterated at all,
        //the delegate should never have been invoked
        delegate.shouldHaveBeenInvoked(NEVER);

        //flatten the list, force the query to go through.
        //this is kind've the second act step, but testing lazy things is difficult.
        result.toList();

        int originalNumberOfInvocations = delegate.getNumberOfInvocations();

        //if the delegate's never invoked, then we cant really say that it was lazily invoked. Theres probably
        //something wrong with this code, but its not in the lazyness per-se. We've probably got a failing test
        //somewhere else. So direct the users attention there by flaggint this test as 'inconclusive'
        assumeTrue(originalNumberOfInvocations > 0);

        //third act, force the query to go through again.
        result.toList();
        //third assert, we should have the same performance characteristic for each call to `toList()`
        delegate.shouldHaveBeenInvoked(TWICE * originalNumberOfInvocations);
    }
}
