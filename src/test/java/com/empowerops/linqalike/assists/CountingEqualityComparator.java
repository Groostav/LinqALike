package com.empowerops.linqalike.assists;

import com.empowerops.linqalike.common.EqualityComparer;
import com.empowerops.linqalike.common.Tuple;

public class CountingEqualityComparator<TCompared> extends CountingDelegate implements EqualityComparer<TCompared> {

    private final EqualityComparer<TCompared> baseComparator;

    public static <TCompared> CountingEqualityComparator<TCompared> track(EqualityComparer<TCompared> baseComparator){
        return new CountingEqualityComparator<>(baseComparator);
    }

    private CountingEqualityComparator(EqualityComparer<TCompared> baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public boolean equals(TCompared left, TCompared right) {
        inspectedElements.add(new Tuple<>(left, right));
        return baseComparator.equals(left, right);
    }
}
