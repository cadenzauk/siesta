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

package com.cadenzauk.siesta.ddl;

import com.cadenzauk.siesta.ddl.action.Action;
import com.cadenzauk.siesta.ddl.action.LoggableAction;
import com.cadenzauk.siesta.ddl.definition.action.ColumnDataType;
import com.cadenzauk.siesta.ddl.definition.action.CreateIndexAction;
import com.cadenzauk.siesta.ddl.definition.action.CreateSequenceAction;
import com.cadenzauk.siesta.ddl.definition.action.CreateTableAction;
import com.cadenzauk.siesta.type.DbTypeId;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.integer;

public class SchemaDefinition {
    private final String id;
    private final List<LoggableAction> actions;

    private SchemaDefinition(Builder builder) {
        id = builder.id;
        actions = ImmutableList.copyOf(builder.actions);
    }

    public Stream<Action> actions() {
        return actions.stream().map(Function.identity());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private final List<LoggableAction> actions = new ArrayList<>();

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder createSequence(Consumer<CreateSequenceAction.Builder<Integer>> sequenceDef) {
            CreateSequenceAction.Builder tableBuilder = CreateSequenceAction.newBuilder(integer())
                .definition(id);
            sequenceDef.accept(tableBuilder);
            actions.add(tableBuilder.build());
            return this;
        }

        public Builder createTable(Consumer<CreateTableAction.Builder> tableDef) {
            CreateTableAction.Builder tableBuilder = CreateTableAction.newBuilder()
                .definition(id);
            tableDef.accept(tableBuilder);
            actions.add(tableBuilder.build());
            return this;
        }

        public Builder createIndex(Consumer<CreateIndexAction.Builder> tableDef) {
            CreateIndexAction.Builder indexBuilder = CreateIndexAction.newBuilder()
                .definition(id);
            tableDef.accept(indexBuilder);
            actions.add(indexBuilder.build());
            return this;
        }

        public SchemaDefinition build() {
            validate();
            return new SchemaDefinition(this);
        }

        private void validate() {
            Set<String> seen = new HashSet<>();
            for (LoggableAction action : actions) {
                if (seen.contains(action.id())) {
                    throw new IllegalArgumentException("Duplicate action " + action);
                }
                seen.add(action.id());
            }
        }
    }
}
