package com.empowerops.linqalike;

import java.util.List;

public interface QueryableList<TElement> extends Queryable<TElement>, List<TElement> {

    QueryableList<TElement> subList(int fromIndex, int toIndex);

    int indexOfElement(TElement elementToFind);

    int lastIndexOfElement(TElement elementToFind);
}
