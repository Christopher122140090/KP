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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.ui.proyek.Proyek
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLocationsScreen(
    locations: List<Location>,
    navController: NavController? = null,
    onViewAllClick: (() -> Unit)? = null,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semua Lokasi", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        val selectedProyek = remember { mutableStateOf<Proyek?>(null) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (locations.isEmpty()) {
                Text(
                    "Tidak ada lokasi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                )
            } else {
                locations.forEach { location ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { selectedProyek.value = Proyek(location.id, location.name, location.address) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(location.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(location.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            if (location.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(location.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (location.type.isNotBlank()) {
                                    Text("Tipe: ${location.type}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1976D2))
                                }
                                if (location.status.isNotBlank()) {
                                    Text("Status: ${location.status}", style = MaterialTheme.typography.bodySmall, color = if (location.status == "active") Color(0xFF388E3C) else Color(0xFFD32F2F))
                                }
                                if (onViewAllClick != null && locations.isNotEmpty()) {
                                    TextButton(onClick = onViewAllClick) {
                                        Text("Lihat Semua")
                                    }
                                }
                            }
                            if (location.contactPerson.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Kontak: ${location.contactPerson}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (location.createdAt > 0L) {
                                Spacer(modifier = Modifier.height(2.dp))
                                val date = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(location.createdAt))
                                Text("Dibuat: $date", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
        // Popup dialog for location details
        if (selectedProyek.value != null) {
            val proyek = selectedProyek.value!!
            AlertDialog(
                onDismissRequest = { selectedProyek.value = null },
                title = {
                    Text(text = "Detail Lokasi", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("ID: ${proyek.id}")
                        Text("Nama: ${proyek.nama}")
                        Text("Alamat: ${proyek.lokasi}")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (proyek.id.isNotBlank() && navController != null) {
                            navController.navigate("proyekItemListPage?lokasi=${proyek.id}&kategori=Semua")
                        }
                        selectedProyek.value = null
                    }) {
                        Text("Lihat Item")
                    }
                }
            )
        }
    }
}
