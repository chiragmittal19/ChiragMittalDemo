package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import com.cm10.chiragmittaldemo.domain.model.PortfolioSummary

class CalculatePortfolioSummaryUseCase : CalculatePortfolioSummaryUseCaseInterface {
    
    override operator fun invoke(holdingSummaries: List<HoldingSummary>?): PortfolioSummary? {
        return if (!holdingSummaries.isNullOrEmpty()) {
            val currentValue = calculateTotalCurrentValue(holdingSummaries)
            val totalInvestment = calculateTotalInvestment(holdingSummaries)
            val totalPnl = calculateTotalPnl(currentValue, totalInvestment)
            val totalPnlPercentage = calculateTotalPnlPercentage(totalPnl, totalInvestment)
            val todaysPnl = calculateTotalTodaysPnl(holdingSummaries)
            val todaysPnlPercentage = calculateTodaysPnlPercentage(todaysPnl, currentValue)
            
            PortfolioSummary(
                currentValue = currentValue,
                totalInvestment = totalInvestment,
                totalPnl = totalPnl,
                totalPnlPercentage = totalPnlPercentage,
                todaysPnl = todaysPnl,
                todaysPnlPercentage = todaysPnlPercentage
            )
        } else null
    }
    
    private fun calculateTotalCurrentValue(holdingSummaries: List<HoldingSummary>): Double? {
        val validValues = holdingSummaries.mapNotNull { it.currentValue }
        return if (validValues.isNotEmpty()) validValues.sum() else null
    }
    
    private fun calculateTotalInvestment(holdingSummaries: List<HoldingSummary>): Double? {
        val validValues = holdingSummaries.mapNotNull { it.totalInvestment }
        return if (validValues.isNotEmpty()) validValues.sum() else null
    }
    
    private fun calculateTotalPnl(currentValue: Double?, totalInvestment: Double?): Double? {
        return if (currentValue != null && totalInvestment != null) {
            currentValue - totalInvestment
        } else null
    }
    
    private fun calculateTotalPnlPercentage(totalPnl: Double?, totalInvestment: Double?): Double? {
        return if (totalPnl != null && totalInvestment != null && totalInvestment != 0.0) {
            (totalPnl / totalInvestment) * 100
        } else null
    }
    
    private fun calculateTotalTodaysPnl(holdingSummaries: List<HoldingSummary>): Double? {
        val validValues = holdingSummaries.mapNotNull { it.todaysPnl }
        return if (validValues.isNotEmpty()) validValues.sum() else null
    }
    
    private fun calculateTodaysPnlPercentage(todaysPnl: Double?, currentValue: Double?): Double? {
        return if (todaysPnl != null && currentValue != null && currentValue != 0.0) {
            (todaysPnl / currentValue) * 100
        } else null
    }
}
