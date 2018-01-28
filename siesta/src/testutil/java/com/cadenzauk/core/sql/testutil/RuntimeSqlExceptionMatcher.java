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

package com.cadenzauk.core.sql.testutil;

import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.util.OptionalUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static com.cadenzauk.core.util.OptionalUtil.with;

public class RuntimeSqlExceptionMatcher extends BaseMatcher<RuntimeSqlException> {
    private final Class<? extends RuntimeSqlException> expectedClass;

    public RuntimeSqlExceptionMatcher(Class<? extends RuntimeSqlException> expectedClass) {
        this.expectedClass = expectedClass;
    }

    @Override
    public boolean matches(Object item) {
        return item != null && expectedClass.isAssignableFrom(item.getClass());
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("<null>");
        } else {
            with(OptionalUtil.as(RuntimeSqlException.class, item))
                .ifPresent(s -> mismatchDescription.appendText(String.format("%s with SQL state %s and error code %d", s.getClass().getName(), s.sqlState(), s.errorCode())))
                .otherwise(() -> mismatchDescription.appendText(item.getClass().getName()));
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expectedClass.getName());
    }

    public static RuntimeSqlExceptionMatcher subclass(Class<? extends RuntimeSqlException> expectedClass) {
        return new RuntimeSqlExceptionMatcher(expectedClass);
    }
}
