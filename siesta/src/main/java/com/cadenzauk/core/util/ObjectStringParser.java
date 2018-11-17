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

import com.cadenzauk.core.reflect.TypeInfo;
import com.cadenzauk.core.reflect.util.ClassUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.stream.StreamUtil.toByteArray;
import static java.util.stream.Collectors.toCollection;

public class ObjectStringParser implements StringParser<Object> {
    private final Map<Class<?>,StringParser<?>> parsers = new ConcurrentHashMap<>();
    private final String nullValue;
    private final String emptyValue;

    public ObjectStringParser(String nullValue, String emptyValue) {
        this.nullValue = nullValue;
        this.emptyValue = emptyValue;

        register(String.class, Function.identity());

        register(Boolean.class, nullOr(Boolean::parseBoolean));
        register(Byte.class, nullOr(this::parseByte));
        register(Short.class, nullOr(Short::parseShort));
        register(Integer.class, nullOr(Integer::parseInt));
        register(Long.class, nullOr(Long::parseLong));

        register(Class.class, nullOr(className -> ClassUtil.forName(className).orElseThrow(IllegalArgumentException::new)));

        register(Boolean.TYPE, defaultOr(false, Boolean::parseBoolean));
        register(Byte.TYPE, defaultOr((byte) 0, this::parseByte));
        register(Short.TYPE, defaultOr((short) 0, Short::parseShort));
        register(Integer.TYPE, defaultOr(0, Integer::parseInt));
        register(Long.TYPE, defaultOr(0L, Long::parseLong));

        register(byte[].class, this::parseByteArray);
        register(String[].class, this::parseStringArray);

        register(List.class, this::parseArrayList);
        register(ArrayList.class, this::parseArrayList);

        register(Optional.class, this::parseOptional);

        register(Enum.class, ObjectStringParser::parseEnum);
    }

    public Object parse(String value, TypeInfo typeInfo) {
        if (value == null || value.equals(nullValue)) {
            return null;
        }
        return findParser(typeInfo).parse(value, typeInfo);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends Enum<T>> Enum<T> parseEnum(String value, TypeInfo typeInfo) {
        return Enum.valueOf((Class<Enum>) typeInfo.rawClass(), value);
    }

    private <T> void register(Class<T> targetClass, Function<String,T> parser) {
        parsers.put(targetClass, (value, typeInfo) -> parser.apply(value));
    }

    private <T> void register(Class<T> targetClass, StringParser<? extends T> parser) {
        parsers.put(targetClass, parser);
    }

    private static <T> StringParser<T> nullOr(Function<String,T> parser) {
        return (value, typeInfo) -> StringUtils.isBlank(value) ? null : parser.apply(value);
    }

    private static <T> StringParser<T> defaultOr(T defaultValue, Function<String,T> parser) {
        return (value, typeInfo) -> StringUtils.isBlank(value) ? defaultValue : parser.apply(value);
    }

    private StringParser<?> findParser(TypeInfo typeInfo) {
        return ClassUtil.superclasses(typeInfo.rawClass())
            .map(parsers::get)
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find a parser for " + typeInfo));
    }

    @SuppressWarnings("OptionalAssignedToNull")
    private Optional<Object> parseOptional(String value, TypeInfo typeInfo) {
        return value == null || value.equals(nullValue)
            ? null
            : value.equals(emptyValue)
            ? Optional.empty()
            : Optional.ofNullable(parse(value, typeInfo.actualTypeArgument(0)));
    }

    private byte parseByte(String byteString) {
        byteString = byteString.trim();
        if (byteString.startsWith("0x") && byteString.length() > 2) {
            return (byte) (int) Integer.valueOf(byteString.substring(2), 16);
        } else if (byteString.startsWith("0") && byteString.length() > 1) {
            return (byte) (int) Integer.valueOf(byteString.substring(1), 8);
        }
        return (byte) (int) Integer.valueOf(byteString);
    }

    private ArrayList<Object> parseArrayList(String value, TypeInfo typeInfo) {
        return parseStream(value, typeInfo.actualTypeArgument(0)).collect(toCollection(ArrayList::new));
    }

    private byte[] parseByteArray(String s, TypeInfo typeInfo) {
        return s == null || s.equals(nullValue)
            ? null
            : toByteArray(parseStream(s, typeInfo.arrayComponentType()).mapToInt(v -> (byte) v));
    }

    private String[] parseStringArray(String s, TypeInfo typeInfo) {
        return s == null || s.equals(nullValue)
            ? null
            : s.equals(emptyValue)
            ? new String[0]
            : s.equals("")
            ? new String[] {""}
            : parseStream(s, typeInfo.arrayComponentType()).toArray(String[]::new);
    }

    private Object[] parseArray(String value, TypeInfo typeInfo) {
        return parseStream(value, typeInfo.arrayComponentType()).toArray();
    }

    private Stream<Object> parseStream(String value, TypeInfo typeInfo) {
        if (StringUtils.isBlank(value)) {
            return Stream.empty();
        }
        if (value.startsWith("{") && value.endsWith("}")) {
            value = value.substring(1, value.length() - 1);
        }
        String[] split = value.split(",", -1);
        return Arrays.stream(split)
            .map(v -> parse(v, typeInfo));
    }
}
