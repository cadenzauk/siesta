package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.stream.Stream;

public class JsonObjectFunction<T> implements TypedExpression<T>{
    private final LabelGenerator labelGenerator = new LabelGenerator("json_");
    private final FunctionName functionName;
    private final List<JsonKeyValue<?>> keyValues;
    private final TypeToken<T> type;

    public JsonObjectFunction(FunctionName functionName, TypeToken<T> type, List<JsonKeyValue<?>> keyValues) {
        this.functionName = functionName;
        this.keyValues = ImmutableList.copyOf(keyValues);
        this.type = type;
    }

    @Override
    public String sql(Scope scope) {
        String[] args = keyValues.stream().flatMap(it -> it.sql(scope)).toArray(String[]::new);
        return scope.dialect().function(functionName).sql(scope, args);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return keyValues.stream().flatMap(it -> it.args(scope));
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return (prefix, label) -> rs -> scope.database().getDataTypeOf(type).get(rs, prefix + label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }
}
