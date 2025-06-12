package com.hadiyarajesh.composetemplate.ui.barang

import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
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
//import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
// Hapus import FirebaseAuth jika error unresolved reference
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BarangTable(
    barangList: List<com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab>,
    onEdit: (com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab) -> Unit = {},
    onDelete: (com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab) -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFE3F2FD) // Biru muda untuk background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (barangList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data barang.", color = Color(0xFF1565C0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                                Text(barang.nama, style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF000000), fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Kategori: " + (barang.kategori.trim().ifBlank { "Tidak diketahui" }.replaceFirstChar { it.uppercase() }), color = Color(0xFF000000))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Kondisi: " + (barang.kondisi.trim().ifBlank { "Tidak diketahui" }.replaceFirstChar { it.uppercase() }), color = Color(0xFF000000))
                                Text("Status: " + (barang.status.trim().ifBlank { "Tidak diketahui" }.replaceFirstChar { it.uppercase() }), color = Color(0xFF000000))
                                Text("Tanggal Masuk: ${barang.tanggalMasuk}", color = Color(0xFF000000))
                                Text("ID: ${barang.id}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
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
    val viewModel: BarangViewModel = hiltViewModel()
    val barangList by viewModel.barangList.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Gunakan state Compose untuk currentUser agar bisa trigger recomposition
    val currentUserState = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { authListener ->
            currentUserState.value = authListener.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }
    val currentUser = currentUserState.value

    // Tambahkan debug Toast untuk melihat status currentUser
    LaunchedEffect(currentUser) {
        Toast.makeText(context, "currentUser: ${currentUser?.email ?: "null"}", Toast.LENGTH_LONG).show()
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Silakan login terlebih dahulu.")
        }
        return
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("User ID: ${currentUser.uid}", color = Color.Gray)
        Text("Email: ${currentUser.email}", color = Color.Gray)
    }

    // Debug: Tampilkan jumlah barang yang diambil dari database
    LaunchedEffect(barangList) {
        Toast.makeText(context, "Jumlah barang: ${barangList.size}", Toast.LENGTH_SHORT).show()
    }

    // Semua user bisa melihat semua barang tanpa filter
    val filteredList = barangList

    // State untuk edit dan delete barang
    val barangToDeleteState = remember { mutableStateOf<com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab?>(null) }
    val barangToEditState = remember { mutableStateOf<com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab?>(null) }
    val barangToDelete = barangToDeleteState.value
    val setBarangToDelete = { b: com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab? -> barangToDeleteState.value = b }
    val barangToEdit = barangToEditState.value
    val setBarangToEdit = { b: com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab? -> barangToEditState.value = b }

    // Tampilkan tabel barang
    BarangTable(
        barangList = filteredList,
        onEdit = setBarangToEdit,
        onDelete = setBarangToDelete
    )

    // Dialog konfirmasi hapus
    if (barangToDelete != null) {
        AlertDialog(
            onDismissRequest = { setBarangToDelete(null) },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus barang '${barangToDelete.nama}'? (id: ${barangToDelete.id})") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        Toast.makeText(context, "Memulai hapus id: ${barangToDelete.id}", Toast.LENGTH_SHORT).show()
                        try {
                            BarangRepository.hapusBarang(barangToDelete)
                            Toast.makeText(context, "Barang berhasil dihapus (id: ${barangToDelete.id})", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal menghapus barang: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                        setBarangToDelete(null)
                    }
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { setBarangToDelete(null) }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog edit dengan form
    if (barangToEdit != null) {
        var nama by remember(barangToEdit) { mutableStateOf(barangToEdit.nama) }
        var kategori by remember(barangToEdit) { mutableStateOf(barangToEdit.kategori) }
        var kondisi by remember(barangToEdit) { mutableStateOf(barangToEdit.kondisi) }
        var status by remember(barangToEdit) { mutableStateOf(barangToEdit.status) }

        AlertDialog(
            onDismissRequest = { setBarangToEdit(null) },
            title = { Text("Edit Barang") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama Barang") }
                    )
                    OutlinedTextField(
                        value = kategori,
                        onValueChange = { kategori = it },
                        label = { Text("Kategori") }
                    )
                    OutlinedTextField(
                        value = kondisi,
                        onValueChange = { kondisi = it },
                        label = { Text("Kondisi") }
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        try {
                            val updatedBarang = barangToEdit.copy(
                                nama = nama,
                                kategori = kategori,
                                kondisi = kondisi,
                                status = status
                            )
                            BarangRepository.updateBarang(updatedBarang)
                            Toast.makeText(context, "Barang berhasil diupdate", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal update: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                        setBarangToEdit(null)
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { setBarangToEdit(null) }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BarangTablePreview() {
    BarangTable(
        barangList = listOf(
            com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab("Laptop", "Elektronik", "Baik", "LT01", "PG01", "Aktif", "18/05/2025"),
            com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab("Proyektor", "Elektronik", "Perlu Perbaikan", "LT02", "PG02", "Nonaktif", "10/04/2024"),
            com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab("Meja", "Furnitur", "Baik", "LT03", "PG03", "Aktif", "05/03/2023")
        )
    )
}
