package com.hadiyarajesh.composetemplate.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hadiyarajesh.composetemplate.R
import com.hadiyarajesh.composetemplate.data.entity.Image
import com.hadiyarajesh.composetemplate.ui.components.ErrorItem
import com.hadiyarajesh.composetemplate.ui.components.LoadingIndicator
import com.hadiyarajesh.composetemplate.utility.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        uiState = uiState,
        loadData = { viewModel.loadData() }
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeScreenUiState,
    loadData: () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        loadData()
        systemUiController.setSystemBarsColor(
            color = Color.White, // Change status bar color to white
            darkIcons = true // Use dark icons for better visibility on white background
        )
    }

    Scaffold(
        containerColor = Color.White // Set scaffold background to white
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth() // Only fill width, let height wrap content for scrolling
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling
                .padding(innerPadding)
                .padding(top = 80.dp) // Add gap at the top so content is not covered by topbar
                .padding(bottom = 80.dp) // Add bottom padding to avoid overlap with BottomNavBar
                .padding(horizontal = 16.dp)
        ) {
            // Top Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Light blue background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Equipment", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.Warehouse, contentDescription = "Total Equipment Icon", tint = Color(0xFF2196F3))
                        }
                        Text("1,247", style = MaterialTheme.typography.headlineMedium)
                        Text("+12 this week", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) // Light orange background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Active Projects", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.LocationOn, contentDescription = "Active Projects Icon", tint = Color(0xFFFF9800))
                        }
                        Text("23", style = MaterialTheme.typography.headlineMedium)
                        Text("3 overdue", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionButton(icon = Icons.Default.QrCodeScanner, text = "Scan Item", onClick = {})
                QuickActionButton(icon = Icons.Default.Add, text = "Add Item", onClick = {})
                QuickActionButton(icon = Icons.Default.SwapHoriz, text = "Transfer", onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Locations
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Locations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {}) {
                    Text("View All")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LocationItem(
                    icon = Icons.Default.Warehouse,
                    name = "Main Warehouse",
                    location = "Jakarta Pusat",
                    items = "456 items",
                    capacity = "98% capacity",
                    capacityColor = Color(0xFF4CAF50) // Green
                )
                LocationItem(
                    icon = Icons.Default.LocationOn,
                    name = "Site Project A",
                    location = "Bekasi Timur",
                    items = "234 items",
                    capacity = "67% capacity",
                    capacityColor = Color(0xFFFF9800) // Orange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activities
            Text("Recent Activities", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityItem(
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF4CAF50), // Green
                    title = "Equipment Received",
                    details = "Excavator CAT 320D • Main Warehouse",
                    time = "2 hours ago"
                )
                ActivityItem(
                    icon = Icons.Default.ArrowForward,
                    iconTint = Color(0xFF2196F3), // Blue
                    title = "Transfer Completed",
                    details = "Bulldozer D6T • To Site Project A",
                    time = "4 hours ago"
                )
                ActivityItem(
                    icon = Icons.Default.Warning,
                    iconTint = Color(0xFFFF9800), // Orange
                    title = "Low Stock Alert",
                    details = "Hydraulic Oil • Only 5 units left",
                    time = "6 hours ago"
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Light grey background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun LocationItem(icon: androidx.compose.ui.graphics.vector.ImageVector, name: String, location: String, items: String, capacity: String, capacityColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = name, modifier = Modifier.size(40.dp), tint = Color(0xFF2196F3)) // Blue icon
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(items, style = MaterialTheme.typography.bodyMedium)
                Text(capacity, style = MaterialTheme.typography.bodySmall, color = capacityColor)
            }
        }
    }
}

@Composable
fun ActivityItem(icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, title: String, details: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp), tint = iconTint)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(details, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}


@Composable
fun DashboardPieChart(kondisiStat: Map<String, Int>) {
    val data = kondisiStat.entries.toList()
    val total = data.sumOf { it.value }.toFloat()
    val pieColors = listOf(Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFFBC02D), Color(0xFFD32F2F), Color(0xFF7B1FA2))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Pemantauan Berdasarkan Kondisi Barang", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            data.forEachIndexed { index, entry ->
                val sweep = if (total == 0f) 0f else (entry.value / total) * 360f
                drawArc(
                    color = pieColors[index % pieColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Keterangan warna
        data.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(pieColors[index % pieColors.size], shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(entry.key + " (" + entry.value + ")", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Preview(showSystemUi = false, showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeScreenUiState.Success(
                data = Image(
                    description = stringResource(id = R.string.welcome_message),
                    altText = stringResource(id = R.string.image),
                    url = Constants.IMAGE_URL
                ),
                kondisiStat = mapOf("Baik" to 10, "Rusak" to 2)
            ),
            loadData = {}
        )
    }
}
