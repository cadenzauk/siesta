package com.cadenzauk.siesta.kotlin

import com.cadenzauk.siesta.Database
import com.cadenzauk.siesta.Sequence
import com.cadenzauk.siesta.Transaction
import com.cadenzauk.siesta.catalog.Table
import com.cadenzauk.siesta.grammar.dml.ExpectingWhere
import com.cadenzauk.siesta.grammar.dml.InSetExpectingWhere
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1
import com.cadenzauk.siesta.grammar.temp.GlobalTempTable
import com.cadenzauk.siesta.grammar.temp.LocalTempTable
import com.cadenzauk.siesta.grammar.temp.TempTable
import com.cadenzauk.siesta.type.DbTypeId
import kotlin.reflect.KClass

fun <R : Any> Database.from(rowClass: KClass<R>): ExpectingJoin1<R> =
    from(rowClass.java)

fun <R : Any> Database.from(rowClass: KClass<R>, alias: String): ExpectingJoin1<R> =
    from(rowClass.java, alias)

fun <T : Number> Database.sequence(valueClass: KClass<T>, name: String): Sequence<T> =
    sequence(valueClass.java, name)

fun <T : Number> Database.sequence(valueClass: KClass<T>, catalog: String, schema: String, name: String): Sequence<T> =
    sequence(valueClass.java, catalog, schema, name)

fun <T : Any> Database.table(rowClass: KClass<T>): Table<T> =
    table(rowClass.java)

fun <T : Any, B : Any> Database.createTemporaryTable(transaction: Transaction, rowClass: KClass<T>, init: LocalTempTable.Builder<T, T>.() -> LocalTempTable.Builder<T, B>): TempTable<T> =
    createTemporaryTable(transaction, rowClass.java, init)

fun <T : Any, B : Any> Database.globalTemporaryTable(transaction: Transaction, rowClass: KClass<T>, init: GlobalTempTable.Builder<T, T>.() -> GlobalTempTable.Builder<T, B>): TempTable<T> =
    globalTemporaryTable(transaction, rowClass.java, init)

fun <U : Any> Database.update(rowClass: KClass<U>, alias: String): InSetExpectingWhere<U> =
    update(rowClass.java, alias)

fun <U : Any> Database.delete(rowClass: KClass<U>, alias: String): ExpectingWhere =
    delete(rowClass.java, alias)

fun <T: Any, D: Any> Database.Builder.adapter(kClass: KClass<T>, dbTypeId: DbTypeId<D>, toDb: (T) -> D, fromDb: (D) -> T): Database.Builder =
    adapter(kClass.java, dbTypeId, toDb, fromDb)

fun <T : Enum<T>> Database.Builder.enumByName(enumClass: KClass<T>): Database.Builder =
    enumByName(enumClass.java)

fun <R : Any> Database.Builder.table(rowClass: KClass<R>, init: Table.Builder<R, *>.() -> Unit): Database.Builder =
    this.table(rowClass.java, init)
