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

package com.cadenzauk.siesta;

import com.cadenzauk.core.MockitoTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HiLoGeneratorTest extends MockitoTest {
    @Mock
    private Sequence<Long> sequence;

    @Test
    void simpleSequenceWithMultiplier() {
        when(sequence.single()).thenReturn(3L, 4L, 5L);
        when(sequence.name()).thenReturn("BOB");
        HiLoGenerator sut = HiLoGenerator.newBuilder(sequence)
            .loSize(4)
            .threshold(4)
            .build();

        long[] result = IntStream.range(0, 10)
            .mapToLong(x -> sut.single())
            .toArray();

        assertThat(result, is(LongStream.range(9, 19).toArray()));
        verify(sequence, times(3)).single();
        verifyNoMoreInteractions(sequence);
    }

    @Test
    void simpleSequenceWithMultiplierOfOne() {
        when(sequence.single()).thenReturn(12L, 16L, 20L);
        when(sequence.name()).thenReturn("BOB");
        HiLoGenerator sut = HiLoGenerator.newBuilder(sequence)
            .loSize(4)
            .hiMultiplier(1)
            .threshold(4)
            .build();

        long[] result = IntStream.range(0, 10)
            .mapToLong(x -> sut.single())
            .toArray();

        assertThat(result, is(LongStream.range(12, 22).toArray()));
        verify(sequence, times(3)).single();
        verifyNoMoreInteractions(sequence);
    }

    @Test
    void simpleSequenceWithOffsetAndIncrement() {
        when(sequence.single()).thenReturn(12L, 13L, 14L);
        when(sequence.name()).thenReturn("BOB");
        HiLoGenerator sut = HiLoGenerator.newBuilder(sequence)
            .loSize(100)
            .increment(20)
            .offset(7)
            .threshold(100)
            .build();

        long[] result = IntStream.range(0, 9)
            .mapToLong(x -> sut.single())
            .toArray();

        assertThat(result, is(LongStream.range(55, 64).map(x -> x * 20 + 7).toArray()));
        verify(sequence, times(2)).single();
        verifyNoMoreInteractions(sequence);
    }

    @Test
    void simpleSequenceWithMultiplierOfOneMultithreaded() {
        when(sequence.single()).thenReturn(12L, 16L, 20L, 24L);
        when(sequence.name()).thenReturn("BOB");
        HiLoGenerator sut = HiLoGenerator.newBuilder(sequence)
            .loSize(4)
            .hiMultiplier(1)
            .threshold(4)
            .build();

        long[] result = IntStream.range(0, 10)
            .parallel()
            .mapToLong(x -> sut.single())
            .sorted()
            .toArray();

        assertThat(result, is(LongStream.range(12, 22).toArray()));
        verify(sequence, times(3)).single();
        verifyNoMoreInteractions(sequence);
    }
}