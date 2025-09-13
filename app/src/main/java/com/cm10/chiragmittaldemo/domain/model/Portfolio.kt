package com.cm10.chiragmittaldemo.domain.model

data class Portfolio(
    val holdings: List<HoldingSummary>?,
    val summary: PortfolioSummary?
)
