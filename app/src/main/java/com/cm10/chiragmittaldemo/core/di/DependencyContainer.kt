package com.cm10.chiragmittaldemo.core.di

import com.cm10.chiragmittaldemo.core.constants.NetworkConstants
import com.cm10.chiragmittaldemo.data.mapper.HoldingMapper
import com.cm10.chiragmittaldemo.data.remote.api.PortfolioApiService
import com.cm10.chiragmittaldemo.domain.repository.PortfolioRepositoryImpl
import com.cm10.chiragmittaldemo.domain.repository.PortfolioRepository
import com.cm10.chiragmittaldemo.domain.usecase.CalculateHoldingSummaryUseCase
import com.cm10.chiragmittaldemo.domain.usecase.CalculateHoldingSummaryUseCaseInterface
import com.cm10.chiragmittaldemo.domain.usecase.CalculatePortfolioSummaryUseCase
import com.cm10.chiragmittaldemo.domain.usecase.CalculatePortfolioSummaryUseCaseInterface
import com.cm10.chiragmittaldemo.domain.usecase.GetPortfolioUseCase
import com.cm10.chiragmittaldemo.domain.usecase.GetPortfolioUseCaseInterface
import com.cm10.chiragmittaldemo.presentation.mapper.PortfolioUiModelMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DependencyContainer {

    // Network layer singletons
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val portfolioApiService: PortfolioApiService by lazy {
        retrofit.create(PortfolioApiService::class.java)
    }

    // Data layer
    private val holdingMapper: HoldingMapper by lazy {
        HoldingMapper()
    }

    private val portfolioRepository: PortfolioRepository by lazy {
        PortfolioRepositoryImpl(portfolioApiService, holdingMapper)
    }

    // Domain layer
    private val calculateHoldingSummaryUseCase: CalculateHoldingSummaryUseCaseInterface by lazy {
        CalculateHoldingSummaryUseCase()
    }

    private val calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCaseInterface by lazy {
        CalculatePortfolioSummaryUseCase()
    }

    // Presentation layer
    private val portfolioUiModelMapper: PortfolioUiModelMapper by lazy {
        PortfolioUiModelMapper()
    }

    private val getPortfolioUseCase: GetPortfolioUseCaseInterface by lazy {
        GetPortfolioUseCase(
            portfolioRepository,
            calculateHoldingSummaryUseCase,
            calculatePortfolioSummaryUseCase,
            portfolioUiModelMapper
        )
    }

    // Public provider methods
    fun provideGetPortfolioUseCase(): GetPortfolioUseCaseInterface = getPortfolioUseCase
}