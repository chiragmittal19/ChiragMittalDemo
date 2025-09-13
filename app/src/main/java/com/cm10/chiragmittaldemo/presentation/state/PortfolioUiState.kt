package com.cm10.chiragmittaldemo.presentation.state

import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel

sealed class PortfolioUiState {
    data object Loading : PortfolioUiState()
    data class Success(val portfolio: PortfolioUiModel) : PortfolioUiState()
    data class Error(val message: String) : PortfolioUiState()
}
