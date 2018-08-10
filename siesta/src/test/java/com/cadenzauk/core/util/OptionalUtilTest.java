/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.cadenzauk.core.mockito.MockUtil.when;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OptionalUtilTest {
    @Mock
    private Consumer<String> consumer;
    @Mock
    private Runnable runnable;
    @Mock
    private Supplier<Optional<Integer>> supplier;

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", "empty"})
    @TestCase({"", "empty"})
    @TestCase({"\n", "empty"})
    @TestCase({"  ", "empty"})
    @TestCase({"\f\r\n ", "empty"})
    @TestCase({"x", "x"})
    void ofBlankable(String input, Optional<String> expected) {
        Optional<String> result = OptionalUtil.ofBlankable(input);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", "empty"})
    @TestCase({"", "empty"})
    @TestCase({"Some Value", "Some Value"})
    void ofOnly(List<String> input, Optional<String> expected) {
        Optional<String> result = OptionalUtil.ofOnly(input);

        assertThat(result, is(expected));
    }

    @Test
    void ofOnlyWithMultipleThrows() {
        calling(() -> OptionalUtil.ofOnly(ImmutableList.of("A", "B")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("expected one element but was: <A, B>");
    }

    @Test
    void withEmpty() {
        OptionalUtil.with(Optional.<String>empty())
            .ifPresent(consumer)
            .otherwise(runnable);

        verify(consumer, never()).accept(any());
        verify(runnable, times(1)).run();
    }

    @Test
    void withValue() {
        String value = RandomStringUtils.randomAscii(10);

        OptionalUtil.with(Optional.of(value))
            .ifPresent(consumer)
            .otherwise(runnable);

        verify(consumer, times(1)).accept(value);
        verify(runnable, never()).run();
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"empty", "empty", "empty"})
    @TestCase({"1", "empty", "1"})
    @TestCase({"empty", "2", "2"})
    @TestCase({"1", "2", "1"})
    void or(Optional<Integer> a, Optional<Integer> b, Optional<Integer> expected) {
        Optional<Integer> result = OptionalUtil.or(a, b);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"empty", "empty", "empty", "1"})
    @TestCase({"123", "empty", "123", "0"})
    @TestCase({"empty", "234", "234", "1"})
    @TestCase({"123", "234", "123", "0"})
    void orGet(Optional<Integer> a, Optional<Integer> b, Optional<Integer> expected, int expectedGets) {
        if (expectedGets > 0) {
            when(supplier.get()).thenReturn(b);
        }

        Optional<Integer> result = OptionalUtil.orGet(a, supplier);

        assertThat(result, is(expected));
        verify(supplier, times(expectedGets)).get();
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"empty", "java.lang.String", "empty"})
    @TestCase({"empty", "java.lang.Integer", "empty"})
    @TestCase({"empty", "java.lang.Object", "empty"})
    @TestCase({"abc", "java.lang.String", "abc"})
    @TestCase({"abc", "java.lang.Object", "abc"})
    @TestCase({"abc", "java.lang.Integer", "empty"})
    void asClass(Optional<String> input, Class<? extends String> target, Optional<String> expected) {
        Optional<?> result = OptionalUtil.as(target, input);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"empty", "java.lang.String", "empty"})
    @TestCase({"empty", "java.lang.Integer", "empty"})
    @TestCase({"empty", "java.lang.Object", "empty"})
    @TestCase({"abc", "java.lang.String", "abc"})
    @TestCase({"abc", "java.lang.Object", "abc"})
    @TestCase({"abc", "java.lang.Integer", "empty"})
    void asTypeToken(Optional<String> input, Class<? extends String> target, Optional<String> expected) {
        Optional<?> result = OptionalUtil.as(TypeToken.of(target), input);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", "java.lang.String", "empty"})
    @TestCase({"null", "java.lang.Integer", "empty"})
    @TestCase({"null", "java.lang.Object", "empty"})
    @TestCase({"abc", "java.lang.String", "abc"})
    @TestCase({"abc", "java.lang.Object", "abc"})
    @TestCase({"abc", "java.lang.Integer", "empty"})
    void asClassWithValue(String input, Class<? extends String> target, Optional<String> expected) {
        Optional<?> result = OptionalUtil.as(target, input);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", "java.lang.String", "empty"})
    @TestCase({"null", "java.lang.Integer", "empty"})
    @TestCase({"null", "java.lang.Object", "empty"})
    @TestCase({"abc", "java.lang.String", "abc"})
    @TestCase({"abc", "java.lang.Object", "abc"})
    @TestCase({"abc", "java.lang.Integer", "empty"})
    void asTypeTokenWithValue(String input, Class<? extends String> target, Optional<String> expected) {
        Optional<?> result = OptionalUtil.as(TypeToken.of(target), input);

        assertThat(result, is(expected));
    }
}