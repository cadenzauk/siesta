# SIESTA [![Build Status](https://api.travis-ci.org/cadenzauk/siesta.svg?branch=master)](https://api.travis-ci.org/cadenzauk/siesta.svg?branch=master)
**S**IESTA **I**s an **E**asy **S**QL **T**ypesafe **A**PI that lets you write SQL queries in Java.  

It is Easy because the interface is discoverable with autocompletion guiding you as you program.  It is Typesafe 
because queries are written in strongly-typed Java code instead of just being embedded in strings.

## Examples

Let's look at a few examples to see how it works.

### Inserting into the Database

First we'll create a table for using [Liquibase](http://www.liquibase.org/).

```xml
<createTable tableName="WIDGET" schemaName="TEST">
    <column name="WIDGET_ID" type="BIGINT">
        <constraints primaryKey="true" nullable="false"/>
    </column>
    <column name="NAME" type="VARCHAR(100)">
        <constraints nullable="false"/>
    </column>
    <column name="MANUFACTURER_ID" type="BIGINT">
        <constraints nullable="false"/>
    </column>
    <column name="DESCRIPTION" type="VARCHAR(200)"/>
</createTable>
```

And then we'll need our Java object we want to store in that table:

```java
public class Widget {
    private long widgetId;
    private String name;
    private long manufacturerId;
    private Optional<String> description;

    private Widget() {
    }

    public Widget(long widgetId, String name, long manufacturerId, Optional<String> description) {
        this.widgetId = widgetId;
        this.name = name;
        this.manufacturerId = manufacturerId;
        this.description = description;
    }

    public long widgetId() {
        return widgetId;
    }

    public String name() {
        return name;
    }

    public long manufacturerId() {
        return manufacturerId;
    }

    public Optional<String> description() {
        return description;
    }
}
```

Now we can create an object and insert it as simply as

```java
Database database = Database.newBuilder().build();
SqlExecutor sqlExecutor = JdbcTemplateSqlExecutor.of(dataSource);
Widget sprocket = new Widget(1L, "Sprocket", 4L, Optional.empty());

database.insert(sqlExecutor, sprocket);
```

[`Database`](https://github.com/cadenzauk/siesta/blob/master/src/main/java/com/cadenzauk/siesta/SqlExecutor.java) is the heart 
of SIESTA.  It holds information about how to map our objects to the database, what dialect of SQL we're
using etc.  [`SqlExecutor`](https://github.com/cadenzauk/siesta/blob/master/src/main/java/com/cadenzauk/siesta/SqlExecutor.java)
is an interface that SIESTA uses to actually execute the SQL or DML.  Here we're using an implementation based on Spring's 
`JdbcTemplate`.

The example works because the default [`NamingStrategy`](https://github.com/cadenzauk/siesta/blob/master/src/main/java/com/cadenzauk/siesta/NamingStrategy.java)
converts Java field names to database column names and Java classes to tables by splitting camel case words with underscores and 
converting to uppercase, so `widgetId` becomes `WIDGET_ID` as in our table.  You can implement a different naming strategy if you like, or you can use JPA 
annotations to explicitly name the columns and Tables.

```java
@Table(name = "WIDGET")
public class WidgetDto {
    @Column(name = "WIDGET_ID")
    private long id;
```

### Queries

Querying the database is done via a fluent API.
```java
Optional<Widget> widgetNumberOne = database.from(Widget.class)
    .where(Widget::widgetId).isEqualTo(1L)
    .optional(sqlExecutor);

List<Widget> sprockets = database.from(Widget.class)
    .where(Widget::name).isEqualTo("Sprocket")
    .list(sqlExecutor);

```
As we said, SIESTA is Typesafe, so the compiler and IDE will protect you from some mistakes.  We don't use strings to refer to 
columns; we use typed method references.  This won't compile because `widgetId` is long so you can't compare it to a string. 

```java
Optional<Widget> oops = database.from(Widget.class)
    .where(Widget::widgetId).isEqualTo("Sprocket")  // Compile error
    .optional(sqlExecutor);
```
