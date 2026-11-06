package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.CreditCardEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.CardFlowRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CardFlowRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CardFlowRepository(database.cardDao(), database.transactionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `appName resource matches CardFlow`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CardFlow", appName)
    }

    @Test
    fun `insert card and fetch cards successfully`() = runBlocking {
        val card = CreditCardEntity(
            cardName = "Sapphire Preferred",
            bankOrNetwork = "Visa",
            lastFourDigits = "4321",
            creditLimit = 5000.0,
            statementClosingDay = 15,
            paymentDueDay = 8
        )
        val id = repository.insertCard(card)
        assertTrue(id > 0)

        val cards = repository.allCards.first()
        assertEquals(1, cards.size)
        assertEquals("Sapphire Preferred", cards[0].cardName)
        assertEquals("4321", cards[0].lastFourDigits)
    }

    @Test
    fun `insert transaction and query balance`() = runBlocking {
        val card = CreditCardEntity(
            cardName = "Freedom Flex",
            bankOrNetwork = "Mastercard",
            lastFourDigits = "9876",
            creditLimit = 3000.0,
            statementClosingDay = 20,
            paymentDueDay = 12
        )
        val cardId = repository.insertCard(card)

        val trx = TransactionEntity(
            cardId = cardId,
            merchant = "Coffee Roasters",
            amount = 7.50,
            category = "Dining"
        )
        val trxId = repository.insertTransaction(trx)
        assertTrue(trxId > 0)

        val transactions = repository.allTransactions.first()
        assertEquals(1, transactions.size)
        assertEquals("Coffee Roasters", transactions[0].merchant)
        assertEquals(7.50, transactions[0].amount, 0.001)
    }

    @Test
    fun `currency helper formats Philippine Peso correctly`() {
        val formatted = com.example.ui.util.CurrencyHelper.format(1280.45, "₱", decimals = true)
        assertEquals("₱1,280.45", formatted)

        val formattedNoDecimals = com.example.ui.util.CurrencyHelper.format(10000.0, "₱", decimals = false)
        assertEquals("₱10,000", formattedNoDecimals)
    }
}
