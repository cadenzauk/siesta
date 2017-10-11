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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;
import com.google.common.reflect.TypeToken;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public class CastExpression<F, T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("cast_");
    private final TypedExpression<F> from;
    private final DataType<T> to;
    private final DbTypeId<T> toType;
    private final BiFunction<DbType,Database,String> toSql;

    public CastExpression(TypedExpression<F> from, DataType<T> to, DbTypeId<T> toType, BiFunction<DbType,Database,String> toSql) {
        this.from = from;
        this.to = to;
        this.toType = toType;
        this.toSql = toSql;
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return rs -> to.get(rs, label, scope.database()).orElse(null);
    }

    @Override
    public TypeToken<T> type() {
        return TypeToken.of(to.javaClass());
    }

    @Override
    public String sql(Scope scope) {
        return String.format("cast(%s as %s)", from.sql(scope), toSql.apply(scope.dialect().type(toType), scope.database()));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return from.args(scope);
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    @Override
    public String toString() {
        return String.format("cast(%s as %s)", from, toSql.apply(new AnsiDialect().type(toType), Database.newBuilder().build()));
    }
}
