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

import com.cadenzauk.core.stream.StreamUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;

public final class IterableUtil extends UtilityClass {
    public static <T> T single(Iterable<T> iterable) {
        Objects.requireNonNull(iterable);
        Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            throw new NoSuchElementException("Expected a single element but was empty.");
        }
        T result = iterator.next();
        if (iterator.hasNext()) {
            String values = StreamUtil.mapWithIndex(
                StreamSupport.stream(iterable.spliterator(), false).limit(4),
                (v, i) -> i == 3 ? "..." : v.toString())
                .collect(joining(", "));
            throw new IllegalArgumentException(String.format("Expected a single element but was <%s>.", values));
        }
        return result;
    }
}
