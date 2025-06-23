package com.rosaliscagroup.admin.ui.item

import android.net.Uri
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import androidx.activity.compose.BackHandler
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
import kotlinx.coroutines.launch
import com.rosaliscagroup.admin.data.SkuRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import com.rosaliscagroup.admin.repository.EquipmentRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahItem(
    onSimpan: (String, String, String, String, Uri, String) -> Unit,
    onCancel: (() -> Unit)? = null, // Add onCancel callback
    onShowNavbarChange: ((Boolean) -> Unit)? = null // Add callback to control Navbar
) {
    val context = LocalContext.current
    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var gambarError by remember { mutableStateOf(false) }
    var namaError by remember { mutableStateOf(false) }
    var deskripsiError by remember { mutableStateOf(false) }
    var kategoriError by remember { mutableStateOf(false) }
    var lokasiError by remember { mutableStateOf(false) }
    var isNamaFocused by remember { mutableStateOf(false) }
    var isDeskripsiFocused by remember { mutableStateOf(false) }
    var isKategoriFocused by remember { mutableStateOf(false) }
    var isLokasiFocused by remember { mutableStateOf(false) }
    var lokasiExpanded by remember { mutableStateOf(false) }
    var lokasiList by remember { mutableStateOf(listOf<Location>()) }
    var lokasiLoading by remember { mutableStateOf(true) }
    var sku by remember { mutableStateOf("") }
    var skuLoading by remember { mutableStateOf(false) }
    var skuError by remember { mutableStateOf(false) }
    var skuJob by remember { mutableStateOf<Job?>(null) }
    var saveProgress by remember { mutableStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }
    var saveResult by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val kategoriOptions = listOf(
        "Alat Berat",
        "Generator",
        "Alat Personel",
        "Alat Tambahan",
        "dan lain-lain"
    )
    var kategoriExpanded by remember { mutableStateOf(false) }

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

    // Fetch lokasi from Firestore
    LaunchedEffect(Unit) {
        lokasiLoading = true
        val repo = HomeRepositoryImpl()
        try {
            lokasiList = repo.getLocations()
        } catch (_: Exception) {
            lokasiList = emptyList()
        }
        lokasiLoading = false
    }

    // Hide Navbar when this page is active
    LaunchedEffect(Unit) {
        onShowNavbarChange?.invoke(false)
    }

    // Always show navbar again when leaving this page (more responsive)
    DisposableEffect(Unit) {
        val showNavbar = {
            onShowNavbarChange?.invoke(true)
        }
        onDispose {
            showNavbar()
        }
    }

    // Also show navbar immediately after any back/cancel action
    fun handleBackAndShowNavbar() {
        onShowNavbarChange?.invoke(true)
        onCancel?.invoke()
    }

    // Handle back press to release SKU if reserved
    BackHandler {
        if (sku.isNotBlank()) {
            coroutineScope.launch {
                SkuRepository.releaseSku(sku)
                skuJob?.cancel()
                sku = ""
                handleBackAndShowNavbar()
            }
        } else {
            handleBackAndShowNavbar()
        }
    }

    // Snackbar for save result
    LaunchedEffect(saveResult) {
        saveResult?.let {
            snackbarHostState.showSnackbar(it)
            saveResult = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA)),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                                        coroutineScope.launch {
                                            if (sku.isNotBlank()) {
                                                try {
                                                    SkuRepository.releaseSku(sku)
                                                } catch (_: Exception) {}
                                                skuJob?.cancel()
                                                sku = ""
                                            }
                                            kategori = option
                                            kategoriExpanded = false
                                            kategoriError = false
                                            // Generate and reserve SKU when category changes
                                            skuLoading = true
                                            skuError = false
                                            sku = ""
                                            skuJob?.cancel()
                                            try {
                                                val generatedSku = SkuRepository.generateAndReserveSku(option)
                                                if (generatedSku != null) {
                                                    sku = generatedSku
                                                    skuLoading = false
                                                    // Start timeout job (e.g., 5 minutes)
                                                    skuJob = launch {
                                                        delay(5 * 60 * 1000)
                                                        SkuRepository.releaseSku(generatedSku)
                                                        sku = ""
                                                    }
                                                } else {
                                                    skuError = true
                                                    skuLoading = false
                                                }
                                            } catch (e: Exception) {
                                                skuError = true
                                                skuLoading = false
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    // SKU read-only field
                    if (skuLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (sku.isNotBlank()) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("SKU (Otomatis)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                unfocusedBorderColor = Color(0xFF757575),
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                    }
                    if (skuError) {
                        Text("Gagal membuat SKU unik, coba lagi.", color = Color.Red, fontSize = 12.sp)
                    }
                    ExposedDropdownMenuBox(
                        expanded = lokasiExpanded,
                        onExpandedChange = { lokasiExpanded = !lokasiExpanded }
                    ) {
                        OutlinedTextField(
                            value = lokasiList.find { it.id == lokasi }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Lokasi", color = if (isLokasiFocused) Color(0xFF9CA3AF) else Color.Unspecified) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = lokasiExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .onFocusChanged { focusState ->
                                    isLokasiFocused = focusState.isFocused
                                },
                            isError = lokasiError,
                            enabled = !lokasiLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                cursorColor = if (isLokasiFocused) Color(0xFF9CA3AF) else Color.Unspecified,
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336),
                                focusedContainerColor = Color(0x1E88E5FF),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = lokasiExpanded,
                            onDismissRequest = { lokasiExpanded = false },
                            modifier = Modifier.background(Color(0xFFF5F5F5))
                        ) {
                            lokasiList.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc.name, color = Color(0xFF757575)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        lokasi = loc.id
                                        lokasiExpanded = false
                                        lokasiError = false
                                    }
                                )
                            }
                        }
                    }
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
                            gambarError = gambarUri == null
                            if (!namaError && !deskripsiError && !kategoriError && !lokasiError && !gambarError && sku.isNotBlank()) {
                                coroutineScope.launch {
                                    isSaving = true
                                    saveProgress = 0
                                    try {
                                        EquipmentRepository.addEquipmentWithImageUrl(
                                            context = context,
                                            nama = nama,
                                            deskripsi = deskripsi,
                                            kategori = kategori,
                                            lokasiId = lokasi,
                                            gambarUri = gambarUri!!,
                                            sku = sku,
                                            onProgress = { progress ->
                                                saveProgress = (progress * 100).toInt()
                                            }
                                        )
                                        SkuRepository.confirmSku(sku, mapOf(
                                            "nama" to nama,
                                            "deskripsi" to deskripsi,
                                            "kategori" to kategori,
                                            "lokasi" to lokasi,
                                            "sku" to sku,
                                            "gambarUri" to (gambarUri?.toString() ?: "")
                                        ))
                                        skuJob?.cancel()
                                        onSimpan(nama, deskripsi, kategori, lokasi, gambarUri!!, sku)
                                        saveResult = "Berhasil disimpan"
                                        isSaving = false
                                        handleBackAndShowNavbar()
                                    } catch (e: Exception) {
                                        saveResult = "Gagal menyimpan: ${e.localizedMessage}"
                                        isSaving = false
                                    }
                                }
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
        // Progress Dialog
        if (isSaving) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                title = { Text("Menyimpan...") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(progress = { saveProgress / 100f }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${saveProgress}%")
                    }
                }
            )
        }
        // Snackbar Host
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TambahItemPreview() {
    TambahItem(onSimpan = { nama, deskripsi, kategori, lokasi, gambarUri, sku ->
        // Preview: do nothing
    }, onShowNavbarChange = {})
}
