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

package com.cadenzauk.core.lang;

import com.cadenzauk.core.util.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public final class StringUtil extends UtilityClass {
    public static String uppercaseFirst(String s) {
        if (StringUtils.isEmpty(s)) {
            return "";
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String camelToUpper(String camelName) {
        if (StringUtils.isEmpty(camelName)) {
            return "";
        }
        return Arrays.stream(camelName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
            .filter(StringUtils::isNotBlank)
            .map(String::toUpperCase)
            .collect(joining("_"));
    }

    public static String hex(byte... bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static String octal(byte... bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(bytes.length * 3);
        for (byte b : bytes) {
            builder.append(String.format("%03o", b));
        }
        return builder.toString();
    }
}
