package com.cm10.chiragmittaldemo.presentation.model

data class PortfolioSummaryUiModel(
    val profitLossLabel: String?,         // "Profit & Loss*"
    val profitLossValue: String?,         // "₹ 1,856.25"
    val profitLossPercentage: String?,    // " (1.31%)"
    val isProfitLossPositive: Boolean?,   // true/false/null
    
    val currentValueLabel: String?,       // "Current value*"
    val currentValueValue: String?,       // "₹ 143,492.25"
    
    val totalInvestmentLabel: String?,    // "Total investment*"
    val totalInvestmentValue: String?,    // "₹ 141,636.00"
    
    val todaysPnlLabel: String?,          // "Today's Profit & Loss*"
    val todaysPnlValue: String?,          // "₹ 4,327.75"
    val isTodaysPnlPositive: Boolean?     // true/false/null
)
