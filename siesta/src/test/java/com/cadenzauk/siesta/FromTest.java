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

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.InvalidJoinException;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

class FromTest {
    @Test
    void validateInvalidThrows() {
        Database database = testDatabase(new AnsiDialect());

        calling(() -> database.from(WidgetRow.class)
            .join(ManufacturerRow.class, "m")
            .on(WidgetRow::name).isEqualTo("Fred")
            .sql())

            .shouldThrow(InvalidJoinException.class)
            .withMessage(is("Joined table 'SIESTA.MANUFACTURER m' is not referenced in the ON clause."));

    }

    @Test
    void validateInvalidThrowsWhenWrongAlias() {
        Database database = testDatabase(new AnsiDialect());

        calling(() -> database.from(ManufacturerRow.class, "m1")
            .join(ManufacturerRow.class, "m2")
            .on("m1", ManufacturerRow::name).isEqualTo("Fred")
            .sql())

            .shouldThrow(InvalidJoinException.class)
            .withMessage(is("Joined table 'SIESTA.MANUFACTURER m2' is not referenced in the ON clause."));
    }

    @Test
    void invalidJoinCanBeSuppressed() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(ManufacturerRow.class, "m1")
            .join(ManufacturerRow.class, "m2")
            .validate(false)
            .on("m1", ManufacturerRow::name).isEqualTo("Fred")
            .sql();

        assertThat(sql, startsWith("select m1.MANUFACTURER_ID as m1_MANUFACTURER_ID"));
    }
}