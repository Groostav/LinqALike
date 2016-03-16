package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.WritableCollection;

public interface WritableObservableQueryable<TElement> extends WritableCollection<TElement>, ObservableQueryable<TElement> {

    void bindToContentOf(ObservableQueryable<TElement> source);
}
