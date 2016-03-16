package com.empowerops.linqalike.experimental;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class ObservableSelectFixture {

    public static class ProjectableMember{
        private static int serialID = 0;

        public int id = serialID++;
        public final StringProperty name = new SimpleStringProperty("");

        public ProjectableMember(String name){
            this.name.set(name);
        }

        @Override
        public String toString() {
            return "ProjectableMember[ id=" + id + ", name=" + name.get() + " ]";
        }
    }

    @DataPoints
    public static WritableObservableQueryable[] differentCollections(){
        return new WritableObservableQueryable[]{
                new ObservableLinqingList<>(),
                new ObservableLinqingSet<>(),
        };
    }

    @Theory
    public void when_projecting_a_simple_set_of_projectable_members_they_appear_in_projected_list(WritableObservableQueryable<ProjectableMember> allMembers){
        //setup
        allMembers.addAll(
                new ProjectableMember("A"),
                new ProjectableMember("B"),
                new ProjectableMember("C")
        );

        //act
        ObservableQueryable<String> projectedMembers = allMembers.selectObservable(x -> x.name);

        //assert
        assertThat(projectedMembers.toList()).containsExactly("A", "B", "C");
    }

    @Theory
    public void when_source_elements_are_added_after_construction_observing_list_is_updated(WritableObservableQueryable<ProjectableMember> allMembers){
        //setup
        allMembers.addAll(
                new ProjectableMember("A"),
                new ProjectableMember("B"),
                new ProjectableMember("C")
        );
        ObservableQueryable<String> projectedMembers = allMembers.selectObservable(x -> x.name);

        //act
        allMembers.add(new ProjectableMember("D"));

        //assert
        assertThat(projectedMembers.toList()).containsExactly("A", "B", "C", "D");
    }

    @Theory
    public void when_source_elements_are_removed_after_construction_observing_list_is_updated(WritableObservableQueryable<ProjectableMember> allMembers){
        //setup
        allMembers.addAll(
                new ProjectableMember("A"),
                new ProjectableMember("B"),
                new ProjectableMember("C")
        );
        ObservableQueryable<String> projectedMembers = allMembers.selectObservable(x -> x.name);

        //act
        allMembers.removeIf(member -> member.name.get().equals("B"));

        //assert
        assertThat(projectedMembers.toList()).containsExactly("A", "C");
    }

    @Test
    public void when_source_elements_are_sorted_projected_list_is_updated(){
        //setup
        ObservableLinqingList<ProjectableMember> allMembers = new ObservableLinqingList<>(
                new ProjectableMember("A"),
                new ProjectableMember("D"),
                new ProjectableMember("E"),
                new ProjectableMember("C"),
                new ProjectableMember("B")
        );
        ObservableQueryable<String> projectedMembers = allMembers.selectObservable(x -> x.name);

        //act
        allMembers.sort((left, right) -> left.name.get().compareTo(right.name.get()));

        //assert
        assertThat(projectedMembers.toList()).containsExactly("A", "B", "C", "D", "E");
    }

}
