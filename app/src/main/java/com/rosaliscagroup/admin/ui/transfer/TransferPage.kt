package com.rosaliscagroup.admin.ui.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MenuAnchorType
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.ui.item.EquipmentUi
import java.util.Calendar
import android.app.DatePickerDialog
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferItem(
    equipment: EquipmentUi,
    onSimpan: (String, String, String, String, String) -> Unit, // id barang, lokasi baru, pengirim, penerima, tanggal kirim
    onCancel: (() -> Unit)? = null,
    userName: String,
    viewModel: com.rosaliscagroup.admin.ui.home.HomeViewModel,
) {
    var lokasiId by remember { mutableStateOf(equipment.lokasiId) }
    var lokasiList by remember { mutableStateOf(listOf<Location>()) }
    var lokasiLoading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    var lokasiExpanded by remember { mutableStateOf(false) }
    var pengirim by remember { mutableStateOf("") }
    var penerima by remember { mutableStateOf("") }
    var tanggalKirim by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    if (tanggalKirim.isEmpty()) {
        val calendar = Calendar.getInstance()
        tanggalKirim = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
    }

    if (showDatePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                tanggalKirim = "$dayOfMonth-${month + 1}-$year"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
        }.show()
    }

    // Ensure lokasiList is populated correctly and handle errors
    LaunchedEffect(Unit) {
        lokasiLoading = true
        try {
            val lokasiRepo = HomeRepositoryImpl()
            val fetchedLocations = lokasiRepo.getLocations()
            if (fetchedLocations.isNotEmpty()) {
                lokasiList = fetchedLocations
                Log.d("TransferPage", "Locations fetched: ${lokasiList.map { it.name }}")
            } else {
                Log.e("TransferPage", "No locations found")
            }
        } catch (e: Exception) {
            Log.e("TransferPage", "Error fetching locations: ${e.message}")
        }
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
            onExpandedChange = {
                lokasiExpanded = !lokasiExpanded
            }
        ) {
            OutlinedTextField(
                value = lokasiList.find { it.id == lokasiId }?.name ?: "",
                onValueChange = {},
                label = { Text("Lokasi Baru") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = lokasiExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = lokasiExpanded,
                onDismissRequest = {
                    lokasiExpanded = false
                }
            ) {
                if (lokasiList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No locations available") },
                        onClick = {}
                    )
                } else {
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
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Pengirim (editable)
        OutlinedTextField(
            value = pengirim,
            onValueChange = { pengirim = it },
            label = { Text("Pengirim") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            isError = pengirim.isBlank()
        )
        if (pengirim.isBlank()) {
            Text("Nama pengirim harus diisi", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Penerima (editable)
        OutlinedTextField(
            value = penerima,
            onValueChange = { penerima = it },
            label = { Text("Penerima") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = penerima.isBlank()
        )
        if (penerima.isBlank()) {
            Text("Nama penerima harus diisi", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Tanggal Kirim (editable)
        OutlinedTextField(
            value = tanggalKirim,
            onValueChange = {},
            label = { Text("Tanggal Kirim") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Set", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
                    onSimpan(equipment.id, lokasiId, pengirim, penerima, tanggalKirim)
                    viewModel.addTransferActivity(
                        equipmentName = equipment.nama,
                        equipmentId = equipment.id,
                        equipmentCategory = equipment.kategori,
                        userName = userName,
                        fromLocation = lokasiList.find { it.id == equipment.lokasiId }?.name ?: "",
                        toLocation = lokasiList.find { it.id == lokasiId }?.name ?: "",
                        sender = pengirim,
                        receiver = penerima,
                        sendDate = tanggalKirim
                    )
                },
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

@Preview(showBackground = true)
@Composable
fun TransferItemPreview() {
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<com.rosaliscagroup.admin.ui.home.HomeViewModel>()
    TransferItem(
        equipment = EquipmentUi(
            id = "1",
            nama = "Excavator",
            deskripsi = "Alat berat untuk menggali",
            kategori = "Alat Berat",
            lokasiId = "Lokasi A",
            sku = "SKU123"
        ),
        onSimpan = { _, _, _, _, _ -> },
        onCancel = {},
        userName = "John Doe",
        viewModel = viewModel
    )
}

