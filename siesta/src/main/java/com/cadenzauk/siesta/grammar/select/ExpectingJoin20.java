/*
 * Copyright (c) 2017, 2020 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.tuple.Tuple20;

public class ExpectingJoin20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, RT20> extends InJoinExpectingAnd<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,RT20>,Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,RT20>> {
    public ExpectingJoin20(SelectStatement<Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,RT20>> statement) {
        super(statement);
    }

}
