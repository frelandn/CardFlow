package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.FintechSurfaceVariant
import com.example.ui.theme.NeonMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun CategoryChips(
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    categorySpendMap: Map<String, Double>,
    currencySymbol: String = "₱",
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" Chip
        item {
            val isSelected = selectedCategory == null
            val bg = if (isSelected) NeonMint.copy(alpha = 0.2f) else FintechSurfaceVariant
            val border = if (isSelected) NeonMint else Color(0x1AFFFFFF)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(20.dp))
                    .clickable { onSelectCategory(null) }
                    .testTag("category_chip_all")
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "All",
                    tint = if (isSelected) NeonMint else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "All",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) NeonMint else TextPrimary
                )
            }
        }

        items(CategoryRegistry.Categories.filter { it.name != "Bill Payment" }) { category ->
            val isSelected = selectedCategory.equals(category.name, ignoreCase = true)
            val bg = if (isSelected) category.color.copy(alpha = 0.25f) else FintechSurfaceVariant
            val border = if (isSelected) category.color else Color(0x1AFFFFFF)
            val spend = categorySpendMap[category.name]

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(20.dp))
                    .clickable { onSelectCategory(category.name) }
                    .testTag("category_chip_${category.name.lowercase(Locale.ROOT)}")
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
                if (spend != null && spend > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = com.example.ui.util.CurrencyHelper.format(spend, currencySymbol, decimals = false),
                        style = MaterialTheme.typography.labelSmall,
                        color = category.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
