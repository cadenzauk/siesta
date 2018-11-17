/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar;

import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.siesta.catalog.Table;

import java.util.Optional;

public class InvalidForeignKeyException extends RuntimeSqlException {
    public InvalidForeignKeyException(Table<?> childTable, Table<?> parentTable, Optional<String> name) {
        super(name
            .map(n -> String.format("No foreign key called %s defined from %s to %s.", n, childTable.qualifiedName(), parentTable.qualifiedName()))
            .orElseGet(() -> String.format("No foreign keys defined from %s to %s.", childTable.qualifiedName(), parentTable.qualifiedName())));
    }

    public InvalidForeignKeyException(String message) {
        super(message);
    }
}
