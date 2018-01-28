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

package com.cadenzauk.core.stream;

import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.util.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtil extends UtilityClass {
    public static <T> Stream<T> ofNullable(T value) {
        return value == null ? Stream.empty() : Stream.of(value);
    }

    public static <T> Stream<T> of(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    public static <T> Stream<Tuple2<T, Long>> zipWithIndex(Stream<? extends T> stream) {
        Iterator<Tuple2<T,Long>> iterator = new Iterator<Tuple2<T,Long>>() {
            private final Iterator<? extends T> streamIterator = stream.iterator();
            private long index = 0;

            @Override
            public boolean hasNext() {
                return streamIterator.hasNext();
            }

            @Override
            public Tuple2<T,Long> next() {
                return Tuple.of(streamIterator.next(), index++);
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    public static <T, R> Stream<R> mapWithIndex(Stream<T> input, BiFunction<? super T, Long, ? extends R> mapping) {
        return zipWithIndex(input).map(tuple -> tuple.map(mapping));
    }

    public static byte[] toByteArray(IntStream stream) {
        return stream.collect(ByteArrayOutputStream::new, (baos, i) -> baos.write(toByte(i)),
            (baos1, baos2) -> baos1.write(baos2.toByteArray(), 0, baos2.size()))
            .toByteArray();
    }

    private static byte toByte(int i) {
        if (i < Byte.MIN_VALUE || i > 255) {
            throw new IllegalArgumentException("Cannot convert " + i + " to a byte.");
        }
        return (byte) i;
    }
}
