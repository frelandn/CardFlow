package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CardFlowViewModel
import com.example.ui.dialogs.AddCardDialog
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.MakePaymentDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CardsManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.CardFlowTheme
import com.example.ui.theme.FintechBackground
import com.example.ui.theme.FintechSurface
import com.example.ui.theme.NeonMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardFlowTheme {
                CardFlowApp()
            }
        }
    }
}

@Composable
fun CardFlowApp(
    viewModel: CardFlowViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Analytics, 2: Cards

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = FintechBackground,
        bottomBar = {
            NavigationBar(
                containerColor = FintechSurface,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FintechBackground,
                        selectedTextColor = NeonMint,
                        indicatorColor = NeonMint,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 1) Icons.Default.Insights else Icons.Outlined.Insights,
                            contentDescription = "Analytics"
                        )
                    },
                    label = { Text("Analytics") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FintechBackground,
                        selectedTextColor = NeonMint,
                        indicatorColor = NeonMint,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_analytics")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Default.CreditCard else Icons.Outlined.CreditCard,
                            contentDescription = "Cards"
                        )
                    },
                    label = { Text("Cards") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FintechBackground,
                        selectedTextColor = NeonMint,
                        indicatorColor = NeonMint,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_cards")
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddTransaction() },
                containerColor = NeonMint,
                contentColor = FintechBackground,
                modifier = Modifier.testTag("global_add_expense_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log Expense",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    uiState = uiState,
                    onSelectCard = { viewModel.selectCard(it) },
                    onSelectCategory = { viewModel.selectCategory(it) },
                    onSelectCurrency = { viewModel.setCurrency(it) },
                    onAddCardClick = { viewModel.openAddCard() },
                    onAddTransactionClick = { viewModel.openAddTransaction() },
                    onPayClick = { card -> viewModel.openMakePayment(card) },
                    onEditCardClick = { card -> viewModel.openEditCard(card) },
                    onTransactionClick = { trx -> viewModel.openEditTransaction(trx) }
                )
                1 -> AnalyticsScreen(
                    uiState = uiState
                )
                2 -> CardsManagementScreen(
                    uiState = uiState,
                    onAddCardClick = { viewModel.openAddCard() },
                    onPayClick = { card -> viewModel.openMakePayment(card) },
                    onEditCardClick = { card -> viewModel.openEditCard(card) }
                )
            }
        }

        // Dialog: Add / Edit Transaction
        if (uiState.showAddTransactionDialog) {
            AddTransactionDialog(
                cards = uiState.cards.map { it.card },
                editingTransaction = uiState.editingTransaction,
                defaultCardId = uiState.selectedCardId,
                onDismiss = { viewModel.dismissDialogs() },
                onSave = { id, cardId, merchant, amount, category, dateMillis, notes ->
                    viewModel.saveTransaction(
                        id = id,
                        cardId = cardId,
                        merchant = merchant,
                        amount = amount,
                        category = category,
                        dateMillis = dateMillis,
                        notes = notes
                    )
                },
                onDelete = { trx -> viewModel.deleteTransaction(trx) },
                currencySymbol = uiState.currencySymbol
            )
        }

        // Dialog: Add / Edit Card
        if (uiState.showAddCardDialog) {
            AddCardDialog(
                editingCard = uiState.editingCard,
                onDismiss = { viewModel.dismissDialogs() },
                onSave = { id, cardName, network, lastFour, limit, closingDay, dueDay, colorIdx, cardholder ->
                    viewModel.saveCard(
                        id = id,
                        cardName = cardName,
                        bankOrNetwork = network,
                        lastFourDigits = lastFour,
                        creditLimit = limit,
                        statementClosingDay = closingDay,
                        paymentDueDay = dueDay,
                        colorPresetIndex = colorIdx,
                        cardholderName = cardholder
                    )
                },
                onDelete = { card -> viewModel.deleteCard(card) },
                currencySymbol = uiState.currencySymbol
            )
        }

        // Dialog: Record Bill Payment
        if (uiState.showMakePaymentDialog && uiState.targetCardForPayment != null) {
            val targetCard = uiState.targetCardForPayment!!
            val cardStats = uiState.cards.find { it.card.id == targetCard.id }
            val balance = cardStats?.currentBalance ?: 0.0

            MakePaymentDialog(
                card = targetCard,
                currentBalance = balance,
                onDismiss = { viewModel.dismissDialogs() },
                onConfirmPayment = { amount, note ->
                    viewModel.recordBillPayment(targetCard.id, amount, note)
                },
                currencySymbol = uiState.currencySymbol
            )
        }
    }
}
