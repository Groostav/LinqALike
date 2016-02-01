package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.LinqingSet;
import com.empowerops.linqalike.delegate.Func1;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Iterator;

public class IndexedLinqingSet<TElement> extends LinqingSet<TElement> {

    public IndexedLinqingSet() {
    }

    public IndexedLinqingSet(int size) {
        super(size);
    }

    public IndexedLinqingSet(TElement... tElements) {
        super(tElements);
    }

    public IndexedLinqingSet(Iterator<? extends TElement> elements) {
        super(elements);
    }

    public IndexedLinqingSet(Iterable<? extends TElement> tElements) {
        super(tElements);
    }

    public interface PropertyMethodRef<THost> extends Serializable {

        Object get(THost host);

    }

    public void index(Func1<? super TElement, ?> propertyToIndex){

    }
}
