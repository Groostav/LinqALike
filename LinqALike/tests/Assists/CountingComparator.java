package Assists;

import LinqALike.Common.Tuple;

import java.util.Comparator;

/**
 * Created by Geoff on 30/04/2014.
 */
public class CountingComparator<TCompared> extends CountingDelegate implements Comparator<TCompared> {

    private final Comparator<TCompared> baseComparator;

    public static <TCompared> CountingComparator<TCompared> track(Comparator<TCompared> baseComparator){
        return new CountingComparator<>(baseComparator);
    }

    private CountingComparator(Comparator<TCompared> baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public int compare(TCompared o1, TCompared o2) {
        inspectedElements.add(new Tuple<>(o1, o2));

        return baseComparator.compare(o1, o2);
    }
}

