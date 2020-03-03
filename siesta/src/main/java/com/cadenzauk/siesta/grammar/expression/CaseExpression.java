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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class CaseExpression<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("case_");
    private final List<Tuple2<BooleanExpression,TypedExpression<T>>> cases = new ArrayList<>();
    private Optional<TypedExpression<T>> orElse = Optional.empty();

    public CaseExpression(BooleanExpression booleanExpression, TypedExpression<T> value) {
        cases.add(Tuple.of(booleanExpression, value));
    }

    @Override
    public String sql(Scope scope) {
        return String.format("case %s%s end",
            cases.stream().map(c -> String.format("when %s then %s", c.item1().sql(scope), c.item2().sql(scope))).collect(joining(" ")),
            orElse.map(e -> " else " + e.sql(scope)).orElse(""));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.concat(
            cases.stream().flatMap(c -> Stream.concat(c.item1().args(scope), c.item2().args(scope))),
            StreamUtil.of(orElse).flatMap(e -> e.args(scope))
        );
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
        return label -> cases.get(0).item2().rowMapperFactory(scope).rowMapper(Optional.of(label.orElseGet(() -> label(scope))));
    }

    @Override
    public TypeToken<T> type() {
        return cases.get(0).item2().type();
    }

    public TypedExpression<T> orElse(T value) {
        orElse = Optional.of(LiteralExpression.of(value));
        return this;
    }

    public TypedExpression<T> orElse(TypedExpression<T> value) {
        orElse = Optional.of(value);
        return this;
    }

    public <R> TypedExpression<T> orElse(Function1<R,T> method) {
        orElse = Optional.of(UnresolvedColumn.of(method));
        return this;
    }

    public <R> TypedExpression<T> orElse(FunctionOptional1<R,T> method) {
        orElse = Optional.of(UnresolvedColumn.of(method));
        return this;
    }

    public <R> TypedExpression<T> orElse(String alias, Function1<R,T> method) {
        orElse = Optional.of(UnresolvedColumn.of(alias, method));
        return this;
    }

    public <R> TypedExpression<T> orElse(String alias, FunctionOptional1<R,T> method) {
        orElse = Optional.of(UnresolvedColumn.of(alias, method));
        return this;
    }

    public <R> TypedExpression<T> orElse(Alias<R> alias, Function1<R,T> method) {
        orElse = Optional.of(ResolvedColumn.of(alias, method));
        return this;
    }

    public <R> TypedExpression<T> orElse(Alias<R> alias, FunctionOptional1<R,T> method) {
        orElse = Optional.of(ResolvedColumn.of(alias, method));
        return this;
    }

    public <X> ExpressionBuilder<X,InSubsequentWhenExpectingThen> when(X val) {
        return ExpressionBuilder.of(ValueExpression.of(val), InSubsequentWhenExpectingThen::new);
    }

    public <X> ExpressionBuilder<X,InSubsequentWhenExpectingThen> when(TypedExpression<X> expression) {
        return ExpressionBuilder.of(expression, InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(Function1<X,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(FunctionOptional1<X,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(String alias, Function1<X,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(String alias, FunctionOptional1<X,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(Alias<X> alias, Function1<X,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), InSubsequentWhenExpectingThen::new);
    }

    public <X,R> ExpressionBuilder<R,InSubsequentWhenExpectingThen> when(Alias<X> alias, FunctionOptional1<X,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), InSubsequentWhenExpectingThen::new);
    }

    public static class InFirstWhenExpectingThen {
        private final BooleanExpression booleanExpression;

        public InFirstWhenExpectingThen(BooleanExpression booleanExpression) {
            this.booleanExpression = booleanExpression;
        }

        public <T> CaseExpression<T> then(T value) {
            return new CaseExpression<>(booleanExpression, LiteralExpression.of(value));
        }

        public <T> CaseExpression<T> then(TypedExpression<T> value) {
            return new CaseExpression<>(booleanExpression, value);
        }

        public <R,T> CaseExpression<T> then(Function1<R,T> method) {
            return new CaseExpression<>(booleanExpression, UnresolvedColumn.of(method));
        }

        public <R,T> CaseExpression<T> then(FunctionOptional1<R,T> method) {
            return new CaseExpression<>(booleanExpression, UnresolvedColumn.of(method));
        }

        public <R,T> CaseExpression<T> then(String alias, Function1<R,T> method) {
            return new CaseExpression<>(booleanExpression, UnresolvedColumn.of(alias, method));
        }

        public <R,T> CaseExpression<T> then(String alias, FunctionOptional1<R,T> method) {
            return new CaseExpression<>(booleanExpression, UnresolvedColumn.of(alias, method));
        }

        public <R,T> CaseExpression<T> then(Alias<R> alias, Function1<R,T> method) {
            return new CaseExpression<>(booleanExpression, ResolvedColumn.of(alias, method));
        }

        public <R,T> CaseExpression<T> then(Alias<R> alias, FunctionOptional1<R,T> method) {
            return new CaseExpression<>(booleanExpression, ResolvedColumn.of(alias, method));
        }
    }

    public class InSubsequentWhenExpectingThen {
        private final BooleanExpression booleanExpression;

        private InSubsequentWhenExpectingThen(BooleanExpression booleanExpression) {
            this.booleanExpression = booleanExpression;
        }

        public CaseExpression<T> then(T value) {
            cases.add(Tuple.of(booleanExpression, LiteralExpression.of(value)));
            return CaseExpression.this;
        }

        public CaseExpression<T> then(TypedExpression<T> value) {
            cases.add(Tuple.of(booleanExpression, value));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(Function1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, UnresolvedColumn.of(method)));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(FunctionOptional1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, UnresolvedColumn.of(method)));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(String alias, Function1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, UnresolvedColumn.of(alias, method)));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(String alias, FunctionOptional1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, UnresolvedColumn.of(alias, method)));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(Alias<R> alias, Function1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, ResolvedColumn.of(alias, method)));
            return CaseExpression.this;
        }

        public <R> CaseExpression<T> then(Alias<R> alias, FunctionOptional1<R,T> method) {
            cases.add(Tuple.of(booleanExpression, ResolvedColumn.of(alias, method)));
            return CaseExpression.this;
        }
    }

}
