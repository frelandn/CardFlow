package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CardWithStats
import com.example.ui.theme.CardColorPresets
import com.example.ui.theme.NeonMint
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantAmber
import java.util.Locale

@Composable
fun CreditCardView(
    cardWithStats: CardWithStats,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPayClick: () -> Unit,
    onEditClick: () -> Unit,
    currencySymbol: String = "₱",
    modifier: Modifier = Modifier
) {
    val card = cardWithStats.card
    val gradientColors = CardColorPresets.getOrElse(card.colorPresetIndex) { CardColorPresets[0] }
    val borderColor = if (isSelected) NeonMint else Color(0x33FFFFFF)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .width(320.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("credit_card_item_${card.id}")
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Row: Card Name & Chip/Wifi Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = card.cardName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = card.bankOrNetwork.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Contactless Wave Icon
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Contactless",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Gold Chip representation
                    Box(
                        modifier = Modifier
                            .size(width = 28.dp, height = 22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFB8860B))
                                )
                            )
                            .border(0.5.dp, Color(0xFFFFF8DC), RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card Number
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "•••• •••• •••• ",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = card.lastFourDigits,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Available Credit & Total Limit Bar
            val progress = (cardWithStats.currentBalance / card.creditLimit).toFloat().coerceIn(0f, 1f)
            val utilColor = when {
                cardWithStats.utilizationPercent < 30f -> NeonMint
                cardWithStats.utilizationPercent < 50f -> VibrantAmber
                else -> NeonRose
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${com.example.ui.util.CurrencyHelper.format(cardWithStats.availableCredit, currencySymbol, decimals = false)} available",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonMint,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${com.example.ui.util.CurrencyHelper.format(card.creditLimit, currencySymbol, decimals = false)} limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = utilColor,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Bar: Payment Due Countdown & Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Next Due: ${cardWithStats.formattedDueDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = VibrantAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (cardWithStats.daysUntilPaymentDue == 0) "Due Today!" else "(${cardWithStats.daysUntilPaymentDue} days left)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pay Button
                    Surface(
                        onClick = onPayClick,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33000000),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = "Pay Bill",
                                tint = NeonMint,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pay",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Card",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
