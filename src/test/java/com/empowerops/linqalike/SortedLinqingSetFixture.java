package com.empowerops.linqalike;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SortedLinqingSetFixture {

    public static class NamedValue{
        public final String name;
        public final int version;

        public NamedValue(String name, int version) {
            this.name = name;
            this.version = version;
        }
    }

    @Test
    public void when_getting_a_symbol_from_a_sorted_set_should_find_appropriate_element(){
        //setup
        SortedLinqingSet<NamedValue> values = SortedLinqingSet.createSortedSetFor((left, right) -> left.name.compareTo(right.name));
        values.addAll(new NamedValue("A", 1), new NamedValue("B", 2));

        //act
        NamedValue target = new NamedValue("A", 2);
        //since we compare by name only, (A, 1) and (A, 2) will compare to be identical
        NamedValue result = values.get(target);

        //assert
        assertThat(target.name).isEqualTo(result.name);
        assertThat(result).isSameAs(values.first());
        assertThat(result).isNotSameAs(target);
    }
}
