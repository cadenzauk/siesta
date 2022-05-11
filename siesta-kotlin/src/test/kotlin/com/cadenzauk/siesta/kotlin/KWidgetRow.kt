/*
 * Copyright (c) 2022 Cadenza United Kingdom Limited
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
