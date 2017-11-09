# SIESTA [![Build Status](https://api.travis-ci.org/cadenzauk/siesta.svg?branch=master)](https://travis-ci.org/cadenzauk/siesta)
**S**IESTA **I**s an **E**asy **S**QL **T**ypesafe **A**PI that lets you write SQL queries in Java.  

It is Easy because the interface is discoverable with autocompletion guiding you as you program.  It is Typesafe 
because queries are written in strongly-typed Java code instead of just being embedded in strings.

## Examples

Let's look at a few examples to see how it works.

### Inserting into the Database

First we'll create a table for using [Liquibase](http://www.liquibase.org/).

```xml
<createTable tableName="WIDGET" schemaName="SIESTA">
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

[`Database`](https://github.com/cadenzauk/siesta/blob/master/siesta/src/main/java/com/cadenzauk/siesta/Database.java) is the heart 
of SIESTA.  It holds information about how to map our objects to the database, what dialect of SQL we're
using etc.  [`SqlExecutor`](https://github.com/cadenzauk/siesta/blob/master/siesta/src/main/java/com/cadenzauk/siesta/SqlExecutor.java)
is an interface that SIESTA uses to actually execute the SQL or DML.  Here we're using an implementation based on Spring's 
`JdbcTemplate`.

The example works because the default [`NamingStrategy`](https://github.com/cadenzauk/siesta/blob/master/siesta/src/main/java/com/cadenzauk/siesta/NamingStrategy.java)
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
You can also do joins and projections:

```java
List<Tuple2<String,String>> makersOfGizmos = database.from(Widget.class, "w")
    .join(Manufacturer.class, "m").on(Manufacturer::manufacturerId).isEqualTo(Widget::manufacturerId)
    .select(Manufacturer::name).comma(Widget::description)
    .where(Widget::name).isEqualTo("Gizmo")
    .orderBy(Widget::widgetId)
    .list(sqlExecutor);
```

One slight difference you'll have noticed from SQL syntax is that we do projection via `select` *after* the `from` and `join` clauses.
Apart from that, the syntax follows SQL pretty closely so it should be easy to write and understand.

### Type Safety

As we said, SIESTA is Typesafe, so the compiler and IDE will protect you from some mistakes.  We don't use strings to refer to 
columns; we use typed method references.  The following won't compile because `widgetId` is of type long so you can't compare it 
to a string. 


```java
Optional<Widget> oops = database.from(Widget.class)
    .where(Widget::widgetId).isEqualTo("Sprocket")  // Compile error
    .optional(sqlExecutor);
```

If you like you can go a step further and use typesafe classes instead of primitives.  For example, suppose we ID for a
Widget was a WidgetId class that wrapped a long.  We can tell SIESTA how to convert a WidgetId to and from the database with
a simple one liner in the database builder:

```java
Database database = Database.newBuilder()
    .adapter(WidgetId.class, DbTypeId.BIGINT, WidgetId::id, WidgetId::new)
    ...
    .build();
```

Now we can use a WidgetId directly.  The following code would compile because both WidgetRow::widgetId and our widgetId 
parameter are the same type:  

```java
Optional<Widget> findWidget(WidgetId widgetId) {
    return database.from(WidgetRowWithTypeSafeId.class)
        .where(WidgetRow::widgetId).isEqualTo(widgetId)
        .optional();
}
```

But if we got our query wrong, we'd get a compile error:

```java
Optional<Widget> findWidget(WidgetId widgetId) {
    return database.from(WidgetRowWithTypeSafeId.class)
        .where(WidgetRow::manufacturerId).isEqualTo(widgetId)
        .optional();
Error:(227, 60) java: no suitable method found for isEqualTo(com.cadenzauk.siesta.model.WidgetId)
    method com.cadenzauk.siesta.grammar.expression.ExpressionBuilder.isEqualTo(com.cadenzauk.siesta.model.ManufacturerId) is not applicable
      (argument mismatch; com.cadenzauk.siesta.model.WidgetId cannot be converted to com.cadenzauk.siesta.model.ManufacturerId)
      ...
}
```

### Complex Queries

Suppose we have some more rows in our database (yes, you can insert multiple rows in a single
statement if they're the same type):

```java
database.insert(
    new Manufacturer(2006L, "Spacely Space Sprockets, Inc"),
    new Manufacturer(2007L, "Cogswell's Cogs"),
    new Manufacturer(2008L, "Orbit City Gears"));
