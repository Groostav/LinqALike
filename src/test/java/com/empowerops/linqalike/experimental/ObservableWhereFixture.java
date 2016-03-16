package com.empowerops.common.linqalike.experimental;

import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.assists.CountRange;
import com.empowerops.linqalike.common.Formatting;
import com.empowerops.linqalike.experimental.ObservableLinqingList;
import com.empowerops.linqalike.experimental.ObservableLinqingSet;
import com.empowerops.linqalike.experimental.ObservableQueryable;
import com.empowerops.linqalike.experimental.WritableObservableQueryable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static com.empowerops.linqalike.Factories.from;
import static org.junit.Assert.fail;

/**
 * Created by Geoff on 2014-08-11.
 */
@RunWith(Theories.class)
public class ObservableWhereFixture {

    private boolean wasInvalidated;
    private final InvalidationListener invalidationListener = invalidationSource -> wasInvalidated = true;

    @After
    public void resetInvalidation(){
        wasInvalidated = false;
    }

    private static class PredicatedMember{
        public String name;

        public final BooleanProperty predicate = new SimpleBooleanProperty(true);
        public PredicatedMember(String name, boolean initiallyPassesPredicate){
            this.name = name;
            this.predicate.set(initiallyPassesPredicate);
        }

        @Override
        public String toString() {
            return "PredicatedMember[name=" + name + ", isMember=" + predicate.get()+ "]";
        }
    }

    private void assertInitiallyLooksLike(ObservableQueryable<PredicatedMember> actualMembers, Queryable<String> expectedMembers) {
        Queryable<String> actualMemberNames = actualMembers.select(elem -> elem.name);
        if ( ! actualMemberNames.sequenceEquals(expectedMembers)){
             throw new IllegalStateException(
                     "expected:\n\t" +
                     Formatting.verticallyPrintMembers(expectedMembers) +
                     "but got:\n\t" +
                     Formatting.verticallyPrintMembers(actualMembers.select(member -> member.name)));
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
    public void when_member_flips_status_it_invalidates_and_appears_in_result(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        PredicatedMember memberFlippingStatus;
        allMembers.addAll(
                                       new PredicatedMember("A", true),
                                       new PredicatedMember("B", false),
                memberFlippingStatus = new PredicatedMember("C", false),
                                       new PredicatedMember("D", true),
                                       new PredicatedMember("E", false)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "D"));

        filteredMembers.addListener(invalidationListener);

        //act
        memberFlippingStatus.predicate.set(true);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "A", "C", "D");
    }

    @Theory
    public void when_first_member_flips_status_it_invalidates_and_appears_in_result(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        PredicatedMember memberFlippingStatus;
        allMembers.addAll(
                memberFlippingStatus =  new PredicatedMember("A", false),
                                        new PredicatedMember("B", false),
                                        new PredicatedMember("C", false),
                                        new PredicatedMember("D", true),
                                        new PredicatedMember("E", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("D", "E"));
        filteredMembers.addListener(invalidationListener);

        //act
        memberFlippingStatus.predicate.set(true);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "A", "D", "E");
    }

