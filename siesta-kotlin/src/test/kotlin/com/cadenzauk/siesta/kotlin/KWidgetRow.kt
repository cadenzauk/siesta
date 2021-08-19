package com.cadenzauk.siesta.kotlin

import javax.persistence.Table

@Table(name = "WIDGET", schema = "SIESTA")
data class KWidgetRow(
    val widgetId: Long,
    val name: String?,
    val manufacturerId: Long,
    val description: String?
)
