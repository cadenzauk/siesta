/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class StringUtil {
    public static String uppercaseFirst(String s) {
        if (StringUtils.isEmpty(s)) {
            return "";
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String camelToUpper(String camelName) {
        return Arrays.stream(camelName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
            .filter(StringUtils::isNotBlank)
            .map(String::toUpperCase)
            .collect(joining("_"));
    }
}
