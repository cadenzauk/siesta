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

package com.cadenzauk.siesta.type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TypeAdapterRegistry {
    private final Map<Class<?>,TypeAdapter<?>> types = new ConcurrentHashMap<>();

    public TypeAdapterRegistry() {
        this
            .register(BigDecimal.class, new DefaultBigDecimalAdapter())
            .register(Byte.class, new DefaultByteTypeAdapter())
            .register(byte[].class, new DefaultBinaryTypeAdapter())
            .register(Double.class, new DefaultDoubleTypeAdapter())
            .register(Float.class, new DefaultFloatTypeAdapter())
            .register(Integer.class, new DefaultIntegerTypeAdapter())
            .register(LocalDate.class, new DefaultLocalDateTypeAdapter())
            .register(LocalDateTime.class, new DefaultLocalDateTimeTypeAdapter())
            .register(LocalTime.class, new DefaultLocalTimeTypeAdapter())
            .register(Long.class, new DefaultLongTypeAdapter())
            .register(Short.class, new DefaultShortTypeAdapter())
            .register(String.class, new DefaultStringTypeAdapter())
            .register(ZonedDateTime.class, new DefaultZonedDateTimeTypeAdapter())
        ;
    }

    public <T> TypeAdapterRegistry register(Class<T> javaClass, TypeAdapter<T> type) {
        types.put(javaClass, type);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> get(Class<T> javaClass) {
        return Optional.ofNullable(types.get(javaClass))
            .map(dt -> (TypeAdapter<T>) dt)
            .orElseThrow(() -> new IllegalArgumentException("No dialect type for " + javaClass + " registered"));
    }
}
