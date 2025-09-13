package com.cm10.chiragmittaldemo.domain.repository

import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.domain.model.Holding
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    suspend fun getHoldings(): Flow<Resource<List<Holding>>>
    suspend fun refreshHoldings(): Flow<Resource<List<Holding>>>
}
