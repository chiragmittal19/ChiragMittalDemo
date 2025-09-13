package com.cm10.chiragmittaldemo.presentation.model

data class PortfolioUiModel(
    val holdings: List<HoldingUiModel>?,
    val summary: PortfolioSummaryUiModel?
)
