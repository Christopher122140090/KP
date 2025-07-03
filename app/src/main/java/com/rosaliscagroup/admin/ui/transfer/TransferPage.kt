package com.rosaliscagroup.admin.ui.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.ui.item.EquipmentUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferItem(
    equipment: EquipmentUi, // Tambahkan parameter equipment
    onSimpan: (String, String) -> Unit, // id barang, lokasi baru
    onCancel: (() -> Unit)? = null
) {
    var lokasiId by remember { mutableStateOf(equipment.lokasiId) }
    var lokasiList by remember { mutableStateOf(listOf<Location>()) }
    var lokasiLoading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    var lokasiExpanded by remember { mutableStateOf(false) }

    // Fetch lokasi dari Firestore
    LaunchedEffect(Unit) {
        lokasiLoading = true
        try {
            val lokasiRepo = HomeRepositoryImpl()
            lokasiList = lokasiRepo.getLocations()
        } catch (_: Exception) {}
        lokasiLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text("Transfer Item", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        // Nama (read-only)
        OutlinedTextField(
            value = equipment.nama,
            onValueChange = {},
            label = { Text("Nama") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Kategori (read-only)
        OutlinedTextField(
            value = equipment.kategori,
            onValueChange = {},
            label = { Text("Kategori") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // SKU (read-only)
        OutlinedTextField(
            value = equipment.sku,
            onValueChange = {},
            label = { Text("SKU") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Deskripsi (read-only)
        OutlinedTextField(
            value = equipment.deskripsi,
            onValueChange = {},
            label = { Text("Deskripsi") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Lokasi (editable)
        ExposedDropdownMenuBox(
            expanded = lokasiExpanded,
            onExpandedChange = { lokasiExpanded = !lokasiExpanded }
        ) {
            OutlinedTextField(
                value = lokasiList.find { it.id == lokasiId }?.name ?: "",
                onValueChange = {},
                label = { Text("Lokasi Baru") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lokasiExpanded) }
            )
            ExposedDropdownMenu(
                expanded = lokasiExpanded,
                onDismissRequest = { lokasiExpanded = false }
            ) {
                lokasiList.forEach { l ->
                    DropdownMenuItem(
                        text = { Text(l.name) },
                        onClick = {
                            lokasiId = l.id
                            lokasiExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { onSimpan(equipment.id, lokasiId) },
                enabled = !lokasiLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan Transfer", color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { onCancel?.invoke() }) {
                Text("Batal")
            }
        }
    }
}

// Preview harus berada di paling bawah file, setelah semua fungsi utama
@Preview(showBackground = true)
@Composable
fun TransferItemPreview() {
    TransferItem(
        equipment = EquipmentUi(
            id = "1",
            nama = "Excavator",
            deskripsi = "Alat berat untuk menggali",
            kategori = "Alat Berat",
            lokasiId = "Lokasi A",
            sku = "SKU123"
        ),
        onSimpan = { _, _ -> },
        onCancel = {}
    )
}

