package com.snuggy.templeaddressbook.ui.donations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DonationsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DonationCard(
            title = "View Report",
            subtitle = "Totals, filters, and donation summaries will appear here."
        )

        DonationCard(
            title = "Add Donation Entry",
            subtitle = "Cash, items, and mixed donation entry will be added here."
        )

        DonationCard(
            title = "View by Contact / Group",
            subtitle = "Drill-down reports by contact or group will appear here."
        )
    }
}

@Composable
private fun DonationCard(
    title: String,
    subtitle: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}