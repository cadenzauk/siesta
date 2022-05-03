package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;

public class JsonKeyValue<T> {
    private final TypedExpression<String> key;
    private final TypedExpression<T> value;

    public JsonKeyValue(TypedExpression<String> key, TypedExpression<T> value) {
        this.key = key;
        this.value = value;
    }

    public TypedExpression<String> key() {
        return key;
    }

    public TypedExpression<T> value() {
        return value;
    }

    public Stream<String> sql(Scope scope) {
        return Stream.of(key.sql(scope), value.sql(scope));
    }

    public Stream<Object> args(Scope scope) {
        return Stream.concat(key.args(scope), value.args(scope));
    }

    public static class Builder {
        private final TypedExpression<String> key;

        public Builder(TypedExpression<String> key) {
            this.key = key;
        }

        public <T> JsonKeyValue<T> value(T value) {
            return new JsonKeyValue<>(key, ValueExpression.of(value));
        }

        public <T> JsonKeyValue<T> value(TypedExpression<T> value) {
            return new JsonKeyValue<>(key, value);
        }

        public <T,R> JsonKeyValue<T> value(Function1<R,T> value) {
            return new JsonKeyValue<>(key, column(value));
        }

        public <T,R> JsonKeyValue<T> value(FunctionOptional1<R, T> value) {
            return new JsonKeyValue<>(key, column(value));
        }

        public <T,R> JsonKeyValue<T> value(String alias, Function1<R,T> value) {
            return new JsonKeyValue<>(key, UnresolvedColumn.of(alias, value));
        }

        public <T,R> JsonKeyValue<T> value(String alias, FunctionOptional1<R,T> value) {
            return new JsonKeyValue<>(key, UnresolvedColumn.of(alias, value));
        }

        public <T, R> JsonKeyValue<T> value(Alias<R> alias, Function1<R,T> value) {
            return new JsonKeyValue<>(key, ResolvedColumn.of(alias, value));
        }

        public <T, R> JsonKeyValue<T> value(Alias<R> alias, FunctionOptional1<R,T> value) {
            return new JsonKeyValue<>(key, ResolvedColumn.of(alias, value));
        }

        public <T> JsonKeyValue<T> value(Label<T> value) {
            return new JsonKeyValue<>(key, UnresolvedColumn.of(value));
        }

        public <T> JsonKeyValue<T> value(String alias, Label<T> value) {
            return new JsonKeyValue<>(key, UnresolvedColumn.of(alias, value));
        }
    }
}
