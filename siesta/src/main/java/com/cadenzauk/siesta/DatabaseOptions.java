/*
 * Copyright (c) 2026 Cadenza United Kingdom Limited
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

import java.util.EnumSet;

import static java.util.EnumSet.noneOf;

public class DatabaseOptions {
    public enum Option {
        // Prior to 0.13.0, SIESTA used binary(16) as the UUID type in all databases.  Now it uses the database uuid type in
        // those database that support it.  Use this database option for the old behaviour.
        UuidAsBinary
    }
    private final EnumSet<Option> set;

    private DatabaseOptions(EnumSet<Option> set) {
        this.set = set;
    }

    public DatabaseOptions with(Option option) {
        EnumSet<Option> newSet = set.clone();
        newSet.add(option);
        return new DatabaseOptions(newSet);
    }

    public boolean isSet(Option option) {
        return set.contains(option);
    }

    public static DatabaseOptions None = new DatabaseOptions(noneOf(Option.class));
}
