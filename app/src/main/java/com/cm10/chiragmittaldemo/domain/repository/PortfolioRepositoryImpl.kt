package com.cm10.chiragmittaldemo.domain.repository

import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.data.mapper.HoldingMapper
import com.cm10.chiragmittaldemo.data.remote.api.PortfolioApiService
import com.cm10.chiragmittaldemo.domain.model.Holding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PortfolioRepositoryImpl(
    private val apiService: PortfolioApiService,
    private val holdingMapper: HoldingMapper
) : PortfolioRepository {
    
    override suspend fun getHoldings(): Flow<Resource<List<Holding>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getPortfolioHoldings()

            if (response.isSuccessful) {
                val holdingsDto = response.body()?.data?.userHolding
                val holdings = holdingMapper.mapToDomainList(holdingsDto) ?: emptyList()
                emit(Resource.Success(holdings))
            } else {
                emit(Resource.Error("Failed to fetch holdings: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }

    override suspend fun refreshHoldings(): Flow<Resource<List<Holding>>> {
        return getHoldings() // Same implementation for now
    }
}