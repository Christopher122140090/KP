package com.rosaliscagroup.admin.ui.proyek

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Proyek(
    val id: String = "",
    val nama: String = "",
    val lokasi: String = ""
)

@Composable
fun ViewProyek(
    proyekList: List<Proyek>,
    modifier: Modifier = Modifier,
    navController: NavController? = null, // Tambahkan NavController opsional
    onViewAllClick: (() -> Unit)? = null
) {
    val selectedProyek = remember { mutableStateOf<Proyek?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (proyekList.isEmpty()) "Belum ada lokasi terdaftar" else "Daftar Lokasi",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (onViewAllClick != null && proyekList.isNotEmpty()) {
                TextButton(onClick = onViewAllClick) {
                    Text("Lihat Semua")
                }
            }
        }
        if (proyekList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Data lokasi kosong.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(proyekList) { proyek ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProyek.value = proyek },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = proyek.nama,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Alamat: ${proyek.lokasi}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
    // Pop up dialog untuk detail proyek
    if (selectedProyek.value != null) {
        val proyek = selectedProyek.value!!
        AlertDialog(
            onDismissRequest = { selectedProyek.value = null },
            title = {
                Text(text = "Detail Lokasi Proyek", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("ID: ${proyek.id}")
                    Text("Nama: ${proyek.nama}")
                    Text("Alamat: ${proyek.lokasi}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (proyek.id.isNotBlank() && navController != null) {
                        navController.navigate("proyekItemListPage?lokasi=${proyek.id}&kategori=Semua")
                    }
                    selectedProyek.value = null
                }) {
                    Text("Lihat Item")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ViewProyekPreview() {
    val dummyList = listOf(
        Proyek(id = "1", nama = "Proyek A", lokasi = "Jakarta"),
        Proyek(id = "2", nama = "Proyek B", lokasi = "Bandung")
    )
    ViewProyek(proyekList = dummyList, onViewAllClick = {})
}
