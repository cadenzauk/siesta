/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.ddl.definition.action;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.action.LoggableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTableAction extends LoggableAction {
    private final Optional<Function<Database,String>> catalog;
    private final Optional<Function<Database,String>> schemaName;
    private final Function<Database,String> tableName;
    private final boolean globalTemporary;
    private final List<Column> columns;

    private CreateTableAction(Builder builder) {
        super(builder);
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        tableName = builder.tableName
            .orElseThrow(() -> new IllegalArgumentException("tableName is required"));
        globalTemporary = builder.globalTemporary;
        columns = builder.columns
            .stream()
            .map(b -> b.build(catalog, schemaName, tableName))
            .collect(Collectors.toList());
    }

    public String catalog(Database database) {
        return catalog.map(cat -> cat.apply(database)).orElse("");
    }

    public String schemaName(Database database) {
        return schemaName.map(schema -> schema.apply(database)).orElse("");
    }

    public String tableName(Database database) {
        return tableName.apply(database);
    }

    public String qualifiedName(Database database) {
        return database.dialect().qualifiedTableName(catalog(database), schemaName(database), tableName(database));
    }

    public boolean globalTemporary() {
        return globalTemporary;
    }

    public Stream<Column> columns() {
        return columns.stream();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder extends LoggableAction.Builder<Builder> {
        private Optional<Function<Database,String>> catalog = Optional.empty();
        private Optional<Function<Database,String>> schemaName = Optional.empty();
        private Optional<Function<Database,String>> tableName = Optional.empty();
        private boolean globalTemporary = false;
        private final List<Column.Builder> columns = new ArrayList<>();

        public Builder catalog(String val) {
            catalog = OptionalUtil.ofBlankable(val).map(name -> d -> name);
            return this;
        }

        public Builder schemaName(String val) {
            schemaName = OptionalUtil.ofBlankable(val).map(name -> d -> name);;
            return this;
        }

        public Builder tableName(String val) {
            tableName = OptionalUtil.ofBlankable(val).map(name -> d -> name);;
            return this;
        }

        public Builder forClass(Class<?> rowClass) {
            catalog = Optional.of(d -> d.table(rowClass).catalog());
            schemaName = Optional.of(d -> d.table(rowClass).schema());
            tableName = Optional.of(d -> d.table(rowClass).tableName());
            return this;
        }

        public Builder globalTemporary() {
            globalTemporary = true;
            return this;
        }

        @SafeVarargs
        public final <T> Builder column(String columnName, ColumnDataType<T> dataType, Function<Column.Builder,Column.Builder>... columnDef) {
            Column.Builder column = Column.newBuilder()
                .columnName(d -> columnName)
                .dataType(dataType);
            for (Function<Column.Builder,Column.Builder> init : columnDef) {
                init.apply(column);
            }
            columns.add(column);
            return this;
        }

        @SafeVarargs
        public final <T,R,D> Builder column(Function1<R,T> columnName, ColumnDataType<D> dataType, Function<Column.Builder,Column.Builder>... columnDef) {
            Column.Builder column = Column.newBuilder()
                .columnName(d -> d.columnNameFor(MethodInfo.of(columnName)))
                .dataType(dataType);
            for (Function<Column.Builder,Column.Builder> init : columnDef) {
                init.apply(column);
            }
            columns.add(column);
            return this;
        }

        @SafeVarargs
        public final <T,R,D> Builder column(FunctionOptional1<R,T> columnName, ColumnDataType<D> dataType, Function<Column.Builder,Column.Builder>... columnDef) {
            Column.Builder column = Column.newBuilder()
                .columnName(d -> d.column(columnName).columnName())
                .dataType(dataType);
            for (Function<Column.Builder,Column.Builder> init : columnDef) {
                init.apply(column);
            }
            columns.add(column);
            return this;
        }

        public CreateTableAction build() {
            return new CreateTableAction(this);
        }
    }
}
