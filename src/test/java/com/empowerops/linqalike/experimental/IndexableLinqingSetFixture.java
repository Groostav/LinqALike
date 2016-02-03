package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.delegate.Func1;
import org.junit.Test;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

import static com.empowerops.linqalike.Factories.range;
import static org.junit.Assert.fail;

/**
 * Created by Geoff on 2/2/2016.
 */
public class IndexableLinqingSetFixture {

    public static class IndexableItem {

        static Random random = new Random();

        private UUID uuid = UUID.randomUUID();
        private int intId = random.nextInt();

        public int getIntID(){
            return intId;
        }

        public UUID getUUID(){
            return uuid;
        }
    }

    @Test
    public void when_ordering_50_000_elem_list_index_runs_faster(){

        //setup
        IndexableLinqingSet<IndexableItem> set = new IndexableLinqingSet<>(IndexableItem.class);
        set.addAll(range(0, 20_000).select(x -> new IndexableItem()));

//        set.treeIndex(IndexableItem::getIntID);
        set.orderBy((Func1<IndexableItem, Integer> & Serializable) IndexableItem::getIntID);

        //act
        long startTime = System.nanoTime();
        set.orderBy((Func1<IndexableItem, Integer> & Serializable) IndexableItem::getIntID);
        long endTime = System.nanoTime();

        //assert
        System.out.println("took: " + (endTime - startTime)/1000 + "us");
    }

    @Test
    public void when_creating_index_after_query_should_get_newly_cached_value_not_other_thing(){

        //ie call orderBy(X) --this will likely get X put in the 'no index for' list
        //call addIndex(X) --SHOULD remove X from the 'no index for' list
        //call orderBy(X) again -- should get the index just added

        fail();
    }
}