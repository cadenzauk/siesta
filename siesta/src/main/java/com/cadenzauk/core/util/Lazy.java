/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.core.util;

import com.cadenzauk.core.function.ThrowingFunction;
import com.cadenzauk.core.function.ThrowingSupplier;
import com.cadenzauk.core.stream.StreamUtil;

import java.util.Optional;
import java.util.stream.Stream;

public class Lazy<T> {
    private final Object lock = new Object();
    private final ThrowingSupplier<T,? extends Exception> supplier;
    private volatile Try<T> value;

    public <E extends Exception> Lazy(ThrowingSupplier<T,E> supplier) {
        this.supplier = supplier;
    }

    public Try<T> tryGet() {
        Try<T> result = value;
        if (result == null) {
            synchronized (lock) {
                result = value;
                if (result == null) {
                    value = result = Try.trySupply(supplier);
                }
            }
        }
        return result;
    }

    public T get() {
        return tryGet().orElseThrow();
    }

    public Optional<T> optional() {
        synchronized (lock) {
            return Optional.ofNullable(value).flatMap(Try::toOptional);
        }
    }

    public Stream<T> stream() {
        synchronized (lock) {
            return StreamUtil.of(Optional.ofNullable(value)).flatMap(Try::stream);
        }
    }

    public boolean isKnown() {
        synchronized (lock) {
            return value != null;
        }
    }

    public boolean isKnownSuccess() {
        synchronized (lock) {
            return value != null && value.isSuccess();
        }
    }

    public boolean isKnownFailure() {
        synchronized (lock) {
            return value != null && value.isFailure();
        }
    }

    public <U, E extends Exception> Lazy<U> map(ThrowingFunction<? super T, ? extends U, E> function) {
        return new Lazy<>(() -> tryGet().map(function).orElseThrow());
    }

    public <U, E extends Exception> Lazy<U> flatMap(ThrowingFunction<? super T, Lazy<U>, E> function) {
        return new Lazy<>(() -> tryGet().map(function).orElseThrow().get());
    }
}
