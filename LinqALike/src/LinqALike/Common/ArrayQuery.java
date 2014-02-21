package LinqALike.Common;

import LinqALike.Queryable;
import java.util.Iterator;

public class ArrayQuery<TElement> implements Queryable<TElement> {

    private final TElement[] elements;

    public ArrayQuery(TElement[] elements){
        this.elements = elements;
    }

    @Override
    public Iterator<TElement> iterator() {
        return new ArrayIterator<TElement>(elements);
    }

}
