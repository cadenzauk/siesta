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

import com.cadenzauk.siesta.grammar.select.CommonTableExpression;

import java.util.Optional;

public class CteAlias<RT> extends Alias<RT> {
    private final CommonTableExpression<RT> commonTableExpression;

    public CteAlias(CommonTableExpression<RT> commonTableExpression, Optional<String> aliasName) {
        super(commonTableExpression.table(), aliasName);
        this.commonTableExpression = commonTableExpression;
    }


    @Override
    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName().orElseGet(commonTableExpression::name), columnName);
    }

    @Override
    protected String columnLabelPrefix() {
        return aliasName().orElseGet(commonTableExpression::name);
    }

    @Override
    protected String inWhereClause() {
        return aliasName()
            .map(a -> String.format("%s %s", commonTableExpression.name(), a))
            .orElseGet(commonTableExpression::name);
    }
}
