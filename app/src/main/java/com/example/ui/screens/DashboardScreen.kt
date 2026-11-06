package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CreditCardEntity
import com.example.data.model.TransactionEntity
import com.example.ui.CardFlowUiState
import com.example.ui.components.CategoryChips
import com.example.ui.components.CreditCardCarousel
import com.example.ui.components.CreditUtilizationGauge
import com.example.ui.components.TransactionItem
import com.example.ui.theme.FintechBackground
import com.example.ui.theme.FintechSurface
import com.example.ui.theme.FintechSurfaceVariant
import com.example.ui.theme.NeonMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.CurrencyHelper

@Composable
fun DashboardScreen(
    uiState: CardFlowUiState,
    onSelectCard: (Long?) -> Unit,
    onSelectCategory: (String?) -> Unit,
    onSelectCurrency: (String) -> Unit,
    onAddCardClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onPayClick: (CreditCardEntity) -> Unit,
    onEditCardClick: (CreditCardEntity) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FintechBackground),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "CardFlow Logo",
                            tint = NeonMint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CardFlow",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Credit Card Tracker",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Currency Picker Chip
                    Box {
                        Surface(
                            onClick = { currencyMenuExpanded = true },
                            shape = RoundedCornerShape(20.dp),
                            color = FintechSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("currency_selector_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.currencySymbol,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonMint
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Currency",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false },
                            modifier = Modifier.background(FintechSurface)
                        ) {
                            CurrencyHelper.supportedCurrencies.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${option.symbol} ${option.code} - ${option.name}",
                                            color = if (uiState.currencySymbol == option.symbol) NeonMint else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        onSelectCurrency(option.symbol)
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Add Expense Button
                    Surface(
                        onClick = onAddTransactionClick,
                        shape = RoundedCornerShape(20.dp),
                        color = NeonMint,
                        modifier = Modifier.testTag("top_add_expense_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Expense",
                                tint = FintechBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = FintechBackground
                            )
                        }
                    }
                }
            }
        }

        // Credit Cards Carousel
        item {
            CreditCardCarousel(
                cards = uiState.cards,
                selectedCardId = uiState.selectedCardId,
                onCardSelect = onSelectCard,
                onAddCardClick = onAddCardClick,
                onPayClick = onPayClick,
                onEditClick = onEditCardClick,
                currencySymbol = uiState.currencySymbol
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Credit Utilization & Monthly Spend Gauge
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                CreditUtilizationGauge(
                    utilizationPercent = uiState.overallUtilization,
                    monthlySpend = uiState.monthlySpend,
                    totalBalance = uiState.totalCurrentBalance,
                    totalLimit = uiState.totalCreditLimit,
                    currencySymbol = uiState.currencySymbol
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Category Filter Chips
        item {
            CategoryChips(
                selectedCategory = uiState.selectedCategory,
                onSelectCategory = onSelectCategory,
                categorySpendMap = uiState.categorySpendMap,
                currencySymbol = uiState.currencySymbol
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${uiState.filteredTransactions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (uiState.selectedCardId != null || uiState.selectedCategory != null) {
                    TextButton(
                        onClick = {
                            onSelectCard(null)
                            onSelectCategory(null)
                        }
                    ) {
                        Text(
                            text = "Reset Filter",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonMint
                        )
                    }
                }
            }
        }

        // Transactions List or Empty State
        if (uiState.filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(FintechSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterListOff,
                                contentDescription = "No transactions",
                                tint = TextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Transactions Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.selectedCategory != null || uiState.selectedCardId != null) {
                                "No transactions match your current filters."
                            } else {
                                "Log your first credit card purchase to start tracking."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddTransactionClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonMint,
                                contentColor = FintechBackground
                            )
                        ) {
                            Text("Log Expense")
                        }
                    }
                }
            }
        } else {
            items(uiState.filteredTransactions, key = { it.transaction.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    TransactionItem(
                        item = item,
                        onClick = { onTransactionClick(item.transaction) },
                        currencySymbol = uiState.currencySymbol
                    )
                }
            }
        }
    }
}
