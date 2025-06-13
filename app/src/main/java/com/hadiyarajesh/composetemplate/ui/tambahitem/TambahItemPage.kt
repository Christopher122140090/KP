package com.hadiyarajesh.composetemplate.ui.tambahitem

import android.net.Uri
import android.widget.Toast
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahItem(
    onSimpan: (String, String, String, String, Uri) -> Unit
) {
    val context = LocalContext.current
    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var gambarError by remember { mutableStateOf(false) }
    var namaError by remember { mutableStateOf(false) }
    var deskripsiError by remember { mutableStateOf(false) }
    var kategoriError by remember { mutableStateOf(false) }
    var lokasiError by remember { mutableStateOf(false) }
    var statusError by remember { mutableStateOf(false) }
    var isNamaFocused by remember { mutableStateOf(false) }
    var isDeskripsiFocused by remember { mutableStateOf(false) }
    var isKategoriFocused by remember { mutableStateOf(false) }
    var isStatusFocused by remember { mutableStateOf(false) }
    var isLokasiFocused by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val kategoriOptions = listOf(
        "Alat Berat",
        "Generator",
        "Alat Personel",
        "Alat Tambahan",
        "dan lain-lain"
    )
    var kategoriExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "Baik",
        "Perlu Diperbaiki",
        "Sedang Tidak Digunakan"
    )
    var statusExpanded by remember { mutableStateOf(false) }

    // Launcher untuk chooser intent (galeri/kamera)
    val chooserImageUri = remember { mutableStateOf<Uri?>(null) }
    val chooserLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data ?: chooserImageUri.value
            gambarUri = uri
            gambarError = uri == null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(
                    top = innerPadding.calculateTopPadding() + 92.dp, // Jarak top lebih besar seperti sebelumnya
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = {
                            nama = it
                            namaError = false
                        },
                        label = { Text("Nama", color = if (isNamaFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                        isError = namaError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState: androidx.compose.ui.focus.FocusState ->
                                isNamaFocused = focusState.isFocused
                            },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            cursorColor = if (isNamaFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336),
                            focusedContainerColor = Color(0x1E88E5FF),
                            unfocusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )
                    OutlinedTextField(
                        value = deskripsi,
                        onValueChange = {
                            deskripsi = it
                            deskripsiError = false
                        },
                        label = { Text("Deskripsi", color = if (isDeskripsiFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                        isError = deskripsiError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState: androidx.compose.ui.focus.FocusState ->
                                isDeskripsiFocused = focusState.isFocused
                            },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            cursorColor = if (isDeskripsiFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336),
                            focusedContainerColor = Color(0x1E88E5FF),
                            unfocusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )
                    ExposedDropdownMenuBox(
                        expanded = kategoriExpanded,
                        onExpandedChange = { kategoriExpanded = !kategoriExpanded }
                    ) {
                        OutlinedTextField(
                            value = kategori,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori", color = if (isKategoriFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = kategoriExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .onFocusChanged { focusState: androidx.compose.ui.focus.FocusState ->
                                    isKategoriFocused = focusState.isFocused
                                },
                            isError = kategoriError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                cursorColor = if (isKategoriFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336),
                                focusedContainerColor = Color(0x1E88E5FF),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = kategoriExpanded,
                            onDismissRequest = { kategoriExpanded = false },
                            modifier = Modifier.background(Color(0xFFF5F5F5))
                        ) {
                            kategoriOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color(0xFF757575)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        kategori = option
                                        kategoriExpanded = false
                                        kategoriError = false
                                    }
                                )
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status", color = if (isStatusFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .onFocusChanged { focusState: androidx.compose.ui.focus.FocusState ->
                                    isStatusFocused = focusState.isFocused
                                },
                            isError = statusError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                cursorColor = if (isStatusFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336),
                                focusedContainerColor = Color(0x1E88E5FF),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                            modifier = Modifier.background(Color(0xFFF5F5F5))
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color(0xFF757575)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        status = option
                                        statusExpanded = false
                                        statusError = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = lokasi,
                        onValueChange = {
                            lokasi = it
                            lokasiError = false
                        },
                        label = { Text("Lokasi", color = if (isLokasiFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                        isError = lokasiError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState: androidx.compose.ui.focus.FocusState ->
                                isLokasiFocused = focusState.isFocused
                            },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            cursorColor = if (isLokasiFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                            unfocusedBorderColor = Color(0xFF757575),
                            errorBorderColor = Color(0xFFF44336),
                            focusedContainerColor = Color(0x1E88E5FF),
                            unfocusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (gambarUri != null) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .clickable {
                                        // Buat intent galeri
                                        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                            type = "image/*"
                                        }
                                        // Buat intent kamera
                                        val contentResolver = context.contentResolver
                                        val contentValues = android.content.ContentValues().apply {
                                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                        }
                                        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                        chooserImageUri.value = uri
                                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                                            putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                        }
                                        // Buat chooser
                                        val chooser = Intent.createChooser(galleryIntent, "Pilih Sumber Gambar")
                                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                                        chooserLauncher.launch(chooser)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(gambarUri),
                                    contentDescription = "Gambar dipilih",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    // Buat intent galeri
                                    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                        type = "image/*"
                                    }
                                    // Buat intent kamera
                                    val contentResolver = context.contentResolver
                                    val contentValues = android.content.ContentValues().apply {
                                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                    }
                                    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                    chooserImageUri.value = uri
                                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                                        putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                    }
                                    // Buat chooser
                                    val chooser = Intent.createChooser(galleryIntent, "Pilih Sumber Gambar")
                                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                                    chooserLauncher.launch(chooser)
                                },
                                modifier = Modifier.size(120.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Gambar", tint = Color(0xFF1E88E5), modifier = Modifier.size(48.dp))
                            }
                        }
                        if (gambarError) {
                            Text("Gambar wajib diupload", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = {
                            namaError = nama.isBlank()
                            deskripsiError = deskripsi.isBlank()
                            kategoriError = kategori.isBlank()
                            lokasiError = lokasi.isBlank()
                            statusError = status.isBlank()
                            gambarError = gambarUri == null
                            if (!namaError && !deskripsiError && !kategoriError && !lokasiError && !statusError && !gambarError) {
                                onSimpan(nama, deskripsi, kategori, status, gambarUri!!)
                            } else {
                                Toast.makeText(context, "Mohon lengkapi data yang wajib diisi", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TambahItemPreview() {
    TambahItem(onSimpan = { nama, deskripsi, kategori, status, gambarUri ->
        // Preview: do nothing
    })
}
