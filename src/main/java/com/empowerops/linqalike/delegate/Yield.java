package com.empowerops.linqalike.delegate;

import java.io.Serializable;

/**
 * Closed delegate object that returns a value based on closed context. a <code>Yield</code> is
 * also known as a <code>Lazy</code>.
 * see {@link Action} for a more general description.
 *
 * @author Geoff on 26/06/13
 * @see Action
 */
public interface Yield<TResult> extends Serializable {
    public TResult get();
}
