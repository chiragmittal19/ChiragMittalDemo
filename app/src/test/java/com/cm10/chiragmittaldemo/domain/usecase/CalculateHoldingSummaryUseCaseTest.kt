package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.Holding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculateHoldingSummaryUseCaseTest {
    
    private lateinit var useCase: CalculateHoldingSummaryUseCaseInterface
    
    @Before
    fun setUp() {
        useCase = CalculateHoldingSummaryUseCase()
    }
    
    @Test
    fun `calculate holding summary with profit`() {
        // Given
        val holding = Holding(
            symbol = "MAHABANK",
            quantity = 990,
            ltp = 38.05,
            avgPrice = 35.0,
            close = 40.0
        )
        
        // When
        val result = useCase(holding)
        
        // Then
        assertNotNull(result)
        assertEquals(37669.50, result!!.currentValue!!, 0.01)
        assertEquals(34650.0, result.totalInvestment!!, 0.01)
        assertEquals(3019.50, result.totalPnl!!, 0.01)
        assertEquals(8.71, result.totalPnlPercentage!!, 0.01)
        assertEquals(1930.50, result.todaysPnl!!, 0.01) // (40 - 38.05) * 990
        assertEquals(5.12, result.todaysPnlPercentage!!, 0.01) // ((40 - 38.05) / 38.05) * 100
    }
    
    @Test
    fun `calculate holding summary with loss`() {
        // Given
        val holding = Holding(
            symbol = "HDFC",
            quantity = 75,
            ltp = 1800.25,
            avgPrice = 1750.0,
            close = 1700.0
        )
        
        // When
        val result = useCase(holding)
        
        // Then
        assertNotNull(result)
        assertEquals(135018.75, result!!.currentValue!!, 0.01)
        assertEquals(131250.0, result.totalInvestment!!, 0.01)
        assertEquals(3768.75, result.totalPnl!!, 0.01)
        assertEquals(2.87, result.totalPnlPercentage!!, 0.01)
        assertEquals(-7518.75, result.todaysPnl!!, 0.01) // (1700 - 1800.25) * 75
        assertEquals(-5.57, result.todaysPnlPercentage!!, 0.01) // ((1700 - 1800.25) / 1800.25) * 100
    }
    
    @Test
    fun `calculate holding summary with zero investment`() {
        // Given
        val holding = Holding(
            symbol = "TEST",
            quantity = 100,
            ltp = 50.0,
            avgPrice = 0.0,
            close = 55.0
        )
        
        // When
        val result = useCase(holding)
        
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
    fun `calculate holding summary with zero ltp`() {
        // Given
        val holding = Holding(
            symbol = "TEST",
            quantity = 100,
            ltp = 0.0,
            avgPrice = 50.0,
            close = 55.0
        )
        
        // When
        val result = useCase(holding)
        
        // Then
        assertNotNull(result)
        assertEquals(0.0, result!!.currentValue!!, 0.01)
        assertEquals(5000.0, result.totalInvestment!!, 0.01)
        assertEquals(-5000.0, result.totalPnl!!, 0.01)
        assertEquals(-100.0, result.totalPnlPercentage!!, 0.01)
        assertEquals(5500.0, result.todaysPnl!!, 0.01)
        assertNull(result.todaysPnlPercentage) // Should be null for division by zero
    }
    
    @Test
    fun `calculate holding summary with null holding returns null`() {
        // When
        val result = useCase(null)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `calculate holding summary with null fields returns partial results`() {
        // Given
        val holding = Holding(
            symbol = "TEST",
            quantity = null, // null quantity
            ltp = 50.0,
            avgPrice = 40.0,
            close = 55.0
        )
        
        // When
        val result = useCase(holding)
        
        // Then
        assertNotNull(result)
        assertNull(result!!.currentValue) // Should be null due to null quantity
        assertNull(result.totalInvestment) // Should be null due to null quantity
        assertNull(result.totalPnl) // Should be null due to null currentValue/totalInvestment
        assertNull(result.totalPnlPercentage) // Should be null due to null totalPnl
        assertNull(result.todaysPnl) // Should be null due to null quantity
        assertNotNull(result.todaysPnlPercentage) // Should NOT be null as it only depends on ltp and close
        assertEquals(10.0, result.todaysPnlPercentage!!, 0.01) // ((55 - 50) / 50) * 100
    }
}
