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

import com.cadenzauk.siesta.Database;

import java.sql.ResultSet;
import java.util.Optional;

public class DefaultVarchar extends DefaultDbType<String> {
    public DefaultVarchar() {
        super("varchar", ResultSet::getString, ResultSet::getString);
    }

    public DefaultVarchar(String sqlType) {
        super(sqlType, ResultSet::getString, ResultSet::getString);
    }

    @Override
    public String literal(Database database, String value) {
        return "'" + value.replaceAll("'", "''") + "'";
    }

    @Override
    public String sqlTypeOf(Database database, Optional<String> value) {
        return String.format("varchar(%d)", Integer.max(1, value.map(String::length).orElse(1)));
    }
}
