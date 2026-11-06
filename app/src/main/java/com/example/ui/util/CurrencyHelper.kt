package com.example.ui.util

import java.util.Locale

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val name: String
)

object CurrencyHelper {
    val supportedCurrencies = listOf(
        CurrencyOption("PHP", "₱", "Philippine Peso"),
        CurrencyOption("USD", "$", "US Dollar"),
        CurrencyOption("EUR", "€", "Euro"),
        CurrencyOption("GBP", "£", "British Pound"),
        CurrencyOption("JPY", "¥", "Japanese Yen"),
        CurrencyOption("SGD", "S$", "Singapore Dollar")
    )

    fun format(amount: Double, symbol: String = "₱", decimals: Boolean = true): String {
        return if (decimals) {
            "$symbol${String.format(Locale.US, "%,.2f", amount)}"
        } else {
            "$symbol${String.format(Locale.US, "%,.0f", amount)}"
        }
    }
}
