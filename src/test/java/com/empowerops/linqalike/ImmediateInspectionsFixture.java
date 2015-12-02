package com.empowerops.linqalike;

import com.empowerops.linqalike.queries.DefaultedCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Geoff on 2015-11-06.
 */
public class ImmediateInspectionsFixture {


    @Test
    public void when_asking_for_capped_count_of_empty_collection_should_get_back_zero(){
        //setup
        List<String> strings = new ArrayList<>();

        //act
        int size = ImmediateInspections.cappedCount(strings, 20);

        //assert
        assertThat(size).isEqualTo(0);
    }

    @Test
    public void when_asking_for_capped_count_of_empty_iterable_should_get_back_zero(){
        //setup
        DefaultedCollection<String> strings = new DefaultedCollection<>();

        //act
        int size = ImmediateInspections.cappedCount(strings, 20);

        //assert
        assertThat(size).isEqualTo(0);
    }
}