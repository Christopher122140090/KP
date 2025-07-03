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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 80.dp, // Jarak topbar lebih kecil agar kategori tidak terdorong ke bawah
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                )
        ) {
            if (barangList.isEmpty()) {
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .padding(bottom = 32.dp), // Tambah bottom padding pada LazyColumn
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(barangList) { barang ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.Companion.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.Companion.padding(16.dp)) {
                                if (barang.gambarUri.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier.Companion
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.Companion.height(8.dp))
                                }
                                Text(
                                    barang.nama,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF000000),
                                        fontWeight = FontWeight.Companion.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.Companion.height(4.dp))
                                Text(
                                    "Kategori: " + (barang.kategori.trim()
                                        .ifBlank { "Tidak diketahui" }
                                        .replaceFirstChar { it.uppercase() }),
                                    color = Color(0xFF000000)
                                )
                                Spacer(modifier = Modifier.Companion.height(4.dp))
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
                                    color = Color.Companion.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Updated: ${barang.updatedAt}",
                                    color = Color.Companion.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.Companion.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.Companion.fillMaxWidth()
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
    lokasiId: String, // Tambahkan parameter lokasiId
    onDetail: (EquipmentUi) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val barangListState = remember { mutableStateOf<List<EquipmentUi>>(emptyList()) }
    val lokasiListState = remember { mutableStateOf<List<Location>>(emptyList()) }
    val loadingState = remember { mutableStateOf(true) }
    val searchQueryState = remember { mutableStateOf("") }
    val kategoriOptions = listOf(
        "Semua",
        "Alat berat",
        "Generator",
        "Alat personel",
        "Alat Tambahan",
        "Lain-lain"
    )
    val selectedKategoriState = remember { mutableStateOf("Semua") }
    val selectedBarangState = remember { mutableStateOf<EquipmentUi?>(null) }
    val showTambahSheetState = remember { mutableStateOf(false) }

    val barangList = barangListState.value
    val lokasiList = lokasiListState.value
    val loading = loadingState.value
    val searchQuery = searchQueryState.value
    val selectedKategori = selectedKategoriState.value
    val selectedBarang = selectedBarangState.value
    val showTambahSheet = showTambahSheetState.value

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
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .background(Color.Companion.White)
                ) {
                    if (!selectedBarang!!.gambarUri.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedBarang!!.gambarUri),
                            contentDescription = "Gambar Barang",
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Color(0xFFF5F5F5),
                                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.Companion.height(12.dp))
                    }
                    Text(
                        selectedBarang!!.nama,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Companion.Bold),
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.Companion.height(8.dp))
                    Text(
                        "Kategori: ${selectedBarang!!.kategori}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Lokasi: ${selectedBarang!!.lokasiId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "SKU: ${selectedBarang!!.sku}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!selectedBarang!!.deskripsi.isNullOrBlank()) {
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                        Text(
                            "Deskripsi:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Companion.Gray
                        )
                        Text(
                            selectedBarang!!.deskripsi,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.Companion.height(8.dp))
                    Row {
                        Text(
                            "Created: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Companion.Gray
                        )
                        Text(
                            selectedBarang!!.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Companion.Gray
                        )
                    }
                    Row {
                        Text(
                            "Updated: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Companion.Gray
                        )
                        Text(
                            selectedBarang!!.updatedAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Companion.Gray
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            containerColor = Color.Companion.White
        )
    }

    if (showTambahSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTambahSheetState.value = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color(0xFFF9FAFB)
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Pilih Jenis Item",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Companion.Bold),
                    color = Color(0xFF23272E)
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.Companion.height(24.dp))
                // Kartu Tambahkan Barang
                Card(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable { /* TODO: Navigasi ke tambah barang */ showTambahSheetState.value = false
                        },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.Companion.padding(20.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.Companion
                                .size(48.dp)
                                .background(
                                    Color(0xFFFFF3E0),
                                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                tint = Color(0xFFFF7043),
                                modifier = Modifier.Companion.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.Companion.width(16.dp))
                        Column(modifier = Modifier.Companion.weight(1f)) {
                            Text(
                                "Tambahkan Barang",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Companion.Bold,
                                    color = Color(0xFF23272E)
                                )
                            )
                            Spacer(modifier = Modifier.Companion.height(2.dp))
                            Text(
                                "Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.Companion.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(text = "Material")
                                Chip(text = "Alat")
                                Chip(text = "Equipment")
                            }
                        }
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFB0B0B0),
                            modifier = Modifier.Companion.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.Companion.height(16.dp))
                // Kartu Tambahkan Lokasi
                Card(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable { /* TODO: Navigasi ke tambah lokasi */ showTambahSheetState.value = false
                        },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.Companion.padding(20.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.Companion
                                .size(48.dp)
                                .background(
                                    Color(0xFFE3F0FF),
                                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.Companion.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.Companion.width(16.dp))
                        Column(modifier = Modifier.Companion.weight(1f)) {
                            Text(
                                "Tambahkan Lokasi",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Companion.Bold,
                                    color = Color(0xFF23272E)
                                )
                            )
                            Spacer(modifier = Modifier.Companion.height(2.dp))
                            Text(
                                "Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.Companion.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(text = "Gudang")
                                Chip(text = "Area Kerja")
                                Chip(text = "Site Office")
                            }
                        }
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFB0B0B0),
                            modifier = Modifier.Companion.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cek Barang",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Companion.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search action */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTambahSheetState.value = true },
                containerColor = Color(0xFF1976D2)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah Barang",
                    tint = Color.Companion.White
                )
            }
        },
        bottomBar = {
            // ...bottom navigation sudah ada di layout utama...
        },
        containerColor = Color.Companion.White
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(Color.Companion.White)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.Companion.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQueryState.value = it },
                placeholder = { Text("Cari barang...") },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.Companion.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kategoriOptions.take(3).forEach { kategori ->
                        val selected = selectedKategori == kategori
                        Button(
                            onClick = { selectedKategoriState.value = kategori },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF1976D2) else Color(
                                    0xFFF5F5F5
                                ),
                                contentColor = if (selected) Color.Companion.White else Color(
                                    0xFF1976D2
                                )
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            modifier = Modifier.Companion.fillMaxWidth()
                        ) {
                            Text(kategori, maxLines = 1)
                        }
                    }
                }
                Column(
                    modifier = Modifier.Companion.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kategoriOptions.drop(3).forEach { kategori ->
                        val selected = selectedKategori == kategori
                        Button(
                            onClick = { selectedKategoriState.value = kategori },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF1976D2) else Color(
                                    0xFFF5F5F5
                                ),
                                contentColor = if (selected) Color.Companion.White else Color(
                                    0xFF1976D2
                                )
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            modifier = Modifier.Companion.fillMaxWidth()
                        ) {
                            Text(kategori, maxLines = 1)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.Companion.height(8.dp))
            if (loading) {
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredBarang.isEmpty()) {
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBarang) { barang ->
                        Card(
                            modifier = Modifier.Companion.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.Companion.padding(12.dp),
                                verticalAlignment = Alignment.Companion.CenterVertically
                            ) {
                                if (barang.gambarUri.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier.Companion
                                            .size(64.dp)
                                            .background(
                                                Color(0xFFF5F5F5),
                                                androidx.compose.foundation.shape.RoundedCornerShape(
                                                    8.dp
                                                )
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.Companion.width(12.dp))
                                }
                                Column(modifier = Modifier.Companion.weight(1f)) {
                                    Text(
                                        barang.nama,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Bold)
                                    )
                                    Text(
                                        barang.kategori,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Companion.Gray
                                    )
                                    Text(
                                        barang.deskripsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Companion.Gray
                                    )
                                    Text(
                                        barang.createdAt,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Companion.Gray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.Companion.End) {
                                    Text(
                                        barang.sku,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Companion.Gray,
                                        modifier = Modifier.Companion.background(
                                            Color(0xFFF5F5F5),
                                            androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ).padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.Companion.height(8.dp))
                                    TextButton(onClick = { selectedBarangState.value = barang }) {
                                        Text("Detail", color = Color(0xFF1976D2))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.Companion.height(8.dp))
            // Statistik Barang
            Card(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.Companion.padding(16.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Column(modifier = Modifier.Companion.weight(1f)) {
                        Text(
                            "Statistik Barang",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Bold)
                        )
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                        Row {
                            Text(
                                "24",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF1976D2)
                                )
                            )
                            Spacer(modifier = Modifier.Companion.width(8.dp))
                            Text("Total Barang", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Column(horizontalAlignment = Alignment.Companion.End) {
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                        Row {
                            Text(
                                "18",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF388E3C)
                                )
                            )
                            Spacer(modifier = Modifier.Companion.width(8.dp))
                            Text("Tersedia", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.Companion.height(16.dp))
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier.Companion
            .background(Color(0xFFF3F4F6), androidx.compose.foundation.shape.RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Companion.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Companion.Medium),
            color = Color(0xFF23272E)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahItemScreen(
    onBack: () -> Unit = {},
    onTambahBarang: () -> Unit = {},
    onTambahLokasi: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Item",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Companion.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Companion.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.Companion.height(16.dp))
            Column(modifier = Modifier.Companion.padding(horizontal = 24.dp)) {
                Text(
                    "Pilih Jenis Item",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Companion.Bold,
                        color = Color(0xFF23272E)
                    )
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.Companion.height(24.dp))
            // Kartu Tambahkan Barang
            Card(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onTambahBarang() },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.Companion.padding(20.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.Companion
                            .size(48.dp)
                            .background(
                                Color(0xFFFFF3E0),
                                androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Color(0xFFFF7043),
                            modifier = Modifier.Companion.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.Companion.width(16.dp))
                    Column(modifier = Modifier.Companion.weight(1f)) {
                        Text(
                            "Tambahkan Barang",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Companion.Bold,
                                color = Color(0xFF23272E)
                            )
                        )
                        Spacer(modifier = Modifier.Companion.height(2.dp))
                        Text(
                            "Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip(text = "Material")
                            Chip(text = "Alat")
                            Chip(text = "Equipment")
                        }
                    }
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.Companion.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.Companion.height(16.dp))
            // Kartu Tambahkan Lokasi
            Card(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onTambahLokasi() },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.Companion.padding(20.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.Companion
                            .size(48.dp)
                            .background(
                                Color(0xFFE3F0FF),
                                androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.Companion.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.Companion.width(16.dp))
                    Column(modifier = Modifier.Companion.weight(1f)) {
                        Text(
                            "Tambahkan Lokasi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Companion.Bold,
                                color = Color(0xFF23272E)
                            )
                        )
                        Spacer(modifier = Modifier.Companion.height(2.dp))
                        Text(
                            "Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip(text = "Gudang")
                            Chip(text = "Area Kerja")
                            Chip(text = "Site Office")
                        }
                    }
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.Companion.size(18.dp)
                    )
                }
            }
        }
    }
}
