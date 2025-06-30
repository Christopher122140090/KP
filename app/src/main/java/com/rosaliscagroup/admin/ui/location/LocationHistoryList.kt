package com.rosaliscagroup.admin.ui.location

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import com.rosaliscagroup.admin.data.entity.LocationHistory

@Composable
fun LocationHistoryList(
    histories: List<LocationHistory>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        histories.forEach { history ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(history.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(history.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Diperbarui: " + java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(java.util.Date(history.updatedAt)), style = MaterialTheme.typography.bodySmall, color = Color(0xFF1976D2))
                }
            }
        }
    }
}
