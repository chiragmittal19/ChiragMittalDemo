package com.cm10.chiragmittaldemo.presentation.viewmodel

import app.cash.turbine.test
import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.domain.usecase.GetPortfolioUseCaseInterface
import com.cm10.chiragmittaldemo.presentation.model.HoldingUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioSummaryUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import com.cm10.chiragmittaldemo.presentation.state.PortfolioUiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {
    
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var getPortfolioUseCase: GetPortfolioUseCaseInterface
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPortfolioUseCase = mockk()
        
        // Setup default mock behavior to prevent initialization issues
        every { getPortfolioUseCase() } returns flowOf(Resource.Loading())
        
        viewModel = PortfolioViewModel(getPortfolioUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `init calls loadPortfolio and starts with loading state`() = runTest {
        // Given - ViewModel is created in setUp
        
        // Then
        verify { getPortfolioUseCase() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PortfolioUiState.Loading)
        }
    }
    
    @Test
    fun `loadPortfolio emits loading then success state when use case returns success`() = runTest {
        // Given
        val portfolioUiModel = PortfolioUiModel(
            holdings = listOf(
                HoldingUiModel(
                    symbol = "AAPL",
                    netQuantityLabel = "NET QTY: ",
                    netQuantityValue = "100",
                    ltpLabel = "LTP: ",
                    ltpValue = "₹ 150.00",
                    pnlLabel = "P&L: ",
                    pnlValue = "₹ 1,000.00",
                    isPnlPositive = true
                )
            ),
            summary = PortfolioSummaryUiModel(
                profitLossLabel = "Profit & Loss*",
                profitLossValue = "₹ 1,000.00",
                profitLossPercentage = " (7.14%)",
                isProfitLossPositive = true,
                currentValueLabel = "Current value*",
                currentValueValue = "₹ 15,000.00",
                totalInvestmentLabel = "Total investment*",
                totalInvestmentValue = "₹ 14,000.00",
                todaysPnlLabel = "Today's Profit & Loss*",
                todaysPnlValue = "₹ 500.00",
                isTodaysPnlPositive = true
            )
        )
        
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Success(portfolioUiModel)
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is PortfolioUiState.Success)
            assertEquals(portfolioUiModel, (successState as PortfolioUiState.Success).portfolio)
        }
    }
    
    @Test
    fun `loadPortfolio emits loading then error state when use case returns error`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is PortfolioUiState.Error)
            assertEquals(errorMessage, (errorState as PortfolioUiState.Error).message)
        }
    }
    
    @Test
    fun `loadPortfolio emits loading state when use case returns only loading`() = runTest {
        // Given
        every { getPortfolioUseCase() } returns flowOf(Resource.Loading())
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            // Should not emit anything else
            expectNoEvents()
        }
    }
    
    @Test
    fun `loadPortfolio method exists and can be called`() = runTest {
        // Given
        val portfolioUiModel = PortfolioUiModel(
            holdings = listOf(
                HoldingUiModel("AAPL", "NET QTY: ", "100", "LTP: ", "₹ 150.00", "P&L: ", "₹ 1,000.00", true)
            ),
            summary = null
        )
        
        every { getPortfolioUseCase() } returns flowOf(Resource.Success(portfolioUiModel))
        
        // Create viewModel
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When - manually call loadPortfolio (should not throw exception)
        viewModel.loadPortfolio()
        
        // Then - Verify method executed without error
        verify(atLeast = 1) { getPortfolioUseCase() }
    }
    
    @Test
    fun `loadPortfolio processes all emissions from use case`() = runTest {
        // Given
        val portfolioUiModel = PortfolioUiModel(
            holdings = listOf(
                HoldingUiModel("AAPL", "NET QTY: ", "100", "LTP: ", "₹ 150.00", "P&L: ", "₹ 1,000.00", true)
            ),
            summary = null
        )
        
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Success(portfolioUiModel)
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is PortfolioUiState.Success)
            assertEquals(portfolioUiModel, (successState as PortfolioUiState.Success).portfolio)
        }
    }
    
    @Test
    fun `loadPortfolio handles error after success correctly`() = runTest {
        // Given
        val portfolioUiModel = PortfolioUiModel(
            holdings = listOf(
                HoldingUiModel("AAPL", "NET QTY: ", "100", "LTP: ", "₹ 150.00", "P&L: ", "₹ 1,000.00", true)
            ),
            summary = null
        )
        val errorMessage = "Connection lost"
        
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Success(portfolioUiModel),
            Resource.Error(errorMessage)
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is PortfolioUiState.Success)
            assertEquals(portfolioUiModel, (successState as PortfolioUiState.Success).portfolio)
            
            val errorState = awaitItem()
            assertTrue(errorState is PortfolioUiState.Error)
            assertEquals(errorMessage, (errorState as PortfolioUiState.Error).message)
        }
    }
    
    @Test
    fun `uiState is exposed as StateFlow`() {
        // When
        val stateFlow = viewModel.uiState
        
        // Then
        assertTrue("uiState should be a StateFlow", stateFlow is kotlinx.coroutines.flow.StateFlow)
    }
    
    @Test
    fun `loadPortfolio handles null error message correctly`() = runTest {
        // Given
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Error("") // Empty string instead of null
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is PortfolioUiState.Error)
            assertEquals("", (errorState as PortfolioUiState.Error).message)
        }
    }
    
    @Test
    fun `viewModel properly handles empty portfolio data`() = runTest {
        // Given
        val emptyPortfolioUiModel = PortfolioUiModel(
            holdings = null,
            summary = null
        )
        
        every { getPortfolioUseCase() } returns flowOf(
            Resource.Loading(),
            Resource.Success(emptyPortfolioUiModel)
        )
        
        // Create new viewModel to trigger init
        viewModel = PortfolioViewModel(getPortfolioUseCase)
        
        // When & Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PortfolioUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is PortfolioUiState.Success)
            val portfolio = (successState as PortfolioUiState.Success).portfolio
            assertEquals(emptyPortfolioUiModel, portfolio)
            assertEquals(null, portfolio.holdings)
            assertEquals(null, portfolio.summary)
        }
    }
}
