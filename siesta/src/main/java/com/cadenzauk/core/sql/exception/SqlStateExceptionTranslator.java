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

package com.cadenzauk.core.sql.exception;

import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.stream.StreamUtil;

import java.sql.SQLException;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class SqlStateExceptionTranslator implements SqlExceptionTranslator {
    private final PriorityQueue<ExceptionMapping> mappings = new PriorityQueue<>(100, comparing(ExceptionMapping::priority));

    @Override
    public RuntimeSqlException translate(String statement, SQLException cause) {
        return mappings
            .stream()
            .flatMap(m -> StreamUtil.of(m.map(statement, cause)))
            .findFirst()
            .orElseGet(() -> new RuntimeSqlException(statement, cause));
    }

    @SuppressWarnings("UnusedReturnValue")
    public SqlStateExceptionTranslator register(String sqlState, SqlExceptionConstructor constructor) {
        mappings.add(new ExceptionMapping(sqlState, constructor));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SqlStateExceptionTranslator register(int errorCode, SqlExceptionConstructor constructor) {
        mappings.add(new ExceptionMapping(".*", errorCode, constructor));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SqlStateExceptionTranslator register(String sqlState, int errorCode, SqlExceptionConstructor constructor) {
        mappings.add(new ExceptionMapping(sqlState, errorCode, constructor));
        return this;
    }

    private static class ExceptionMapping {
        private final Predicate<SQLException> applies;
        private final SqlExceptionConstructor constructor;
        private final int priority;

        private ExceptionMapping(String sqlState, SqlExceptionConstructor constructor) {
            Predicate<String> sqlStateMatches = Pattern.compile("^" + sqlState + "$").asPredicate();
            this.applies = e -> sqlStateMatches.test(e.getSQLState());
            this.constructor = constructor;
            priority = 1;
        }

        private ExceptionMapping(String sqlState, int errorCode, SqlExceptionConstructor constructor) {
            Predicate<String> sqlStateMatches = Pattern.compile("^" + sqlState + "$").asPredicate();
            this.applies = e -> sqlStateMatches.test(e.getSQLState()) && e.getErrorCode() == errorCode;
            this.constructor = constructor;
            priority = 1;
        }

        private int priority() {
            return priority;
        }

        private Optional<RuntimeSqlException> map(String statement, SQLException cause) {
            return applies.test(cause)
                ? Optional.of(constructor.construct(statement, cause))
                : Optional.empty();
        }
    }
}
