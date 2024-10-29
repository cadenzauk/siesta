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

package com.cadenzauk.siesta.grammar.expression.olap;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.select.OrderByExpression;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

class OlapFunction<T> {
    private final LabelGenerator labelGenerator;
    private final String functionName;
    private final TypeToken<T> type;
    private final TypedExpression<?>[] arguments;
    private final Lazy<List<TypedExpression<?>>> partitionBy = new Lazy<>(ArrayList::new);
    private final Lazy<List<OrderByExpression<?>>> orderBy = new Lazy<>(ArrayList::new);

    OlapFunction(String functionName, TypeToken<T> type, TypedExpression<?>... arguments) {
        this.functionName = functionName;
        this.type = type;
        this.arguments = arguments;
        labelGenerator = new LabelGenerator(functionName + "_");
    }

    String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    RowMapper<T> rowMapper(Scope scope, String label) {
        DataType<T> dataType = scope.database().getDataTypeOf(type);
        return rs -> dataType.get(rs, label, scope.database()).orElse(null);
    }

    RowMapperFactory<T> rowMapperFactory(Scope scope, String defaultLabel) {
        DataType<T> dataType = scope.database().getDataTypeOf(type);
        return (prefix, label) -> rs -> dataType.get(rs, prefix + label.orElse(defaultLabel), scope.database()).orElse(null);
    }

    TypeToken<T> type() {
        return type;
    }

    String sql(Scope scope) {
        return String.format("%s(%s) over (%s)",
            functionName,
            argumentsSql(scope),
            Stream.of(partitionBySql(scope), orderBySql(scope))
                .filter(StringUtils::isNotBlank)
                .collect(joining(" ")));
    }

    Stream<Object> args(Scope scope) {
        return Stream.concat(
            Stream.concat(
                Arrays.stream(arguments),
                partitionBy.stream().flatMap(Collection::stream)
            ).flatMap(e -> e.args(scope)),
            orderBy.stream().flatMap(Collection::stream).flatMap(o -> o.args(scope))
        );
    }

    Precedence precedence() {
        return Precedence.UNARY;
    }

    <V> void addPartitionBy(TypedExpression<V> expression) {
        partitionBy.get().add(expression);
    }

    <V> void addOrderBy(OrderByExpression<V> ordering) {
        orderBy.get().add(ordering);
    }

    private String argumentsSql(Scope scope) {
        return Arrays.stream(arguments)
            .map(a -> a.sql(scope))
            .collect(joining(", "));
    }

    private String partitionBySql(Scope scope) {
        return partitionBy
            .optional()
            .filter(p -> scope.dialect().supportsPartitionByInOlap())
            .map(p -> String.format("partition by %s", p.stream().map(e -> e.sql(scope)).collect(joining(", "))))
            .orElse("");
    }

    private String orderBySql(Scope scope) {
        return orderBy
            .optional()
            .filter(p -> scope.dialect().supportsOrderByInOlap())
            .map(p -> String.format("order by %s", p.stream().map(e -> e.sql(scope)).collect(joining(", "))))
            .orElse("");
    }
}
