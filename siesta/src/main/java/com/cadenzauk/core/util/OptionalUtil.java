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

package com.cadenzauk.core.util;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class OptionalUtil extends UtilityClass {
    public static class OptionalWrapper<T> {
        private final Optional<T> optional;

        public OptionalWrapper(Optional<T> optional) {
            this.optional = optional;
        }

        public WasPresent ifPresent(Consumer<? super T> consumer) {
            optional.ifPresent(consumer);
            return new WasPresent(optional.isPresent());
        }
    }

    public static class WasPresent {
        private final boolean present;

        private WasPresent(boolean present) {
            this.present = present;
        }

        public void otherwise(Runnable runnable) {
            if (!present) {
                runnable.run();
            }
        }
    }

    public static Optional<String> ofBlankable(String s) {
        return StringUtils.isBlank(s) ? Optional.empty() : Optional.of(s);
    }

    public static <T> Optional<T> ofOnly(Iterable<T> iterable) {
        return Optional.ofNullable(Iterables.getOnlyElement(iterable, null));
    }

    public static <T> OptionalWrapper<T> with(Optional<T> optional) {
        return new OptionalWrapper<>(optional);
    }

    public static <T> Optional<T> orGet(Optional<T> optional, Supplier<? extends Optional<T>> supplier) {
        return optional.isPresent()
            ? optional
            : supplier.get();
    }

    public static <T, U> Optional<U> as(Class<U> targetClass, Optional<T> source) {
        return source
            .filter(v -> targetClass.isAssignableFrom(v.getClass()))
            .map(targetClass::cast);
    }

    @SuppressWarnings("unchecked")
    public static <T, U> Optional<U> as(TypeToken<U> targetType, Optional<T> source) {
        Class<U> rawType = (Class<U>) targetType.getRawType();
        return as(rawType, source);
    }

    public static <T, U> Optional<U> as(Class<U> targetClass, T source) {
        return Optional.ofNullable(source)
            .filter(v -> targetClass.isAssignableFrom(v.getClass()))
            .map(targetClass::cast);
    }

    @SuppressWarnings("unchecked")
    public static <T, U> Optional<U> as(TypeToken<U> targetType, T source) {
        Class<U> rawType = (Class<U>) targetType.getRawType();
        return as(rawType, source);
    }
}
