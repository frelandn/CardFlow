package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CreditCardEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.CardFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class CardWithStats(
    val card: CreditCardEntity,
    val currentBalance: Double,
    val availableCredit: Double,
    val utilizationPercent: Float,
    val daysUntilPaymentDue: Int,
    val formattedDueDate: String,
    val daysUntilClosing: Int,
    val formattedClosingDate: String
)

data class TransactionWithCard(
    val transaction: TransactionEntity,
    val card: CreditCardEntity?
)

data class CardFlowUiState(
    val isLoading: Boolean = false,
    val cards: List<CardWithStats> = emptyList(),
    val selectedCardId: Long? = null, // null = all cards
    val selectedCategory: String? = null, // null = all categories
    val allTransactions: List<TransactionWithCard> = emptyList(),
    val filteredTransactions: List<TransactionWithCard> = emptyList(),
    val totalCreditLimit: Double = 0.0,
    val totalCurrentBalance: Double = 0.0,
    val totalAvailableCredit: Double = 0.0,
    val overallUtilization: Float = 0f,
    val monthlySpend: Double = 0.0,
    val categorySpendMap: Map<String, Double> = emptyMap(),
    val currencySymbol: String = "₱",
    // Dialog / Sheet states
    val showAddTransactionDialog: Boolean = false,
    val showAddCardDialog: Boolean = false,
    val showMakePaymentDialog: Boolean = false,
    val editingTransaction: TransactionEntity? = null,
    val editingCard: CreditCardEntity? = null,
    val targetCardForPayment: CreditCardEntity? = null
)

class CardFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardFlowRepository

    private val _selectedCardId = MutableStateFlow<Long?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _currencySymbol = MutableStateFlow("₱")

    private val _showAddTransactionDialog = MutableStateFlow(false)
    private val _showAddCardDialog = MutableStateFlow(false)
    private val _showMakePaymentDialog = MutableStateFlow(false)
    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    private val _editingCard = MutableStateFlow<CreditCardEntity?>(null)
    private val _targetCardForPayment = MutableStateFlow<CreditCardEntity?>(null)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CardFlowRepository(database.cardDao(), database.transactionDao())

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val uiState: StateFlow<CardFlowUiState> = combine(
        repository.allCards,
        repository.allTransactions,
        _selectedCardId,
        _selectedCategory,
        _currencySymbol,
        _showAddTransactionDialog,
        _showAddCardDialog,
        _showMakePaymentDialog,
        _editingTransaction,
        _editingCard,
        _targetCardForPayment
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val cards = args[0] as List<CreditCardEntity>
        @Suppress("UNCHECKED_CAST")
        val transactions = args[1] as List<TransactionEntity>
        val selectedCardId = args[2] as Long?
        val selectedCategory = args[3] as String?
        val currencySymbol = args[4] as String
        val showAddTrx = args[5] as Boolean
        val showAddCard = args[6] as Boolean
        val showMakePayment = args[7] as Boolean
        val editingTrx = args[8] as TransactionEntity?
        val editingCard = args[9] as CreditCardEntity?
        val targetCardForPay = args[10] as CreditCardEntity?

        val cardMap = cards.associateBy { it.id }

        // Compute balances per card
        val balanceByCard = mutableMapOf<Long, Double>()
        for (trx in transactions) {
            val current = balanceByCard.getOrDefault(trx.cardId, 0.0)
            if (trx.isPayment) {
                balanceByCard[trx.cardId] = current - trx.amount
            } else {
                balanceByCard[trx.cardId] = current + trx.amount
            }
        }

        val cardsWithStats = cards.map { card ->
            val balance = max(0.0, balanceByCard.getOrDefault(card.id, 0.0))
            val available = max(0.0, card.creditLimit - balance)
            val util = if (card.creditLimit > 0) {
                ((balance / card.creditLimit) * 100).toFloat().coerceIn(0f, 100f)
            } else 0f

            val (dueDays, formattedDueDate) = calculateNextOccurrence(card.paymentDueDay)
            val (closingDays, formattedClosingDate) = calculateNextOccurrence(card.statementClosingDay)

            CardWithStats(
                card = card,
                currentBalance = balance,
                availableCredit = available,
                utilizationPercent = util,
                daysUntilPaymentDue = dueDays,
                formattedDueDate = formattedDueDate,
                daysUntilClosing = closingDays,
                formattedClosingDate = formattedClosingDate
            )
        }

        val totalLimit = cards.sumOf { it.creditLimit }
        val totalBalance = cardsWithStats.sumOf { it.currentBalance }
        val totalAvailable = max(0.0, totalLimit - totalBalance)
        val overallUtil = if (totalLimit > 0) {
            ((totalBalance / totalLimit) * 100).toFloat().coerceIn(0f, 100f)
        } else 0f

        val transactionsWithCard = transactions.map { trx ->
            TransactionWithCard(trx, cardMap[trx.cardId])
        }

        // Filter transactions
        val filtered = transactionsWithCard.filter { item ->
            val matchesCard = selectedCardId == null || item.transaction.cardId == selectedCardId
            val matchesCategory = selectedCategory == null || item.transaction.category.equals(selectedCategory, ignoreCase = true)
            matchesCard && matchesCategory
        }

        // Calculate current month spend (excluding payments)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val calCheck = Calendar.getInstance()
        var monthlySpend = 0.0
        val categorySpend = mutableMapOf<String, Double>()

        for (item in transactions) {
            if (!item.isPayment) {
                calCheck.timeInMillis = item.dateMillis
                if (calCheck.get(Calendar.MONTH) == currentMonth && calCheck.get(Calendar.YEAR) == currentYear) {
                    monthlySpend += item.amount
                    val catTotal = categorySpend.getOrDefault(item.category, 0.0)
                    categorySpend[item.category] = catTotal + item.amount
                }
            }
        }

        CardFlowUiState(
            isLoading = false,
            cards = cardsWithStats,
            selectedCardId = selectedCardId,
            selectedCategory = selectedCategory,
            allTransactions = transactionsWithCard,
            filteredTransactions = filtered,
            totalCreditLimit = totalLimit,
            totalCurrentBalance = totalBalance,
            totalAvailableCredit = totalAvailable,
            overallUtilization = overallUtil,
            monthlySpend = monthlySpend,
            categorySpendMap = categorySpend,
            currencySymbol = currencySymbol,
            showAddTransactionDialog = showAddTrx,
            showAddCardDialog = showAddCard,
            showMakePaymentDialog = showMakePayment,
            editingTransaction = editingTrx,
            editingCard = editingCard,
            targetCardForPayment = targetCardForPay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CardFlowUiState(isLoading = true)
    )

    fun setCurrency(symbol: String) {
        _currencySymbol.value = symbol
    }

    fun selectCard(cardId: Long?) {
        _selectedCardId.value = if (_selectedCardId.value == cardId) null else cardId
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun openAddTransaction(presetCardId: Long? = null) {
        if (presetCardId != null) {
            _selectedCardId.value = presetCardId
        }
        _editingTransaction.value = null
        _showAddTransactionDialog.value = true
    }

    fun openEditTransaction(transaction: TransactionEntity) {
        _editingTransaction.value = transaction
        _showAddTransactionDialog.value = true
    }

    fun openAddCard() {
        _editingCard.value = null
        _showAddCardDialog.value = true
    }

    fun openEditCard(card: CreditCardEntity) {
        _editingCard.value = card
        _showAddCardDialog.value = true
    }

    fun openMakePayment(card: CreditCardEntity) {
        _targetCardForPayment.value = card
        _showMakePaymentDialog.value = true
    }

    fun dismissDialogs() {
        _showAddTransactionDialog.value = false
        _showAddCardDialog.value = false
        _showMakePaymentDialog.value = false
        _editingTransaction.value = null
        _editingCard.value = null
        _targetCardForPayment.value = null
    }

    fun saveTransaction(
        id: Long = 0,
        cardId: Long,
        merchant: String,
        amount: Double,
        category: String,
        dateMillis: Long,
        notes: String = "",
        isPayment: Boolean = false
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                cardId = cardId,
                merchant = merchant.trim(),
                amount = amount,
                category = category,
                dateMillis = dateMillis,
                notes = notes.trim(),
                isPayment = isPayment
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
            } else {
                repository.updateTransaction(entity)
            }
            dismissDialogs()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            dismissDialogs()
        }
    }

    fun saveCard(
        id: Long = 0,
        cardName: String,
        bankOrNetwork: String,
        lastFourDigits: String,
        creditLimit: Double,
        statementClosingDay: Int,
        paymentDueDay: Int,
        colorPresetIndex: Int,
        cardholderName: String
    ) {
        viewModelScope.launch {
            val entity = CreditCardEntity(
                id = id,
                cardName = cardName.trim(),
                bankOrNetwork = bankOrNetwork.trim(),
                lastFourDigits = lastFourDigits.trim().takeLast(4),
                creditLimit = creditLimit,
                statementClosingDay = statementClosingDay.coerceIn(1, 31),
                paymentDueDay = paymentDueDay.coerceIn(1, 31),
                colorPresetIndex = colorPresetIndex,
                cardholderName = cardholderName.trim().ifEmpty { "CARDHOLDER" }
            )
            if (id == 0L) {
                repository.insertCard(entity)
            } else {
                repository.updateCard(entity)
            }
            dismissDialogs()
        }
    }

    fun deleteCard(card: CreditCardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
            if (_selectedCardId.value == card.id) {
                _selectedCardId.value = null
            }
            dismissDialogs()
        }
    }

    fun recordBillPayment(cardId: Long, amount: Double, note: String) {
        val targetCard = uiState.value.cards.find { it.card.id == cardId }?.card
        val cardLabel = targetCard?.cardName ?: "Credit Card"
        saveTransaction(
            cardId = cardId,
            merchant = "Bill Payment - $cardLabel",
            amount = amount,
            category = "Bill Payment",
            dateMillis = System.currentTimeMillis(),
            notes = note.ifEmpty { "Paid towards credit card balance" },
            isPayment = true
        )
    }

    private fun calculateNextOccurrence(dayOfMonth: Int): Pair<Int, String> {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDaysCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val targetDayInCurrentMonth = dayOfMonth.coerceAtMost(maxDaysCurrentMonth)

        val targetCal = Calendar.getInstance()
        if (currentDay <= targetDayInCurrentMonth) {
            targetCal.set(Calendar.DAY_OF_MONTH, targetDayInCurrentMonth)
        } else {
            targetCal.add(Calendar.MONTH, 1)
            val maxNext = targetCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            targetCal.set(Calendar.DAY_OF_MONTH, dayOfMonth.coerceAtMost(maxNext))
        }

        // Strip time
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetZeroCal = (targetCal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = targetZeroCal.timeInMillis - todayCal.timeInMillis
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        return Pair(days, format.format(targetCal.time))
    }
}
