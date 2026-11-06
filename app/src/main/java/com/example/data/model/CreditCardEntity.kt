package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_cards")
data class CreditCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardName: String,
    val bankOrNetwork: String = "Visa", // Visa, Mastercard, Amex, Discover, Capital One, Chase, etc.
    val lastFourDigits: String,
    val creditLimit: Double,
    val statementClosingDay: Int, // e.g. 15th of month
    val paymentDueDay: Int, // e.g. 8th of month
    val colorPresetIndex: Int = 0,
    val cardholderName: String = "CARDHOLDER"
)
