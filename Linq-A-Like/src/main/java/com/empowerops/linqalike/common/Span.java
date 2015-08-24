package com.empowerops.linqalike.common;

import javax.annotation.concurrent.Immutable;

import static com.empowerops.linqalike.CommonDelegates.identity;
import static com.empowerops.linqalike.Factories.from;
import static com.empowerops.linqalike.Factories.fromDoubles;

/**
 * Created by Geoff on 2015-03-16.
 */
@Immutable
public class Span extends Tuple<Double, Double>{

    private final double average;

    public Span(double... values){
        this(fromDoubles(values));
    }

    public Span(Iterable<Double> values){
        super(
                from(values).min(identity()),
                from(values).max(identity())
        );

        average = from(values).average(identity());
    }

    public double getLowerBound(){
        return left;
    }

    public double getUpperBound(){
        return right;
    }

    public double getMagnitude(){
        return getUpperBound() - getLowerBound();
    }

    public double getAverage(){
        return average;
    }

    public double getRelativeTo(Span largerSpan){
        return (average - largerSpan.getLowerBound()) / largerSpan.getMagnitude();
    }

    public boolean contains(double value){
        return getLowerBound() <= value && value <= getUpperBound();
    }
}
