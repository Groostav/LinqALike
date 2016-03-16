package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.common.SetIsEmptyException;
import org.junit.Test;

import static com.empowerops.linqalike.assists.Exceptions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2014-08-09.
 */
public class ObservableLinkedListFixture {

    @Test
    public void when_empty_list_is_constructed_it_should_be_sensible(){
        //act
        ObservableLinkedLinqingList<Integer> emptyList = new ObservableLinkedLinqingList<>();

        //assert
        assertThat(emptyList).hasSize(0);
        assertThrows(SetIsEmptyException.class, emptyList::first);
    }

    @Test
    public void when_adding_element_to_list_the_element_should_appear(){
        //setup
        ObservableLinkedLinqingList<Integer> list = new ObservableLinkedLinqingList<>();

        //act
        list.add(42);

        //asser
        assertThat(list).hasSize(1);
        assertThat(list.first()).isEqualTo(42);
        assertThat(list.get(0)).isEqualTo(42);
    }

    @Test
    public void when_adding_multiple_elements_to_list_elements_should_appear(){
        //setup
        //setup
        ObservableLinkedLinqingList<Integer> list = new ObservableLinkedLinqingList<>();

        //act
        list.add(1);
        list.add(2);

        //asser
        assertThat(list).hasSize(2);
        assertThat(list.first()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(2);
    }

    @Test
    public void when_constructed_elements_should_be_added_appropriately(){
        //setup
        ObservableLinkedLinqingList<Integer> linkedList = new ObservableLinkedLinqingList<>(1, 2, 3, 4);

        //act
        int firstByIndex = linkedList.get(0);
        int first = linkedList.first();
        int atIndex2 = linkedList.get(2);
        int last = linkedList.last();

        //assert
        assertThat(linkedList.size()).isEqualTo(4);
        assertThat(firstByIndex).isEqualTo(1);
        assertThat(first).isEqualTo(1);
        assertThat(atIndex2).isEqualTo(3);
        assertThat(last).isEqualTo(4);
    }

    @Test
    public void when_removing_down_to_empty_list(){
        //setup
        ObservableLinkedLinqingList<Double> initiallyFullList = new ObservableLinkedLinqingList<>(1.0, 2.0);

        //act
        initiallyFullList.remove(1.0);
        initiallyFullList.remove(2.0);

        //assert
        assertThat(initiallyFullList.isEmpty());
        assertThat(initiallyFullList.size() == 0);
        assertThrows(SetIsEmptyException.class, initiallyFullList::first);
    }

    @Test
    public void when_removing_the_last_element_list_should_be_one_shorter(){
        //setup
        ObservableLinkedLinqingList<String> pair = new ObservableLinkedLinqingList<>("first", "second");

        //act
        pair.remove("second");

        //assert
        assertThat(pair).hasSize(1);
        assertThat(pair.single()).isEqualTo("first");
    }

    @Test
    public void when_removing_the_first_element_the_list_should_be_one_shorted(){
        //setup
        ObservableLinkedLinqingList<String> pair = new ObservableLinkedLinqingList<>("first", "second");

        //act
        pair.remove("first");

        //assert
        assertThat(pair).hasSize(1);
        assertThat(pair.single()).isEqualTo("second");
    }

    @Test
    public void when_setting_element_in_multi_element_set(){
        //setup
        ObservableLinkedLinqingList<String> elements = new ObservableLinkedLinqingList<>("first", "second", "third", "fourth");

        //act
        elements.set(2, "third_alternative");

        //assert
        assertThat(elements.get(2)).isEqualTo("third_alternative");
        assertThat(elements.reversed().skip(1).first()).isEqualTo("third_alternative");
    }

    @Test
    public void when_setting_first_element_in_multi_element_set(){
        //setup
        ObservableLinkedLinqingList<String> elements = new ObservableLinkedLinqingList<>("first", "second", "third", "fourth");

        //act
        elements.set(0, "first_alternative");

        //assert
        assertThat(elements.get(0)).isEqualTo("first_alternative");
        assertThat(elements.reversed().last()).isEqualTo("first_alternative");
    }

    @Test
    public void when_setting_last_element_in_multi_element_set(){
        //setup
        ObservableLinkedLinqingList<String> elements = new ObservableLinkedLinqingList<>("first", "second", "third", "fourth");

        //act
        elements.set(3, "last");

        //assert
        assertThat(elements.get(3)).isEqualTo("last");
        assertThat(elements.last()).isEqualTo("last");
    }

    @Test
    public void when_setting_element_in_single_element_set(){
        //setup
        ObservableLinkedLinqingList<String> singleElement = new ObservableLinkedLinqingList<>("single");

        //act
        singleElement.set(0, "first");

        //assert
        assertThat(singleElement.get(0)).isEqualTo("first");
        assertThat(singleElement.single()).isEqualTo("first");
    }

    private static class ExampleView{

    }

    private static class ExampleController implements ObservableLinkedLinqingList.Nodable<ExampleController> {

        public ExampleView                                                view;
        public ObservableLinkedLinqingList.ElementNode<ExampleController> node;

        @Override
        public void setLinkedListNode(ObservableLinkedLinqingList.ElementNode<ExampleController> node) {
            this.node = node;
        }
    }

    @Test
    public void when_adding_elements_to_a_list_that_are_node_aware_node_should_be_set() {
        //setup
        ObservableLinkedLinqingList<ExampleController> list = new ObservableLinkedLinqingList<>();
        ExampleController firstAddition = new ExampleController();

        //act
        list.add(firstAddition);

        //assert
        assertThat(firstAddition.node).isNotNull();
        assertThat(firstAddition.node).isEqualTo(list.nodeFor(firstAddition));
    }

}
