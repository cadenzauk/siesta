package com.cadenzauk.core.function;

import java.util.Objects;

public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;

    default ThrowingConsumer<T, E> andThen(ThrowingConsumer<? super T, ? extends E> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
