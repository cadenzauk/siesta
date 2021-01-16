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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.siesta.TempTableInfo;
import com.cadenzauk.siesta.grammar.temp.TempTableCommitAction;

public class Db2TempTableInfo extends TempTableInfo {
    public Db2TempTableInfo() {
        super(TempTableInfo.newBuilder()
                  .listGlobalSql("select '' as TEMP_TABLE_CATALOG, tabschema as TEMP_TABLE_SCHEMA_NAME, tabname as TEMP_TABLE_NAME from syscat.tables where type = 'G'")
                  .localCommitOptions(TempTableCommitAction.PRESERVE_ROWS, TempTableCommitAction.DELETE_ROWS)
                  .createLocalPreserveRowsSqlFormat("declare global temporary table %s (%s) on commit preserve rows not logged on rollback delete rows")
                  .createLocalDeleteRowsSqlFormat("declare global temporary table %s (%s) on commit delete rows not logged on rollback delete rows ")
                  .tableNameFormat("session.%s"));
    }
}
