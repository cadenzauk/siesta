/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.grammar.expression.ColumnExpression;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public interface ColumnSpecifier<T> {
    Optional<Class<?>> referringClass();

    default TypeToken<T> effectiveType() {
        return TypeToken.of(effectiveClass());
    }

    Class<T> effectiveClass();

    <E> Optional<ColumnSpecifier<E>> asEffective(TypeToken<E> type);

    Optional<Method> method();

    String columnName(Scope scope);

    <RT> Optional<MethodInfo<RT,T>> asReferringMethodInfo(TypeToken<RT> type);

    boolean isSpecificationFor(TypedExpression<T> expression, Optional<String> label);

    Optional<Column<T,?>> column(Scope scope);

    boolean specifies(Scope scope, AliasColumn<T> x);

    boolean specifies(Scope scope, ProjectionColumn<T> x);

    class MethodInfoColumnSpecifier<T> implements ColumnSpecifier<T> {
        private final MethodInfo<?, T> getterMethod;

        public MethodInfoColumnSpecifier(MethodInfo<?,T> getterMethod) {
            this.getterMethod = getterMethod;
        }

        @Override
        public String toString() {
            return getterMethod.propertyName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            MethodInfoColumnSpecifier<?> that = (MethodInfoColumnSpecifier<?>) o;

            return new EqualsBuilder()
                .append(getterMethod, that.getterMethod)
                .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                .append(getterMethod)
                .toHashCode();
        }

        @Override
        public Optional<Class<?>> referringClass() {
            return Optional.of(getterMethod.referringClass());
        }

        @Override
        public Class<T> effectiveClass() {
            return getterMethod.effectiveClass();
        }

        @Override
        public <E> Optional<ColumnSpecifier<E>> asEffective(TypeToken<E> type) {
            return getterMethod
                .asEffective(type)
                .map(ColumnSpecifier::of);
        }

        @Override
        public Optional<Method> method() {
            return Optional.of(getterMethod.method());
        }

        @Override
        public String columnName(Scope scope) {
            return scope.database().columnNameFor(getterMethod);
        }

        @Override
        public <RT> Optional<MethodInfo<RT,T>> asReferringMethodInfo(TypeToken<RT> type) {
            return getterMethod.asReferring(type);
        }

        @Override
        public boolean isSpecificationFor(TypedExpression<T> expression, Optional<String> label) {
            return OptionalUtil.as(ColumnExpression.class, expression)
                .map(c -> c.includes(this))
                .orElse(false);
        }

        @Override
        public Optional<Column<T,?>> column(Scope scope) {
            return Optional.of(scope.database().column(getterMethod));
        }

        @Override
        public boolean specifies(Scope scope, AliasColumn<T> x) {
            return StringUtils.equals(x.propertyName(), getterMethod.propertyName());
        }

        @Override
        public boolean specifies(Scope scope, ProjectionColumn<T> x) {
            return StringUtils.equals(x.propertyName(), getterMethod.propertyName());
        }
    }

    class NamedColumnSpecifier<T> implements ColumnSpecifier<T> {
        private final Class<T> effectiveClass;
        private final String columnLabel;

        public NamedColumnSpecifier(Class<T> effectiveClass, String columnLabel) {
            this.effectiveClass = effectiveClass;
            this.columnLabel = columnLabel;
        }

        @Override
        public String toString() {
            return columnLabel;
        }

        @Override
        public Optional<Class<?>> referringClass() {
            return Optional.empty();
        }

        @Override
        public Class<T> effectiveClass() {
            return effectiveClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<ColumnSpecifier<E>> asEffective(TypeToken<E> type) {
            return type.isSupertypeOf(this.effectiveClass) ? Optional.of((ColumnSpecifier<E>)this) : Optional.empty();
        }

        @Override
        public Optional<Method> method() {
            return Optional.empty();
        }

        @Override
        public String columnName(Scope scope) {
            return columnLabel;
        }

        @Override
        public <RT> Optional<MethodInfo<RT,T>> asReferringMethodInfo(TypeToken<RT> type) {
            return Optional.empty();
        }

        @Override
        public boolean isSpecificationFor(TypedExpression<T> expression, Optional<String> label) {
            return Objects.equals(label, Optional.of(columnLabel));
        }

        @Override
        public Optional<Column<T,?>> column(Scope scope) {
            return Optional.empty();
        }

        @Override
        public boolean specifies(Scope scope, AliasColumn<T> x) {
            return StringUtils.equals(x.columnName(), columnLabel);
        }

        @Override
        public boolean specifies(Scope scope, ProjectionColumn<T> x) {
            return StringUtils.equals(x.columnName(), columnLabel);
        }
    }

    static <T> ColumnSpecifier<T> of(MethodInfo<?, T> methodInfo) {
        return new MethodInfoColumnSpecifier<>(methodInfo);
    }

    static <RT,T> ColumnSpecifier<T> of(Function1<RT,T> method) {
        return new MethodInfoColumnSpecifier<>(MethodInfo.of(method));
    }

    static <RT,T> ColumnSpecifier<T> of(FunctionOptional1<RT,T> method) {
        return new MethodInfoColumnSpecifier<>(MethodInfo.of(method));
    }

    static <T> ColumnSpecifier<T> of(Class<T> effectiveClass, String columnName) {
        return new NamedColumnSpecifier<>(effectiveClass, columnName);
    }
}
