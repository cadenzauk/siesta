/*
 * Copyright (c) 2025 Cadenza United Kingdom Limited
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

package com.cadenzauk.core.concurrent;

import com.cadenzauk.core.lang.AggregateError;
import com.cadenzauk.core.lang.AggregateException;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletionException;

import static com.cadenzauk.core.concurrent.CompletableFutureUtil.allAsList;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class CompletableFutureUtilTest {

    @Test
    void allAsListCompletesAll() {
        List<Integer> result = allAsList(ImmutableList.of(completedFuture(1), completedFuture(2), completedFuture(3))).join();

        assertThat(result, contains(1, 2, 3));
    }

    @Test
    void allAsListCompleteWithFailureIfAnyFail() {
        calling(() -> allAsList(ImmutableList.of(completedFuture(1), failedFuture(new IllegalStateException("failed")), completedFuture(3))).join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalStateException.class)
            .withMessage("failed");
    }

    @Test
    void allAsListCompletesWithFailureIfMultipleFailWithExceptions() {
        IllegalStateException illegalStateException = new IllegalStateException("failed");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("also failed");
        calling(() -> allAsList(ImmutableList.of(completedFuture(1), failedFuture(illegalStateException), failedFuture(illegalArgumentException))).join())
            .shouldThrow(CompletionException.class)
            .withCause(AggregateException.class)
            .with(AggregateException::causes, contains(illegalStateException, illegalArgumentException));
    }

    @Test
    void allAsListCompletesWithFailureIfMultipleFailWithErrors() {
        NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("error");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("also failed");
        calling(() -> allAsList(ImmutableList.of(completedFuture(1), failedFuture(noClassDefFoundError), failedFuture(illegalArgumentException))).join())
            .shouldThrow(CompletionException.class)
            .withCause(AggregateError.class)
            .with(AggregateError::errors, contains(noClassDefFoundError))
            .with(AggregateError::exceptions, contains(illegalArgumentException));
    }
}
