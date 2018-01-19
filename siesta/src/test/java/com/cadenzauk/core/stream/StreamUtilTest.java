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

package com.cadenzauk.core.stream;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static co.unruly.matchers.StreamMatchers.contains;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StreamUtilTest extends MockitoTest {
    @Mock
    private Stream<String> stream;

    @Mock
    private Iterator<String> iterator;

    @Test
    void isUtility() {
        assertThat(StreamUtil.class, isUtilityClass());
    }

    @Test
    void ofEmpty() {
        Stream<Object> result = StreamUtil.of(Optional.empty());

        assertThat(result.count(), is(0L));
    }

    @Test
    void ofValue() {
        Stream<String> result = StreamUtil.of(Optional.of("ABC"));

        assertThat(result.toArray(String[]::new), arrayContaining("ABC"));
    }

    @Test
    void zipWithIndex() {
        Stream<Tuple2<String, Long>> result = StreamUtil.zipWithIndex(Stream.of("a", "b", "c"));

        assertThat(result, contains(Tuple.of("a", 0L), Tuple.of("b", 1L), Tuple.of("c", 2L)));
    }

    @Test
    void zipWithIndexLazy() {
        when(stream.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true);
        when(iterator.next()).thenReturn("a");
        Stream<Tuple2<String, Long>> result = StreamUtil.zipWithIndex(stream).limit(1);

        assertThat(result, contains(Tuple.of("a", 0L)));
        verify(iterator, times(1)).hasNext();
        verify(iterator, times(1)).next();
        verifyNoMoreInteractions(iterator);
    }

    @Test
    void mapWithIndex() {
        Stream<String> result = StreamUtil.mapWithIndex(Stream.of("a", "bc", "def"), (s, i) -> s + "." + (i + 1));

        assertThat(result, contains("a.1", "bc.2", "def.3"));
    }

    @Test
    void toByteArrayOfNonEmptyStream() {
        byte[] result = StreamUtil.toByteArray(IntStream.of(1, -1, 255, 0).parallel());

        assertThat(result.length, is(4));
        assertThat(result[0], is((byte)0x01));
        assertThat(result[1], is((byte)0xff));
        assertThat(result[2], is((byte)0xff));
        assertThat(result[3], is((byte)0x00));
    }

    @Test
    void toByteArrayOfEmptyStream() {
        byte[] result = StreamUtil.toByteArray(IntStream.of());

        assertThat(result.length, is(0));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"256"})
    @TestCase({"-129"})
    void toByteArrayThrowsForOutOfRange(int value) {
        calling(() -> StreamUtil.toByteArray(IntStream.of(value)))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Cannot convert " + value + " to a byte.");
    }

}