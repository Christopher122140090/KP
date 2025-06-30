package com.rosaliscagroup.admin.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rosaliscagroup.admin.data.entity.Activity
import java.text.SimpleDateFormat
import java.util.*
import com.rosaliscagroup.admin.ui.home.HomeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewActivitiesPage(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val activities = homeViewModel.allActivities.collectAsStateWithLifecycle().value
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Riwayat Aktivitas") })
        },
        content = { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(activities) { activity ->
                    ActivityItem(activity)
                    Divider()
                }
            }
        }
    )
}

@Composable
fun ActivityItem(activity: Activity) {
    val date = rememberFormattedDate(activity.createdAt)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun rememberFormattedDate(timestamp: Long): String {
    return remember(timestamp) {
        if (timestamp == 0L) "" else SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
