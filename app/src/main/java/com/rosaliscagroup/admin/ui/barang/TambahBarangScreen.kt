package com.hadiyarajesh.composetemplate.ui.barang

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahBarangScreen(
    onSimpan: (String, String, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current

    var namaBarang by remember { mutableStateOf("") }
    var kategoriBarang by remember { mutableStateOf("") }
    var kondisiExpanded by remember { mutableStateOf(false) }
    var kondisiSelected by remember { mutableStateOf("Pilih Kondisi") }
    val kondisiOptions = listOf("Baik", "Perlu Perbaikan", "Rusak")
    var labtekId by remember { mutableStateOf("") }
    var pengelolaId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var tanggalMasuk by remember { mutableStateOf("") }

    // State untuk DatePicker
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Format tanggal
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val viewModel: BarangViewModel = hiltViewModel()
    val isLoading by viewModel.isTambahBarangLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Tambah Barang")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1E88E5),
                    titleContentColor = Color.White
                )
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Softer background color
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(
                    top = innerPadding.calculateTopPadding(), // Respect TopAppBar
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Slightly higher elevation
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp), // Increased spacing
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = namaBarang,
                        onValueChange = { namaBarang = it },
                        label = { Text("Nama Barang") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = namaBarang.isBlank() && status.isNotBlank() // Error if empty after interaction
                    )

                    OutlinedTextField(
                        value = kategoriBarang,
                        onValueChange = { kategoriBarang = it },
                        label = { Text("Kategori Barang") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = kategoriBarang.isBlank() && status.isNotBlank()
                    )

                    ExposedDropdownMenuBox(
                        expanded = kondisiExpanded,
                        onExpandedChange = { kondisiExpanded = !kondisiExpanded }
                    ) {
                        OutlinedTextField(
                            value = kondisiSelected,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kondisi Barang") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .menuAnchor(), // Tambahkan menuAnchor agar dropdown bisa dibuka
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336)
                            ),
                            isError = kondisiSelected == "Pilih Kondisi" && status.isNotBlank()
                        )
                        ExposedDropdownMenu(
                            expanded = kondisiExpanded,
                            onDismissRequest = { kondisiExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            kondisiOptions.forEach { opsi ->
                                DropdownMenuItem(
                                    text = { Text(opsi) },
                                    onClick = {
                                        kondisiSelected = opsi
                                        kondisiExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = labtekId,
                        onValueChange = { labtekId = it },
                        label = { Text("Labtek ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = labtekId.isBlank() && status.isNotBlank()
                    )

                    OutlinedTextField(
                        value = pengelolaId,
                        onValueChange = { pengelolaId = it },
                        label = { Text("Pengelola ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = pengelolaId.isBlank() && status.isNotBlank()
                    )

                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = status.isBlank() && namaBarang.isNotBlank()
                    )

                    OutlinedTextField(
                        value = tanggalMasuk,
                        onValueChange = { /* Tidak diizinkan input manual */ },
                        label = { Text("Tanggal Masuk") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Tanggal")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336)
                        ),
                        isError = tanggalMasuk.isBlank() && status.isNotBlank()
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            tanggalMasuk = dateFormatter.format(Date(millis))
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            },
                            modifier = Modifier.background(Color(0xFFF5F7FA)) // Consistent dialog background
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Increased spacing

                    Button(
                        onClick = {
                            if (namaBarang.isNotBlank() && kategoriBarang.isNotBlank() && kondisiSelected != "Pilih Kondisi" && labtekId.isNotBlank() && pengelolaId.isNotBlank() && status.isNotBlank() && tanggalMasuk.isNotBlank()) {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val pengelolaEmail = currentUser?.email ?: pengelolaId
                                val barang = BarangLab(
                                    nama = namaBarang,
                                    kategori = kategoriBarang,
                                    kondisi = kondisiSelected,
                                    labtekId = labtekId,
                                    pengelolaId = pengelolaEmail,
                                    status = status,
                                    tanggalMasuk = tanggalMasuk,
                                    ownerUid = currentUser?.uid ?: ""
                                )
                                viewModel.tambahBarang(barang,
                                    onSelesai = {
                                        Toast.makeText(context, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                        // Reset form jika perlu
                                    },
                                    onError = { e ->
                                        Toast.makeText(context, "Gagal menambah barang: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp), // Slightly taller button
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                "Simpan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold // Bolder text
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TambahBarangScreenPreview() {
    TambahBarangScreen(
        onSimpan = { _, _, _, _, _, _, _ -> }
    )
}

