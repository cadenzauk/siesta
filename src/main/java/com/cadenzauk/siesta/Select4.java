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

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.siesta.grammar.select.JoinClause;
import com.cadenzauk.siesta.grammar.select.JoinClauseStart;

public class Select4<RT1, RT2, RT3,RT4> extends Select<Tuple4<RT1,RT2,RT3,RT4>> {
    public Select4(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, RowMapper<RT3> rowMapper3, RowMapper<RT4> rowMapper4, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2, rowMapper3, rowMapper4), Projection.of(p1, p2));
    }

    Select4JoinClauseStart joinClause() {
        return new Select4JoinClauseStart();
    }

    public class Select4JoinClauseStart extends JoinClauseStart<Select4JoinClauseStart,Select4JoinClause,Tuple4<RT1,RT2,RT3,RT4>> {
        Select4JoinClauseStart() {
            super(Select4.this.selectStatement, Select4JoinClauseStart::newJoinClause);
        }

        private Select4JoinClause newJoinClause() {
            return new Select4JoinClause();
        }
    }

    public class Select4JoinClause extends JoinClause<Select4JoinClause,Tuple4<RT1,RT2,RT3,RT4>> {
        public Select4JoinClause() {
            super(Select4.this.selectStatement);
        }
    }
}
