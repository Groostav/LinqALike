package collisions;

import com.EmpowerOperations.LinqALike.LinqingList;
import org.junit.Test;

/**
 * Created by Geoff on 2014-07-22.
 */
public class LinqingListCollisions {

    @Test
    public void when_calling_addAll_most_specific_method_is_invoked_without_casting(){
        LinqingList<String> left = new LinqingList<>();
        LinqingList<String> right = new LinqingList<>();

        left.addAll(right);
    }
}
