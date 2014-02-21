package LinqALike.Common;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Alleviates the difficulties of determining the hasNext() state, by simply allowing you to only fetch the value.</p>
 *
 * <p>Consider the case where you're iterator is a wrapper of a very complex object. Determining the result for {@link #hasNext()}
 * can be difficult or impossible to do without also determining the next value to fetch. In such circumstances, it is inconvenient
 * to split up our code based on <tt>hasNext</tt> and <tt>getNext</tt> methods. This class allows the programmer to effectively get
 * a "free" implementation by setting a flag when there is no next value available. </p>
 *
 * <p>to achieve this, we use a </p>
 *
 * @author Geoff on 31/10/13
 */
public abstract class PrefetchingIterator<TElement> implements Iterator<TElement> {
    private boolean cachedValueIsValid = false;
    private TElement prefetchedValue;

    @Override
    public boolean hasNext() {
        updateCache();
        return hasPrefetchedValue();
    }

    @Override
    public TElement next() {

        updateCache();

        if( ! hasPrefetchedValue()){
            throw new NoSuchElementException();
        }

        TElement returnable = getPrefetchedValue();
        invalidateCache();

        return returnable;
    }

    /**
     * <p>Method to retrieve the next value (eagerly) that will be yielded to {@link #next()}.</p> Use
     * {@link #setPrefetchedValue(Object)} to supply that yielded value.
     *
     * <p>This method will be called as lazily as possible. It will be forced by either a call to {@link #next()}
     * <b>or a call to {@link #hasNext()}</b>. Once this call has gone through, its result is cached, meaning any
     * further calls to {@link #hasNext()} will not result in a call to this method. Once a successive call to
     * {@link #next()} has occured, the value set as prefetched (the argument passed to {@link #setPrefetchedValue(Object)}
     * is invalidated, and the next call to either {@link #next()} or {@link #hasNext()} will force another call to
     * <tt>prefetch()</tt>.</p>
     */
    protected abstract void prefetch();

    /**
     * <p>Allows you to set the prefetched value discovered in a call to {@link #prefetch()}. Unforunately, as far as the
     * {@link PDOL.Common.Queryable.Queryable} interface-contract is concerned, <tt>null</tt> elements are always legal,
     * so we cant simply have the {@link #prefetch()} method return null as a signal to indicate that <i>I cant prefetch
     * another value --we're out.</i> So, instead, we use this method.</p>
     *
     * <p>once called, with any argument, the result of {@link #hasPrefetchedValue()} will be <tt>true</tt> until a call
     * is made to {@link #next()}, at which point the value suppliede here will be given to the caller of {@link #next()}
     * and {@link #hasPrefetchedValue()} will once again be false (ie, the prefetch cache will be invalidated). </p>
     *
     * <p>You may not invoke <tt>setPrefetchedValue(Object prefetchedValue)</tt> twice without a call to
     * {@link #next()} between them.</p>
     *
     * @param prefetchedValue the value to use as the next value for the iteration.
     * @throws IllegalStateException if two calls are made to <tt>setPrefetchedValue(Object prefetchedValue)</tt> without
     * an intervening call to {@link #next()}
     */
    protected final void setPrefetchedValue(TElement prefetchedValue){
        if(cachedValueIsValid){
            throw new IllegalStateException("A prefetched value was set and then never retrieved.");
        }
        this.cachedValueIsValid = true;
        this.prefetchedValue = prefetchedValue;
    }

    private void invalidateCache() {
        cachedValueIsValid = false;
        prefetchedValue = null;
    }

    protected boolean hasPrefetchedValue(){
        return cachedValueIsValid;
    }

    private TElement getPrefetchedValue(){
        if ( ! hasPrefetchedValue()){
            throw new NoSuchElementException();
        }
        return prefetchedValue;
    }

    private void updateCache() {
        if (hasPrefetchedValue()){
            return;
        }

        prefetch();

        return;
    }
}
