package LinqALike.Common;

import java.util.Iterator;

public class ArrayIterator<TElement> implements Iterator<TElement> {

    private final TElement[] arrayToIterateOver;
    private int currentIndex;

    public ArrayIterator(TElement[] arrayToIterateOver){
        this.arrayToIterateOver = arrayToIterateOver;
        currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < arrayToIterateOver.length;
    }

    @Override
    public TElement next() {
        return arrayToIterateOver[currentIndex++];
    }

    @Override
    public void remove() {
        arrayToIterateOver[currentIndex++] = null;
    }
}
