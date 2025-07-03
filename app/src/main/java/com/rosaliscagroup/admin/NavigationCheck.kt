package com.rosaliscagroup.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CheckNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pilih Jenis Pemeriksaan",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Pilih jenis item atau lokasi yang ingin Anda periksa di proyek konstruksi",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(16.dp))

            // Card 1: Pemeriksaan Barang
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("Cek_Barang") }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFECB3), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.Inventory2, contentDescription = "Cek Barang Icon", tint = Color(0xFFF57C00)) // Warna oranye lebih berani
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Cek Item", style = MaterialTheme.typography.titleMedium)
                            Text("Periksa material, alat, atau peralatan konstruksi di inventori proyek", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Arrow")
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                            Text("Material")
                            Text("Alat")
                            Text("Equipment")
                    }
                }
            }

            // Card 2: Pemeriksaan Lokasi
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("ViewProyekPage") }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFBBDEFB), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Cek Lokasi Icon", tint = Color(0xFF1976D2)) // Warna biru lebih berani
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Cek Lokasi", style = MaterialTheme.typography.titleMedium)
                            Text("Periksa lokasi seperti gudang, area kerja, atau titik distribusi material", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Arrow")
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Gudang")
                        Text("Area Kerja")
                        Text("Site Office")

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckNavPreview() {
    val fakeNavController = NavController(LocalContext.current)
    MaterialTheme {
        CheckNav(navController = fakeNavController)
    }
}
