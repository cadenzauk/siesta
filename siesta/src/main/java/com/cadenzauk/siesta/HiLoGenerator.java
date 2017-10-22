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

package com.cadenzauk.siesta;

import com.cadenzauk.core.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class HiLoGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(HiLoGenerator.class);
    private static final Lazy<ForkJoinPool> COMMON_POOL = new Lazy<>(() -> new ForkJoinPool(2));

    private final Sequence<Long> sequence;
    private final long hiMultiplier;
    private final long increment;
    private final long offset;
    private final long loSize;
    private final long threshold;
    private final Executor executor;

    private final Object lock = new Object();
    private long loValue = 0;
    private CompletableFuture<Long> currHi;
    private CompletableFuture<Long> nextHi = null;

    private HiLoGenerator(Builder builder) {
        sequence = builder.sequence;
        hiMultiplier = builder.hiMultiplier;
        increment = builder.increment;
        offset = builder.offset;
        loSize = builder.loSize;
        threshold = builder.threshold;
        executor = builder.executor.orElseGet(COMMON_POOL::get);

        initialize();
    }

    private void initialize() {
        currHi = nextHiAsync();
        if (loValue >= threshold) {
            nextHi = nextHiAsync();
        }
    }

    @Override
    public String toString() {
        return String.format("HiLoGenerator[%s]", sequence.name());
    }

    private CompletableFuture<Long> nextHiAsync() {
        return CompletableFuture.supplyAsync(() -> {
            long nextVal = sequence.single();
            LOG.debug("{} <= {}", this, nextVal);
            return nextVal;
        }, executor);
    }

    public long single() {
        long hi, lo;
        synchronized (lock) {
            hi = currHi.join() - 1;
            lo = loValue;
            loValue += increment;
            if ((loValue >= threshold || loValue >= loSize) && nextHi == null) {
                nextHi = nextHiAsync();
            }
            if (loValue >= loSize) {
                loValue = 0;
                currHi = nextHi;
                nextHi = loValue >= threshold
                    ? nextHiAsync()
                    : null;
            }
        }
        long result = hi * hiMultiplier + lo + offset;
        LOG.trace("{} => {}", this, result);
        return result;
    }

    public static Builder newBuilder(Sequence<Long> sequence) {
        return new Builder(sequence);
    }

    public static final class Builder {
        private final Sequence<Long> sequence;
        private long loSize = 1000;
        private long hiMultiplier = 1000;
        private long increment = 1;
        private long offset = 1;
        private long threshold = 900;
        private Optional<Executor> executor = Optional.empty();

        private Builder(Sequence<Long> sequence) {
            this.sequence = sequence;
        }

        public Builder loSize(long val) {
            loSize = val;
            hiMultiplier = val;
            threshold = val * 9 / 10;
            return this;
        }

        public Builder hiMultiplier(long val) {
            hiMultiplier = val;
            return this;
        }

        public Builder increment(long val) {
            increment = val;
            return this;
        }

        public Builder offset(long val) {
            offset = val;
            return this;
        }

        public Builder threshold(long val) {
            threshold = val;
            return this;
        }

        public Builder executor(Executor val) {
            executor = Optional.ofNullable(val);
            return this;
        }

        public HiLoGenerator build() {
            return new HiLoGenerator(this);
        }
    }
}
