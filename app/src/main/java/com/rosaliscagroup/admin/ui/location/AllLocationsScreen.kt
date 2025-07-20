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
                Text("Tidak ada lokasi tersedia.", style = MaterialTheme.typography.bodyMedium)
            } else {
                locations.forEach { location ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(location.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(location.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                navController?.navigate("cekBarang/${location.id}")
                            }) {
                                Text("Lihat Item")
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            if (proyek.id.isNotBlank() && navController != null) {
                                navController.navigate("editProyekScreen?locationId=${proyek.id}")
                            } else {
                                // Log or handle the case where navController or proyek.id is invalid
                            }
                            selectedProyek.value = null
                        }) {
                            Text("Edit Proyek")
                        }
                        Button(onClick = {
                            if (proyek.id.isNotBlank() && navController != null) {
                                navController.navigate("proyekItemListPage?lokasi=${proyek.id}&kategori=Semua")
                            }
                            selectedProyek.value = null
                        }) {
                            Text("Lihat Item")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProyek.value = null }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}
