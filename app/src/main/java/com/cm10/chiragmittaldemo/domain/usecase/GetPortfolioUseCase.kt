package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.domain.model.Portfolio
import com.cm10.chiragmittaldemo.domain.repository.PortfolioRepository
import com.cm10.chiragmittaldemo.presentation.mapper.PortfolioUiModelMapper
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val calculateHoldingSummaryUseCase: CalculateHoldingSummaryUseCaseInterface,
    private val calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCaseInterface,
    private val portfolioUiModelMapper: PortfolioUiModelMapper
) : GetPortfolioUseCaseInterface {
    
    override operator fun invoke(): Flow<Resource<PortfolioUiModel>> = flow {
        try {
            emit(Resource.Loading())
            
            portfolioRepository.getHoldings().collect { resource ->
                when (resource) {
                    is Resource.Loading -> emit(Resource.Loading())
                    is Resource.Error -> emit(Resource.Error(resource.message))
                    is Resource.Success -> {
                        val holdings = resource.data
                        val holdingSummaries = holdings.mapNotNull { holding ->
                            calculateHoldingSummaryUseCase(holding)
                        }
                        val portfolioSummary = calculatePortfolioSummaryUseCase(holdingSummaries)
                        
                        val portfolio = Portfolio(
                            holdings = holdingSummaries.ifEmpty { null },
                            summary = portfolioSummary
                        )
                        
                        // Map to UI Model using the mapper
                        val uiModel = portfolioUiModelMapper.mapToUiModel(portfolio)
                        if (uiModel != null) {
                            emit(Resource.Success(uiModel))
                        } else {
                            emit(Resource.Error("Failed to map portfolio data to UI model"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
}
