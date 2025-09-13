package com.cm10.chiragmittaldemo.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cm10.chiragmittaldemo.presentation.model.PortfolioSummaryUiModel
import com.cm10.chiragmittaldemo.ui.theme.LossColor
import com.cm10.chiragmittaldemo.ui.theme.ProfitColor

@Composable
fun PortfolioSummaryCard(
    portfolioSummaryUiModel: PortfolioSummaryUiModel?,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Only render the card if we have at least the main P&L data
    if (portfolioSummaryUiModel?.profitLossLabel != null && 
        portfolioSummaryUiModel.profitLossValue != null &&
        portfolioSummaryUiModel.isProfitLossPositive != null) {
        
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp
                ))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
        ) {
            // Expanded content with upward animation - appears ABOVE the main P&L row
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                ) + fadeIn(),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    var hasAnyExpandedContent = false
                    
                    // Only show current value if available
                    if (portfolioSummaryUiModel.currentValueLabel != null && 
                        portfolioSummaryUiModel.currentValueValue != null) {
                        PortfolioDetailRow(
                            label = portfolioSummaryUiModel.currentValueLabel,
                            value = portfolioSummaryUiModel.currentValueValue
                        )
                        hasAnyExpandedContent = true
                    }

                    // Only show total investment if available
                    if (portfolioSummaryUiModel.totalInvestmentLabel != null && 
                        portfolioSummaryUiModel.totalInvestmentValue != null) {
                        if (hasAnyExpandedContent) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        PortfolioDetailRow(
                            label = portfolioSummaryUiModel.totalInvestmentLabel,
                            value = portfolioSummaryUiModel.totalInvestmentValue
                        )
                        hasAnyExpandedContent = true
                    }

                    // Only show today's P&L if available
                    if (portfolioSummaryUiModel.todaysPnlLabel != null && 
                        portfolioSummaryUiModel.todaysPnlValue != null &&
                        portfolioSummaryUiModel.isTodaysPnlPositive != null) {
                        if (hasAnyExpandedContent) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        PortfolioDetailRow(
                            label = portfolioSummaryUiModel.todaysPnlLabel,
                            value = portfolioSummaryUiModel.todaysPnlValue,
                            valueColor = if (portfolioSummaryUiModel.isTodaysPnlPositive) ProfitColor else LossColor
                        )
                        hasAnyExpandedContent = true
                    }

                    if (hasAnyExpandedContent) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                    }
                }
            }

            // Main P&L row - now at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = portfolioSummaryUiModel.profitLossLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = portfolioSummaryUiModel.profitLossValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (portfolioSummaryUiModel.isProfitLossPositive) ProfitColor else LossColor
                )

                // Only show percentage if available
                portfolioSummaryUiModel.profitLossPercentage?.let { percentage ->
                    Text(
                        text = percentage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (portfolioSummaryUiModel.isProfitLossPositive) ProfitColor else LossColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}
