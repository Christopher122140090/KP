package com.rosaliscagroup.admin.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import coil.compose.rememberAsyncImagePainter
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import kotlinx.serialization.Serializable
import androidx.navigation.compose.rememberNavController

@Serializable
data class EquipmentUi(
    val id: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val kategori: String = "",
    val lokasiId: String = "",
    val lokasiNama: String = "",
    val sku: String = "",
    val gambarUri: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val kondisi: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CekBarangScreen(
    locationId: String? = null,
    onTransfer: (EquipmentUi) -> Unit = {},
    onEdit: (EquipmentUi) -> Unit = {},
    history: (EquipmentUi) -> Unit = {}
) {
    val navController = rememberNavController()

    val context = LocalContext.current
    var barangList by remember { mutableStateOf<List<EquipmentUi>>(emptyList()) }
    var lokasiList by remember { mutableStateOf<List<Location>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var kategoriList by remember { mutableStateOf(listOf("Semua")) }
    var selectedKategori by remember { mutableStateOf("Semua") }
    var selectedBarang by remember { mutableStateOf<EquipmentUi?>(null) }
    var showKategoriDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(locationId) {
        loading = true
        try {
            val lokasiRepo = HomeRepositoryImpl()
            lokasiList = lokasiRepo.getLocations()
            val result = EquipmentRepository.getAllEquipments()
            barangList = if (locationId == null) {
                result.map { eq ->
                    val lokasiNama = lokasiList.find { it.id == eq.lokasiId }?.name ?: "-"
                    EquipmentUi(
                        id = eq.id,
                        nama = eq.nama,
                        deskripsi = eq.deskripsi,
                        kategori = eq.kategori,
                        lokasiId = eq.lokasiId,
                        lokasiNama = lokasiNama,
                        gambarUri = eq.gambarUri,
                        createdAt = eq.createdAt?.toDate()?.toString() ?: "",
                        updatedAt = eq.updatedAt?.toDate()?.toString() ?: "",
                        kondisi = eq.kondisi ?: "",
                        sku = eq.sku
                    )
                }
            } else {
                result.filter { it.lokasiId == locationId }.map { eq ->
                    val lokasiNama = lokasiList.find { it.id == eq.lokasiId }?.name ?: "-"
                    EquipmentUi(
                        id = eq.id,
                        nama = eq.nama,
                        deskripsi = eq.deskripsi,
                        kategori = eq.kategori,
                        lokasiId = eq.lokasiId,
                        lokasiNama = lokasiNama,
                        gambarUri = eq.gambarUri,
                        createdAt = eq.createdAt?.toDate()?.toString() ?: "",
                        updatedAt = eq.updatedAt?.toDate()?.toString() ?: "",
                        kondisi = eq.kondisi ?: "",
                        sku = eq.sku
                    )
                }
            }
            val categories = EquipmentRepository.getAllCategories()
            kategoriList = mutableListOf("Semua").apply { addAll(categories) }
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
        var deleteConfirmationText by remember { mutableStateOf("") }
        var showDeleteDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedBarang = null },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    if (selectedBarang!!.gambarUri.isNotBlank()) {
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
                    Text("Lokasi: ${selectedBarang!!.lokasiNama}", style = MaterialTheme.typography.bodyMedium)
                    Text("SKU: ${selectedBarang!!.sku}", style = MaterialTheme.typography.bodyMedium)
                    Text("Kondisi: " + (selectedBarang!!.kondisi.ifBlank { "Tidak diketahui" }), style = MaterialTheme.typography.bodyMedium)
                    if (selectedBarang!!.deskripsi.isNotBlank()) {
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hapus Item", color = Color.White)
                    }
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
                    Button(
                        onClick = {
                            onEdit(selectedBarang!!)
                            selectedBarang = null // Close dialog after navigation
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Edit Item", color = Color.White)
                    }
                    Button(
                        onClick = {
                            history(selectedBarang!!)
                            selectedBarang = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Item History", color = Color.White)
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { selectedBarang = null }) {
                        Text("Tutup", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text("Konfirmasi Penghapusan", style = MaterialTheme.typography.titleMedium)
                },
                text = {
                    Column {
                        Text("Ketik HAPUS untuk menghapus item.")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = deleteConfirmationText,
                            onValueChange = { deleteConfirmationText = it },
                            placeholder = { Text("HAPUS") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (deleteConfirmationText == "HAPUS") {
                                // Hapus item dari daftar lokal
                                barangList = barangList.filter { it.id != selectedBarang?.id }

                                // Hapus item dari Firestore
                                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                selectedBarang?.let { barang ->
                                    db.collection("equipments").document(barang.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Gagal menghapus item : ${e.message}", Toast.LENGTH_SHORT).show()
                                        }

                                    db.collection("items").whereEqualTo("sku", barang.sku).get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot.documents) {
                                                document.reference.delete()
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "SKU berhasil dihapus", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(context, "Gagal menghapus SKU : ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Gagal mencari item dengan SKU : ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }

                                showDeleteDialog = false
                                selectedBarang = null
                            } else {
                                Toast.makeText(context, "Input tidak valid", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Konfirmasi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
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

        bottomBar = {
            // ...bottom navigation sudah ada di layout utama...
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .navigationBarsPadding(), // Tambahkan ini agar konten tidak tertutup navigation bar
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
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
                // UI: Button to show all categories in a modal
                Box(modifier = Modifier.padding(8.dp)) {
                    Button(onClick = { showKategoriDropdown = true }) {
                        Text("Kategori: $selectedKategori")
                    }
                    if (showKategoriDropdown) {
                        ModalBottomSheet(onDismissRequest = { showKategoriDropdown = false }) {
                            Column {
                                kategoriList.forEach { kategori ->
                                    TextButton(onClick = {
                                        selectedKategori = kategori
                                        showKategoriDropdown = false
                                    }) {
                                        Text(kategori)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                if (loading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (filteredBarang.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Tidak ada data barang.", color = Color.Gray)
                    }
                }
            }
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
            item { Spacer(modifier = Modifier.height(64.dp)) } // Ganti dari 32.dp ke 64.dp agar ada ruang ekstra di bawah
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPopupDetailItem() {
    CekBarangScreen(
        onTransfer = {},
        onEdit = {}
    )
}