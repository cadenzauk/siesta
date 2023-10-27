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

import com.cadenzauk.core.util.TokenReplacer;
import com.cadenzauk.siesta.grammar.temp.TempTableCommitAction;
import com.google.common.collect.ImmutableSet;
import org.intellij.lang.annotations.PrintFormat;

import java.util.EnumSet;

public class TempTableInfo {
    private final boolean supportsGlobal;
    private final boolean supportsLocal;
    private final String listGlobalSql;
    private final String createGlobalSqlFormat;
    private final String createLocalPreserveRowsSqlFormat;
    private final String createLocalDeleteRowsSqlFormat;
    private final String createLocalDropTableSqlFormat;
    private final String tableNameFormat;
    private final EnumSet<TempTableCommitAction> localCommitOptions;
    private final boolean createLocalIsTransactional;
    private final boolean clearsGlobalOnCommit;

    protected TempTableInfo(Builder builder) {
        supportsGlobal = builder.supportsGlobal;
        supportsLocal = builder.supportsLocal;
        listGlobalSql = builder.listGlobalSql;
        createGlobalSqlFormat = builder.createGlobalSqlFormat;
        createLocalPreserveRowsSqlFormat = builder.createLocalPreserveRowsSqlFormat;
        createLocalDeleteRowsSqlFormat = builder.createLocalDeleteRowsSqlFormat;
        createLocalDropTableSqlFormat = builder.createLocalDropTableSqlFormat;
        tableNameFormat = builder.tableNameFormat;
        localCommitOptions = builder.localCommitOptions;
        createLocalIsTransactional = builder.createLocalIsTransactional;
        clearsGlobalOnCommit = builder.clearsGlobalOnCommit;
    }

    public boolean supportsGlobal() {
        return supportsGlobal;
    }

    public boolean supportsLocal() {
        return supportsLocal;
    }

    public boolean clearsGlobalOnCommit() {
        return clearsGlobalOnCommit;
    }

    public boolean createLocalIsTransactional() {
        return createLocalIsTransactional;
    }

    public String listGlobalSql() {
        return listGlobalSql;
    }

    public String createGlobalSql(TokenReplacer tokenReplacer) {
        return tokenReplacer.replace(createGlobalSqlFormat);
    }

    public String createLocalPreserveRowsSql(TokenReplacer tokenReplacer) {
        return tokenReplacer.replace(createLocalPreserveRowsSqlFormat);
    }

    public String createLocalDeleteRowsSql(TokenReplacer tokenReplacer) {
        return supportsLocalOnCommitDeleteRows()
                   ? tokenReplacer.replace(createLocalDeleteRowsSqlFormat)
                   : createLocalPreserveRowsSql(tokenReplacer);
    }

    public String createLocalDropTableSql(TokenReplacer tokenReplacer) {
        return supportsLocalOnCommitDropTable()
                   ? tokenReplacer.replace(createLocalDropTableSqlFormat)
                   : createLocalPreserveRowsSql(tokenReplacer);
    }

    public boolean supportsLocalOnCommitPreserveRows() {
        return localCommitOptions.contains(TempTableCommitAction.PRESERVE_ROWS);
    }

    public boolean supportsLocalOnCommitDeleteRows() {
        return localCommitOptions.contains(TempTableCommitAction.DELETE_ROWS);
    }

    public boolean supportsLocalOnCommitDropTable() {
        return localCommitOptions.contains(TempTableCommitAction.DROP_TABLE);
    }

    public String tableName(TokenReplacer tokenReplacer) {
        return tokenReplacer.replace(tableNameFormat);
    }

    public String globalTempTableName(Dialect dialect, String catalog, String schemaName, String tableName) {
        return dialect.qualifiedTableName(catalog, schemaName, tableName);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean supportsGlobal = true;
        private boolean supportsLocal = true;
        private String listGlobalSql = "";
        private String createGlobalSqlFormat = "create global temporary table ${tableName}(${columnDefs}${primaryKeyDef}${foreignKeyDefs})";
        private String createLocalPreserveRowsSqlFormat = "create temporary table ${tableName}(${columnDefs})";
        private String createLocalDeleteRowsSqlFormat = "";
        private String createLocalDropTableSqlFormat = "";
        private String tableNameFormat = "${tableName}";
        private EnumSet<TempTableCommitAction> localCommitOptions = EnumSet.of(TempTableCommitAction.PRESERVE_ROWS);
        private boolean createLocalIsTransactional = true;
        private boolean clearsGlobalOnCommit = true;

        private Builder() {
        }

        public Builder supportsGlobal(boolean val) {
            supportsGlobal = val;
            return this;
        }

        public Builder supportsLocal(boolean val) {
            supportsLocal = val;
            return this;
        }

        public Builder listGlobalSql(String val) {
            listGlobalSql = val;
            return this;
        }

        public Builder createGlobalSqlFormat(@PrintFormat String val) {
            createGlobalSqlFormat = val;
            return this;
        }

        public Builder createLocalPreserveRowsSqlFormat(@PrintFormat String val) {
            createLocalPreserveRowsSqlFormat = val;
            return this;
        }

        public Builder createLocalDeleteRowsSqlFormat(@PrintFormat String val) {
            createLocalDeleteRowsSqlFormat = val;
            return this;
        }

        public Builder createLocalDropTableSqlFormat(@PrintFormat String val) {
            createLocalDropTableSqlFormat = val;
            return this;
        }

        public Builder tableNameFormat(@PrintFormat String val) {
            tableNameFormat = val;
            return this;
        }

        public Builder createLocalIsTransactional(boolean val) {
            createLocalIsTransactional = val;
            return this;
        }

        public Builder clearsGlobalOnCommit(boolean val) {
            clearsGlobalOnCommit = val;
            return this;
        }

        public Builder localCommitOptions(TempTableCommitAction... val) {
            localCommitOptions = EnumSet.copyOf(ImmutableSet.copyOf(val));
            return this;
        }

        public TempTableInfo build() {
            return new TempTableInfo(this);
        }
    }
}
