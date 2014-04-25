package LinqALike.Common;

/**
 * Created by Geoff on 14/04/14.
 */
@FunctionalInterface
public interface EqualityComparer<TCompared> {

    boolean equals(TCompared left, TCompared right);
}
