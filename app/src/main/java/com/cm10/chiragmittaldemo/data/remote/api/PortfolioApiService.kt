package com.cm10.chiragmittaldemo.data.remote.api

import com.cm10.chiragmittaldemo.data.remote.dto.PortfolioResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface PortfolioApiService {
    @GET(".")
    suspend fun getPortfolioHoldings(): Response<PortfolioResponseDto>
}
