package com.cm10.chiragmittaldemo.presentation.model

data class HoldingUiModel(
    val symbol: String?,
    val netQuantityLabel: String?,        // "NET QTY: "
    val netQuantityValue: String?,        // "3"
    val ltpLabel: String?,                // "LTP: "
    val ltpValue: String?,                // "₹ 119.10"
    val pnlLabel: String?,                // "P&L: "
    val pnlValue: String?,                // "₹ 57.30"
    val isPnlPositive: Boolean?           // true/false/null for unknown state
)
