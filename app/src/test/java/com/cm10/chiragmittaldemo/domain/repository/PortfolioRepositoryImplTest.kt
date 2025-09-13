package com.cm10.chiragmittaldemo.domain.repository

import app.cash.turbine.test
import com.cm10.chiragmittaldemo.core.util.Resource
import com.cm10.chiragmittaldemo.data.mapper.HoldingMapper
import com.cm10.chiragmittaldemo.data.remote.api.PortfolioApiService
import com.cm10.chiragmittaldemo.data.remote.dto.HoldingDto
import com.cm10.chiragmittaldemo.data.remote.dto.PortfolioDataDto
import com.cm10.chiragmittaldemo.data.remote.dto.PortfolioResponseDto
import com.cm10.chiragmittaldemo.domain.model.Holding
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PortfolioRepositoryImplTest {
    
    private lateinit var repository: PortfolioRepositoryImpl
    private lateinit var apiService: PortfolioApiService
    private lateinit var holdingMapper: HoldingMapper
    
    @Before
    fun setUp() {
        apiService = mockk()
        holdingMapper = mockk()
        repository = PortfolioRepositoryImpl(apiService, holdingMapper)
    }
    
    @Test
    fun `getHoldings returns loading then success when api call succeeds`() = runTest {
        // Given
        val holdingDto1 = HoldingDto("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingDto2 = HoldingDto("GOOGL", 50, 2500.0, 2400.0, 2450.0)
        val holdingDtos = listOf(holdingDto1, holdingDto2)
        
        val holding1 = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holding2 = Holding("GOOGL", 50, 2500.0, 2400.0, 2450.0)
        val holdings = listOf(holding1, holding2)
        
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } returns holdings
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(holdings, (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(holdingDtos) }
    }
    
    @Test
    fun `getHoldings returns loading then success with empty list when mapper returns null`() = runTest {
        // Given
        val holdingDtos = listOf(HoldingDto("AAPL", 100, 150.0, 140.0, 145.0))
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } returns null // Mapper returns null
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(emptyList<Holding>(), (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(holdingDtos) }
    }
    
    @Test
    fun `getHoldings returns loading then success with empty list when userHolding is null`() = runTest {
        // Given
        val portfolioDataDto = PortfolioDataDto(null) // null userHolding
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(null) } returns null
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(emptyList<Holding>(), (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(null) }
    }
    
    @Test
    fun `getHoldings returns loading then success with empty list when data is null`() = runTest {
        // Given
        val portfolioResponseDto = PortfolioResponseDto(null) // null data
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(null) } returns null
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(emptyList<Holding>(), (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(null) }
    }
    
    @Test
    fun `getHoldings returns loading then success with empty list when response body is null`() = runTest {
        // Given
        val response = Response.success<PortfolioResponseDto>(null) // null body
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(null) } returns null
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(emptyList<Holding>(), (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(null) }
    }
    
    @Test
    fun `getHoldings returns loading then error when api call fails with error response`() = runTest {
        // Given
        val errorMessage = "Not Found"
        val response = Response.error<PortfolioResponseDto>(404, errorMessage.toResponseBody())
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val errorResource = awaitItem()
            assertTrue(errorResource is Resource.Error)
            assertTrue((errorResource as Resource.Error).message.contains("Failed to fetch holdings"))
            
            awaitComplete()
        }
    }
    
    @Test
    fun `getHoldings returns loading then error when api call throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Network timeout")
        coEvery { apiService.getPortfolioHoldings() } throws exception
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val errorResource = awaitItem()
            assertTrue(errorResource is Resource.Error)
            assertEquals("Network timeout", (errorResource as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `getHoldings returns loading then error when api call throws exception with null message`() = runTest {
        // Given
        val exception = RuntimeException(null as String?) // null message
        coEvery { apiService.getPortfolioHoldings() } throws exception
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val errorResource = awaitItem()
            assertTrue(errorResource is Resource.Error)
            assertEquals("Something went wrong", (errorResource as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `getHoldings returns loading then error when mapper throws exception`() = runTest {
        // Given
        val holdingDtos = listOf(HoldingDto("AAPL", 100, 150.0, 140.0, 145.0))
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } throws RuntimeException("Mapping error")
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val errorResource = awaitItem()
            assertTrue(errorResource is Resource.Error)
            assertEquals("Mapping error", (errorResource as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `refreshHoldings delegates to getHoldings`() = runTest {
        // Given
        val holdingDto = HoldingDto("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingDtos = listOf(holdingDto)
        val holding = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holdings = listOf(holding)
        
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } returns holdings
        
        // When & Then
        repository.refreshHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(holdings, (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        // Verify the same API service and mapper are called as in getHoldings
        verify { holdingMapper.mapToDomainList(holdingDtos) }
    }
    
    @Test
    fun `refreshHoldings returns error when api call fails`() = runTest {
        // Given
        val exception = RuntimeException("Connection failed")
        coEvery { apiService.getPortfolioHoldings() } throws exception
        
        // When & Then
        repository.refreshHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val errorResource = awaitItem()
            assertTrue(errorResource is Resource.Error)
            assertEquals("Connection failed", (errorResource as Resource.Error).message)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `getHoldings handles complex nested data structure correctly`() = runTest {
        // Given
        val holdingDto1 = HoldingDto("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingDto2 = HoldingDto("GOOGL", null, 2500.0, null, 2450.0) // Partial data
        val holdingDto3 = HoldingDto(null, 75, null, 280.0, null) // Different partial data
        val holdingDtos = listOf(holdingDto1, holdingDto2, holdingDto3)
        
        val holding1 = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holding2 = Holding("GOOGL", null, 2500.0, null, 2450.0)
        val holdings = listOf(holding1, holding2) // Third holding filtered out by mapper
        
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } returns holdings
        
        // When & Then
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(holdings, (successResource as Resource.Success).data)
            assertEquals(2, (successResource as Resource.Success).data.size)
            
            awaitComplete()
        }
        
        verify { holdingMapper.mapToDomainList(holdingDtos) }
    }
    
    @Test
    fun `getHoldings can be called multiple times independently`() = runTest {
        // Given
        val holdingDto = HoldingDto("AAPL", 100, 150.0, 140.0, 145.0)
        val holdingDtos = listOf(holdingDto)
        val holding = Holding("AAPL", 100, 150.0, 140.0, 145.0)
        val holdings = listOf(holding)
        
        val portfolioDataDto = PortfolioDataDto(holdingDtos)
        val portfolioResponseDto = PortfolioResponseDto(portfolioDataDto)
        val response = Response.success(portfolioResponseDto)
        
        coEvery { apiService.getPortfolioHoldings() } returns response
        every { holdingMapper.mapToDomainList(holdingDtos) } returns holdings
        
        // When & Then - First call
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(holdings, (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        // When & Then - Second call
        repository.getHoldings().test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Resource.Loading)
            
            val successResource = awaitItem()
            assertTrue(successResource is Resource.Success)
            assertEquals(holdings, (successResource as Resource.Success).data)
            
            awaitComplete()
        }
        
        // Verify API service was called twice
        io.mockk.coVerify(exactly = 2) { apiService.getPortfolioHoldings() }
        verify(exactly = 2) { holdingMapper.mapToDomainList(holdingDtos) }
    }
}
