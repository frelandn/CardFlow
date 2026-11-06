package com.example.data.repository

import com.example.data.db.CardDao
import com.example.data.db.TransactionDao
import com.example.data.model.CreditCardEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class CardFlowRepository(
    private val cardDao: CardDao,
    private val transactionDao: TransactionDao
) {
    val allCards: Flow<List<CreditCardEntity>> = cardDao.getAllCards()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsForCard(cardId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCard(cardId)
    }

    suspend fun insertCard(card: CreditCardEntity): Long = cardDao.insertCard(card)
    suspend fun updateCard(card: CreditCardEntity) = cardDao.updateCard(card)
    suspend fun deleteCard(card: CreditCardEntity) = cardDao.deleteCard(card)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun seedInitialDataIfNeeded() {
        if (cardDao.getCardCount() == 0) {
            val goldCard = CreditCardEntity(
                cardName = "Gold Travel Rewards",
                bankOrNetwork = "Mastercard",
                lastFourDigits = "6789",
                creditLimit = 10000.0,
                statementClosingDay = 15,
                paymentDueDay = 21,
                colorPresetIndex = 0,
                cardholderName = "FRELAND N."
            )
            val sapphireCard = CreditCardEntity(
                cardName = "Sapphire Preferred",
                bankOrNetwork = "Visa",
                lastFourDigits = "3412",
                creditLimit = 8500.0,
                statementClosingDay = 28,
                paymentDueDay = 5,
                colorPresetIndex = 1,
                cardholderName = "FRELAND N."
            )

            val goldId = cardDao.insertCard(goldCard)
            val sapphireId = cardDao.insertCard(sapphireCard)

            val now = System.currentTimeMillis()
            val dayMillis = 86_400_000L

            val initialTransactions = listOf(
                TransactionEntity(
                    cardId = goldId,
                    merchant = "Uber Technologies",
                    amount = 24.50,
                    category = "Travel",
                    dateMillis = now - (dayMillis * 0),
                    notes = "Ride to downtown office"
                ),
                TransactionEntity(
                    cardId = goldId,
                    merchant = "Target Stores",
                    amount = 89.10,
                    category = "Groceries",
                    dateMillis = now - (dayMillis * 1),
                    notes = "Weekly grocery & home essentials"
                ),
                TransactionEntity(
                    cardId = sapphireId,
                    merchant = "Amazon.com",
                    amount = 115.00,
                    category = "Shopping",
                    dateMillis = now - (dayMillis * 2),
                    notes = "Tech accessories"
                ),
                TransactionEntity(
                    cardId = goldId,
                    merchant = "Starbucks Coffee",
                    amount = 12.80,
                    category = "Dining",
                    dateMillis = now - (dayMillis * 2),
                    notes = "Morning coffee & breakfast"
                ),
                TransactionEntity(
                    cardId = sapphireId,
                    merchant = "Netflix Subscription",
                    amount = 22.99,
                    category = "Subscriptions",
                    dateMillis = now - (dayMillis * 3),
                    notes = "Monthly premium subscription"
                ),
                TransactionEntity(
                    cardId = goldId,
                    merchant = "Shell Oil Gas",
                    amount = 48.25,
                    category = "Utilities",
                    dateMillis = now - (dayMillis * 4),
                    notes = "Gas refill"
                ),
                TransactionEntity(
                    cardId = sapphireId,
                    merchant = "Whole Foods Market",
                    amount = 134.75,
                    category = "Groceries",
                    dateMillis = now - (dayMillis * 5),
                    notes = "Organic fruits and pantry"
                ),
                TransactionEntity(
                    cardId = goldId,
                    merchant = "Previous Statement Payment",
                    amount = 500.00,
                    category = "Bill Payment",
                    dateMillis = now - (dayMillis * 8),
                    notes = "Auto-pay statement balance",
                    isPayment = true
                )
            )

            for (trx in initialTransactions) {
                transactionDao.insertTransaction(trx)
            }
        }
    }
}
