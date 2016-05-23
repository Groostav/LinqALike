package com.empowerops.linqalike.common;

import com.empowerops.linqalike.BiQueryable;
import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.EmptyIterator;

import java.util.Iterator;

public class EmptyBiQuery implements DefaultedBiQueryable {

    private static final EmptyBiQuery instance = new EmptyBiQuery();

    @SuppressWarnings("unchecked") //safe through covariance.
    public static <TL, TR> BiQueryable<TL, TR> getInstance(){
        return instance;
    }

    @Override
    public Iterator<Tuple<?, ?>> iterator() {
        return EmptyIterator.getInstance();
    }
}
