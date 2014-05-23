package UnitTests;

import Assists.QueryFixtureBase;
import com.EmpowerOperations.LinqALike.Common.Tuple;
import com.EmpowerOperations.LinqALike.LinqingMap;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2014-05-23.
 */
public class UsageTests extends QueryFixtureBase {

    @Test
    public void when_using_a_few_methods_on_linqing_map(){
        LinqingMap<String, NamedValue> map = new LinqingMap<>(new Tuple<>("Bob", new NamedValue("Bob")));

        NamedValue value = map.getValueFor("Bob");

        assertThat(value).isSameAs(map.first().getValue());

        assertThat(map.inverted().getValueFor(map.first().getValue())).isEqualTo("Bob");
    }
}
