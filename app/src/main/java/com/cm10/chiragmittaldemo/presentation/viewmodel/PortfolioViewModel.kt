package com.cm10.chiragmittaldemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.domain.usecase.GetPortfolioUseCaseInterface
import com.cm10.chiragmittaldemo.presentation.state.PortfolioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val getPortfolioUseCase: GetPortfolioUseCaseInterface
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolio()
    }
    
    fun loadPortfolio() {
        viewModelScope.launch {
            getPortfolioUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = PortfolioUiState.Loading
                    }
                    is Resource.Success -> {
                        _uiState.value = PortfolioUiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = PortfolioUiState.Error(resource.message)
                    }
                }
            }
        }
    }

}
