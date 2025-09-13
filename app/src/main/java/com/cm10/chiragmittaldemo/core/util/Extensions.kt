package com.cm10.chiragmittaldemo.core.util

import java.text.DecimalFormat

fun Double.formatCurrency(currencySymbol: String = "₹"): String? {
    return try {
        val formatter = DecimalFormat("#,##0.00")
        "$currencySymbol ${formatter.format(this)}"
    } catch (e: Exception) {
        null
    }
}

fun Double.formatPercentage(): String? {
    return try {
        val formatter = DecimalFormat("#0.00")
        formatter.format(this)
    } catch (e: Exception) {
        null
    }
}
