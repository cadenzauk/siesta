package com.cadenzauk.siesta.kotlin

import com.cadenzauk.siesta.model.MoneyAmount
import java.util.Optional
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Table

@Table(name = "WIDGET", schema = "SIESTA")
data class KWidgetRow(
    val widgetId: Long,
    val name: String?,
    val manufacturerId: Long,
    val description: String?
)

@MappedSuperclass
@Table(name = "PART", schema = "SIESTA")
data class KPartRow(
    @Id
    val partId: Long,
    val widgetId: Long,
    val description: String?,
    val purchasePrice: MoneyAmount?,
    val retailPrice: Optional<MoneyAmount>?,
)
