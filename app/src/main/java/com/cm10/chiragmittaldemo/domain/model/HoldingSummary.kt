package com.cm10.chiragmittaldemo.domain.model

data class HoldingSummary(
    val holding: Holding?,
    val currentValue: Double?,      // ltp * quantity
    val totalInvestment: Double?,   // avgPrice * quantity
    val totalPnl: Double?,          // currentValue - totalInvestment
    val totalPnlPercentage: Double?, // (totalPnl / totalInvestment) * 100
    val todaysPnl: Double?,         // (close - ltp) * quantity
    val todaysPnlPercentage: Double? // ((close - ltp) / ltp) * 100
)
