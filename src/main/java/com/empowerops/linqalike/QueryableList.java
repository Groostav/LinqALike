package com.empowerops.linqalike;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.delegate.Func1;

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

    default int indexOfElement(TElement elementToFind, EqualityComparer<? super TElement> equalityComparer){
        return Linq.indexOf(this, elementToFind, equalityComparer);
    }

    default int lastIndexOfElement(TElement elementToFind, EqualityComparer<? super TElement> equalityComparer){
        return Linq.lastIndexOf(this, elementToFind, equalityComparer);
    }

    default <TCompared> int indexOfElement(TElement elementToFind, Func1<? super TElement, TCompared> comparableSelector){
        return Linq.indexOf(this, elementToFind, comparableSelector);
    }

    default <TCompared> int lastIndexOfElement(TElement elementToFind, Func1<? super TElement, TCompared> comparableSelector){
        return Linq.lastIndexOf(this, elementToFind, comparableSelector);
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
