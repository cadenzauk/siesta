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

package com.cadenzauk.siesta.type;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DatabaseOptions;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DefaultUuid implements DbType<UUID> {
    private final boolean uuidIsSupported;
    private final String typename;

    public DefaultUuid(boolean uuidIsSupported, String typename) {
        this.uuidIsSupported = uuidIsSupported;
        this.typename = typename;
    }

    public DefaultUuid(boolean uuidIsSupported) {
        this(uuidIsSupported, "uuid");
    }

    public DefaultUuid(String typename) {
        this(true, typename);
    }

    public DefaultUuid() {
        this(false, "uuid");
    }

    @Override
    public UUID getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        if (useUuid(database)) {
            UUID uuid = rs.getObject(col, UUID.class);
            return rs.wasNull() || uuid == null ? null : uuid;
        } else {
            byte[] bytes = rs.getBytes(col);
            return rs.wasNull() || bytes == null ? null : fromBytes(bytes);
        }
    }

    @Override
    public UUID getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        if (useUuid(database)) {
            UUID uuid = rs.getObject(col, UUID.class);
            return rs.wasNull() || uuid == null ? null : uuid;
        } else {
            byte[] bytes = rs.getBytes(col);
            return rs.wasNull() || bytes == null ? null : fromBytes(bytes);
        }
    }

    @Override
    public String sqlType(Database database) {
        if (useUuid(database)) {
            return typename;
        } else {
            return binaryType(database).sqlType(database, 16);
        }
    }

    @Override
    public String sqlType(Database database, int arg) {
        if (useUuid(database)) {
            return typename;
        } else {
            return binaryType(database).sqlType(database, arg);
        }
    }

    @Override
    public String sqlType(Database database, int arg1, int arg2) {
        if (useUuid(database)) {
            return typename;
        } else {
            return binaryType(database).sqlType(database, arg1, arg2);
        }
    }

    @Override
    public Object convertToDatabase(Database database, UUID value) {
        if (useUuid(database)) {
            return value;
        } else {
            return binaryType(database).convertToDatabase(database, value == null ? null : toBytes(value));
        }
    }

    @Override
    public String literal(Database database, UUID value) {
        if (useUuid(database)) {
            return "'" + value.toString() + "'";
        } else {
            return binaryType(database).literal(database, toBytes(value));
        }
    }

    @Override
    public String parameter(Database database, Optional<UUID> value) {
        if (useUuid(database)) {
            return "?";
        } else {
            return binaryType(database).parameter(database, value.map(DefaultUuid::toBytes));
        }
    }

    private DbType<byte[]> binaryType(Database database) {
        return database.dialect().type(DbTypeId.BINARY);
    }

    private static UUID fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }

    private static byte[] toBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    private boolean useUuid(Database database) {
        return uuidIsSupported && database.isNotSet(DatabaseOptions.Option.UuidAsBinary);
    }
}
