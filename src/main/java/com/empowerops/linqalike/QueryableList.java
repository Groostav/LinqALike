package com.empowerops.linqalike;

import java.util.List;

public interface QueryableList<TElement> extends Queryable<TElement>, List<TElement> {

    @Override
    QueryableList<TElement> subList(int fromIndex, int toIndex);

    default int indexOfElement(TElement elementToFind){
        return indexOf(elementToFind);
    }

    default int lastIndexOfElement(TElement elementToFind){
        return lastIndexOf(elementToFind);
    }

    default TElement setOrAppend(int index, TElement newElement){
        if(index < 0) { throw new IllegalArgumentException("index"); }

        if(index >= size()){
            add(newElement);
            return null;
        }
        else{
            return set(index, newElement);
        }
    }
}
