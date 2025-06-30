package com.rosaliscagroup.admin.ui.location

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecentLocationHistorySection(
    viewModel: LocationHistoryViewModel = hiltViewModel()
) {
    val recentLocations by viewModel.recentLocationHistory.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadRecent() }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Riwayat Lokasi Terbaru", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LocationHistoryList(histories = recentLocations)
    }
}
