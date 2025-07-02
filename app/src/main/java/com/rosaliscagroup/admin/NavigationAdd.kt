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
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos

@Composable
fun AddNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold{ paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues) // Apply padding from Scaffold
                .fillMaxSize()
                .background(Color.White) // Set background to white
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and Description
            Text(
                text = "Pilih Jenis Item",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(16.dp))

            // Card 1: Tambahkan Barang
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
                        .clickable { /* Handle click */ navController.navigate("TambahItemPage") }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon with colored curved rectangular background
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFECB3), shape = RoundedCornerShape(8.dp)) // Light orange background with curved corners
                                .padding(12.dp) // Adjust padding as needed
                        ) {
                            Icon(Icons.Default.Inventory2, contentDescription = "Tambah Barang Icon", tint = Color(0xFFFFA726)) // Orange color
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Tambahkan Barang", style = MaterialTheme.typography.titleMedium)
                            Text("Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.weight(1f))
                        // Updated arrow icon
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Arrow")
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                    ) {
                        // Material Button
                        OutlinedButton(onClick = { /* Handle Material click */ }) {
                            Text("Material")
                        }
                        // Alat Button
                        OutlinedButton(onClick = { /* Handle Alat click */ }) {
                            Text("Alat")
                        }
                        // Equipment Button
                        OutlinedButton(onClick = { /* Handle Equipment click */ }) {
                            Text("Equipment")
                        }
                    }
                }
            }

            // Card 2: Tambahkan Lokasi
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
                        .clickable { /* Handle click */ navController.navigate("TambahProyekPage") }
                        .padding(16.dp)
                ) {
                     Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon with colored curved rectangular background
                         Box(
                            modifier = Modifier
                                .background(Color(0xFFBBDEFB), shape = RoundedCornerShape(8.dp)) // Light blue background with curved corners
                                .padding(12.dp) // Adjust padding as needed
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Tambah Lokasi Icon", tint = Color(0xFF42A5F5)) // Blue color
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Tambahkan Lokasi", style = MaterialTheme.typography.titleMedium)
                            Text("Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.weight(1f))
                        // Updated arrow icon
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Arrow")
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                    ) {
                        // Gudang Button
                        OutlinedButton(onClick = { /* Handle Gudang click */ }) {
                            Text("Gudang")
                        }
                        // Area Kerja Button
                        OutlinedButton(onClick = { /* Handle Area Kerja click */ }) {
                            Text("Area Kerja")
                        }
                        // Site Office Button
                        OutlinedButton(onClick = { /* Handle Site Office click */ }) {
                            Text("Site Office")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddNavPreview() {
    // Use a minimal fake NavController for preview
    val fakeNavController = NavController(LocalContext.current)
    MaterialTheme {
        AddNav(navController = fakeNavController)
    }
}
