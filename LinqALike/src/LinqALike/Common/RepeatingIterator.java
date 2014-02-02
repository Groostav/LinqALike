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

    @Override
    public TElement next() {
        return valueToRepeat;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
