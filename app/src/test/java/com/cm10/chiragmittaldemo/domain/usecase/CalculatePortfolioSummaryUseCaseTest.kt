package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.Holding
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculatePortfolioSummaryUseCaseTest {
    
    private lateinit var useCase: CalculatePortfolioSummaryUseCaseInterface
    
    @Before
    fun setUp() {
        useCase = CalculatePortfolioSummaryUseCase()
    }
    
    @Test
    fun `calculate portfolio summary with mixed profits and losses`() {
        // Given
        val holdingSummaries = listOf(
            HoldingSummary(
                holding = Holding("MAHABANK", 990, 38.05, 35.0, 40.0),
                currentValue = 37669.50,
                totalInvestment = 34650.0,
                totalPnl = 3019.50,
                totalPnlPercentage = 8.71,
                todaysPnl = 1930.50,
                todaysPnlPercentage = 5.12
            ),
            HoldingSummary(
                holding = Holding("ICICI", 100, 118.25, 110.0, 105.0),
                currentValue = 11825.0,
                totalInvestment = 11000.0,
                totalPnl = 825.0,
                totalPnlPercentage = 7.50,
                todaysPnl = -1325.0,
                todaysPnlPercentage = -11.20
            ),
            HoldingSummary(
                holding = Holding("SBI", 150, 550.05, 501.0, 590.0),
                currentValue = 82507.50,
                totalInvestment = 75150.0,
                totalPnl = 7357.50,
                totalPnlPercentage = 9.79,
                todaysPnl = 5992.50,
                todaysPnlPercentage = 7.26
            )
        )
        
        // When
        val result = useCase(holdingSummaries)
        
        // Then
        assertNotNull(result)
        assertEquals(132002.0, result!!.currentValue!!, 0.01)
        assertEquals(120800.0, result.totalInvestment!!, 0.01)
        assertEquals(11202.0, result.totalPnl!!, 0.01)
        assertEquals(9.27, result.totalPnlPercentage!!, 0.01)
        assertEquals(6598.0, result.todaysPnl!!, 0.01)
        assertEquals(5.00, result.todaysPnlPercentage!!, 0.01)
    }
    
    @Test
    fun `calculate portfolio summary with all losses`() {
        // Given
        val holdingSummaries = listOf(
            HoldingSummary(
                holding = Holding("STOCK1", 100, 50.0, 60.0, 45.0),
                currentValue = 5000.0,
                totalInvestment = 6000.0,
                totalPnl = -1000.0,
                totalPnlPercentage = -16.67,
                todaysPnl = -500.0,
                todaysPnlPercentage = -10.0
            ),
            HoldingSummary(
                holding = Holding("STOCK2", 200, 25.0, 30.0, 24.0),
                currentValue = 5000.0,
                totalInvestment = 6000.0,
                totalPnl = -1000.0,
                totalPnlPercentage = -16.67,
                todaysPnl = -200.0,
                todaysPnlPercentage = -4.0
            )
        )
        
        // When
        val result = useCase(holdingSummaries)
        
        // Then
        assertNotNull(result)
        assertEquals(10000.0, result!!.currentValue!!, 0.01)
        assertEquals(12000.0, result.totalInvestment!!, 0.01)
        assertEquals(-2000.0, result.totalPnl!!, 0.01)
        assertEquals(-16.67, result.totalPnlPercentage!!, 0.01)
        assertEquals(-700.0, result.todaysPnl!!, 0.01)
        assertEquals(-7.0, result.todaysPnlPercentage!!, 0.01)
    }
    
    @Test
    fun `calculate portfolio summary with empty holdings list`() {
        // Given
        val holdingSummaries = emptyList<HoldingSummary>()
        
        // When
        val result = useCase(holdingSummaries)
        
        // Then
        assertNull(result) // Should return null for empty list
    }
    
    @Test
    fun `calculate portfolio summary with null holdings list`() {
        // When
        val result = useCase(null)
        
        // Then
        assertNull(result) // Should return null for null input
    }
    
    @Test
    fun `calculate portfolio summary with zero investment`() {
        // Given
        val holdingSummaries = listOf(
            HoldingSummary(
                holding = Holding("FREE_STOCK", 100, 50.0, 0.0, 55.0),
                currentValue = 5000.0,
                totalInvestment = 0.0,
                totalPnl = 5000.0,
                totalPnlPercentage = null, // null for division by zero
                todaysPnl = 500.0,
                todaysPnlPercentage = 10.0
            )
        )
        
        // When
        val result = useCase(holdingSummaries)
        
        // Then
        assertNotNull(result)
        assertEquals(5000.0, result!!.currentValue!!, 0.01)
        assertEquals(0.0, result.totalInvestment!!, 0.01)
        assertEquals(5000.0, result.totalPnl!!, 0.01)
        assertNull(result.totalPnlPercentage) // Should be null for division by zero
        assertEquals(500.0, result.todaysPnl!!, 0.01)
        assertEquals(10.0, result.todaysPnlPercentage!!, 0.01)
    }
    
    @Test
    fun `calculate portfolio summary with partial null values`() {
        // Given
        val holdingSummaries = listOf(
            HoldingSummary(
                holding = Holding("STOCK1", 100, 50.0, 60.0, 45.0),
                currentValue = 5000.0,
                totalInvestment = 6000.0,
                totalPnl = -1000.0,
                totalPnlPercentage = -16.67,
                todaysPnl = null, // null value
                todaysPnlPercentage = null // null value
            ),
            HoldingSummary(
                holding = Holding("STOCK2", 200, 25.0, 30.0, 24.0),
                currentValue = null, // null value
                totalInvestment = null, // null value
                totalPnl = null, // null value
                totalPnlPercentage = null, // null value
                todaysPnl = -200.0,
                todaysPnlPercentage = -4.0
            )
        )
        
        // When
        val result = useCase(holdingSummaries)
        
        // Then
        assertNotNull(result)
        assertEquals(5000.0, result!!.currentValue!!, 0.01) // Only valid values summed
        assertEquals(6000.0, result.totalInvestment!!, 0.01) // Only valid values summed
        assertEquals(-1000.0, result.totalPnl!!, 0.01)
        assertEquals(-16.67, result.totalPnlPercentage!!, 0.01)
        assertEquals(-200.0, result.todaysPnl!!, 0.01) // Only valid values summed
        assertNotNull(result.todaysPnlPercentage) // Should calculate based on available data
    }
}
