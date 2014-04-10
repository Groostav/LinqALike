package LinqALike.Common;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

	/*
	FindBugs flagged this as:
	Iterator next() method can't throw NoSuchElementException
	This class implements the java.util.Iterator interface.  However, its next() method is not capable of throwing java.util.NoSuchElementException.  The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.

	Bug kind and pattern: It - IT_NO_SUCH_ELEMENT*/
    @Override
    public TElement next() {
        if(currentIndex == arrayToIterateOver.length){
            throw new NoSuchElementException("attempting to iterate past the end of an array");
        }
        return arrayToIterateOver[currentIndex++];
    }

    @Override
    public void remove() {
        arrayToIterateOver[currentIndex++] = null;
    }
}
