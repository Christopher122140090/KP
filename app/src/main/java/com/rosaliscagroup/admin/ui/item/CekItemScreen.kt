package com.rosaliscagroup.admin.ui.item

import androidx.compose.foundation.background
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

@Composable
fun CekBarangScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var barangList by remember { mutableStateOf<List<EquipmentUi>>(emptyList()) }
    var lokasiList by remember { mutableStateOf<List<Location>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val kategoriOptions = listOf(
        "Alat Berat",
        "Generator",
        "Alat Personel",
        "Alat Tambahan",
        "dan lain-lain"
    )
    var selectedKategori by remember { mutableStateOf<String?>(null) }
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
    val filteredBarang = if (selectedKategori == null) barangList else barangList.filter { it.kategori == selectedKategori }
    BarangTable(barangList = filteredBarang)
}
