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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;

import java.util.stream.Stream;

public class ResolvedColumn<T,R> implements TypedExpression<T> {
    private final Alias<R> alias;
    private final Column<T,R> column;

    @SuppressWarnings("unchecked")
    private ResolvedColumn(Alias<R> alias, MethodInfo<R,T> method) {
        this.alias = alias;
        this.column = alias.column(method);
    }

    @Override
    public String sql(Scope scope) {
        return alias.inSelectClauseSql(column.name());
    }

    @Override
    public Stream<Object> args(Scope args) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.COLUMN;
    }

    @Override
    public String label(Scope scope) {
        return alias.inSelectClauseLabel(column.name());
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return rs -> column.dataType().get(rs, label).orElse(null);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, Function1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, FunctionOptional1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }
}
