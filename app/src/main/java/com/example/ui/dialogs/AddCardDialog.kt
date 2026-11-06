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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CreditCardEntity
import com.example.ui.theme.CardColorPresets
import com.example.ui.theme.FintechBackground
import com.example.ui.theme.FintechSurface
import com.example.ui.theme.FintechSurfaceVariant
import com.example.ui.theme.NeonMint
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCardDialog(
    editingCard: CreditCardEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        cardName: String,
        bankOrNetwork: String,
        lastFourDigits: String,
        creditLimit: Double,
        statementClosingDay: Int,
        paymentDueDay: Int,
        colorPresetIndex: Int,
        cardholderName: String
    ) -> Unit,
    onDelete: ((CreditCardEntity) -> Unit)? = null,
    currencySymbol: String = "₱"
) {
    var cardName by remember { mutableStateOf(editingCard?.cardName ?: "") }
    var network by remember { mutableStateOf(editingCard?.bankOrNetwork ?: "Visa") }
    var lastFour by remember { mutableStateOf(editingCard?.lastFourDigits ?: "") }
    var creditLimitText by remember {
        mutableStateOf(editingCard?.creditLimit?.let { String.format(Locale.US, "%.0f", it) } ?: "")
    }
    var closingDayText by remember {
        mutableStateOf(editingCard?.statementClosingDay?.toString() ?: "15")
    }
    var dueDayText by remember {
        mutableStateOf(editingCard?.paymentDueDay?.toString() ?: "8")
    }
    var selectedColorIndex by remember {
        mutableIntStateOf(editingCard?.colorPresetIndex ?: 0)
    }
    var cardholderName by remember {
        mutableStateOf(editingCard?.cardholderName ?: "PRIMARY CARD")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val networkOptions = listOf("Visa", "Mastercard", "Amex", "Discover", "Capital One", "Chase")

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
                        text = if (editingCard == null) "Add Credit Card" else "Edit Card Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_card_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Name
                OutlinedTextField(
                    value = cardName,
                    onValueChange = {
                        cardName = it
                        errorMessage = null
                    },
                    label = { Text("Card Nickname") },
                    placeholder = { Text("e.g. Sapphire Preferred, Gold Rewards") },
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
                        .testTag("card_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Network Selector
                Text(
                    text = "Card Network / Bank",
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
                    networkOptions.forEach { net ->
                        val isSelected = network.equals(net, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonMint.copy(alpha = 0.2f) else FintechSurfaceVariant)
                                .border(1.dp, if (isSelected) NeonMint else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .clickable { network = net }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = net,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonMint else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Last 4 Digits & Credit Limit in Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = lastFour,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                lastFour = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Last 4 Digits") },
                        placeholder = { Text("6789") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            .weight(1f)
                            .testTag("card_last_four_input")
                    )

                    OutlinedTextField(
                        value = creditLimitText,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                creditLimitText = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Credit Line ($currencySymbol)") },
                        placeholder = { Text("10000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            .weight(1.5f)
                            .testTag("card_credit_limit_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Statement Cycle: Closing Day & Payment Due Day
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = closingDayText,
                        onValueChange = {
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 1..31)) {
                                closingDayText = it
                            }
                        },
                        label = { Text("Statement Close") },
                        placeholder = { Text("Day 1-31") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedLabelColor = NeonMint,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = dueDayText,
                        onValueChange = {
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 1..31)) {
                                dueDayText = it
                            }
                        },
                        label = { Text("Payment Due") },
                        placeholder = { Text("Day 1-31") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedLabelColor = NeonMint,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Theme Color Preset
                Text(
                    text = "Card Gradient Theme",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CardColorPresets.forEachIndexed { index, gradient ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(gradient))
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) NeonMint else Color(0x33FFFFFF),
                                    CircleShape
                                )
                                .clickable { selectedColorIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

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
                    if (editingCard != null && onDelete != null) {
                        OutlinedButton(
                            onClick = { onDelete(editingCard) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRose),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonRose)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("delete_card_button")
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
                            if (cardName.isBlank()) {
                                errorMessage = "Please enter a card nickname."
                                return@Button
                            }
                            if (lastFour.length < 4) {
                                errorMessage = "Please enter 4 digits."
                                return@Button
                            }
                            val limit = creditLimitText.toDoubleOrNull()
                            if (limit == null || limit <= 0.0) {
                                errorMessage = "Please enter a valid credit line limit."
                                return@Button
                            }
                            val closingDay = closingDayText.toIntOrNull() ?: 15
                            val dueDay = dueDayText.toIntOrNull() ?: 8

                            onSave(
                                editingCard?.id ?: 0L,
                                cardName,
                                network,
                                lastFour,
                                limit,
                                closingDay,
                                dueDay,
                                selectedColorIndex,
                                cardholderName
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonMint,
                            contentColor = FintechBackground
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("save_card_button")
                    ) {
                        Text(
                            text = if (editingCard == null) "Add Card" else "Save Card",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
