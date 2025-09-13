package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.Holding
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary

class CalculateHoldingSummaryUseCase : CalculateHoldingSummaryUseCaseInterface {
    
    override operator fun invoke(holding: Holding?): HoldingSummary? {
        return holding?.let {
            val currentValue = calculateCurrentValue(it)
            val totalInvestment = calculateTotalInvestment(it)
            val totalPnl = calculateTotalPnl(currentValue, totalInvestment)
            val totalPnlPercentage = calculateTotalPnlPercentage(totalPnl, totalInvestment)
            val todaysPnl = calculateTodaysPnl(it)
            val todaysPnlPercentage = calculateTodaysPnlPercentage(it)
            
            HoldingSummary(
                holding = it,
                currentValue = currentValue,
                totalInvestment = totalInvestment,
                totalPnl = totalPnl,
                totalPnlPercentage = totalPnlPercentage,
                todaysPnl = todaysPnl,
                todaysPnlPercentage = todaysPnlPercentage
            )
        }
    }
    
    private fun calculateCurrentValue(holding: Holding): Double? {
        return if (holding.ltp != null && holding.quantity != null) {
            holding.ltp * holding.quantity
        } else null
    }
    
    private fun calculateTotalInvestment(holding: Holding): Double? {
        return if (holding.avgPrice != null && holding.quantity != null) {
            holding.avgPrice * holding.quantity
        } else null
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
    
    private fun calculateTodaysPnl(holding: Holding): Double? {
        return if (holding.close != null && holding.ltp != null && holding.quantity != null) {
            (holding.close - holding.ltp) * holding.quantity
        } else null
    }
    
    private fun calculateTodaysPnlPercentage(holding: Holding): Double? {
        return if (holding.ltp != null && holding.close != null && holding.ltp != 0.0) {
            ((holding.close - holding.ltp) / holding.ltp) * 100
        } else null
    }
}
