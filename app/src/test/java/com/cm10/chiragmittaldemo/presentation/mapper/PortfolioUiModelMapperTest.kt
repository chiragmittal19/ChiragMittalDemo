package com.cm10.chiragmittaldemo.presentation.mapper

import com.cm10.chiragmittaldemo.domain.model.Holding
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import com.cm10.chiragmittaldemo.domain.model.Portfolio
import com.cm10.chiragmittaldemo.domain.model.PortfolioSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PortfolioUiModelMapperTest {
    
    private lateinit var mapper: PortfolioUiModelMapper
    
    @Before
    fun setUp() {
        mapper = PortfolioUiModelMapper()
    }
    
    @Test
    fun `mapToUiModel with null portfolio returns null`() {
        // When
        val result = mapper.mapToUiModel(null)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `mapToUiModel with complete portfolio returns ui model`() {
        // Given
        val holding = Holding(
            symbol = "AAPL",
            quantity = 100,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 145.0
        )
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
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        assertNotNull(result!!.holdings)
        assertEquals(1, result.holdings!!.size)
        
        val holdingUi = result.holdings!![0]
        assertEquals("AAPL", holdingUi.symbol)
        assertEquals("NET QTY: ", holdingUi.netQuantityLabel)
        assertEquals("100", holdingUi.netQuantityValue)
        assertEquals("LTP: ", holdingUi.ltpLabel)
        assertEquals("₹ 150.00", holdingUi.ltpValue)
        assertEquals("P&L: ", holdingUi.pnlLabel)
        assertEquals("₹ 1,000.00", holdingUi.pnlValue)
        assertEquals(true, holdingUi.isPnlPositive)
        
        assertNotNull(result.summary)
        val summaryUi = result.summary!!
        assertEquals("Profit & Loss*", summaryUi.profitLossLabel)
        assertEquals("₹ 1,000.00", summaryUi.profitLossValue)
        assertEquals(" (7.14%)", summaryUi.profitLossPercentage)
        assertEquals(true, summaryUi.isProfitLossPositive)
        assertEquals("Current value*", summaryUi.currentValueLabel)
        assertEquals("₹ 15,000.00", summaryUi.currentValueValue)
        assertEquals("Total investment*", summaryUi.totalInvestmentLabel)
        assertEquals("₹ 14,000.00", summaryUi.totalInvestmentValue)
        assertEquals("Today's Profit & Loss*", summaryUi.todaysPnlLabel)
        assertEquals("₹ 500.00", summaryUi.todaysPnlValue)
        assertEquals(true, summaryUi.isTodaysPnlPositive)
    }
    
    @Test
    fun `mapToUiModel with negative pnl shows negative indicators`() {
        // Given
        val holding = Holding(
            symbol = "TSLA",
            quantity = 50,
            ltp = 200.0,
            avgPrice = 250.0,
            close = 220.0
        )
        val holdingSummary = HoldingSummary(
            holding = holding,
            currentValue = 10000.0,
            totalInvestment = 12500.0,
            totalPnl = -2500.0,
            totalPnlPercentage = -20.0,
            todaysPnl = -1000.0,
            todaysPnlPercentage = -9.09
        )
        val portfolioSummary = PortfolioSummary(
            currentValue = 10000.0,
            totalInvestment = 12500.0,
            totalPnl = -2500.0,
            totalPnlPercentage = -20.0,
            todaysPnl = -1000.0,
            todaysPnlPercentage = -9.09
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = portfolioSummary
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        
        val holdingUi = result!!.holdings!![0]
        assertEquals(false, holdingUi.isPnlPositive)
        assertEquals("₹ -2,500.00", holdingUi.pnlValue)
        
        val summaryUi = result.summary!!
        assertEquals(false, summaryUi.isProfitLossPositive)
        assertEquals(false, summaryUi.isTodaysPnlPositive)
        assertEquals("₹ -2,500.00", summaryUi.profitLossValue)
        assertEquals(" (-20.00%)", summaryUi.profitLossPercentage)
        assertEquals("₹ -1,000.00", summaryUi.todaysPnlValue)
    }
    
    @Test
    fun `mapToUiModel with zero pnl shows positive indicator`() {
        // Given
        val holding = Holding(
            symbol = "AMZN",
            quantity = 25,
            ltp = 100.0,
            avgPrice = 100.0,
            close = 100.0
        )
        val holdingSummary = HoldingSummary(
            holding = holding,
            currentValue = 2500.0,
            totalInvestment = 2500.0,
            totalPnl = 0.0,
            totalPnlPercentage = 0.0,
            todaysPnl = 0.0,
            todaysPnlPercentage = 0.0
        )
        val portfolioSummary = PortfolioSummary(
            currentValue = 2500.0,
            totalInvestment = 2500.0,
            totalPnl = 0.0,
            totalPnlPercentage = 0.0,
            todaysPnl = 0.0,
            todaysPnlPercentage = 0.0
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = portfolioSummary
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        
        val holdingUi = result!!.holdings!![0]
        assertEquals(true, holdingUi.isPnlPositive) // Zero is considered positive
        assertEquals("₹ 0.00", holdingUi.pnlValue)
        
        val summaryUi = result.summary!!
        assertEquals(true, summaryUi.isProfitLossPositive) // Zero is considered positive
        assertEquals(true, summaryUi.isTodaysPnlPositive) // Zero is considered positive
    }
    
    @Test
    fun `mapToUiModel with null holdings returns ui model with null holdings`() {
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
            holdings = null,
            summary = portfolioSummary
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        assertNull(result!!.holdings)
        assertNotNull(result.summary)
    }
    
    @Test
    fun `mapToUiModel with null summary returns ui model with null summary`() {
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
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = null
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        assertNotNull(result!!.holdings)
        assertNull(result.summary)
    }
    
    @Test
    fun `mapHoldingToUiModel with null holding fields shows null labels and values`() {
        // Given
        val holding = Holding(
            symbol = "TEST",
            quantity = null, // null quantity
            ltp = null,      // null ltp
            avgPrice = 100.0,
            close = 105.0
        )
        val holdingSummary = HoldingSummary(
            holding = holding,
            currentValue = null, // null because quantity is null
            totalInvestment = null, // null because quantity is null
            totalPnl = null,     // null because currentValue is null
            totalPnlPercentage = 5.0,
            todaysPnl = null,    // null because quantity is null
            todaysPnlPercentage = 4.76
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary),
            summary = null
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        val holdingUi = result!!.holdings!![0]
        
        assertEquals("TEST", holdingUi.symbol)
        assertNull(holdingUi.netQuantityLabel) // null because quantity is null
        assertNull(holdingUi.netQuantityValue)
        assertNull(holdingUi.ltpLabel)         // null because ltp is null
        assertNull(holdingUi.ltpValue)
        assertNull(holdingUi.pnlLabel)         // null because totalPnl is null
        assertNull(holdingUi.pnlValue)
        assertNull(holdingUi.isPnlPositive)    // null because totalPnl is null
    }
    
    @Test
    fun `mapSummaryToUiModel with null fields shows null labels and values`() {
        // Given
        val portfolioSummary = PortfolioSummary(
            currentValue = null,
            totalInvestment = null,
            totalPnl = null,
            totalPnlPercentage = null,
            todaysPnl = null,
            todaysPnlPercentage = null
        )
        val portfolio = Portfolio(
            holdings = emptyList(),
            summary = portfolioSummary
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        val summaryUi = result!!.summary!!
        
        assertNull(summaryUi.profitLossLabel)
        assertNull(summaryUi.profitLossValue)
        assertNull(summaryUi.profitLossPercentage)
        assertNull(summaryUi.isProfitLossPositive)
        
        assertNull(summaryUi.currentValueLabel)
        assertNull(summaryUi.currentValueValue)
        
        assertNull(summaryUi.totalInvestmentLabel)
        assertNull(summaryUi.totalInvestmentValue)
        
        assertNull(summaryUi.todaysPnlLabel)
        assertNull(summaryUi.todaysPnlValue)
        assertNull(summaryUi.isTodaysPnlPositive)
    }
    
    @Test
    fun `mapToUiModel with multiple holdings maps all correctly`() {
        // Given
        val holding1 = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holding2 = Holding("GOOGL", 50, 2500.0, 2400.0, 2450.0)
        
        val holdingSummary1 = HoldingSummary(
            holding = holding1,
            currentValue = 15000.0,
            totalInvestment = 14000.0,
            totalPnl = 1000.0,
            totalPnlPercentage = 7.14,
            todaysPnl = 500.0,
            todaysPnlPercentage = 3.45
        )
        val holdingSummary2 = HoldingSummary(
            holding = holding2,
            currentValue = 125000.0,
            totalInvestment = 120000.0,
            totalPnl = 5000.0,
            totalPnlPercentage = 4.17,
            todaysPnl = -2500.0,
            todaysPnlPercentage = -2.04
        )
        
        val portfolioSummary = PortfolioSummary(
            currentValue = 140000.0,
            totalInvestment = 134000.0,
            totalPnl = 6000.0,
            totalPnlPercentage = 4.48,
            todaysPnl = -2000.0,
            todaysPnlPercentage = -1.45
        )
        val portfolio = Portfolio(
            holdings = listOf(holdingSummary1, holdingSummary2),
            summary = portfolioSummary
        )
        
        // When
        val result = mapper.mapToUiModel(portfolio)
        
        // Then
        assertNotNull(result)
        assertEquals(2, result!!.holdings!!.size)
        
        val holdingUi1 = result.holdings!![0]
        assertEquals("AAPL", holdingUi1.symbol)
        assertEquals(true, holdingUi1.isPnlPositive)
        
        val holdingUi2 = result.holdings!![1]
        assertEquals("GOOGL", holdingUi2.symbol)
        assertEquals(true, holdingUi2.isPnlPositive) // Overall PnL is positive despite negative today's PnL
        
        val summaryUi = result.summary!!
        assertEquals(true, summaryUi.isProfitLossPositive) // Overall positive
        assertEquals(false, summaryUi.isTodaysPnlPositive) // Today's is negative
    }
}
