package com.rosaliscagroup.admin.ui.proyek

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl

data class EquipmentUi(
    val id: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val kategori: String = "",
    val lokasiId: String = "",
    val sku: String = "",
    val gambarUri: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Composable
fun BarangTableTransfer(
    barangList: List<EquipmentUi>,
    onEdit: (EquipmentUi) -> Unit = {},
    onDelete: (EquipmentUi) -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFE3F2FD)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 80.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                )
        ) {
            if (barangList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(barangList) { barang ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (barang.gambarUri.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    barang.nama,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF000000),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Kategori: " + (barang.kategori.trim()
                                        .ifBlank { "Tidak diketahui" }
                                        .replaceFirstChar { it.uppercase() }),
                                    color = Color(0xFF000000)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Lokasi: ${barang.lokasiId}", color = Color(0xFF000000))
                                if (barang.deskripsi.isNotBlank()) {
                                    Text(
                                        "Deskripsi: ${barang.deskripsi}",
                                        color = Color(0xFF000000)
                                    )
                                }
                                Text("SKU: ${barang.sku}", color = Color(0xFF000000))
                                Text(
                                    "Created: ${barang.createdAt}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Updated: ${barang.updatedAt}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = { onEdit(barang) }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = Color(0xFF1976D2)
                                        )
                                    }
                                    IconButton(onClick = { onDelete(barang) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CekBarangScreenTransfer(
    lokasiId: String,
    onDetail: (EquipmentUi) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val barangListState = remember { mutableStateOf<List<EquipmentUi>>(emptyList()) }
    val lokasiListState = remember { mutableStateOf<List<Location>>(emptyList()) }
    val loadingState = remember { mutableStateOf(true) }
    val searchQueryState = remember { mutableStateOf("") }
    val kategoriListState = remember { mutableStateOf<List<String>>(listOf("Semua")) }
    val selectedKategoriState = remember { mutableStateOf("Semua") }
    val selectedBarangState = remember { mutableStateOf<EquipmentUi?>(null) }

    val barangList = barangListState.value
    val lokasiList = lokasiListState.value
    val loading = loadingState.value
    val searchQuery = searchQueryState.value
    val kategoriList = kategoriListState.value
    val selectedKategori = selectedKategoriState.value
    val selectedBarang = selectedBarangState.value

    LaunchedEffect(Unit) {
        loadingState.value = true
        try {
            val lokasiRepo = HomeRepositoryImpl()
            lokasiListState.value = lokasiRepo.getLocations()
            val result = EquipmentRepository.getAllEquipments()
            barangListState.value = result.map { eq ->
                EquipmentUi(
                    id = eq.id,
                    nama = eq.nama,
                    deskripsi = eq.deskripsi,
                    kategori = eq.kategori,
                    lokasiId = eq.lokasiId,
                    sku = eq.sku,
                    gambarUri = eq.gambarUri,
                    createdAt = eq.createdAt?.toDate()?.toString() ?: "",
                    updatedAt = eq.updatedAt?.toDate()?.toString() ?: ""
                )
            }
            // Ambil kategori unik dari data barang yang hanya ada di lokasi ini
            val categories = result.filter { it.lokasiId == lokasiId }.map { it.kategori }.distinct().sorted()
            kategoriListState.value = listOf("Semua") + categories
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        loadingState.value = false
    }

    val filteredBarang = barangList.filter { barang ->
        barang.lokasiId == lokasiId &&
        (selectedKategori == "Semua" || barang.kategori.equals(selectedKategori, ignoreCase = true)) &&
        (searchQuery.isBlank() || barang.nama.contains(searchQuery, ignoreCase = true) || barang.sku.contains(searchQuery, ignoreCase = true))
    }

    if (selectedBarang != null) {
        AlertDialog(
            onDismissRequest = { selectedBarangState.value = null },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    if (selectedBarang.gambarUri.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedBarang.gambarUri),
                            contentDescription = "Gambar Barang",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Color(0xFFF5F5F5),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        selectedBarang.nama,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Kategori: ${selectedBarang.kategori}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Lokasi: ${selectedBarang.lokasiId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "SKU: ${selectedBarang.sku}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (selectedBarang.deskripsi.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Deskripsi:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            selectedBarang.deskripsi,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(
                            "Created: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            selectedBarang.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row {
                        Text(
                            "Updated: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            selectedBarang.updatedAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { selectedBarangState.value = null }) {
                        Text("Tutup", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* TODO: Transfer Item action */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Transfer Item", color = Color.White)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cek Barang",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search action */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            // Bottom navigation already exists in the main layout
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQueryState.value = it },
                placeholder = { Text("Cari barang...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                kategoriList.forEach { kategori ->
                    val selected = selectedKategori == kategori
                    Button(
                        onClick = { selectedKategoriState.value = kategori },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFF1976D2) else Color(0xFFF5F5F5),
                            contentColor = if (selected) Color.White else Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(kategori, maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredBarang.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBarang) { barang ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (barang.gambarUri.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(
                                                Color(0xFFF5F5F5),
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        barang.nama,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        barang.kategori,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        barang.deskripsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        barang.createdAt,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        barang.sku,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.background(
                                            Color(0xFFF5F5F5),
                                            RoundedCornerShape(8.dp)
                                        ).padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { selectedBarangState.value = barang }) {
                                        Text("Detail", color = Color(0xFF1976D2))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF23272E)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChipPreview() {
    MaterialTheme {
        Chip(text = "Preview Chip")
    }
}

@Preview(showBackground = true)
@Composable
fun CekBarangScreenTransferPreview() {
    MaterialTheme {
        CekBarangScreenTransfer(lokasiId = "12345")
    }
}
