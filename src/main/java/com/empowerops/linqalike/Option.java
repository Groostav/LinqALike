package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Immutable;
import com.empowerops.linqalike.delegate.Action1;
import com.empowerops.linqalike.delegate.Condition;
import com.empowerops.linqalike.delegate.Func;
import com.empowerops.linqalike.delegate.Func1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

@Immutable
public final class Option<T> implements Iterable<T>{

    private static final Option Empty = new Option<>(null);

    private final T value;

    private Option(T value){
        this.value = value;
    }

    @SuppressWarnings("unchecked") // Option is Covariant, so this is safe.
    public static <T> Option<T> empty(){
        return Empty;
    }

    public static <T> Option<T> optionally(@Nonnull T value){
        if(value == null) { throw new IllegalArgumentException("value"); }
        return new Option<>(value);
    }

    public static <T> Option<T> optionallyNullable(@Nullable T value){
        return new Option<>(value);
    }

    public <TTransformed> Option<TTransformed> selectMany(Func1<? super T, Option<TTransformed>> selector) {
        return isPresent() ? selector.getFrom(value) : empty();
    }

    public Option<T> where(Condition<? super T> condition) {
        return isPresent() && condition.passesFor(value) ? this : empty();
    }

    public <TTransformed> Option<TTransformed> select(Func1<? super T, TTransformed> selector) {
        return isPresent() ? optionally(selector.getFrom(value)) : empty();
    }



    @Override
    public Iterator<T> iterator() {
        return new OptionIterator();
    }

    public T get(){
        if(value == null){
            throw new NoSuchElementException();
        }
        return value;
    }

    public Option<T> orOptionally(Option<T> alternative){
        return isPresent() ? this : alternative;
    }

    public @Nullable T orElse(@Nullable T alternative){
        return value == null ? alternative : value;
    }

    public @Nullable T orElseNull(){
        return orElse(null);
    }

    public @Nullable T orElseGet(Func<T> altnerativeGetter){
        return value == null ? altnerativeGetter.getValue() : value;
    }

    public boolean contains(T candidate){
        return CommonDelegates.nullSafeEquals(value, candidate);
    }

    public boolean isPresent(){
        return value != null;
    }

    public boolean isEmpty() { return ! isPresent(); }

    public void ifPresent(Action1<T> consumer){
        if(isPresent()){
            consumer.doUsing(value);
        }
    }

    private final class OptionIterator implements Iterator<T>{

        private AtomicBoolean wasRetrieved = new AtomicBoolean(false);

        @Override
        public boolean hasNext() {
            return ! wasRetrieved.getAndSet(true);
        }

        @Override
        public T next() {
            if ( ! hasNext()) {
                throw new NoSuchElementException();
            }
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option)) return false;

        Option<?> option = (Option<?>) o;

        return !(value != null ? !value.equals(option.value) : option.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
