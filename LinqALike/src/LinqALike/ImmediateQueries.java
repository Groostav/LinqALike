package LinqALike;

import LinqALike.Common.QueryableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Geoff on 13/04/14.
 */
public class ImmediateQueries {

    public static <TElement> boolean isDistinct(Iterable<TElement> sourceElements){
        if(sourceElements instanceof Set){
            return true;
        }

        HashSet<TElement> set = new HashSet<>();

        for(TElement element : sourceElements){

            boolean modified = set.add(element);
            if ( ! modified){
                return false;
            }
        }
        return true;
    }
}
