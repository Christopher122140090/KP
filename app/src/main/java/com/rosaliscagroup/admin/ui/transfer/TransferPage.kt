package com.rosaliscagroup.admin.ui.transfer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rosaliscagroup.admin.ui.transfer.TransferViewModel

data class LokasiProyek(
    val id: String,
    val nama: String,
    val alamat: String,
    val items: List<ItemBarang>
)

data class ItemBarang(
    val id: String,
    val nama: String,
    val kategori: String
)

@Composable
fun TransferPage(
    viewModel: TransferViewModel = viewModel(TransferViewModel::class.java)
) {
    val lokasiList = viewModel.lokasiList.collectAsStateWithLifecycle().value
    val selectedLokasi = remember { mutableStateOf<LokasiProyek?>(null) }
    val selectedKategori = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        Text(
            text = if (lokasiList.isEmpty()) "Belum ada lokasi terdaftar" else "Daftar Lokasi Proyek",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (lokasiList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Data lokasi kosong.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(lokasiList) { lokasi ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLokasi.value = lokasi; selectedKategori.value = null },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = lokasi.nama,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Alamat: ${lokasi.alamat}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    // Pop Up Dialog for detail
    if (selectedLokasi.value != null) {
        val kategoriList = selectedLokasi.value!!.items.map { it.kategori }.distinct()
        AlertDialog(
            onDismissRequest = { selectedLokasi.value = null; selectedKategori.value = null },
            title = {
                Text(selectedLokasi.value!!.nama, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Alamat: ${selectedLokasi.value!!.alamat}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pilih Kategori Barang:", fontWeight = FontWeight.SemiBold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        kategoriList.forEach { kategori: String ->
                            Button(
                                onClick = { selectedKategori.value = kategori },
                                colors = if (selectedKategori.value == kategori) ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1976D2)
                                ) else ButtonDefaults.buttonColors()
                            ) {
                                Text(kategori)
                            }
                        }
                    }
                    if (selectedKategori.value != null) {
                        val filteredItems = selectedLokasi.value!!.items.filter { it.kategori == selectedKategori.value }
                        Text("Barang pada kategori '", fontWeight = FontWeight.SemiBold)
                        Text(selectedKategori.value!! + "':")
                        if (filteredItems.isEmpty()) {
                            Text(
                                "Tidak ada barang pada kategori ini.",
                                color = Color.Gray
                            )
                        } else {
                            Column {
                                filteredItems.forEach { item: ItemBarang ->
                                    Text("- ${item.nama}")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedLokasi.value = null; selectedKategori.value = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransferPagePreview() {
    TransferPage()
}
