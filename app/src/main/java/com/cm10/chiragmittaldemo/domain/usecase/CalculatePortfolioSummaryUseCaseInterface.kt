package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import com.cm10.chiragmittaldemo.domain.model.PortfolioSummary

interface CalculatePortfolioSummaryUseCaseInterface {
    operator fun invoke(holdingSummaries: List<HoldingSummary>?): PortfolioSummary?
}
