package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CreditCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"]), Index(value = ["dateMillis"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long,
    val merchant: String,
    val amount: Double,
    val category: String, // Dining, Groceries, Shopping, Travel, etc.
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isPayment: Boolean = false // True = credit card bill payment / statement payoff
)
