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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ExpectingEndOfStatement<RT> implements TypedExpression<RT> {
    protected Select<RT> statement;

    public ExpectingEndOfStatement(Select<RT> statement) {
        this.statement = statement;
    }

    public List<RT> list(SqlExecutor sqlExecutor) {
        return statement.list(sqlExecutor);
    }

    public Optional<RT> optional(SqlExecutor sqlExecutor) {
        return statement.optional(sqlExecutor);
    }

    public String sql() {
        return statement.sql();
    }

    @Override
    public String sql(Scope scope) {
        return statement.sql(scope);
    }

    @Override
    public String label(Scope scope) {
        return statement.label(scope);
    }

    @Override
    public int precedence() {
        return 0;
    }

    @Override
    public RowMapper<RT> rowMapper(Scope scope, String label) {
        return statement.rowMapper(scope, label);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return statement.args(scope);
    }

    protected Scope scope() {
        return statement.scope();
    }

    protected Database database() {
        return statement.scope.database();
    }
}
