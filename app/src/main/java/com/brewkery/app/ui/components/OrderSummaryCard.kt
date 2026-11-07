package com.brewkery.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brewkery.app.R
import com.brewkery.app.domain.model.formatMoney
import com.brewkery.app.domain.model.formatPercent

@Composable
fun OrderSummaryCard(
    currencySymbol: String,
    subtotal: Double,
    tax: Double,
    taxRatePercent: Double,
    deliveryFee: Double,
    total: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.order_summary),
                style = MaterialTheme.typography.titleMedium,
            )
            SummaryRow(stringResource(R.string.subtotal), formatMoney(currencySymbol, subtotal))
            SummaryRow(
                label = "Tax (${formatPercent(taxRatePercent)}%)",
                value = formatMoney(currencySymbol, tax),
            )
            SummaryRow(stringResource(R.string.delivery), formatMoney(currencySymbol, deliveryFee))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            SummaryRow(
                label = stringResource(R.string.total),
                value = formatMoney(currencySymbol, total),
                emphasize = true,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    emphasize: Boolean = false,
) {
    val style = if (emphasize) {
        MaterialTheme.typography.titleMedium
    } else {
        MaterialTheme.typography.bodyLarge
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = style)
        Text(text = value, style = style)
    }
}
