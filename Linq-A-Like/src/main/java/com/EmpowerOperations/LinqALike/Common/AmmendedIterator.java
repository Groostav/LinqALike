package com.EmpowerOperations.LinqALike.Common;

import java.util.Iterator;

/**
* Created with IntelliJ IDEA.
* User: Geoff
* Date: 03/11/13
* Time: 14:44
* To change this template use File | Settings | File Templates.
*/
public class AmmendedIterator<TElement> implements Iterator<TElement> {

    private TElement front;
    private Iterator<? extends TElement> body;

    boolean tookFront;

    public AmmendedIterator(TElement front, Iterator<? extends TElement> body){
        this.front = front;
        this.body = body;
        this.tookFront = false;
    }

    @Override
    public boolean hasNext() {
        return ! tookFront || body.hasNext();
    }

    @Override
    public TElement next() {
        TElement returnable = tookFront ? body.next() : front;
        tookFront = true;
        return returnable;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
