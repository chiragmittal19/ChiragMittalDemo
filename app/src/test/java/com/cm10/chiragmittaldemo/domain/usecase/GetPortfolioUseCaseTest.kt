package com.cm10.chiragmittaldemo.domain.usecase

import app.cash.turbine.test
import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.domain.model.Holding
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import com.cm10.chiragmittaldemo.domain.model.Portfolio
import com.cm10.chiragmittaldemo.domain.model.PortfolioSummary
import com.cm10.chiragmittaldemo.domain.repository.PortfolioRepository
import com.cm10.chiragmittaldemo.presentation.mapper.PortfolioUiModelMapper
import com.cm10.chiragmittaldemo.presentation.model.HoldingUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioSummaryUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPortfolioUseCaseTest {
    
    private lateinit var useCase: GetPortfolioUseCase
    private lateinit var portfolioRepository: PortfolioRepository
    private lateinit var calculateHoldingSummaryUseCase: CalculateHoldingSummaryUseCaseInterface
    private lateinit var calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCaseInterface
    private lateinit var portfolioUiModelMapper: PortfolioUiModelMapper
    
    @Before
    fun setUp() {
        portfolioRepository = mockk()
        calculateHoldingSummaryUseCase = mockk()
        calculatePortfolioSummaryUseCase = mockk()
        portfolioUiModelMapper = mockk()
        
        useCase = GetPortfolioUseCase(
            portfolioRepository,
            calculateHoldingSummaryUseCase,
            calculatePortfolioSummaryUseCase,
            portfolioUiModelMapper
        )
    }
    
    @Test
    fun `invoke returns loading then success when repository returns success`() = runTest {
        // Given
        val holding = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingSummary = HoldingSummary(
            holding = holding,
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val portfolioSummary = PortfolioSummary(
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = portfolioSummary
        )
        val uiModel = PortfolioUiModel(
            holdings = listOf(HoldingUiModel("AAPL", "NET QTY: ", "100", "LTP: ", "₹ 150.00", "P&L: ", "₹ 1,000.00", true)),
            summary = PortfolioSummaryUiModel("Profit & Loss*", "₹ 1,000.00", " (7.14%)", true, "Current value*", "₹ 15,000.00", "Total investment*", "₹ 14,000.00", "Today's Profit & Loss*", "₹ 500.00", true)
        )
        
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Success(listOf(holding)))
        every { calculateHoldingSummaryUseCase(holding) } returns holdingSummary
        every { calculatePortfolioSummaryUseCase(listOf(holdingSummary)) } returns portfolioSummary
        every { portfolioUiModelMapper.mapToUiModel(portfolio) } returns uiModel
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Success)
            assertEquals(uiModel, (secondEmission as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { calculateHoldingSummaryUseCase(holding) }
        verify { calculatePortfolioSummaryUseCase(listOf(holdingSummary)) }
        verify { portfolioUiModelMapper.mapToUiModel(portfolio) }
    }
    
    @Test
    fun `invoke returns loading then error when repository returns error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Error(errorMessage))
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Error)
            assertEquals(errorMessage, (secondEmission as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke returns loading when repository returns loading`() = runTest {
        // Given
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Loading())
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Loading)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke returns success with empty holdings when holding summary calculation returns null`() = runTest {
        // Given
        val holding = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val portfolioSummary = PortfolioSummary(
            currentValue = 0.0,
            totalInvestment = 0.0,
            totalPnl = 0.0,
            totalPnlPercentage = 0.0,
            todaysPnl = 0.0,
            todaysPnlPercentage = 0.0
        )
        val portfolio = Portfolio(
            holdings = null, // Empty holdings when summary calculation returns null
            summary = portfolioSummary
        )
        val uiModel = PortfolioUiModel(
            holdings = null,
            summary = PortfolioSummaryUiModel("Profit & Loss*", "₹ 0.00", " (0.00%)", true, "Current value*", "₹ 0.00", "Total investment*", "₹ 0.00", "Today's Profit & Loss*", "₹ 0.00", true)
        )
        
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Success(listOf(holding)))
        every { calculateHoldingSummaryUseCase(holding) } returns null
        every { calculatePortfolioSummaryUseCase(emptyList()) } returns portfolioSummary
        every { portfolioUiModelMapper.mapToUiModel(portfolio) } returns uiModel
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Success)
            val successData = (secondEmission as Resource.Success).data
            assertEquals(uiModel, successData)
            
            awaitComplete()
        }
        
        verify { calculatePortfolioSummaryUseCase(emptyList()) }
        verify { portfolioUiModelMapper.mapToUiModel(portfolio) }
    }
    
    @Test
    fun `invoke returns error when ui model mapper returns null`() = runTest {
        // Given
        val holding = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingSummary = HoldingSummary(
            holding = holding,
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val portfolioSummary = PortfolioSummary(
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = portfolioSummary
        )
        
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Success(listOf(holding)))
        every { calculateHoldingSummaryUseCase(holding) } returns holdingSummary
        every { calculatePortfolioSummaryUseCase(listOf(holdingSummary)) } returns portfolioSummary
        every { portfolioUiModelMapper.mapToUiModel(portfolio) } returns null
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Error)
            assertEquals("Failed to map portfolio data to UI model", (secondEmission as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke returns error when exception is thrown`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery { portfolioRepository.getHoldings() } throws exception
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Error)
            assertEquals("Unexpected error", (secondEmission as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke handles empty holdings list correctly`() = runTest {
        // Given
        val portfolioSummary = PortfolioSummary(
            currentValue = 0.0,
            totalInvestment = 0.0,
            totalPnl = 0.0,
            totalPnlPercentage = 0.0,
            todaysPnl = 0.0,
            todaysPnlPercentage = 0.0
        )
        val portfolio = Portfolio(
            holdings = null, // Empty holdings should result in null
            summary = portfolioSummary
        )
        val uiModel = PortfolioUiModel(
            holdings = null,
            summary = PortfolioSummaryUiModel("Profit & Loss*", "₹ 0.00", " (0.00%)", true, "Current value*", "₹ 0.00", "Total investment*", "₹ 0.00", "Today's Profit & Loss*", "₹ 0.00", true)
        )
        
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Success(emptyList()))
        every { calculatePortfolioSummaryUseCase(emptyList()) } returns portfolioSummary
        every { portfolioUiModelMapper.mapToUiModel(portfolio) } returns uiModel
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Success)
            assertEquals(uiModel, (secondEmission as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { calculatePortfolioSummaryUseCase(emptyList()) }
        verify { portfolioUiModelMapper.mapToUiModel(portfolio) }
    }
    
    @Test
    fun `invoke handles mixed valid and null holding summaries`() = runTest {
        // Given
        val holding1 = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holding2 = Holding("GOOGL", 50, null, 2400.0, 2450.0) // This will return null summary
        val holding3 = Holding("MSFT", 75, 300.0, 280.0, 295.0)
        
        val holdingSummary1 = HoldingSummary(
            holding = holding1,
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val holdingSummary3 = HoldingSummary(
            holding = holding3,
            currentValue = 22500.0,
            totalInvestment = 21000.0,
            totalPnl = 1500.0,
            totalPnlPercentage = 7.14,
            todaysPnl = -375.0,
            todaysPnlPercentage = -1.67
        )
        
        val portfolioSummary = PortfolioSummary(
            currentValue = 37500.0,
            totalInvestment = 35000.0,
            totalPnl = 2500.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 125.0,
            todaysPnlPercentage = 0.33
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary1, holdingSummary3), // Only valid summaries
            summary = portfolioSummary
        )
        val uiModel = PortfolioUiModel(
            holdings = listOf(
                HoldingUiModel("AAPL", "NET QTY: ", "100", "LTP: ", "₹ 150.00", "P&L: ", "₹ 1,000.00", true),
                HoldingUiModel("MSFT", "NET QTY: ", "75", "LTP: ", "₹ 300.00", "P&L: ", "₹ 1,500.00", true)
            ),
            summary = PortfolioSummaryUiModel("Profit & Loss*", "₹ 2,500.00", " (7.14%)", true, "Current value*", "₹ 37,500.00", "Total investment*", "₹ 35,000.00", "Today's Profit & Loss*", "₹ 125.00", true)
        )
        
        coEvery { portfolioRepository.getHoldings() } returns flowOf(Resource.Success(listOf(holding1, holding2, holding3)))
        every { calculateHoldingSummaryUseCase(holding1) } returns holdingSummary1
        every { calculateHoldingSummaryUseCase(holding2) } returns null // This will be filtered out
        every { calculateHoldingSummaryUseCase(holding3) } returns holdingSummary3
        every { calculatePortfolioSummaryUseCase(listOf(holdingSummary1, holdingSummary3)) } returns portfolioSummary
        every { portfolioUiModelMapper.mapToUiModel(portfolio) } returns uiModel
        
        // When & Then
        useCase().test {
            val firstEmission = awaitItem()
            assertTrue(firstEmission is Resource.Loading)
            
            val secondEmission = awaitItem()
            assertTrue(secondEmission is Resource.Success)
            assertEquals(uiModel, (secondEmission as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { calculateHoldingSummaryUseCase(holding1) }
        verify { calculateHoldingSummaryUseCase(holding2) }
        verify { calculateHoldingSummaryUseCase(holding3) }
        verify { calculatePortfolioSummaryUseCase(listOf(holdingSummary1, holdingSummary3)) }
        verify { portfolioUiModelMapper.mapToUiModel(portfolio) }
    }
}
