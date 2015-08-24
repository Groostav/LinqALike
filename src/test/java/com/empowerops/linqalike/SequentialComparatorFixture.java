package com.empowerops.linqalike;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Geoff on 2015-06-03.
 */
public class SequentialComparatorFixture {

    public static final int LeftWins    = -1;
    public static final int RightWins   = +1;
    public static final int Tied        = 0;

    @Test
    public void when_comparing_two_sets_of_strings_and_one_wins_3_entries_in_should_rank_that_one_first(){
        //setup
        Queryable<String> winner = new LinqingList<>("A", "B", "C");
        Queryable<String> loser = new LinqingList<>("A", "B", "D");

        //act
        int winnerToLoser = LinqingList.<String>SequentialComparator().compare(winner, loser);
        int loserToWinner = LinqingList.<String>SequentialComparator().compare(loser, winner);

        //assert
        assertThat(winnerToLoser).isEqualTo(LeftWins);
        assertThat(loserToWinner).isEqualTo(RightWins);
    }

    @Test
    public void when_comparing_the_empty_list_to_a_nonempty_one_the_empty_one_wins(){
        //setup
        Queryable<String> winner = new LinqingList<>();
        Queryable<String> loser = new LinqingList<>("A", "D");

        //act
        int winnerToLoser = LinqingList.<String>SequentialComparator().compare(winner, loser);
        int loserToWinner = LinqingList.<String>SequentialComparator().compare(loser, winner);

        //assert
        assertThat(winnerToLoser).isEqualTo(LeftWins);
        assertThat(loserToWinner).isEqualTo(RightWins);
    }

    @Test
    public void when_comparing_two_empty_lists_comparator_yields_equals(){
        //setup
        Queryable<String> first = new LinqingList<>();
        Queryable<String> second = new LinqingList<>();

        //act
        int winnerToLoser = LinqingList.<String>SequentialComparator().compare(first, second);
        int loserToWinner = LinqingList.<String>SequentialComparator().compare(second, first);

        //assert
        assertThat(winnerToLoser).isEqualTo(Tied);
        assertThat(loserToWinner).isEqualTo(Tied);
    }

    @Test
    public void when_comparing_two_lists_where_the_shorter_one_is_worse_should_get_larger(){
        //setup
        Queryable<String> winner = new LinqingList<>("A", "B", "C");
        Queryable<String> loser = new LinqingList<>("A", "C");

        //act
        int winnerToLoser = LinqingList.<String>SequentialComparator().compare(winner, loser);
        int loserToWinner = LinqingList.<String>SequentialComparator().compare(loser, winner);

        //assert
        assertThat(winnerToLoser).isEqualTo(LeftWins);
        assertThat(loserToWinner).isEqualTo(RightWins);
    }
}