    @Theory
    public void when_last_member_flips_status_to_be_false_it_invalidates_and_disappears_from_result(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        PredicatedMember memberFlippingStatus;
        allMembers.addAll(
                                        new PredicatedMember("A", true),
                                        new PredicatedMember("B", false),
                                        new PredicatedMember("C", true),
                                        new PredicatedMember("D", true),
                memberFlippingStatus =  new PredicatedMember("E", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "C", "D", "E"));

        filteredMembers.addListener(invalidationListener);

        //act
        memberFlippingStatus.predicate.set(false);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "A", "C", "D");
    }

    @Theory
    public void when_the_single_non_passing_member_flips_to_become_a_member_the_sourceList_and_destionationLists_are_sequence_equals(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        PredicatedMember memberFlippingStatus;
        allMembers.addAll(
                                       new PredicatedMember("A", true),
                memberFlippingStatus = new PredicatedMember("B", false),
                                       new PredicatedMember("C", true),
                                       new PredicatedMember("D", true),
                                       new PredicatedMember("E", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "C", "D", "E"));

        filteredMembers.addListener(invalidationListener);

        //act
        memberFlippingStatus.predicate.set(true);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "A", "B", "C", "D", "E");
    }

    @Theory
    public void when_a_new_member_is_added_that_passes_its_predicate_its_automatically_added_to_the_filtered_list(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        allMembers.addAll(
                new PredicatedMember("A", true),
                new PredicatedMember("B", false),
                new PredicatedMember("C", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "C"));
        PredicatedMember newMember = new PredicatedMember("D", true);
        filteredMembers.addListener(invalidationListener);

        //act
        allMembers.add(newMember);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "A", "C", "D");
    }

    @Theory
    public void when_member_is_removed_from_source_list_it_is_removed_from_filtered_lists_also(WritableObservableQueryable<PredicatedMember> allMembers){
        //setup
        PredicatedMember memberToDelete;
        allMembers.addAll(
                memberToDelete = new PredicatedMember("A", true),
                                 new PredicatedMember("B", false),
                                 new PredicatedMember("C", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "C"));
        filteredMembers.addListener(invalidationListener);

        //act
        allMembers.removeElement(memberToDelete);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        assertContains(allMembers, memberNames, "C");
    }

    @Test
    public void when_a_new_member_is_added_at_a_specific_index_that_passes_its_predicate_its_automatically_added_to_the_filtered_list(){
        //setup
        ObservableLinqingList<PredicatedMember> allMembers = new ObservableLinqingList<>(
                new PredicatedMember("A", false),
                // new member added here, index = 1.
                new PredicatedMember("C", true),
                new PredicatedMember("D", false)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("C"));
        PredicatedMember newMember = new PredicatedMember("B", true);
        filteredMembers.addListener(invalidationListener);

        //act
        allMembers.add(1, newMember);

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        Assertions.assertThat(memberNames).containsExactly("B", "C");
    }


    @Test
    public void when_sorting_a_list_the_permutation_is_exposed_in_filtered_list(){
        //setup
        ObservableLinqingList<PredicatedMember> allMembers = new ObservableLinqingList<>(
                new PredicatedMember("A", true),
                new PredicatedMember("C", false),
                new PredicatedMember("E", true),
                new PredicatedMember("D", true),
                new PredicatedMember("B", false)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("A", "E", "D"));
        filteredMembers.addListener(invalidationListener);

        //act
        allMembers.sort((left, right) -> left.name.compareTo(right.name));

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        Assertions.assertThat(memberNames).containsExactly("A", "D", "E");
    }

    @Test
    public void when_sorting_a_subsection_of_the_list_it_maintains_its_order(){
        //setup
        PredicatedMember firstSortedMember;
        PredicatedMember lastSortedMember;
        ObservableLinqingList<PredicatedMember> allMembers = new ObservableLinqingList<>(
                                    new PredicatedMember("F-NotSorted", true),
                                    new PredicatedMember("C-NotSorted", false),
                firstSortedMember = new PredicatedMember("A-Sorted", true),
                                    new PredicatedMember("D-Sorted", true),
                                    new PredicatedMember("B-Sorted", false),
                                    new PredicatedMember("E-Sorted", true),
                 lastSortedMember = new PredicatedMember("C-Sorted", true)
        );

        ObservableQueryable<PredicatedMember> filteredMembers = allMembers.whereObservable(elem -> elem.predicate);
        assertInitiallyLooksLike(filteredMembers, from("F-NotSorted", "A-Sorted", "D-Sorted", "E-Sorted", "C-Sorted"));
        filteredMembers.addListener(invalidationListener);
        CountRange rangeToSublist = new CountRange(allMembers.indexOf(firstSortedMember), allMembers.indexOf(lastSortedMember));
        List<PredicatedMember> sectionToSort = rangeToSublist.subList(allMembers);

        //act
        sectionToSort.sort((left, right) -> left.name.compareTo(right.name));

        //assert
        Assertions.assertThat(wasInvalidated).isTrue();
        List<String> memberNames = filteredMembers.select(member -> member.name).toList();
        Assertions.assertThat(memberNames).containsExactly("F-NotSorted", "A-Sorted", "C-Sorted", "D-Sorted", "E-Sorted");
    }

    /**
     * Kloogy way to assert that one collection that is <i>derrived</i> via 'where'
     * from another WritableObservableCollection contains the specified members.
     *
     * <p>The problem this solves specifically is that sets dont maintain order where lists do,
     * we're testing we sets we want to use 'containsOnly' and when we're testing
     * lists we want to use 'containsExactly'</p>
     */
    private <TElement> void assertContains(WritableObservableQueryable<PredicatedMember> source,
                                           List<TElement> actual,
                                           TElement... expected){
        if(source instanceof ObservableLinqingList){
            Assertions.assertThat(actual).containsExactly(expected);
        }
        else if (source instanceof ObservableLinqingSet){
            Assertions.assertThat(actual).containsOnly(expected);
        }
        else{
            fail("unknown type?");
        }
    }
}

