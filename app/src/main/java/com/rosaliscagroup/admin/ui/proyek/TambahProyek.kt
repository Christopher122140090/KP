package com.rosaliscagroup.admin.ui.proyek

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahProyek(
    onSimpan: (String, String) -> Unit
) {
    var namaProyek by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var namaProyekError by remember { mutableStateOf(false) }
    var lokasiError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tambah Proyek",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                OutlinedTextField(
                    value = namaProyek,
                    onValueChange = {
                        namaProyek = it
                        namaProyekError = false
                    },
                    label = { Text("Nama Proyek", color = Color(0xFF9CA3AF)) },
                    isError = namaProyekError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E88E5),
                        unfocusedBorderColor = Color(0xFF757575),
                        errorBorderColor = Color(0xFFF44336),
                        focusedContainerColor = Color(0x1E88E5FF),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
                if (namaProyekError) {
                    Text("Nama Proyek wajib diisi", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = {
                        lokasi = it
                        lokasiError = false
                    },
                    label = { Text("Lokasi", color = Color(0xFF9CA3AF)) },
                    isError = lokasiError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E88E5),
                        unfocusedBorderColor = Color(0xFF757575),
                        errorBorderColor = Color(0xFFF44336),
                        focusedContainerColor = Color(0x1E88E5FF),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
                if (lokasiError) {
                    Text("Lokasi wajib diisi", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        namaProyekError = namaProyek.isBlank()
                        lokasiError = lokasi.isBlank()
                        if (!namaProyekError && !lokasiError) {
                            onSimpan(namaProyek, lokasi)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Simpan", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TambahProyekPreview() {
    TambahProyek { _, _ -> }
}
