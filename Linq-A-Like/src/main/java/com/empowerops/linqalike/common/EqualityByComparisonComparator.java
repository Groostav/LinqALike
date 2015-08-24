package com.empowerops.linqalike.common;

import java.util.Comparator;

/**
 * Created by Geoff on 11/05/2014.
 */
public class EqualityByComparisonComparator<TCompared> implements EqualityComparer<TCompared> {
    private final Comparator<TCompared> comparator;

    public EqualityByComparisonComparator(Comparator<TCompared> comparator){
        this.comparator = comparator;
    }

    @Override
    public boolean equals(TCompared left, TCompared right) {
        return comparator.compare(left, right) == 0;
    }
}
