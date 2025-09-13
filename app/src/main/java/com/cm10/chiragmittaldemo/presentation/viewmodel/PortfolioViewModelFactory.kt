package com.cm10.chiragmittaldemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cm10.chiragmittaldemo.core.di.DependencyContainer

class PortfolioViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(
                getPortfolioUseCase = DependencyContainer.provideGetPortfolioUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
