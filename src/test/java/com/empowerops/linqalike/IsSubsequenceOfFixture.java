package com.empowerops.linqalike;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class IsSubsequenceOfFixture{


    @Test
    public void when_asking_if_simple_sequence_is_the_subsequence_of_a_larger_supersequence_result_should_be_true(){
        //setup
        Queryable<String> midAlphabet = new LinqingList<>("C", "D", "E");
        Queryable<String> largerAlphabet = new LinqingList<>("A", "B", "C", "D", "E", "F", "G");

        //act
        boolean result = midAlphabet.isSubsequenceOf(largerAlphabet);

        //assert
        assertThat(result).describedAs("midAlphabet is subsequence of largerAlphabet").isTrue();
    }

    @Test
    public void when_asking_if_a_sparse_sequence_is_the_subsequence_of_a_larger_supersequence_result_should_be_true(){
        //setup
        Queryable<String> midAlphabet = new LinqingList<>("C", "E", "G");
        Queryable<String> largerAlphabet = new LinqingList<>("A", "B", "C", "D", "E", "F", "G");

        //act
        boolean result = midAlphabet.isSubsequenceOf(largerAlphabet);

        //assert
        assertThat(result).describedAs("odd midAlphabet is subsequence of largerAlphabet").isTrue();
    }

    @Test
    public void when_asking_if_huge_duplicate_sequence_is_subsequence_of_another_larger_squence_should_be_true(){
        //setup
        Queryable<Integer> coupleOnes = new LinqingList<>(1, 1, 1, 1, 1, 1, 1, 1);
        Queryable<Integer> lotsOfOnes = new LinqingList<>(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        //act
        boolean result = coupleOnes.isSubsequenceOf(lotsOfOnes);

        //assert
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_if_non_subsequence_is_a_subsequence_of_a_non_supersequence_result_should_be_false(){
        //setup
        Queryable<String> disjointGroup = new LinqingList<>("Mark Messier", "Pavel Datsyuk");
        Queryable<String> canucks = new LinqingList<>("Hendrik Sedin", "Daniel Sedin", "Pavel Bure", "Ryan Miller");

        //act
        boolean result = disjointGroup.isSubsequenceOf(canucks);

        //assert
        assertThat(result).isFalse();
    }

    @Test
    public void when_asking_if_a_would_be_subsequence_except_for_its_duplicate_is_the_subsequence_of_another_should_get_false(){
        //setup
        Queryable<Character> grades = new LinqingList<>('c', 'c');
        Queryable<Character> alphabet = new LinqingList<>('c', 'd');

        //act
        boolean results = grades.isSubsequenceOf(alphabet);

        //assert
        assertThat(results).isFalse();
    }

    @Test
    public void when_asking_if_a_would_be_subsequence_except_for_its_duplicate_is_the_subsequence_of_another_long_seq_should_get_false(){
        //setup
        Queryable<Character> grades = new LinqingList<>('b', 'c', 'c');
        Queryable<Character> alphabet = new LinqingList<>('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');

        //act
        boolean results = grades.isSubsequenceOf(alphabet);

        //assert
        assertThat(results).isFalse();
    }

    @Test
    public void when_asking_if_empty_set_is_subsequence_of_another_seq_should_get_true(){
        //setup
        Queryable<String> empty = new LinqingList<>();
        Queryable<String> full = new LinqingList<>("A-F", "G-H");

        //act
        boolean result = empty.isSubsequenceOf(full);

        //asser
        assertThat(result).isTrue();
    }

    @Test
    public void when_asking_empty_set_if_its_a_subsequence_of_empty_set_should_get_true(){
        //setup
        Queryable<String> empty = new LinqingList<>();
        Queryable<String> anotherEmpty = new LinqingList<>();

        //act
        boolean result = empty.isSubsequenceOf(anotherEmpty);

        //asser
        assertThat(result).isTrue();
    }
}
