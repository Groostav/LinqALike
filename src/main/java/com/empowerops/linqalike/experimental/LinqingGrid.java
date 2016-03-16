package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.LinqingList;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func1;

import java.util.Iterator;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 2014-10-28.
 */
public class LinqingGrid<TElement> extends LinqingList<LinqingList<TElement>> {

    public LinqingGrid() {}
    public LinqingGrid(LinqingList<TElement>... linqingLists)                   { super(linqingLists); }
    public LinqingGrid(Iterator<? extends LinqingList<TElement>> elements)      { super(elements); }
    public LinqingGrid(Iterable<? extends LinqingList<TElement>> linqingLists)  { super(linqingLists); }
    public LinqingGrid(Object[][] initializer){
        this(from(initializer).select(row -> from(row).<TElement>cast().toList()).toList());
    }

    public static <TSource> LinqingGrid<Boolean> forDecisions(Iterable<Condition<? super TSource>> columns, Iterable<TSource> rows){
        return new<TSource> LinqingGrid<Boolean>(rows, from(columns)
                                                                .select(condition -> (Func1<TSource, Boolean>)condition::passesFor)
                                                                .toArray(Func1[]::new));
    }

    @SafeVarargs
    public <TSource> LinqingGrid(Iterable<TSource> rows, Func1<? super TSource, ? extends TElement>... columns){
        this();
        for(TSource unformattedRow : rows){
            LinqingList<TElement> row = new LinqingList<>();
            for(Func1<? super TSource, ? extends TElement> columnGetter : columns){
                row.add(columnGetter.getFrom(unformattedRow));
            }
            add(row);
        }
    }

    public Queryable<Queryable<TElement>> toQueryable(){
        return cast();
    }

    @Override
    public boolean remove(Object toRemove) {
        return super.remove(toRemove);
    }
}