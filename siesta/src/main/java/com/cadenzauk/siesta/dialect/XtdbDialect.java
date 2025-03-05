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

 package com.cadenzauk.siesta.dialect;

 import com.cadenzauk.core.lang.StringUtil;
 import com.cadenzauk.core.sql.exception.DuplicateKeyException;
 import com.cadenzauk.core.sql.exception.IllegalNullException;
 import com.cadenzauk.core.sql.exception.InvalidValueException;
 import com.cadenzauk.core.sql.exception.LockingException;
 import com.cadenzauk.core.sql.exception.NoSuchObjectException;
 import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
 import com.cadenzauk.core.sql.exception.SqlSyntaxException;
 import com.cadenzauk.siesta.Database;
 import com.cadenzauk.siesta.Scope;
 import com.cadenzauk.siesta.dialect.function.ArgumentlessFunctionSpec;
 import com.cadenzauk.siesta.dialect.function.FunctionSpec;
 import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
 import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
 import com.cadenzauk.siesta.dialect.function.aggregate.CountDistinctFunctionSpec;
 import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
 import com.cadenzauk.siesta.dialect.merge.PostgresMergeInfo;
 import com.cadenzauk.siesta.dialect.merge.XtdbMergeInfo;
 import com.cadenzauk.siesta.grammar.expression.TypedExpression;
 import com.cadenzauk.siesta.jdbc.JdbcParameterSetter;
 import com.cadenzauk.siesta.json.BinaryJson;
 import com.cadenzauk.siesta.json.Json;
 import com.cadenzauk.siesta.json.JsonProvider;
 import com.cadenzauk.siesta.json.JsonSupport;
 import com.cadenzauk.siesta.type.BooleanAsTinyInt;
 import com.cadenzauk.siesta.type.DbTypeAdapter;
 import com.cadenzauk.siesta.type.DbTypeId;
 import com.cadenzauk.siesta.type.DefaultBigint;
 import com.cadenzauk.siesta.type.DefaultBinaryJson;
 import com.cadenzauk.siesta.type.DefaultDate;
 import com.cadenzauk.siesta.type.DefaultDouble;
 import com.cadenzauk.siesta.type.DefaultInteger;
 import com.cadenzauk.siesta.type.DefaultJson;
 import com.cadenzauk.siesta.type.DefaultReal;
 import com.cadenzauk.siesta.type.DefaultSmallint;
 import com.cadenzauk.siesta.type.DefaultTimestamp;
 import com.cadenzauk.siesta.type.DefaultTinyint;
 import com.cadenzauk.siesta.type.DefaultUtcTimestamp;
 import com.cadenzauk.siesta.type.DefaultVarchar;

 import java.math.BigDecimal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.SQLType;
 import java.sql.Types;
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.time.LocalTime;
 import java.time.ZoneId;
 import java.time.ZonedDateTime;
 import java.time.format.DateTimeFormatter;
 import java.time.format.DateTimeFormatterBuilder;
 import java.time.temporal.ChronoField;
 import java.util.Arrays;
 import java.util.Optional;
 import java.util.OptionalLong;
 import java.util.concurrent.TimeUnit;
 import java.util.stream.Stream;

 import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
 import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
 import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_FIELD_TEXT;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_OBJECT;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_VALUE;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_FIELD_TEXT;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_OBJECT;
 import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_VALUE;
 import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.INSTR;
 import static java.time.temporal.ChronoField.NANO_OF_SECOND;

 public class XtdbDialect extends AnsiDialect {
     public XtdbDialect() {
         functions()
             .register(DateFunctionSpecs::registerExtract)
             .register(DateFunctionSpecs::registerPlusNumber)
             .register(AggregateFunctionSpecs.COUNT_BIG, SimpleFunctionSpec.of("count"))
             .register(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, CountDistinctFunctionSpec.of("count"))
             .register(DateFunctionSpecs.CURRENT_TIMESTAMP_UTC, ArgumentlessFunctionSpec.of("localtimestamp"))
             .register(DateFunctionSpecs.CURRENT_TIMESTAMP, ArgumentlessFunctionSpec.of("localtimestamp"))
             .register(HOUR_DIFF, new FunctionSpec() {
                 @Override
                 public String sql(Scope scope, String[] argsSql) {
                     return String.format("date_part('day', %1$s - %2$s) * 24 + date_part('hour', %1$s - %2$s)", argsSql[0], argsSql[1]);
                 }

                 @Override
                 public Stream<Object> args(Scope scope, TypedExpression<?>[] args) {
                     return Stream.concat(
                         Arrays.stream(args),
                         Arrays.stream(args)).flatMap(a -> a.args(scope));
                 }
             })
             .register(MINUTE_DIFF, (s, argsSql) -> String.format("extract(epoch from (date_trunc('minute', %1$s) - date_trunc('minute', %2$s))) / 60", argsSql[0], argsSql[1]))
             .register(SECOND_DIFF, (s, argsSql) -> String.format("extract(epoch from (date_trunc('second', %1$s) - date_trunc('second', %2$s)))", argsSql[0], argsSql[1]))
             .register(INSTR, SimpleFunctionSpec.of("strpos"))
             .register(JSON_OBJECT, SimpleFunctionSpec.of("json_build_object"))
             .register(JSONB_OBJECT, SimpleFunctionSpec.of("jsonb_build_object"))
             .register(JSON_VALUE, (sql, argsSql) -> String.format("(jsonb_path_query_first(cast(%s as jsonb), cast(%s as jsonpath)) #>> '{}')", argsSql[0], argsSql[1]))
             .register(JSONB_VALUE, (sql, argsSql) -> String.format("(jsonb_path_query_first(cast(%s as jsonb), cast(%s as jsonpath)) #>> '{}')", argsSql[0], argsSql[1]))
             .register(JSON_FIELD_TEXT, (sql, argsSql) -> String.format("(%s ->> %s)", argsSql[0], argsSql[1]))
             .register(JSONB_FIELD_TEXT, (sql, argsSql) -> String.format("(%s ->> %s)", argsSql[0], argsSql[1]))
         ;

         types()
             .register(DbTypeId.BOOLEAN, new BooleanAsTinyInt("smallint") {
                 @Override
                 public Object convertToDatabase(Database database, Boolean value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setInt(paramIndex, value ? 1 : 0);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.SMALLINT);
                 }
             })
             .register(DbTypeId.TINYINT, new DefaultTinyint("smallint") {
                 @Override
                 public Object convertToDatabase(Database database, Byte value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setInt(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.SMALLINT);
                 }
             })
             .register(DbTypeId.SMALLINT, new DefaultSmallint() {
                 @Override
                 public Object convertToDatabase(Database database, Short value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setInt(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.SMALLINT);
                 }
             })
             .register(DbTypeId.INTEGER, new DefaultInteger() {
                 @Override
                 public Object convertToDatabase(Database database, Integer value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setInt(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.INTEGER);
                 }
             })
             .register(DbTypeId.BIGINT, new DefaultBigint() {
                 @Override
                 public Object convertToDatabase(Database database, Long value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setLong(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.BIGINT);
                 }
             })
             .register(DbTypeId.REAL, new DefaultReal() {
                 @Override
                 public Object convertToDatabase(Database database, Float value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setFloat(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.REAL);
                 }
             })
             .register(DbTypeId.DOUBLE, new DefaultDouble() {
                 @Override
                 public Object convertToDatabase(Database database, Double value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> ps.setDouble(paramIndex, value);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.DOUBLE);
                 }
             })
             .register(DbTypeId.DECIMAL, new DbTypeAdapter<>(DbTypeId.DOUBLE, BigDecimal::doubleValue, BigDecimal::valueOf) {
                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.DOUBLE);
                 }
             })
             .register(DbTypeId.DATE, new DefaultDate() {
                 @Override
                 public LocalDate getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDate.parse(str.replace("\"", "").replace("T", " "));
                 }

                 @Override
                 public LocalDate getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDate.parse(str);
                 }

                 @Override
                 public Object convertToDatabase(Database database, LocalDate value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) ->
                         ps.setString(paramIndex, value.format(DateTimeFormatter.ISO_LOCAL_DATE));
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.TIMESTAMP, "timestamp");
                 }
             })
             .register(DbTypeId.TIME, new DbTypeAdapter<>(DbTypeId.VARCHAR, LocalTime::toString, LocalTime::parse))
             .register(DbTypeId.TIMESTAMP, new DefaultTimestamp() {
                 private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                     .appendValue(ChronoField.YEAR, 4)
                     .appendLiteral('-')
                     .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                     .appendLiteral('-')
                     .appendValue(ChronoField.DAY_OF_MONTH, 2)
                     .appendLiteral(' ')
                     .appendValue(ChronoField.HOUR_OF_DAY, 2)
                     .appendLiteral(':')
                     .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                     .optionalStart()
                     .appendLiteral(':')
                     .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                     .appendFraction(NANO_OF_SECOND, 0, 9, true)
                     .toFormatter();

                 @Override
                 public LocalDateTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDateTime.parse(str.replace("\"", "").replace("T", " "), formatter);
                 }

                 @Override
                 public LocalDateTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDateTime.parse(str);
                 }

                 @Override
                 public Object convertToDatabase(Database database, LocalDateTime value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) ->
                         ps.setString(paramIndex, formatter.format(value));
                 }

                 @Override
                 public String sqlType(Database database, int arg) {
                     return "timestamp";
                 }

                 @Override
                 public String sqlType(Database database, int arg1, int arg2) {
                     return "timestamp";
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.TIMESTAMP, "timestamp");
                 }
             })
             .register(DbTypeId.UTC_TIMESTAMP, new DefaultUtcTimestamp() {
                 private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                     .appendValue(ChronoField.YEAR, 4)
                     .appendLiteral('-')
                     .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                     .appendLiteral('-')
                     .appendValue(ChronoField.DAY_OF_MONTH, 2)
                     .appendLiteral(' ')
                     .appendValue(ChronoField.HOUR_OF_DAY, 2)
                     .appendLiteral(':')
                     .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                     .optionalStart()
                     .appendLiteral(':')
                     .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                     .appendFraction(NANO_OF_SECOND, 0, 9, true)
                     .toFormatter();

                 @Override
                 public ZonedDateTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDateTime.parse(str.replace("\"", "").replace("T", " "), formatter).atZone(database.databaseTimeZone()).withZoneSameInstant(ZoneId.of("UTC"));
                 }

                 @Override
                 public ZonedDateTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
                     String str = rs.getString(col);
                     return rs.wasNull() ? null : LocalDateTime.parse(str.replace("\"", "").replace("T", " "), formatter).atZone(database.databaseTimeZone()).withZoneSameInstant(ZoneId.of("UTC"));
                 }

                 @Override
                 public Object convertToDatabase(Database database, ZonedDateTime value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) ->
                         ps.setString(paramIndex, formatter.format(value.withZoneSameInstant(database.databaseTimeZone())));
                 }

                 @Override
                 public String sqlType(Database database, int arg) {
                     return "timestamp";
                 }

                 @Override
                 public String sqlType(Database database, int arg1, int arg2) {
                     return "timestamp";
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.TIMESTAMP, "timestamp");
                 }
             })
             .register(DbTypeId.BINARY, new DbTypeAdapter<>(DbTypeId.VARCHAR, StringUtil::hex, XtdbDialect::hexToBytes))
             .register(DbTypeId.VARBINARY, new DbTypeAdapter<>(DbTypeId.VARCHAR, StringUtil::hex, XtdbDialect::hexToBytes))
             .register(DbTypeId.CHAR, new DefaultVarchar() {
                 @Override
                 public Object convertToDatabase(Database database, String value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) ->
                         ps.setString(paramIndex, value);
                 }

                 @Override
                 public String sqlType(Database database, int arg) {
                     return sqlType(database);
                 }

                 @Override
                 public String sqlType(Database database, int arg1, int arg2) {
                     return sqlType(database);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.VARCHAR);
                 }
             })
             .register(DbTypeId.VARCHAR, new DefaultVarchar() {
                 @Override
                 public Object convertToDatabase(Database database, String value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) ->
                         ps.setString(paramIndex, value);
                 }

                 @Override
                 public String sqlType(Database database, int arg) {
                     return sqlType(database);
                 }

                 @Override
                 public String sqlType(Database database, int arg1, int arg2) {
                     return sqlType(database);
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.VARCHAR);
                 }
             })
             .register(DbTypeId.JSON, new DefaultJson() {
                 @Override
                 public Object convertToDatabase(Database database, Json value) {
                     return (JdbcParameterSetter<Object>) (ps, paramIndex, databaseValue) -> {
                         //ps.setString(paramIndex, value.data());
                     };
                 }

                 @Override
                 public String sqlType(Database database, int arg) {
                     return "object";
                 }

                 @Override
                 public String literal(Database database, Json value) {
                     return "cast(" + super.literal(database, value) + "as object)";
                 }

                 @Override
                 public String parameter(Database database, Optional<Json> value) {
                     return value.map(Json::data).orElse("null");
                 }

                 @Override
                 public JdbcParameterSetter<Object> nullParameterSetter(Database database) {
                     return (ps, paramIndex, databaseValue) -> ps.setNull(paramIndex, Types.JAVA_OBJECT, "json");
                 }
             })
             .register(DbTypeId.JSONB, new DbTypeAdapter<>(DbTypeId.JSON, b -> new Json(b.data()), j -> new BinaryJson(j.data())))
         ;

         exceptions()
             .register("22001", InvalidValueException::new)
             .register("22003", InvalidValueException::new)
             .register("23502", IllegalNullException::new)
             .register("23503", ReferentialIntegrityException::new)
             .register("23505", DuplicateKeyException::new)
             .register("40P01", LockingException::new)
             .register("42P01", NoSuchObjectException::new)
             .register("42.+", SqlSyntaxException::new)
             .register("55P03", LockingException::new);

         setTempTableInfo(new PostgresTempTableInfo());
         setMergeInfo(new XtdbMergeInfo(this));
     }

     @Override
     public boolean supportsMultiInsert() {
         return true;
     }

     @Override
     public boolean requiresFromDual() {
         return false;
     }

     @Override
     public String qualifiedIndexName(String catalog, String schema, String name) {
         return name;
     }

     @Override
     public String fetchFirst(String sql, long n, OptionalLong offset) {
         return String.format("%s offset %d rows fetch next %d rows only", sql, offset.orElse(0), n);
     }

     @Override
     public boolean supportsLockTimeout() {
         return true;
     }

     @Override
     public String setLockTimeout(long time, TimeUnit unit) {
         long millis = unit.toMillis(time);
         if (millis == 0) {
             millis = 1;
         }
         return String.format("SET LOCAL lock_timeout = '%dms'", millis);
     }

     @Override
     public String resetLockTimeout() {
         return "SET LOCAL lock_timeout = '10s'";
     }

     @Override
     public boolean supportsJsonFunctions() {
         return true;
     }

     @Override
     public String nextFromSequence(String catalog, String schema, String sequenceName) {
         return "nextval('" + sequenceName + "')";
     }

     private static byte[] hexToBytes(String s) {
         int len = s.length();
         byte[] data = new byte[len / 2];
         for (int i = 0; i < len; i += 2) {
             data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                 + Character.digit(s.charAt(i + 1), 16));
         }
         return data;
     }
 }
