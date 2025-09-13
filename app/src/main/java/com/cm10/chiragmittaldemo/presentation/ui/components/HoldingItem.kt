package com.cm10.chiragmittaldemo.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cm10.chiragmittaldemo.presentation.model.HoldingUiModel
import com.cm10.chiragmittaldemo.ui.theme.LossColor
import com.cm10.chiragmittaldemo.ui.theme.ProfitColor

@Composable
fun HoldingItem(
    holdingUiModel: HoldingUiModel?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side - Symbol and Quantity
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Only show symbol if available
            holdingUiModel?.symbol?.let { symbol ->
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Only show quantity row if both label and value are available
            if (holdingUiModel?.netQuantityLabel != null && holdingUiModel.netQuantityValue != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = holdingUiModel.netQuantityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = holdingUiModel.netQuantityValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }

        // Right side - Prices and P&L
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Only show LTP row if both label and value are available
            if (holdingUiModel?.ltpLabel != null && holdingUiModel.ltpValue != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = holdingUiModel.ltpLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = holdingUiModel.ltpValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }

            // Only show P&L row if all required data is available
            if (holdingUiModel?.pnlLabel != null && 
                holdingUiModel.pnlValue != null && 
                holdingUiModel.isPnlPositive != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = holdingUiModel.pnlLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = holdingUiModel.pnlValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (holdingUiModel.isPnlPositive) ProfitColor else LossColor
                    )
                }
            }
        }
    }
}
