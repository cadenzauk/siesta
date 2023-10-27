/*
 * Copyright (c) 2023 Cadenza United Kingdom Limited
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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TokenReplacer {
    private static final Pattern tokenPattern = Pattern.compile("\\$\\{([a-zA-Z]+)}");

    private final Map<String, String> tokens;

    public TokenReplacer(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    public String valueOf(String token) {
        String value = tokens.get(token);
        if (value == null) {
            String available = tokens.keySet().stream().sorted().collect(Collectors.joining(", "));
            throw new IllegalArgumentException("No token '" + token + "' defined.  Available tokens are: " + available + ".");
        }
        return value;
    }

    public String replace(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int start = 0;
        Matcher matcher = tokenPattern.matcher(input);
        while (matcher.find(start)) {
            int matchStart = matcher.start();
            if (matcher.start() > start) {
                result.append(input, start, matchStart);
            }
            String replacement = valueOf(matcher.group(1));
            result.append(replacement);
            start = matcher.end();
        }
        if (start < input.length()) {
            result.append(input, start, input.length());
        }
        return result.toString();
    }
}
