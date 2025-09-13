package com.cm10.chiragmittaldemo.domain.model

data class Holding(
    val symbol: String?,
    val quantity: Int?,
    val ltp: Double?,
    val avgPrice: Double?,
    val close: Double?
)
