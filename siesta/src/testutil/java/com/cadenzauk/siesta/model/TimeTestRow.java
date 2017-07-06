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

package com.cadenzauk.siesta.model;

import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Table(name = "TIME_TEST", schema = "SIESTA")
public class TimeTestRow {
    private final String guid;
    private final Optional<LocalDateTime> localDateTime;
    private final Optional<ZonedDateTime> utcDateTime;

    public TimeTestRow(LocalDateTime localDateTime) {
        this.guid = UUID.randomUUID().toString();
        this.localDateTime = Optional.of(localDateTime);
        this.utcDateTime = Optional.empty();
    }

    public TimeTestRow(ZonedDateTime utcDateTime) {
        this.guid = UUID.randomUUID().toString();
        this.localDateTime = Optional.empty();
        this.utcDateTime = Optional.of(utcDateTime);
    }

    public String guid() {
        return guid;
    }

    public Optional<LocalDateTime> localDateTime() {
        return localDateTime;
    }

    public Optional<ZonedDateTime> utcDateTime() {
        return utcDateTime;
    }
}
