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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CompositeAutoCloseable implements UncheckedAutoCloseable {
    private final Object lock = new Object[0];
    private List<AutoCloseable> closeables = new ArrayList<>();

    @Override
    public void close() {
        List<AutoCloseable> copy = null;
        synchronized (lock) {
            if (closeables != null) {
                copy = closeables;
                closeables = null;
            }
        }
        if (copy != null) {
            closeAll(copy);
        }
    }

    public <T extends AutoCloseable> T add(T closeable) {
        Objects.requireNonNull(closeable, "closeable");
        boolean alreadyClosed = false;
        synchronized (lock) {
            if (closeables == null) {
                alreadyClosed = true;
            } else {
                closeables.add(closeable);
            }
        }
        if (alreadyClosed) {
            closeOne(closeable);
        }
        return closeable;
    }

    private void closeAll(List<AutoCloseable> autoCloseables) {
        Exception[] exceptions = IntStream.range(0, autoCloseables.size())
            .mapToObj(i -> tryClose(autoCloseables.get(autoCloseables.size() - i - 1)))
            .filter(Objects::nonNull)
            .toArray(Exception[]::new);
        if (exceptions.length > 0) {
            RuntimeException runtimeException = new RuntimeException("One or more exceptions occurred while closing.");
            Arrays.stream(exceptions).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
    }

    private Exception tryClose(AutoCloseable closeable) {
        try {
            closeable.close();
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    private void closeOne(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
