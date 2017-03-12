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

package com.cadenzauk.persistence.converter;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime,Timestamp>{
    private final ZoneId zoneId;

    public ZonedDateTimeConverter() {
        zoneId = ZoneId.of("UTC");
    }

    public ZonedDateTimeConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime attribute) {
        return Optional.ofNullable(attribute)
            .map(zdt -> Timestamp.valueOf(zdt.toLocalDateTime()))
            .orElse(null);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp dbData) {
        return Optional.ofNullable(dbData)
            .map(ts -> ZonedDateTime.ofInstant(ts.toInstant(), zoneId))
            .orElse(null);
    }
}
