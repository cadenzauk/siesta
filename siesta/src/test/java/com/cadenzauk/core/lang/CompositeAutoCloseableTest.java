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

package com.cadenzauk.core.lang;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CompositeAutoCloseableTest {
    @Test
    void canCloseWithCustomCloser() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        List<?> list = sut.add(mock(List.class), List::clear);

        sut.close();

        verify(list).clear();
    }

    @Test
    void closeClosesAll() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        UncheckedAutoCloseable closeable1 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable2 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable3 = sut.add(mock(UncheckedAutoCloseable.class));

        sut.close();

        verify(closeable1).close();
        verify(closeable2).close();
        verify(closeable3).close();
    }

    @Test
    void doubleCloseClosesAllOnlyOnce() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        UncheckedAutoCloseable closeable1 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable2 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable3 = sut.add(mock(UncheckedAutoCloseable.class));

        sut.close();
        sut.close();

        verify(closeable1).close();
        verify(closeable2).close();
        verify(closeable3).close();
    }

    @Test
    void closeClosesAllIfSomeThrowAndExceptionsReported() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        UncheckedAutoCloseable closeable1 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable2 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable3 = sut.add(mock(UncheckedAutoCloseable.class));
        UncheckedAutoCloseable closeable4 = sut.add(mock(UncheckedAutoCloseable.class));
        IllegalStateException illegalStateException2 = new IllegalStateException("can't close 2");
        IllegalStateException illegalStateException4 = new IllegalStateException("can't close 4");
        Mockito.doThrow(illegalStateException2).when(closeable2).close();
        Mockito.doThrow(illegalStateException4).when(closeable4).close();

        calling(sut::close)
            .shouldThrow(RuntimeException.class)
            .withSuppressed(is(toArray(illegalStateException4, illegalStateException2)))
            .withMessage(is("One or more exceptions occurred while closing."));

        verify(closeable1).close();
        verify(closeable2).close();
        verify(closeable3).close();
        verify(closeable3).close();
    }

    @Test
    void addWhenClosedCloses() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        sut.close();
        UncheckedAutoCloseable closeable = mock(UncheckedAutoCloseable.class);

        sut.add(closeable);

        verify(closeable).close();
    }

    @Test
    void addWhenClosedButCloseThrows() {
        CompositeAutoCloseable sut = new CompositeAutoCloseable();
        sut.close();
        UncheckedAutoCloseable closeable = mock(UncheckedAutoCloseable.class);
        IllegalStateException illegalStateException = new IllegalStateException("can't close");
        Mockito.doThrow(illegalStateException).when(closeable).close();

        calling(() -> sut.add(closeable))
            .shouldThrow(IllegalStateException.class)
            .withMessage(is("can't close"));

        verify(closeable).close();
    }
}