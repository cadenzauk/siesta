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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

import java.util.Optional;
import java.util.function.Function;

import static com.cadenzauk.siesta.grammar.expression.ForeignKeyExpression.Direction.CHILD_TO_PARENT;
import static com.cadenzauk.siesta.grammar.expression.ForeignKeyExpression.Direction.PARENT_TO_CHILD;

public class InJoinExpectingOn<J extends InJoinExpectingAnd<J,RT>, RT> {
    private final SelectStatement<RT> statement;
    private final Function<SelectStatement<RT>,J> newJoinClause;
    private boolean validate = true;

    protected InJoinExpectingOn(SelectStatement<RT> statement, Function<SelectStatement<RT>,J> newJoinClause) {
        this.statement = statement;
        this.newJoinClause = newJoinClause;
    }

    public InJoinExpectingOn<J,RT> validate(boolean validate) {
        this.validate = validate;
        return this;
    }

    public InJoinExpectingForeignKeyOrAnd onForeignKey() {
        return new InJoinExpectingForeignKeyOrAnd(Optional.empty());
    }

    public InJoinExpectingForeignKeyOrAnd onForeignKey(String keyName) {
        return new InJoinExpectingForeignKeyOrAnd(Optional.of(keyName));
    }

    public <T> J on(BooleanExpression on) {
        return setOnClause(on);
    }

    public <T> ExpressionBuilder<T,J> on(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::setOnClause);
    }

    public <T> ExpressionBuilder<T,J> on(Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setOnClause);
    }

    public <T> ExpressionBuilder<T,J> on(String alias, Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setOnClause);
    }

    public <T, R> ExpressionBuilder<T,J> on(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setOnClause);
    }

    public class InJoinExpectingForeignKeyOrAnd {
        private final Optional<String> keyName;

        protected InJoinExpectingForeignKeyOrAnd(Optional<String> keyName) {
            this.keyName = keyName;
        }

        public <C> J from(Class<C> childClass) {
            statement.from().onForeignKey(keyName, childClass, Optional.empty(), CHILD_TO_PARENT);
            return newJoinClause.apply(statement);
        }

        public <C> J from(Alias<C> childAlias) {
            statement.from().onForeignKey(keyName, childAlias, CHILD_TO_PARENT);
            return newJoinClause.apply(statement);
        }

        public <C> J from(Class<C> childClass, String childAlias) {
            statement.from().onForeignKey(keyName, childClass, Optional.of(childAlias), CHILD_TO_PARENT);
            return newJoinClause.apply(statement);
        }

        public <P> J to(Class<P> parentClass) {
            statement.from().onForeignKey(keyName, parentClass, Optional.empty(), PARENT_TO_CHILD);
            return newJoinClause.apply(statement);
        }

        public <P> J to(Alias<P> parentAlias) {
            statement.from().onForeignKey(keyName, parentAlias, PARENT_TO_CHILD);
            return newJoinClause.apply(statement);
        }

        public <P> J to(Class<P> parentClass, String parentAlias) {
            statement.from().onForeignKey(keyName, parentClass, Optional.of(parentAlias), PARENT_TO_CHILD);
            return newJoinClause.apply(statement);
        }
    }

    @SuppressWarnings("unchecked")
    private J setOnClause(BooleanExpression e) {
        statement.from().on(e, validate);
        return newJoinClause.apply(statement);
    }
}
