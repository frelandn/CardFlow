package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CreditCardEntity
import com.example.ui.CardWithStats
import com.example.ui.theme.FintechSurfaceVariant
import com.example.ui.theme.NeonMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CreditCardCarousel(
    cards: List<CardWithStats>,
    selectedCardId: Long?,
    onCardSelect: (Long?) -> Unit,
    onAddCardClick: () -> Unit,
    onPayClick: (CreditCardEntity) -> Unit,
    onEditClick: (CreditCardEntity) -> Unit,
    currencySymbol: String = "₱",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Credit Cards",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonMint.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${cards.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonMint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (selectedCardId != null) {
                TextButton(
                    onClick = { onCardSelect(null) },
                    modifier = Modifier.testTag("clear_card_filter_button")
                ) {
                    Text(
                        text = "Show All",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonMint
                    )
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cards, key = { it.card.id }) { cardWithStats ->
                CreditCardView(
                    cardWithStats = cardWithStats,
                    isSelected = selectedCardId == cardWithStats.card.id,
                    onSelect = { onCardSelect(cardWithStats.card.id) },
                    onPayClick = { onPayClick(cardWithStats.card) },
                    onEditClick = { onEditClick(cardWithStats.card) },
                    currencySymbol = currencySymbol
                )
            }

            // Add Card placeholder item
            item {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FintechSurfaceVariant)
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                        .clickable { onAddCardClick() }
                        .testTag("add_card_carousel_item")
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonMint.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Card",
                                tint = NeonMint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Add Card",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "New limit",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
