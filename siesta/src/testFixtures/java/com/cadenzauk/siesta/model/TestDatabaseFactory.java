/*
 * Copyright (c) 2024 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.model;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;

import javax.sql.DataSource;

public class TestDatabaseFactory {
    public Database testDatabase(DataSource dataSource) {
        return TestDatabase.testDatabase(dataSource);
    }

    public Database testDatabase(DataSource dataSource, Dialect dialect) {
        return TestDatabase.testDatabase(dataSource, dialect);
    }

    public Database testDatabase(Dialect dialect) {
        return TestDatabase.testDatabase(dialect);
    }

    public Database.Builder testDatabaseBuilder() {
        return TestDatabase.testDatabaseBuilder();
    }

    public Database.Builder testDatabaseBuilder(Dialect dialect) {
        return TestDatabase.testDatabaseBuilder(dialect);
    }
}
