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

import com.cadenzauk.core.lang.AggregateError;
import com.cadenzauk.core.lang.AggregateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TryTest {
    @ParameterizedTest
    @MethodSource("successesAndFailuresWithBoolean")
    void isSuccess(Try<Integer> candidate, boolean expectedSuccess) {
        boolean result = candidate.isSuccess();

        assertThat(result, is(expectedSuccess));
    }

    @ParameterizedTest
    @MethodSource("successesAndFailuresWithBoolean")
    void isFailure(Try<Integer> candidate, boolean expectedSuccess) {
        boolean result = candidate.isFailure();

        assertThat(result, is(!expectedSuccess));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseThrowSuccess(Try<Integer> candidate, Integer expectedValue) {
        Integer result = candidate.orElseThrow();

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("failuresWithWrappedThrowable")
    void orElseThrowFailure(Try<Integer> candidate, Throwable expectedThrowable) {
        calling((Supplier<Integer>) candidate::orElseThrow)
            .shouldThrow(expectedThrowable.getClass())
            .withMessage(expectedThrowable.getMessage());
    }

    @ParameterizedTest
    @MethodSource({"failuresWithRawException", "successes"})
    void orElseThrowFailure(Try<Integer> candidate) {
        candidate.throwIfError();
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawError")
    void orElseThrowFailure(Try<Integer> candidate, Error expectedError) {
        calling(candidate::throwIfError)
            .shouldThrow(expectedError.getClass())
            .withMessage(expectedError.getMessage());
    }

    @ParameterizedTest
    @MethodSource("successes")
    void throwableSuccess(Try<Integer> candidate) {
        calling(candidate::throwable)
            .shouldThrow(IllegalStateException.class)
            .withMessage("Cannot get a throwable from a successful Try.");
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void throwableFailure(Try<Integer> candidate, Throwable expectedThrowable) {
        Throwable result = candidate.throwable();

        assertThat(result, instanceOf(expectedThrowable.getClass()));
        assertThat(result.getMessage(), is(expectedThrowable.getMessage()));
        assertThat(result.getCause(), is(expectedThrowable.getCause()));
    }

    @ParameterizedTest
    @MethodSource("successesWithValuePlus20")
    void mapSuccessSuccessfully(Try<Integer> candidate, int candidatePlus20) {
        Try<Integer> result = candidate.map(x -> x + 20);

        assertThat(result.orElseThrow(), is(candidatePlus20));
    }

    @ParameterizedTest
    @MethodSource("successes")
    void mapSuccessFailsUnchecked(Try<Integer> candidate) {
        Try<Integer> result = candidate.map(x -> {
            throw new IllegalArgumentException("Kaboom!");
        });

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(IllegalArgumentException.class));
        assertThat(result.throwable().getMessage(), is("Kaboom!"));
    }

    @ParameterizedTest
    @MethodSource("successes")
    void mapSuccessFailsChecked(Try<Integer> candidate) {
        Try<Integer> result = candidate.map(x -> {
            throw new IOException("File not found.");
        });

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(IOException.class));
        assertThat(result.throwable().getMessage(), is("File not found."));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void mapFailure(Try<Integer> candidate, Throwable expectedThrowable) {
        Try<Integer> result = candidate.map(x -> x + 20);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(expectedThrowable.getClass()));
        assertThat(result.throwable().getMessage(), is(expectedThrowable.getMessage()));
        assertThat(result.throwable().getCause(), is(expectedThrowable.getCause()));
    }

    @ParameterizedTest
    @MethodSource("successesWithValuePlus20")
    void flatMapSuccessSuccessfully(Try<Integer> candidate, int candidatePlus20) {
        Try<Integer> result = candidate.flatMap(x -> Try.success(x + 20));

        assertThat(result.orElseThrow(), is(candidatePlus20));
    }

    @ParameterizedTest
    @MethodSource("successes")
    void flatMapSuccessFailsUnchecked(Try<Integer> candidate) {
        Try<Integer> result = candidate.flatMap(x -> {
            throw new IllegalArgumentException("Kaboom!");
        });

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(IllegalArgumentException.class));
        assertThat(result.throwable().getMessage(), is("Kaboom!"));
    }

    @ParameterizedTest
    @MethodSource("successes")
    void flatMapSuccessFailsChecked(Try<Integer> candidate) {
        Try<Integer> result = candidate.flatMap(x -> {
            throw new IOException("File not found.");
        });

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(IOException.class));
        assertThat(result.throwable().getMessage(), is("File not found."));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void flatMapFailure(Try<Integer> candidate, Throwable expectedThrowable) {
        Try<Integer> result = candidate.flatMap(x -> Try.success(x + 20));

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(expectedThrowable.getClass()));
        assertThat(result.throwable().getMessage(), is(expectedThrowable.getMessage()));
        assertThat(result.throwable().getCause(), is(expectedThrowable.getCause()));
    }

    @ParameterizedTest
    @MethodSource("successesWithValuePlus20")
    void combineOfSuccessAndSuccessAppliesFunction(Try<Integer> candidate, int candidatePlus20) {
        Try<Integer> result = candidate.combine(Try.success(20), (i, j) -> i + j);

        assertThat(result.orElseThrow(), is(candidatePlus20));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void combineOfSuccessAndFailureReturnsFailure(Try<Integer> failure, Throwable expectedThrowable) {
        Try<Integer> result = Try.success(123).combine(failure, (i, j) -> i + j);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(expectedThrowable.getClass()));
        assertThat(result.throwable().getMessage(), is(expectedThrowable.getMessage()));
        assertThat(result.throwable().getCause(), is(expectedThrowable.getCause()));
    }

    @ParameterizedTest
    @MethodSource("throwables")
    void combineOfSuccessWhereFunctionThrowsResultsInFailure(Throwable expectedThrowable) {
        Try<Integer> result = Try.success(123).combine(Try.success(456), (i, j) -> {
            throw expectedThrowable;
        });

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), sameInstance(expectedThrowable));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void combineOFailureAndSuccessReturnsFailure(Try<Integer> failure, Throwable expectedThrowable) {
        Try<Integer> result = failure.combine(Try.success(123), (i, j) -> i + j);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(expectedThrowable.getClass()));
        assertThat(result.throwable().getMessage(), is(expectedThrowable.getMessage()));
        assertThat(result.throwable().getCause(), is(expectedThrowable.getCause()));
    }

    @Test
    void combineOfErrorAndExceptionReturnsAggregatedError() {
        Error error = new InstantiationError();
        Exception exception = new IllegalArgumentException();
        Try<Object> result = Try.failure(error).combine(Try.failure(exception), (i, j) -> i);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(AggregateError.class));
        assertThat(result.throwable().getCause(), is(error));
        assertThat(result.throwable().getSuppressed(), arrayContaining(exception));
    }

    @Test
    void combineOfExceptionAndErrorReturnsAggregatedError() {
        Exception exception = new IllegalArgumentException();
        Error error = new InstantiationError();
        Try<Object> result = Try.failure(exception).combine(Try.failure(error), (i, j) -> i);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(AggregateError.class));
        assertThat(result.throwable().getCause(), is(error));
        assertThat(result.throwable().getSuppressed(), arrayContaining(exception));
    }

    @Test
    void combineOfExceptionAndExceptionReturnsAggregatedException() {
        Exception exception1 = new IllegalArgumentException();
        Exception exception2 = new IOException();
        Try<Object> result = Try.failure(exception1).combine(Try.failure(exception2), (i, j) -> i);

        assertThat(result.isFailure(), is(true));
        assertThat(result.throwable(), instanceOf(AggregateException.class));
        assertThat(result.throwable().getCause(), is(exception1));
        assertThat(result.throwable().getSuppressed(), arrayContaining(exception2));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseOnSuccess(Try<Integer> candidate, Integer expectedValue) {
        int result = candidate.orElse(expectedValue + 1000);

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseOnFailure(Try<Integer> candidate) {
        int result = candidate.orElse(1000);

        assertThat(result, is(1000));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseGetOnSuccess(Try<Integer> candidate, Integer expectedValue) {
        int result = candidate.orElseGet(() -> 1000);

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseGetOnSuccessWithNullSupplier(Try<Integer> candidate, Integer expectedValue) {
        int result = candidate.orElseGet(null);

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseGetOnFailure(Try<Integer> candidate) {
        int result = candidate.orElseGet(() -> 1000);

        assertThat(result, is(1000));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseGetOnFailureWithNullSupplier(Try<Integer> candidate) {
        calling(() -> candidate.orElseGet(null))
            .shouldThrow(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseTryOnSuccess(Try<Integer> candidate, Integer expectedValue) {
        Try<Integer> result = candidate.orElseTry(() -> expectedValue + 1000);

        assertThat(result.orElseThrow(), is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseTryOnSuccessWithSupplierThatThrows(Try<Integer> candidate, Integer expectedValue) {
        Try<Integer> result = candidate.orElseTry(() -> {
            throw new NoSuchElementException();
        });

        assertThat(result.orElseThrow(), is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseTryOnFailureSuccessfully(Try<Integer> candidate) {
        Try<Integer> result = candidate.orElseTry(() -> 8124);

        assertThat(result, is(Try.success(8124)));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseTryOnFailureWithNullSupplier(Try<Integer> candidate) {
        calling(() -> candidate.orElseTry(null))
            .shouldThrow(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseThrowWithSupplierOnSuccess(Try<Integer> candidate, Integer expectedValue) {
        int result = candidate.orElseThrow(IllegalArgumentException::new);

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void orElseThrowWithNullSupplierOnSuccess(Try<Integer> candidate, Integer expectedValue) {
        int result = candidate.orElseThrow(null);

        assertThat(result, is(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseThrowWithSuppliertOnFailure(Try<Integer> candidate) {
        calling(() -> candidate.orElseThrow(ArrayIndexOutOfBoundsException::new))
            .shouldThrow(ArrayIndexOutOfBoundsException.class);
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void orElseThrowWithNullSuppliertOnFailure(Try<Integer> candidate) {
        calling(() -> candidate.orElseThrow(null))
            .shouldThrow(NullPointerException.class);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("successesWithValue")
    void ifSuccessOnSuccess(Try<Integer> candidate, int expectedValue) {
        Consumer<Integer> consumer = mock(Consumer.class);
        Mockito.doNothing().when(consumer).accept(expectedValue);

        candidate.ifSuccess(consumer);

        verify(consumer).accept(expectedValue);
    }

    @ParameterizedTest
    @MethodSource("successes")
    void ifSuccessOnSuccessWithNullConsumer(Try<Integer> candidate) {
        calling(() -> candidate.ifSuccess(null))
            .shouldThrow(NullPointerException.class);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void ifSuccessOnFailure(Try<Integer> candidate) {
        Consumer<Integer> consumer = mock(Consumer.class);

        candidate.ifSuccess(consumer);

        verifyNoInteractions(consumer);
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void ifSuccessOnFailuerWithNullConsumer(Try<Integer> candidate) {
        candidate.ifSuccess(null);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("successes")
    void ifFaiureOnSuccess(Try<Integer> candidate) {
        Consumer<Throwable> consumer = mock(Consumer.class);

        candidate.ifFailure(consumer);

        verifyNoInteractions(consumer);
    }

    @ParameterizedTest
    @MethodSource("successes")
    void ifFailureOnSuccessWithNullConsumer(Try<Integer> candidate) {
        candidate.ifFailure(null);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void ifFailureOnFailure(Try<Integer> candidate) {
        Consumer<Throwable> consumer = mock(Consumer.class);
        Mockito.doNothing().when(consumer).accept(candidate.throwable());

        candidate.ifFailure(consumer);

        verify(consumer).accept(candidate.throwable());
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void ifFailureOnFailureWithNullConsumer(Try<Integer> candidate) {
        calling(() -> candidate.ifFailure(null))
            .shouldThrow(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void toOptionalOnSuccess(Try<Integer> candidate, int expectedValue) {
        Optional<Integer> result = candidate.toOptional();

        assertThat(result, is(Optional.of(expectedValue)));
    }

    @Test
    void toOptionalOnNullSuccess() {
        Try<Integer> candidate = Try.success(null);

        Optional<Integer> result = candidate.toOptional();

        assertThat(result, is(Optional.empty()));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void toOptionalOnFailure(Try<Integer> candidate) {
        Optional<Integer> result = candidate.toOptional();

        assertThat(result, is(Optional.empty()));
    }

    @ParameterizedTest
    @MethodSource("successesWithValue")
    void toStreamOnSuccess(Try<Integer> candidate, int expectedValue) {
        Stream<Integer> result = candidate.stream();

        assertThat(result.toArray(), is(toArray(expectedValue)));
    }

    @Test
    void toStreamOnNullSuccess() {
        Try<Integer> candidate = Try.success(null);

        Stream<Integer> result = candidate.stream();

        assertThat(result.count(), is(0L));
    }

    @ParameterizedTest
    @MethodSource("failuresWithRawThrowable")
    void toStreamOnFailure(Try<Integer> candidate) {
        Stream<Integer> result = candidate.stream();

        assertThat(result.count(), is(0L));
    }

    @Test
    void trySupplySuccess() {
        Try<Integer> result = Try.trySupply(() -> 124345);

        assertThat(result, is(Try.success(124345)));
    }

    @Test
    void trySupplyFails() {
        Try<Integer> result = Try.trySupply(TryTest::failChecked);

        assertThat(result.isFailure(), is(true));
    }

    @Test
    void trySupplyWithNullSupplier() {
        calling(() -> Try.trySupply(null))
            .shouldThrow(NullPointerException.class);
    }

    private static int fail() {
        throw new IllegalArgumentException();
    }

    private static int failChecked() throws IOException {
        throw new IOException();
    }

    private static int failError() {
        throw new IllegalAccessError();
    }

    private static Stream<Arguments> successesAndFailuresWithBoolean() {
        return Stream.of(
            arguments(Try.success(5), true),
            arguments(Try.trySupply(() -> 5), true),
            arguments(Try.failure(new RuntimeException()), false),
            arguments(Try.trySupply(TryTest::fail), false)
        );
    }

    private static Stream<Arguments> successesWithValue() {
        return Stream.of(
            arguments(Try.success(545), 545),
            arguments(Try.trySupply(() -> 12), 12)
        );
    }

    private static Stream<Arguments> failuresWithWrappedThrowable() {
        return Stream.of(
            arguments(Try.failure(new IllegalStateException()), new IllegalStateException()),
            arguments(Try.trySupply(TryTest::fail), new IllegalArgumentException()),
            arguments(Try.trySupply(TryTest::failChecked), new RuntimeException(new IOException())),
            arguments(Try.trySupply(TryTest::failError), new IllegalAccessError())
        );
    }

    private static Stream<Arguments> successes() {
        return Stream.of(
            arguments(Try.success(15)),
            arguments(Try.trySupply(() -> 25))
        );
    }

    private static Stream<Arguments> failuresWithRawThrowable() {
        return Stream.of(
            arguments(Try.failure(new IllegalStateException()), new IllegalStateException()),
            arguments(Try.trySupply(TryTest::fail), new IllegalArgumentException()),
            arguments(Try.trySupply(TryTest::failChecked), new IOException()),
            arguments(Try.trySupply(TryTest::failError), new IllegalAccessError())
        );
    }

    private static Stream<Arguments> throwables() {
        return Stream.of(
            arguments(new IllegalStateException()),
            arguments(new IllegalArgumentException()),
            arguments(new IOException()),
            arguments(new IllegalAccessError())
        );
    }

    private static Stream<Arguments> failuresWithRawException() {
        return Stream.of(
            arguments(Try.failure(new IllegalStateException()), new IllegalStateException()),
            arguments(Try.trySupply(TryTest::fail), new IllegalArgumentException()),
            arguments(Try.trySupply(TryTest::failChecked), new IOException())
        );
    }

    private static Stream<Arguments> failuresWithRawError() {
        return Stream.of(
            arguments(Try.failure(new InstantiationError()), new InstantiationError()),
            arguments(Try.trySupply(TryTest::failError), new IllegalAccessError())
        );
    }

    private static Stream<Arguments> successesWithValuePlus20() {
        return Stream.of(
            arguments(Try.success(37), 57),
            arguments(Try.trySupply(() -> 653), 673)
        );
    }
}
