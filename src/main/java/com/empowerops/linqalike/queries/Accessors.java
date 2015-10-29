package com.empowerops.linqalike.queries;

import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.Queryable;

/**
 * Hack to try to get access to delegate member methods.
 *
 * <p>the problem this class is trying to solve is performance critical methods
 * If we have a castQuery wrapping a selectQuery, and we call outerQuery.size(),
 * that <i>should</i> be an O(1) time operation, cast bounces to select, select
 * bounces to source. The problem is that cast has to be able to call select's
 * 'size' method, when the only thing it knows about select's type is that it's an
 * 'Iterable'. I could copy-paste types implementations, replacing Iterable
 * with Queryable and Collection, which would avoid this kind of manual type-check,
 * but then I'm copy-pasting code.
 *
 * Created by Geoff on 2015-10-29.
 */
class Accessors {
    static final int vSize(Iterable<?> source){
        return source instanceof Queryable ? ((Queryable) source).size() : Linq.size(source);
    }
}
