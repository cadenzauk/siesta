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

package com.cadenzauk.core.mockito;

import com.cadenzauk.core.util.UtilityClass;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.stream.Stream;

import static com.cadenzauk.core.util.ArrayUtil.arrayOf;
import static org.apache.commons.lang3.ArrayUtils.remove;

public final class MockUtil extends UtilityClass  {
    public static <T> OngoingStubbingWrapper<T> when(T invocation) {
        return new OngoingStubbingWrapper<>(Mockito.when(invocation));
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class OngoingStubbingWrapper<T> {
        private OngoingStubbing<T> stubbing;

        private OngoingStubbingWrapper(OngoingStubbing<T> stubbing) {
            this.stubbing = stubbing;
        }

        public OngoingStubbingWrapper<T> thenReturn(T[] array) {
            stubbing = stubbing.thenReturn(array[0], remove(array, 0));
            return this;
        }

        public OngoingStubbingWrapper<T> thenReturn(int n, T value) {
            stubbing = stubbing.thenReturn(value, arrayOf(n - 1, value));
            return this;
        }

        public OngoingStubbingWrapper<T> thenReturn(Stream<T> stream) {
            stubbing = stream.reduce(stubbing, OngoingStubbing::thenReturn, (x, y) -> { throw new IllegalArgumentException(); });
            return this;
        }

        public OngoingStubbingWrapper<T> thenReturn(T value) {
            stubbing = stubbing.thenReturn(value);
            return this;
        }
    }
}
