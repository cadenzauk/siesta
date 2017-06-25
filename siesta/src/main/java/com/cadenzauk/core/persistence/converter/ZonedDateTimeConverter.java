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

package com.cadenzauk.core.persistence.converter;

import com.cadenzauk.core.sql.TimestampUtil;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime,Timestamp>{
    private final ZoneId dbZoneId;
    private final ZoneId zoneId;

    public ZonedDateTimeConverter() {
        dbZoneId = ZoneId.systemDefault();
        zoneId = ZoneId.of("UTC");
    }

    public ZonedDateTimeConverter(ZoneId zoneId) {
        dbZoneId = ZoneId.systemDefault();
        this.zoneId = zoneId;
    }

    public ZonedDateTimeConverter(ZoneId dbZoneId, ZoneId zoneId) {
        this.dbZoneId = dbZoneId;
        this.zoneId = zoneId;
    }

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime attribute) {
        return Optional.ofNullable(attribute)
            .map(zonedDateTime -> TimestampUtil.valueOf(dbZoneId, zonedDateTime))
            .orElse(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp dbData) {
        return Optional.ofNullable(dbData)
            .map(ts -> TimestampUtil.toZonedDateTime(ts, dbZoneId, zoneId))
            .orElse(null);
    }
}
