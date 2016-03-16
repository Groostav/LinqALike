package com.empowerops.linqalike.assists;

import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.experimental.ObservableWhereSourceListener;
import javafx.collections.ListChangeListener;

import java.util.Collection;
import java.util.List;

public class CountRange{

    private int lower = Integer.MIN_VALUE;
    private int upper = Integer.MAX_VALUE;

    public static CountRange forPermutationRangeIn(ListChangeListener.Change change){
        return new CountRange(change.getFrom(), change.getTo());
    }

    public static <TElement> CountRange forSizeOf(Collection<TElement> boundingList) {
        return new CountRange(0, boundingList.size() - 1);
    }

    public CountRange(){
    }

    public CountRange(int lower, int upper){
        this.lower = lower;
        this.upper = upper;
    }

    public int getUpper() {
        return upper;
    }

    public int getLower() {
        return lower;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }

    public void setRange(int lower, int upper){
        setLower(lower);
        setUpper(upper);
    }

    public Queryable<Integer> asIndexRange(){
        return Factories.range(getLower(), getUpper() + 1);
    }

    /**
     * Epsilon has always been the a sort of fleeting or zero or empty or insignificant value,
     * so here I'm cashing in on those connotations to deal with this: a count range that spans
     * negative space. Typically you'll have to apply some domain knowledge to interpret
     * what it means to have an epsilon count range. In the case of our searching used in
     * {@link ObservableWhereSourceListener},
     * it means that the search process didn't find any elements in the count range contained
     * in the filtered list (ie, a change happened to the source list that has had no impact at all
     * on the filtered list).
     *
     * @return true if the
     */
    public boolean isEpsilon(){
        return getUpper() < getLower();
    }

    public <TElement> List<TElement> subList(List<TElement> completeList) {
        return completeList.subList(getLower(), getUpper() + 1);
    }
}
