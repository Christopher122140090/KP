package com.rosaliscagroup.admin.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import kotlinx.serialization.Serializable

@Serializable
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
fun BarangTable(
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
                    top = innerPadding.calculateTopPadding() + 80.dp, // Jarak topbar lebih kecil agar kategori tidak terdorong ke bawah
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                )
        ) {
            if (barangList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp), // Tambah bottom padding pada LazyColumn
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
                                    androidx.compose.foundation.Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(barang.nama, style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF000000), fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Kategori: " + (barang.kategori.trim().ifBlank { "Tidak diketahui" }.replaceFirstChar { it.uppercase() }), color = Color(0xFF000000))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Lokasi: ${barang.lokasiId}", color = Color(0xFF000000))
                                if (barang.deskripsi.isNotBlank()) {
                                    Text("Deskripsi: ${barang.deskripsi}", color = Color(0xFF000000))
                                }
                                Text("SKU: ${barang.sku}", color = Color(0xFF000000))
                                Text("Created: ${barang.createdAt}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                Text("Updated: ${barang.updatedAt}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = { onEdit(barang) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1976D2))
                                    }
                                    IconButton(onClick = { onDelete(barang) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFD32F2F))
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CekBarangScreen(
    onDetail: (EquipmentUi) -> Unit = {},
    onTransfer: (EquipmentUi) -> Unit = {} // Tambahkan parameter callback transfer
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var barangList by remember { mutableStateOf<List<EquipmentUi>>(emptyList()) }
    var lokasiList by remember { mutableStateOf<List<Location>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    val kategoriOptions = listOf(
        "Semua",
        "Alat berat",
        "Generator",
        "Alat personel",
        "Alat Tambahan",
        "Lain-lain"
    )
    var selectedKategori by remember { mutableStateOf("Semua") }
    var selectedBarang by remember { mutableStateOf<EquipmentUi?>(null) }
    var showTambahSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loading = true
        try {
            val lokasiRepo = HomeRepositoryImpl()
            lokasiList = lokasiRepo.getLocations()
            val result = EquipmentRepository.getAllEquipments()
            barangList = result.map { eq ->
                val lokasiNama = lokasiList.find { it.id == eq.lokasiId }?.name ?: "-"
                EquipmentUi(
                    id = eq.id,
                    nama = eq.nama,
                    deskripsi = eq.deskripsi,
                    kategori = eq.kategori,
                    lokasiId = lokasiNama,
                    sku = eq.sku,
                    gambarUri = eq.gambarUri,
                    createdAt = eq.createdAt?.toDate()?.toString() ?: "",
                    updatedAt = eq.updatedAt?.toDate()?.toString() ?: ""
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        loading = false
    }

    val filteredBarang = barangList.filter {
        (selectedKategori == "Semua" || it.kategori.equals(selectedKategori, ignoreCase = true)) &&
        (searchQuery.isBlank() || it.nama.contains(searchQuery, ignoreCase = true) || it.sku.contains(searchQuery, ignoreCase = true))
    }

    if (selectedBarang != null) {
        AlertDialog(
            onDismissRequest = { selectedBarang = null },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    if (!selectedBarang!!.gambarUri.isNullOrBlank()) {
                        androidx.compose.foundation.Image(
                            painter = rememberAsyncImagePainter(selectedBarang!!.gambarUri),
                            contentDescription = "Gambar Barang",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        selectedBarang!!.nama,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kategori: ${selectedBarang!!.kategori}", style = MaterialTheme.typography.bodyMedium)
                    Text("Lokasi: ${selectedBarang!!.lokasiId}", style = MaterialTheme.typography.bodyMedium)
                    Text("SKU: ${selectedBarang!!.sku}", style = MaterialTheme.typography.bodyMedium)
                    if (!selectedBarang!!.deskripsi.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Deskripsi:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(selectedBarang!!.deskripsi, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Created: ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(selectedBarang!!.createdAt, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Row {
                        Text("Updated: ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(selectedBarang!!.updatedAt, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { selectedBarang = null }) {
                        Text("Tutup", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onTransfer(selectedBarang!!)
                            selectedBarang = null // Tutup dialog setelah navigasi
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Transfer Item", color = Color.White)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    if (showTambahSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTambahSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color(0xFFF9FAFB)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Pilih Jenis Item",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF23272E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Kartu Tambahkan Barang
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Navigasi ke tambah barang */ showTambahSheet = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = Color(0xFFFF7043), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tambahkan Barang", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E)))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(text = "Material")
                                Chip(text = "Alat")
                                Chip(text = "Equipment")
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFB0B0B0), modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Kartu Tambahkan Lokasi
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Navigasi ke tambah lokasi */ showTambahSheet = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE3F0FF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tambahkan Lokasi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E)))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(text = "Gudang")
                                Chip(text = "Area Kerja")
                                Chip(text = "Site Office")
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFB0B0B0), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cek Barang", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                actions = {
                    IconButton(onClick = { /* TODO: Search action */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showTambahSheet = true }, containerColor = Color(0xFF1976D2)) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang", tint = Color.White)
            }
        },
        bottomBar = {
            // ...bottom navigation sudah ada di layout utama...
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
                onValueChange = { searchQuery = it },
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kategoriOptions.take(3).forEach { kategori ->
                        val selected = selectedKategori == kategori
                        Button(
                            onClick = { selectedKategori = kategori },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF1976D2) else Color(0xFFF5F5F5),
                                contentColor = if (selected) Color.White else Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(kategori, maxLines = 1)
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kategoriOptions.drop(3).forEach { kategori ->
                        val selected = selectedKategori == kategori
                        Button(
                            onClick = { selectedKategori = kategori },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF1976D2) else Color(0xFFF5F5F5),
                                contentColor = if (selected) Color.White else Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(kategori, maxLines = 1)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredBarang.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data barang.", color = Color.Gray)
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
                                    androidx.compose.foundation.Image(
                                        painter = rememberAsyncImagePainter(barang.gambarUri),
                                        contentDescription = "Gambar Barang",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(barang.nama, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(barang.kategori, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(barang.deskripsi, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(barang.createdAt, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(barang.sku, style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { selectedBarang = barang }) {
                                        Text("Detail", color = Color(0xFF1976D2))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "Pilih Jenis Item",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Kartu Tambahkan Barang
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onTambahBarang() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Color(0xFFFF7043),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tambahkan Barang",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Kartu Tambahkan Lokasi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onTambahLokasi() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE3F0FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tambahkan Lokasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
