/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.util.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ThrowableUtil extends UtilityClass {
    public static Throwable aggregate(Throwable... throwables) {
        List<Error> errors = Arrays.stream(throwables)
            .filter(t -> Error.class.isAssignableFrom(t.getClass()))
            .map(Error.class::cast)
            .collect(Collectors.toList());
        List<Exception> exceptions = Arrays.stream(throwables)
            .filter(t -> Exception.class.isAssignableFrom(t.getClass()))
            .map(Exception.class::cast)
            .collect(Collectors.toList());
        if (errors.isEmpty()) {
            return exceptions.size() == 1
                ? exceptions.get(0)
                : new AggregateException(exceptions);
        } else if (exceptions.isEmpty()) {
            return errors.size() == 1
                ? errors.get(0)
                : new AggregateError(errors);
        } else {
            return new AggregateError(errors, exceptions);
        }
    }
}
