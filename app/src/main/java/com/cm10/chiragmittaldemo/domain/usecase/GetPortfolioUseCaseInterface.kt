package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import kotlinx.coroutines.flow.Flow

interface GetPortfolioUseCaseInterface {
    operator fun invoke(): Flow<Resource<PortfolioUiModel>>
}
