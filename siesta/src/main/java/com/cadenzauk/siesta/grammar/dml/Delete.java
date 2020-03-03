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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TableAlias;
import com.cadenzauk.siesta.catalog.Table;
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

public class Delete<D> extends ExecutableStatement {
    private final TableAlias<D> alias;

    private Delete(Database database, TableAlias<D> alias) {
        super(new Scope(database, alias));
        this.alias = alias;
    }

    @Override
    protected String sql(Scope scope) {
        return String.format("delete from %s%s%s",
            alias.table().qualifiedName(),
            alias.aliasName().map(a -> " " + a).orElse(""),
            whereClauseSql(scope));
    }

    @Override
    protected Stream<Object> args(Scope scope) {
        return whereClauseArgs(scope);
    }

    private ExpectingWhere start() {
        return new ExpectingWhere(this);
    }

    public static <U> ExpectingWhere delete(Database database, Table<U> table) {
        return delete(database, TableAlias.of(table));
    }

    public static <U> ExpectingWhere delete(Database database, Alias<U> alias) {
        TableAlias<U> tableAlias = OptionalUtil.as(new TypeToken<TableAlias<U>>() {}, alias)
            .orElseThrow(() -> new IllegalArgumentException("Can only use table aliases in DELETE."));
        return new Delete<>(database, tableAlias).start();
    }
}