database.insert(
    new Widget(1006L, "Cog", 2006L, Optional.of("Spacely Sprocket")),
    new Widget(1007L, "Cog", 2007L, Optional.of("Cogswell Cog")),
    new Widget(1008L, "Cog", 2007L, Optional.of("Cogswell Sprocket")));

```

We can do more complicated queries such as using aggregate functions and grouping like this:

```java
List<Tuple2<String,Integer>> partCountsBySupplier = database.from(Manufacturer.class, "m")
    .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
    .select(Manufacturer::name).comma(countDistinct(Widget::widgetId))
    .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
    .groupBy(Manufacturer::manufacturerId)
    .orderBy(Manufacturer::manufacturerId)
    .list();
```

When we select columns the results are returned as tuples, which isn't always convenient - `item1()` is 
not as nice as being able to refer to `name()`.  It is also quite limiting since we currently only have tuples
up to `Tuple9` so you can only have nine columns in your result set this way.

An alternative is to define a class to hold your results:

```java
public class ManufacturerSummary {
    private final String name;
    private final int numberOfPartsSupplied;

    public ManufacturerSummary(String name, int numberOfPartsSupplied) {
        this.name = name;
        this.numberOfPartsSupplied = numberOfPartsSupplied;
    }

    public String name() {
        return name;
    }

    public int numberOfPartsSupplied() {
        return numberOfPartsSupplied;
    }
}

```

Now you can map your results into this class as part of the query.

```java
List<ManufacturerSummary> manufacturerSummaries = database.from(Manufacturer.class, "m")
    .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
    .selectInto(ManufacturerSummary.class)
    .with(Manufacturer::name).as(ManufacturerSummary::name)
    .with(countDistinct(Widget::widgetId)).as(ManufacturerSummary::numberOfPartsSupplied)
    .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
    .groupBy(Manufacturer::manufacturerId)
    .orderBy(Manufacturer::manufacturerId)
    .list();
```

You could also add a HAVING clause as follows.

```java
List<ManufacturerSummary> nonSuppliers = database.from(Manufacturer.class, "m")
    .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
    .selectInto(ManufacturerSummary.class)
    .with(Manufacturer::name).as(ManufacturerSummary::name)
    .with(countDistinct(Widget::widgetId)).as(ManufacturerSummary::numberOfPartsSupplied)
    .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
    .groupBy(Manufacturer::manufacturerId)
    .having(countDistinct(Widget::widgetId)).isEqualTo(0)
    .orderBy(Manufacturer::manufacturerId)
    .list();

```

### Working With Streams

Instead of `list()` or `optional()`, or `single()`, you can use `stream()` to get the results of your query as a `Stream`.  The stream needs to
be closed when you're finished with it so it can close the JDBC resources used to execute the query.  To make this convenient and 
foolproof, the stream method takes a [`CompositeAutoCloseable`](https://github.com/cadenzauk/siesta/blob/master/siesta/src/main/java/com/cadenzauk/core/lang/CompositeAutoCloseable.java) 
object to which the stream is added.  Closing the `CompositeAutoCloseable` closes the stream, which of course can easily be done with Java's
"try with resources":

```java
// Not a good example - see below
try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
    database.from(Manufacturer.class)
        .stream(autoCloseable)
        .forEach(m -> System.out.println(m.name()));
}
```

SIESTA's [`JdbcSqlExecutor`](https://github.com/cadenzauk/siesta/blob/master/siesta/src/main/java/com/cadenzauk/siesta/jdbc/JdbcSqlExecutor.java)
implements `stream()` based on a spliterator that scrolls through the result set so by using this you can write code that
handles very large queries.

Of course you should do as much work to filter and project in the database rather than shipping all the unneeded results to the client
and then filtering the stream.

```java
// Don't do this!
try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
    database.from(Manufacturer.class)
        .stream(autoCloseable)
        .filter(m -> m.manufacturerId() == 1L)
        .forEach(m -> System.out.println(m.name()));
}

// Do this
try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
    database.from(Manufacturer.class)
        .select(Manufacturer::name)
        .where(Manufacturer::manufacturerId).isEqualTo(1L)
        .stream(autoCloseable)
        .forEach(System.out::println);
}

```

## Example Code

Complete working tests for the examples shown here can be found in 
[SiestaExample.java](https://github.com/cadenzauk/siesta/blob/master/siesta/src/test/java/com/cadenzauk/siesta/example/SiestaExample.java) 