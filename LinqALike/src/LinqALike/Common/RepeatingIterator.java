package LinqALike.Common;

import java.util.Iterator;

/**
* Created with IntelliJ IDEA.
* User: Geoff
* Date: 03/11/13
* Time: 15:10
* To change this template use File | Settings | File Templates.
*/
public class RepeatingIterator<TElement> implements Iterator<TElement> {

    private TElement valueToRepeat;

    public RepeatingIterator(TElement valueToRepeat){
        this.valueToRepeat = valueToRepeat;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

	/*
	FindBugs flagged this as:
	Iterator next() method can't throw NoSuchElementException
	This class implements the java.util.Iterator interface.  However, its next() method is not capable of throwing java.util.NoSuchElementException.  The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.

	Bug kind and pattern: It - IT_NO_SUCH_ELEMENT*/
    @Override
    public TElement next() {
        return valueToRepeat;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
