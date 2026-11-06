package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CategoryRegistry
import com.example.data.model.CreditCardEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.FintechBackground
import com.example.ui.theme.FintechSurface
import com.example.ui.theme.FintechSurfaceVariant
import com.example.ui.theme.NeonMint
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    cards: List<CreditCardEntity>,
    editingTransaction: TransactionEntity?,
    defaultCardId: Long?,
    onDismiss: () -> Unit,
    onSave: (id: Long, cardId: Long, merchant: String, amount: Double, category: String, dateMillis: Long, notes: String) -> Unit,
    onDelete: ((TransactionEntity) -> Unit)? = null,
    currencySymbol: String = "₱"
) {
    var amountText by remember {
        mutableStateOf(editingTransaction?.amount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var merchantText by remember {
        mutableStateOf(editingTransaction?.merchant ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(editingTransaction?.category ?: "Dining")
    }
    var selectedCardId by remember {
        mutableStateOf(editingTransaction?.cardId ?: defaultCardId ?: cards.firstOrNull()?.id ?: 0L)
    }
    var notesText by remember {
        mutableStateOf(editingTransaction?.notes ?: "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val popularMerchants = listOf("Starbucks", "Uber", "Target", "Amazon", "Costco", "Shell", "Netflix", "Whole Foods")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp)),
            color = FintechSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingTransaction == null) "Log Expense" else "Edit Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_transaction_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                            amountText = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Amount ($currencySymbol)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMint,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedLabelColor = NeonMint,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Merchant Field
                OutlinedTextField(
                    value = merchantText,
                    onValueChange = {
                        merchantText = it
                        errorMessage = null
                    },
                    label = { Text("Merchant / Store") },
                    placeholder = { Text("e.g. Target, Uber, Starbucks") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMint,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedLabelColor = NeonMint,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_merchant_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick merchant pills
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    popularMerchants.take(6).forEach { name ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(FintechSurfaceVariant)
                                .clickable { merchantText = name }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Picker
                Text(
                    text = "Charged to Card",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cards.forEach { card ->
                        val isSelected = selectedCardId == card.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonMint.copy(alpha = 0.2f) else FintechSurfaceVariant)
                                .border(1.dp, if (isSelected) NeonMint else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .clickable { selectedCardId = card.id }
                                .testTag("card_picker_option_${card.id}")
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${card.cardName} (*${card.lastFourDigits})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonMint else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Picker
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryRegistry.Categories.filter { it.name != "Bill Payment" }.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) cat.color.copy(alpha = 0.25f) else FintechSurfaceVariant)
                                .border(1.dp, if (isSelected) cat.color else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat.name }
                                .testTag("category_select_${cat.name.lowercase(Locale.ROOT)}")
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.name,
                                tint = cat.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Add receipt note or tag") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMint,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedLabelColor = NeonMint,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_notes_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonRose
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (editingTransaction != null && onDelete != null) {
                        OutlinedButton(
                            onClick = { onDelete(editingTransaction) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRose),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonRose)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("delete_transaction_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }

                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            if (amount == null || amount <= 0.0) {
                                errorMessage = "Please enter a valid amount greater than ${currencySymbol}0."
                                return@Button
                            }
                            if (merchantText.isBlank()) {
                                errorMessage = "Please enter merchant name."
                                return@Button
                            }
                            if (selectedCardId <= 0L) {
                                errorMessage = "Please select a credit card."
                                return@Button
                            }

                            onSave(
                                editingTransaction?.id ?: 0L,
                                selectedCardId,
                                merchantText,
                                amount,
                                selectedCategory,
                                editingTransaction?.dateMillis ?: System.currentTimeMillis(),
                                notesText
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonMint,
                            contentColor = FintechBackground
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("save_transaction_button")
                    ) {
                        Text(
                            text = if (editingTransaction == null) "Add Expense" else "Save Changes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
