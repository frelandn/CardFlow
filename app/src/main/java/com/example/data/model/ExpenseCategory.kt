package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpenseCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryRegistry {
    val Categories = listOf(
        ExpenseCategory("Dining", Icons.Default.Restaurant, Color(0xFFFB923C)),
        ExpenseCategory("Groceries", Icons.Default.ShoppingCart, Color(0xFF34D399)),
        ExpenseCategory("Shopping", Icons.Default.ShoppingBag, Color(0xFFF472B6)),
        ExpenseCategory("Travel", Icons.Default.Flight, Color(0xFF38BDF8)),
        ExpenseCategory("Utilities", Icons.Default.LocalGasStation, Color(0xFFFBBF24)),
        ExpenseCategory("Entertainment", Icons.Default.Movie, Color(0xFFA78BFA)),
        ExpenseCategory("Subscriptions", Icons.Default.Subscriptions, Color(0xFF818CF8)),
        ExpenseCategory("Health", Icons.Default.MedicalServices, Color(0xFFF87171)),
        ExpenseCategory("Bill Payment", Icons.Default.Payments, Color(0xFF10B981)),
        ExpenseCategory("Other", Icons.Default.Receipt, Color(0xFF94A3B8))
    )

    fun getCategory(name: String): ExpenseCategory {
        return Categories.find { it.name.equals(name, ignoreCase = true) }
            ?: ExpenseCategory(name, Icons.Default.Receipt, Color(0xFF94A3B8))
    }
}
