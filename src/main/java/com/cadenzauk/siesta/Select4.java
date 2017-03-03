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

public class Select4<RT1, RT2, RT3,RT4> extends Select<Tuple4<RT1,RT2,RT3,RT4>> {
    public Select4(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, RowMapper<RT3> rowMapper3, RowMapper<RT4> rowMapper4, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2, rowMapper3, rowMapper4), Projection.of(p1, p2));
    }

    Select4JoinClauseStartBuilder joinClause() {
        return new Select4JoinClauseStartBuilder();
    }

    public class Select4JoinClauseStartBuilder extends JoinClauseStartBuilder<Select4JoinClauseStartBuilder,Select4JoinClauseBuilder> {
        Select4JoinClauseStartBuilder() {
            super(Select4JoinClauseStartBuilder::newJoinClause);
        }

        private Select4JoinClauseBuilder newJoinClause() {
            return new Select4JoinClauseBuilder();
        }
    }

    public class Select4JoinClauseBuilder extends JoinClauseBuilder<Select4JoinClauseBuilder> {
    }
}
