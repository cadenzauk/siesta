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

import co.unruly.matchers.StreamMatchers;
import com.cadenzauk.core.function.ThrowingFunction;
import com.cadenzauk.core.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LazyTest {
    @Mock
    private ThrowingSupplier<String,RuntimeException> supplier;

    @Mock
    private ThrowingSupplier<String,RuntimeException> supplier2;

    @Test
    void tryGetWhenNoSupplierIsFailure() {
        Lazy<String> sut = new Lazy<>();

        Try<String> result = sut.tryGet();

        assertThat(result, is(Try.failure(new IllegalStateException("No supplier was specified at construction time.  Use getOrCompute() or tryGetOrCompute() to retrieve the value of this Lazy."))));
    }

    @Test
    void tryGetSuccess() {
        Lazy<String> sut = new Lazy<>(() -> "Hello World");

        Try<String> result = sut.tryGet();

        assertThat(result, is(Try.success("Hello World")));
    }

    @Test
    void tryGetFailure() {
        Lazy<String> sut = new Lazy<>(() -> {
            throw new RuntimeException("Epic fail.");
        });

        Try<String> result = sut.tryGet();

        assertThat(result, is(Try.failure(new RuntimeException("Epic fail."))));
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryGetOnlyOnceIfSuccessful() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn("Listen very carefully, I shall say this only once.");
        Lazy<String> sut = new Lazy<>(supplier);

        Try<String> result1 = sut.tryGet();
        Try<String> result2 = sut.tryGet();

        assertThat(result1, is(Try.success("Listen very carefully, I shall say this only once.")));
        assertThat(result2, is(Try.success("Listen very carefully, I shall say this only once.")));
        verify(supplier, times(1)).get();
    }

    @Test
    void tryGetOnlyOnceIfFailed() {
        when(supplier.get()).thenThrow(new IllegalArgumentException("Bad argument."));
        Lazy<String> sut = new Lazy<>(supplier);

        Try<String> result1 = sut.tryGet();
        Try<String> result2 = sut.tryGet();

        assertThat(result1, is(Try.failure(new IllegalArgumentException("Bad argument."))));
        assertThat(result2, is(Try.failure(new IllegalArgumentException("Bad argument."))));
        verify(supplier, times(1)).get();
    }

    @Test
    void tryGetOrComputeUsesSupplierWhenNoValue() {
        when(supplier2.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>(supplier);

        Try<String> result = sut.tryGetOrCompute(supplier2);

        assertThat(result, is(Try.success("Computed value")));
        verifyNoInteractions(supplier);
        verify(supplier2, times(1)).get();
    }

    @Test
    void tryGetOrComputeDoesNotCallSupplierWhenValueAlreadyPresent() {
        when(supplier.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>(supplier);
        sut.get();

        Try<String> result = sut.tryGetOrCompute(supplier2);

        assertThat(result, is(Try.success("Computed value")));
        verifyNoInteractions(supplier2);
    }

    @Test
    void tryGetOrComputeDoesNotCallSupplierWhenAlreadyFailed() {
        when(supplier.get()).thenThrow(new IllegalArgumentException("Bad argument."));
        Lazy<String> sut = new Lazy<>(supplier);
        sut.tryGet();

        Try<String> result = sut.tryGetOrCompute(supplier2);

        assertThat(result, is(Try.failure(new IllegalArgumentException("Bad argument."))));
        verifyNoInteractions(supplier2);
    }

    @Test
    void tryGetOrComputeOnlyCallsSupplierOnce() {
        when(supplier.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>();

        Try<String> result1 = sut.tryGetOrCompute(supplier);
        Try<String> result2 = sut.tryGetOrCompute(supplier2);

        assertThat(result1, is(Try.success("Computed value")));
        assertThat(result2, is(Try.success("Computed value")));
        verifyNoInteractions(supplier2);
    }

    @Test
    void getSuccess() {
        Lazy<String> sut = new Lazy<>(() -> "Hello World");

        String result = sut.get();

        assertThat(result, is("Hello World"));
    }

    @Test
    void getFailure() {
        Lazy<String> sut = new Lazy<>(() -> {
            throw new RuntimeException("Oops!");
        });

        calling(sut::get)
            .shouldThrow(RuntimeException.class)
            .withMessage("Oops!");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getOnceIfSuccessful() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn("Listen very carefully, I shall say this only once.");
        Lazy<String> sut = new Lazy<>(supplier);

        String result1 = sut.get();
        String result2 = sut.get();

        assertThat(result1, is("Listen very carefully, I shall say this only once."));
        assertThat(result2, is("Listen very carefully, I shall say this only once."));
        verify(supplier, times(1)).get();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getOnlyOnceIfFailed() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        when(supplier.get()).thenThrow(new IllegalArgumentException("Bad argument."));
        Lazy<String> sut = new Lazy<>(supplier);

        calling(sut::get)
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Bad argument.");
        calling(sut::get)
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Bad argument.");

        verify(supplier, times(1)).get();
    }


    @Test
    void getOrComputeUsesSupplierWhenNoValue() {
        when(supplier2.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>(supplier);

        String result = sut.getOrCompute(supplier2);

        assertThat(result, is("Computed value"));
        verifyNoInteractions(supplier);
        verify(supplier2, times(1)).get();
    }

    @Test
    void getOrComputeDoesNotCallSupplierWhenValueAlreadyPresent() {
        when(supplier.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>(supplier);
        sut.get();

        String result = sut.getOrCompute(supplier2);

        assertThat(result, is("Computed value"));
        verifyNoInteractions(supplier2);
    }

    @Test
    void getOrComputeDoesNotCallSupplierWhenAlreadyFailed() {
        when(supplier.get()).thenThrow(new IllegalArgumentException("Bad argument."));
        Lazy<String> sut = new Lazy<>(supplier);
        sut.tryGet();

        calling(() -> sut.getOrCompute(supplier2))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Bad argument.");

        verifyNoInteractions(supplier2);
    }

    @Test
    void getOrComputeOnlyCallsSupplierOnce() {
        when(supplier.get()).thenReturn("Computed value");
        Lazy<String> sut = new Lazy<>();

        String result1 = sut.getOrCompute(supplier);
        String result2 = sut.getOrCompute(supplier2);

        assertThat(result1, is("Computed value"));
        assertThat(result2, is("Computed value"));
        verifyNoInteractions(supplier2);
    }

    @Test
    void optionalOfUnknownIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> "Nothing");

        Optional<String> result = sut.optional();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void optionalOfNullIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> null);
        sut.get();

        Optional<String> result = sut.optional();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void optionalOfFailureIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> { throw new IllegalArgumentException("Boom"); });
        sut.tryGet();

        Optional<String> result = sut.optional();

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void optionalOfKnownIsValue() {
        Lazy<String> sut = new Lazy<>(() -> "42");
        sut.get();

        Optional<String> result = sut.optional();

        assertThat(result, is(Optional.of("42")));
    }

    @Test
    void streamOfUnknownIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> "Nothing");

        Stream<String> result = sut.stream();

        assertThat(result, StreamMatchers.empty());
    }

    @Test
    void streamOfNullIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> null);
        sut.get();

        Stream<String> result = sut.stream();

        assertThat(result, StreamMatchers.empty());
    }

    @Test
    void streamOfFailureIsEmpty() {
        Lazy<String> sut = new Lazy<>(() -> { throw new IllegalArgumentException("Boom"); });
        sut.tryGet();

        Stream<String> result = sut.stream();

        assertThat(result, StreamMatchers.empty());
    }

    @Test
    void streamOfKnownIsValue() {
        Lazy<String> sut = new Lazy<>(() -> "42");
        sut.get();

        Stream<String> result = sut.stream();

        assertThat(result, StreamMatchers.contains("42"));
    }

    @Test
    void isKnownWhenNot() {
        Lazy<String> sut = new Lazy<>(() -> "Good boy");

        assertThat(sut.isKnown(), is(false));
        assertThat(sut.isKnownSuccess(), is(false));
        assertThat(sut.isKnownFailure(), is(false));
    }

    @Test
    void isKnownWhenSuccess() {
        Lazy<String> sut = new Lazy<>(() -> "Good boy");

        sut.tryGet();

        assertThat(sut.isKnown(), is(true));
        assertThat(sut.isKnownSuccess(), is(true));
        assertThat(sut.isKnownFailure(), is(false));
    }

    @Test
    void isKnownWhenFailure() {
        Lazy<String> sut = new Lazy<>(() -> {
            throw new ArithmeticException("Bad boy");
        });

        sut.tryGet();

        assertThat(sut.isKnown(), is(true));
        assertThat(sut.isKnownSuccess(), is(false));
        assertThat(sut.isKnownFailure(), is(true));
    }

    @Test
    void mapSuccessSuccess() {
        Lazy<String> sut = new Lazy<>(() -> "Hello");

        Lazy<String> map = sut.map(s -> s + " World!");

        assertThat(map.get(), is("Hello World!"));
    }

    @Test
    void mapSuccessFailure() {
        Lazy<String> sut = new Lazy<>(() -> "Hello");

        Lazy<String> map = sut.map(s -> {
            throw new IllegalArgumentException("Too unfriendly.");
        });

        calling(map::get)
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Too unfriendly.");
    }

    @Test
    void mapFailure() {
        Lazy<String> sut = new Lazy<>(() -> {
            throw new UnsupportedOperationException("No greetings for you.");
        });

        Lazy<String> map = sut.map(s -> s + " World!");

        calling(map::get)
            .shouldThrow(UnsupportedOperationException.class)
            .withMessage("No greetings for you.");
    }

    @SuppressWarnings("unchecked")
    @Test
    void mapEachOnlyOnceIfSuccessful() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        ThrowingFunction<String,String,RuntimeException> mapFunction = Mockito.mock(ThrowingFunction.class);
        when(supplier.get()).thenReturn("Listen very carefully, ...");
        when(mapFunction.apply("Listen very carefully, ...")).thenReturn("I shall say this only once.");
        Lazy<String> sut = new Lazy<>(supplier);
        Lazy<String> map = sut.map(mapFunction);

        verifyNoInteractions(supplier, mapFunction);

        Try<String> result1 = map.tryGet();
        Try<String> result2 = map.tryGet();

        assertThat(result1, is(Try.success("I shall say this only once.")));
        assertThat(result2, is(Try.success("I shall say this only once.")));
        verify(supplier, times(1)).get();
        verify(mapFunction, times(1)).apply("Listen very carefully, ...");
        verifyNoMoreInteractions(supplier, mapFunction);
    }

    @SuppressWarnings("unchecked")
    @Test
    void mapNotCalledOnFailure() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        ThrowingFunction<String,String,RuntimeException> mapFunction = Mockito.mock(ThrowingFunction.class);
        when(supplier.get()).thenThrow(new RuntimeException("Allo Allo"));
        Lazy<String> sut = new Lazy<>(supplier);
        Lazy<String> map = sut.map(mapFunction);

        verifyNoInteractions(supplier, mapFunction);

        Try<String> result1 = map.tryGet();
        Try<String> result2 = map.tryGet();

        assertThat(result1, is(Try.failure(new RuntimeException("Allo Allo"))));
        assertThat(result2, is(Try.failure(new RuntimeException("Allo Allo"))));
        verify(supplier, times(1)).get();
        verifyNoMoreInteractions(supplier, mapFunction);
    }

    @SuppressWarnings("unchecked")
    @Test
    void flatMapEachOnlyOnceIfSuccessful() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        ThrowingFunction<String,Lazy<String>,RuntimeException> mapFunction = Mockito.mock(ThrowingFunction.class);
        when(supplier.get()).thenReturn("Listen very carefully, ...");
        when(mapFunction.apply("Listen very carefully, ...")).thenReturn(new Lazy<>(() -> "I shall say this only once."));
        Lazy<String> sut = new Lazy<>(supplier);
        Lazy<String> map = sut.flatMap(mapFunction);

        verifyNoInteractions(supplier, mapFunction);

        Try<String> result1 = map.tryGet();
        Try<String> result2 = map.tryGet();

        assertThat(result1, is(Try.success("I shall say this only once.")));
        assertThat(result2, is(Try.success("I shall say this only once.")));
        verify(supplier, times(1)).get();
        verify(mapFunction, times(1)).apply("Listen very carefully, ...");
        verifyNoMoreInteractions(supplier, mapFunction);
    }

    @SuppressWarnings("unchecked")
    @Test
    void flatMapNotCalledOnFailure() {
        ThrowingSupplier<String,RuntimeException> supplier = Mockito.mock(ThrowingSupplier.class);
        ThrowingFunction<String,Lazy<String>,RuntimeException> mapFunction = Mockito.mock(ThrowingFunction.class);
        when(supplier.get()).thenThrow(new RuntimeException("Allo Allo"));
        Lazy<String> sut = new Lazy<>(supplier);
        Lazy<String> map = sut.flatMap(mapFunction);

        verifyNoInteractions(supplier, mapFunction);

        Try<String> result1 = map.tryGet();
        Try<String> result2 = map.tryGet();

        assertThat(result1, is(Try.failure(new RuntimeException("Allo Allo"))));
        assertThat(result2, is(Try.failure(new RuntimeException("Allo Allo"))));
        verify(supplier, times(1)).get();
        verifyNoMoreInteractions(supplier, mapFunction);
    }

}
