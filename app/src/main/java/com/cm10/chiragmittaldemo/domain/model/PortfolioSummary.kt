package com.cm10.chiragmittaldemo.domain.model

data class PortfolioSummary(
    val currentValue: Double?,
    val totalInvestment: Double?,
    val totalPnl: Double?,
    val totalPnlPercentage: Double?,
    val todaysPnl: Double?,
    val todaysPnlPercentage: Double?
)
