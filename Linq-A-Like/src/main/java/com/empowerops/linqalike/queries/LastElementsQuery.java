package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;

import java.util.Iterator;
import java.util.List;

import static com.empowerops.linqalike.Factories.from;

public class LastElementsQuery<TElement> implements DefaultedQueryable<TElement> {
    private final Iterable<TElement> sourceElements;
    private final int maxToReturn;

    public LastElementsQuery(Iterable<TElement> sourceElements, int maxToReturn) {
        this.sourceElements = sourceElements;
        this.maxToReturn = maxToReturn;
    }

    @Override
    public Iterator<TElement> iterator() {

        if(sourceElements instanceof List){
            List<TElement> actualSourceElements = (List) sourceElements;
            int firstIncludedIndex = Math.max(0, actualSourceElements.size() - maxToReturn);
            return actualSourceElements.listIterator(firstIncludedIndex);
        }
        else {
            int size = Linq.size(sourceElements);
            return from(sourceElements).skip(size - Math.min(maxToReturn, size)).iterator();
        }

    }
}
