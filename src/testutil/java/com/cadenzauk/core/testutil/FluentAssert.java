/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.testutil;

import com.cadenzauk.core.util.OptionalUtil;
import org.hamcrest.Matcher;

import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;

public class FluentAssert {
    public static Result<Void> calling(Runnable runnable) {
        try {
            runnable.run();
            return Result.success(null);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public static <T> Result<T> calling(Supplier<T> supplier) {
        try {
            T result = supplier.get();
            return Result.success(result);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public static class ThrowableMatcher<T extends Throwable> {
        private final T actual;

        public ThrowableMatcher(T actual) {
            this.actual = actual;
        }

        public ThrowableMatcher<T> withMessage(Matcher<String> matcher) {
            assertThat(actual.getMessage(), matcher);
            return this;
        }

        public <C extends Throwable> ThrowableMatcher<C> withCause(Class<C> causeClass) {
            return OptionalUtil.as(causeClass, Optional.ofNullable(actual.getCause()))
                .map(ThrowableMatcher::new)
                .orElseThrow(() -> new AssertionError("Expected: " + causeClass + " but: " + actual.getCause()));
        }
    }

    public static class Result<T> {
        private final T result;
        private final Optional<Throwable> throwable;

        private Result(T result, Optional<Throwable> throwable) {
            this.result = result;
            this.throwable = throwable;
        }

        public Result<T> hasResult(Matcher<T> matcher) {
            assertThat(result, matcher);
            return this;
        }

        public <U extends Throwable> ThrowableMatcher<U> shouldThrow(Class<U> throwableClass) {
            return
                throwable
                    .map(t -> OptionalUtil.as(throwableClass, t)
                        .map(ThrowableMatcher::new)
                        .orElseThrow(() -> new AssertionError("Expected exception: " + throwableClass + " but: " + t)))
                    .orElseThrow(() -> new AssertionError("Expected exception: " + throwableClass + " but no exception thrown."));
        }

        public static <T> Result<T> success(T result) {
            return new Result<>(result, Optional.empty());
        }

        public static <T> Result<T> failure(Throwable t) {
            return new Result<>(null, Optional.of(t));
        }
    }
}
