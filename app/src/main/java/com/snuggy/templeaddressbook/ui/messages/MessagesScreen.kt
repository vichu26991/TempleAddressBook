package com.snuggy.templeaddressbook.ui.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MessagesScreen(modifier: Modifier = Modifier) {
    var recipient by remember { mutableStateOf("All Contacts") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("To") }
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    label = { Text("Message") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send SMS")
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send WhatsApp")
                    }
                }
            }
        }
    }
}