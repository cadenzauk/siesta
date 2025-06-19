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

import com.cadenzauk.core.lang.ThrowableUtil;
import com.cadenzauk.core.util.Try;
import com.cadenzauk.core.util.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

public final class CompletableFutureUtil extends UtilityClass {
    public static <T> CompletableFuture<List<T>> allAsList(List<CompletableFuture<T>> futures) {
        return allAsList(futures.stream());
    }

    public static <T> CompletableFuture<List<T>> allAsList(Stream<CompletableFuture<T>> futures) {
        List<CompletableFuture<Try<T>>> list = futures
            .map(f -> f
                .thenApply(Try::success)
                .exceptionally(Try::failure)
            ).collect(Collectors.toList());
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new))
            .thenCompose(x -> {
                List<Try<T>> results = list
                    .stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
                Throwable[] exceptions = results.stream()
                    .flatMap(t -> t.isFailure() ? Stream.of(unwrap(t.throwable())) : Stream.empty())
                    .toArray(Throwable[]::new);
                if (exceptions.length > 0) {
                    return failedFuture(ThrowableUtil.aggregate(exceptions));
                } else {
                    return completedFuture(results.stream().flatMap(Try::stream).collect(Collectors.toList()));
                }
            }
        );
    }

    private static Throwable unwrap(Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            return throwable.getCause();
        } else {
            return throwable;
        }
    }

    public static <T> Optional<Throwable> exception(CompletableFuture<T> future) {
        if (future.isCompletedExceptionally()) {
            try {
                future.join();
                return Optional.empty();
            } catch (CompletionException e) {
                return Optional.ofNullable(e.getCause());
            }
        } else {
            return Optional.empty();
        }
    }
}
