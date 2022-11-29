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
import com.cadenzauk.siesta.json.Json;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DefaultJson extends DefaultDbType<Json> {
    public DefaultJson() {
        super("json", DefaultJson::getJson, DefaultJson::getJson);
    }

    public DefaultJson(String sqlType) {
        super(sqlType, DefaultJson::getJson, DefaultJson::getJson);
    }

    @Override
    public String literal(Database database, Json value) {
        return "'" + value.data().replaceAll("'", "''") + "'";
    }

    @Override
    public Object convertToDatabase(Database database, Json value) {
        return value == null ? null : value.data();
    }

    private static Json getJson(ResultSet rs, String colName) throws SQLException {
        String json = rs.getString(colName);
        return json == null ? null : new Json(json);
    }

    private static Json getJson(ResultSet rs, int colNo) throws SQLException {
        String json = rs.getString(colNo);
        return json == null ? null : new Json(json);
    }
}
