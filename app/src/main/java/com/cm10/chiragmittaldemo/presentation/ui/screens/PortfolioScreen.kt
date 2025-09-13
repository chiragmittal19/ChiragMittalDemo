package com.cm10.chiragmittaldemo.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cm10.chiragmittaldemo.presentation.model.HoldingUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioSummaryUiModel
import com.cm10.chiragmittaldemo.presentation.model.PortfolioUiModel
import com.cm10.chiragmittaldemo.presentation.state.PortfolioUiState
import com.cm10.chiragmittaldemo.presentation.ui.components.HoldingItem
import com.cm10.chiragmittaldemo.presentation.ui.components.PortfolioHeader
import com.cm10.chiragmittaldemo.presentation.ui.components.PortfolioSummaryCard
import com.cm10.chiragmittaldemo.ui.theme.ChiragMittalDemoTheme

@Composable
fun PortfolioScreen(
    uiState: PortfolioUiState,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        PortfolioHeader()

        Box(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
        ) {
            when (uiState) {
                is PortfolioUiState.Loading -> {
                    PortfolioLoadingView(modifier = Modifier.align(Alignment.Center))
                }

                is PortfolioUiState.Error -> {
                    PortfolioErrorView(
                        error = uiState.message,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is PortfolioUiState.Success -> {
                    PortfolioContentView(
                        portfolio = uiState.portfolio,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioLoadingView(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun PortfolioErrorView(
    error: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetryClick) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun PortfolioContentView(
    portfolio: PortfolioUiModel?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
        ) {
            val holdings = portfolio?.holdings ?: emptyList()
            itemsIndexed(holdings) { index, holdingUiModel ->
                HoldingItem(
                    holdingUiModel = holdingUiModel,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < holdings.lastIndex) {
                    Spacer(Modifier.height(1.dp))
                }
            }
        }

        // Only show summary card if it exists
        portfolio?.summary?.let { summary ->
            PortfolioSummaryCard(
                portfolioSummaryUiModel = summary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    ChiragMittalDemoTheme {
        val sampleUiState = PortfolioUiState.Success(createSamplePortfolioUiData())

        PortfolioScreen(
            uiState = sampleUiState,
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenLoadingPreview() {
    ChiragMittalDemoTheme {
        val loadingUiState = PortfolioUiState.Loading

        PortfolioScreen(
            uiState = loadingUiState,
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenErrorPreview() {
    ChiragMittalDemoTheme {
        val errorUiState = PortfolioUiState.Error("Network error occurred. Please check your internet connection.")

        PortfolioScreen(
            uiState = errorUiState,
            onRetryClick = {}
        )
    }
}

private fun createSamplePortfolioUiData(): PortfolioUiModel {
    val holdings = listOf(
        HoldingUiModel(
            symbol = "ASHOKLEY",
            netQuantityLabel = "NET QTY: ",
            netQuantityValue = "3",
            ltpLabel = "LTP: ",
            ltpValue = "₹ 119.10",
            pnlLabel = "P&L: ",
            pnlValue = "₹ 57.30",
            isPnlPositive = true
        ),
        HoldingUiModel(
            symbol = "HDFC",
            netQuantityLabel = "NET QTY: ",
            netQuantityValue = "7",
            ltpLabel = "LTP: ",
            ltpValue = "₹ 2,497.20",
            pnlLabel = "P&L: ",
            pnlValue = "₹ -719.60",
            isPnlPositive = false
        ),
        HoldingUiModel(
            symbol = "ICICIBANK",
            netQuantityLabel = "NET QTY: ",
            netQuantityValue = "1",
            ltpLabel = "LTP: ",
            ltpValue = "₹ 624.70",
            pnlLabel = "P&L: ",
            pnlValue = "₹ 24.70",
            isPnlPositive = true
        ),
        HoldingUiModel(
            symbol = "IDEA",
            netQuantityLabel = "NET QTY: ",
            netQuantityValue = "3",
            ltpLabel = "LTP: ",
            ltpValue = "₹ 9.95",
            pnlLabel = "P&L: ",
            pnlValue = "₹ -6.15",
            isPnlPositive = false
        ),
        HoldingUiModel(
            symbol = "RELIANCE",
            netQuantityLabel = "NET QTY: ",
            netQuantityValue = "50",
            ltpLabel = "LTP: ",
            ltpValue = "₹ 2,500.00",
            pnlLabel = "P&L: ",
            pnlValue = "₹ 2,500.00",
            isPnlPositive = true
        )
    )

    val portfolioSummary = PortfolioSummaryUiModel(
        profitLossLabel = "Profit & Loss*",
        profitLossValue = "₹ 1,856.25",
        profitLossPercentage = " (1.31%)",
        isProfitLossPositive = true,
        currentValueLabel = "Current value*",
        currentValueValue = "₹ 143,492.25",
        totalInvestmentLabel = "Total investment*",
        totalInvestmentValue = "₹ 141,636.00",
        todaysPnlLabel = "Today's Profit & Loss*",
        todaysPnlValue = "₹ 4,327.75",
        isTodaysPnlPositive = true
    )

    return PortfolioUiModel(
        holdings = holdings,
        summary = portfolioSummary
    )
}
