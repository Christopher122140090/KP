package com.rosaliscagroup.admin.ui.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hadiyarajesh.admin.R
import com.rosaliscagroup.admin.data.entity.Image
import com.rosaliscagroup.admin.ui.components.ErrorItem
import com.rosaliscagroup.admin.ui.components.LoadingIndicator
import com.rosaliscagroup.admin.utility.Constants
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        uiState = uiState,
        loadData = { viewModel.loadData() },
        navController = navController
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeScreenUiState,
    loadData: () -> Unit,
    navController: NavController
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        loadData()
        systemUiController.setSystemBarsColor(
            color = Color.White,
            darkIcons = true
        )
    }

    when (uiState) {
        is HomeScreenUiState.Loading -> {
            LoadingIndicator()
        }
        is HomeScreenUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(text = uiState.msg)
            }
        }
        is HomeScreenUiState.Success -> {
            // Tampilkan data dinamis dari Firestore
            Scaffold(
                containerColor = Color.White
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(top = 80.dp)
                        .padding(bottom = 80.dp)
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
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Total Equipment", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.Warehouse, contentDescription = "Total Equipment Icon", tint = Color(0xFF2196F3))
                                }
                                Text(uiState.totalEquipments.toString(), style = MaterialTheme.typography.headlineMedium)
                                // Dummy: Text("+12 this week", ...)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Active Projects", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.LocationOn, contentDescription = "Active Projects Icon", tint = Color(0xFFFF9800))
                                }
                                Text(uiState.totalProjects.toString(), style = MaterialTheme.typography.headlineMedium)
                                // Dummy: Text("3 overdue", ...)
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
                QuickActionButton(icon = Icons.Default.Add, text = "Add Item", onClick = { navController.navigate("TambahItemPage") })
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
                        TextButton(onClick = { navController.navigate("ViewProyekPage") }) {
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
                            capacityColor = Color(0xFF4CAF50)
                        )
                        LocationItem(
                            icon = Icons.Default.LocationOn,
                            name = "Site Project A",
                            location = "Bekasi Timur",
                            items = "234 items",
                            capacity = "67% capacity",
                            capacityColor = Color(0xFFFF9800)
                        )
                    }

            Spacer(modifier = Modifier.height(24.dp))

                    // Recent Activities
                    Text("Recent Activities", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActivityItem(
                            icon = Icons.Default.CheckCircle,
                            iconTint = Color(0xFF4CAF50),
                            title = "Equipment Received",
                            details = "Excavator CAT 320D • Main Warehouse",
                            time = "2 hours ago"
                        )
                        ActivityItem(
                            icon = Icons.Default.ArrowForward,
                            iconTint = Color(0xFF2196F3),
                            title = "Transfer Completed",
                            details = "Bulldozer D6T • To Site Project A",
                            time = "4 hours ago"
                        )
                        ActivityItem(
                            icon = Icons.Default.Warning,
                            iconTint = Color(0xFFFF9800),
                            title = "Low Stock Alert",
                            details = "Hydraulic Oil • Only 5 units left",
                            time = "6 hours ago"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hapus bagian Main Image (dummy)
                }
            }
        }
        else -> {}
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
fun ActivityCard(activity: com.rosaliscagroup.admin.data.entity.Activity) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("Type: ${activity.type}", style = MaterialTheme.typography.bodyLarge)
            Text("Details: ${activity.details}")
            Text("Project: ${activity.projectId}")
            Text("Equipment: ${activity.equipmentId}")
            Text("Location: ${activity.locationId}")
            Text("Created: ${activity.createdAt}")
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
                kondisiStat = mapOf("Baik" to 10, "Rusak" to 2),
                totalActivities = 5,
                totalEquipments = 10,
                totalLocations = 3,
                totalProjects = 2,
                totalUsers = 7,
                recentActivities = emptyList()
            ),
            loadData = {},
            navController = rememberNavController()
        )
    }
}




