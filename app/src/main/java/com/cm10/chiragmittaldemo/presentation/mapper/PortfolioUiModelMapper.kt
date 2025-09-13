package com.cm10.chiragmittaldemo.presentation.mapper

import com.cm10.chiragmittaldemo.core.util.formatCurrency
import com.cm10.chiragmittaldemo.core.util.formatPercentage
import com.cm10.chiragmittaldemo.domain.model.Portfolio
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import com.cm10.chiragmittaldemo.domain.model.PortfolioSummary
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import com.cm10.chiragmittaldemo.presentation.model.HoldingUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioSummaryUiModel

class PortfolioUiModelMapper {
    
    fun mapToUiModel(portfolio: Portfolio?): PortfolioUiModel? {
        return portfolio?.let {
            PortfolioUiModel(
                holdings = it.holdings?.map { holding -> mapHoldingToUiModel(holding) },
                summary = mapSummaryToUiModel(it.summary)
            )
        }
    }
    
    private fun mapHoldingToUiModel(holdingSummary: HoldingSummary?): HoldingUiModel {
        return HoldingUiModel(
            symbol = holdingSummary?.holding?.symbol,
            netQuantityLabel = if (holdingSummary?.holding?.quantity != null) "NET QTY: " else null,
            netQuantityValue = holdingSummary?.holding?.quantity?.toString(),
            ltpLabel = if (holdingSummary?.holding?.ltp != null) "LTP: " else null,
            ltpValue = holdingSummary?.holding?.ltp?.formatCurrency(),
            pnlLabel = if (holdingSummary?.totalPnl != null) "P&L: " else null,
            pnlValue = holdingSummary?.totalPnl?.formatCurrency(),
            isPnlPositive = holdingSummary?.totalPnl?.let { it >= 0 }
        )
    }
    
    private fun mapSummaryToUiModel(portfolioSummary: PortfolioSummary?): PortfolioSummaryUiModel? {
        return portfolioSummary?.let {
            PortfolioSummaryUiModel(
                profitLossLabel = if (it.totalPnl != null) "Profit & Loss*" else null,
                profitLossValue = it.totalPnl?.formatCurrency(),
                profitLossPercentage = it.totalPnlPercentage?.formatPercentage()?.let { " ($it%)" },
                isProfitLossPositive = it.totalPnl?.let { pnl -> pnl >= 0 },
                
                currentValueLabel = if (it.currentValue != null) "Current value*" else null,
                currentValueValue = it.currentValue?.formatCurrency(),
                
                totalInvestmentLabel = if (it.totalInvestment != null) "Total investment*" else null,
                totalInvestmentValue = it.totalInvestment?.formatCurrency(),
                
                todaysPnlLabel = if (it.todaysPnl != null) "Today's Profit & Loss*" else null,
                todaysPnlValue = it.todaysPnl?.formatCurrency(),
                isTodaysPnlPositive = it.todaysPnl?.let { pnl -> pnl >= 0 }
            )
        }
    }
}
