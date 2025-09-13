package com.cm10.chiragmittaldemo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PortfolioResponseDto(
    @SerializedName("data")
    val data: PortfolioDataDto?
)

data class PortfolioDataDto(
    @SerializedName("userHolding")
    val userHolding: List<HoldingDto>?
)

data class HoldingDto(
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("ltp")
    val ltp: Double?,
    @SerializedName("avgPrice")
    val avgPrice: Double?,
    @SerializedName("close")
    val close: Double?
)
