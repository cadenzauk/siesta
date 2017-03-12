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

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.TypeUtil;

import javax.persistence.AttributeConverter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DataTypeRegistry {
    private Map<Class<?>,DataType<?>> entries = new ConcurrentHashMap<>();

    public DataTypeRegistry() {
        register(DataType.BIG_DECIMAL);
        register(DataType.BYTE);
        register(DataType.BYTE_ARRAY);
        register(DataType.DOUBLE);
        register(DataType.FLOAT);
        register(DataType.INTEGER);
        register(DataType.LOCAL_DATE);
        register(DataType.LONG);
        register(DataType.SHORT);
        register(DataType.STRING);
        register(DataType.ZONED_DATE_TIME);
    }

    public <T> void register(DataType<T> dataType) {
        entries.put(dataType.javaClass(), dataType);
    }

    public <T> void register(AttributeConverter<T,?> converter) {
        DataType<T> dataType = DataType.fromConverter(converter);
        entries.put(dataType.javaClass(), dataType);
    }

    public <T> Optional<DataType<T>> dataTypeOf(T value) {
        Objects.requireNonNull(value);
        Class<T> klass = ClassUtil.forObject(value);
        return dataTypeOf(klass);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<DataType<T>> dataTypeOf(Class<T> javaClass) {
        return Optional.ofNullable(entries.get(TypeUtil.boxedType(javaClass)))
            .map(dataType -> (DataType<T>) dataType);
    }
}
