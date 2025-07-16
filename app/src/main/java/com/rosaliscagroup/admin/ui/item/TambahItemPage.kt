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
import androidx.compose.foundation.border
import kotlinx.coroutines.launch
import com.rosaliscagroup.admin.data.SkuRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.HomeRepositoryImpl
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
        "Mesin 1000 Herrenknecht",
        "Generator",
        "Alat Personel",
        "Alat Tambahan",
        "Mesin 1000 Iseki",
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

    // State untuk daftar kategori global
    var kategoriList by remember { mutableStateOf(listOf<String>()) }
    var kategoriLoading by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()

    // Ambil kategori dari Firestore saat halaman dibuka
    LaunchedEffect(Unit) {
        kategoriLoading = true
        coroutineScope.launch {
            try {
                val snapshot = db.collection("categories").get().await()
                kategoriList = snapshot.documents.mapNotNull { doc -> doc.getString("name") }.distinct().sorted()
            } catch (_: Exception) {
                kategoriList = kategoriOptions // fallback
            }
            kategoriLoading = false
        }
    }

    // Fungsi untuk menambah kategori baru ke Firestore
    suspend fun tambahKategoriBaru(namaKategori: String) {
        if (namaKategori.isNotBlank() && !kategoriList.contains(namaKategori)) {
            db.collection("categories").add(mapOf("name" to namaKategori)).await()
            kategoriList = (kategoriList + namaKategori).distinct().sorted()
        }
    }

    // State untuk dialog input kategori manual
    var showKategoriDialog by remember { mutableStateOf(false) }
    var kategoriBaruInput by remember { mutableStateOf("") }
    var kategoriBaruError by remember { mutableStateOf(false) }

    // Fungsi dialog input kategori manual
    @Composable
    fun KategoriInputDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Tambah Kategori Baru") },
            text = {
                Column {
                    OutlinedTextField(
                        value = kategoriBaruInput,
                        onValueChange = {
                            kategoriBaruInput = it
                            kategoriBaruError = false
                        },
                        label = { Text("Nama Kategori") },
                        isError = kategoriBaruError,
                        singleLine = true
                    )
                    if (kategoriBaruError) {
                        Text("Kategori tidak boleh kosong atau sudah ada", color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (kategoriBaruInput.isBlank() || kategoriList.contains(kategoriBaruInput.trim())) {
                        kategoriBaruError = true
                    } else {
                        onConfirm(kategoriBaruInput.trim())
                        kategoriBaruInput = ""
                        kategoriBaruError = false
                    }
                }) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    onDismiss()
                    kategoriBaruInput = ""
                    kategoriBaruError = false
                }) {
                    Text("Batal")
                }
            }
        )
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

    // Kondisi alat
    val kondisiOptions = listOf("Baik", "Rusak", "Maintenance")
    var kondisi by remember { mutableStateOf("") }
    var kondisiExpanded by remember { mutableStateOf(false) }
    var kondisiError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA)),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp, // Jarak top lebih besar seperti sebelumnya
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Hilangkan shadow
                colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Hilangkan warna dasar
                border = null // Hilangkan border
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image Upload Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (gambarUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0x33000000), // abu-abu gelap transparan (tersamarkan)
                                        shape = RoundedCornerShape(8.dp)
                                    )
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0x33000000), // abu-abu gelap transparan (tersamarkan)
                                        shape = RoundedCornerShape(8.dp)
                                    )
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
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Gambar", tint = Color(0xFF9CA3AF), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tap untuk menambah foto",
                                    fontSize = 14.sp, // Adjusted font size
                                    color = Color(0xFF9CA3AF),
                                )
                                Text(
                                    "JPG, PNG maksimal 5MB",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                        if (gambarError) {
                            Text("Gambar wajib diupload", color = Color.Red, fontSize = 12.sp)
                        }
                    }

                    // Label di atas kiri kolom Nama
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nama Barang",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = nama,
                            onValueChange = {
                                nama = it
                                namaError = false
                            },
                            label = null,
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
                    }

                    // Label di atas kiri kolom Kategori
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Kategori",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = kategoriExpanded,
                            onExpandedChange = { kategoriExpanded = !kategoriExpanded }
                        ) {
                            OutlinedTextField(
                                value = kategori,
                                onValueChange = {},
                                readOnly = true,
                                label = null,
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
                                kategoriList.forEach { option ->
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
                                // Opsi input manual kategori baru
                                DropdownMenuItem(
                                    text = { Text("+ Tambah kategori baru", color = Color(0xFF1976D2)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        kategoriExpanded = false
                                        showKategoriDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // SKU read-only field
                    if (skuLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    // Label di atas kiri kolom SKU
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SKU:",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (sku.isNotBlank()) sku else "-",
                            onValueChange = {},
                            readOnly = true,
                            label = null,
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

                    // Label di atas kiri kolom Deskripsi
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Deskripsi",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = {
                                deskripsi = it
                                deskripsiError = false
                            },
                            label = null,
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
                    }

                    // Label di atas kiri kolom Lokasi
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Lokasi",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = lokasiExpanded,
                            onExpandedChange = { lokasiExpanded = !lokasiExpanded }
                        ) {
                            OutlinedTextField(
                                value = lokasiList.find { it.id == lokasi }?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = null,
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
                    }

                    // Kondisi Alat
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Kondisi Alat",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = kondisiExpanded,
                            onExpandedChange = { kondisiExpanded = !kondisiExpanded }
                        ) {
                            OutlinedTextField(
                                value = kondisi,
                                onValueChange = {},
                                readOnly = true,
                                label = null,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = kondisiExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .onFocusChanged { focusState ->
                                        // Update fokus state untuk kondisi
                                        isLokasiFocused = focusState.isFocused
                                    },
                                isError = kondisiError,
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
                                expanded = kondisiExpanded,
                                onDismissRequest = { kondisiExpanded = false },
                                modifier = Modifier.background(Color(0xFFF5F5F5))
                            ) {
                                kondisiOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = Color(0xFF757575)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            kondisi = option
                                            kondisiExpanded = false
                                            kondisiError = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Informasi Sistem
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Informasi Sistem", fontSize = 16.sp, color = Color(0xFF374151))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Dibuat", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                Text("24 Jun 2025, 11:54", fontSize = 14.sp, color = Color(0xFF374151)) // Placeholder date
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Diperbarui", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                Text("24 Jun 2025, 11:54", fontSize = 14.sp, color = Color(0xFF374151)) // Placeholder date
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { handleBackAndShowNavbar() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2563EB)))
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                namaError = nama.isBlank()
                                deskripsiError = deskripsi.isBlank()
                                kategoriError = kategori.isBlank()
                                lokasiError = lokasi.isBlank()
                                gambarError = gambarUri == null
                                kondisiError = kondisi.isBlank()
                                if (!namaError && !deskripsiError && !kategoriError && !lokasiError && !gambarError && sku.isNotBlank() && !kondisiError) {
                                    coroutineScope.launch {
                                        isSaving = true
                                        saveProgress = 0
                                        try {
                                            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                                            val namaUser = currentUser?.displayName ?: currentUser?.email ?: "User Tidak Diketahui"
                                            EquipmentRepository.addEquipmentWithImageUrl(
                                                context = context,
                                                nama = nama,
                                                deskripsi = deskripsi,
                                                kategori = kategori,
                                                lokasiId = lokasi,
                                                gambarUri = gambarUri!!,
                                                sku = sku,
                                                onProgress = { progress: Float ->
                                                    saveProgress = (progress * 100).toInt()
                                                },
                                                namaUser = namaUser,
                                                kondisi = kondisi
                                            )
                                            SkuRepository.confirmSku(sku, mapOf(
                                                "nama" to nama,
                                                "deskripsi" to deskripsi,
                                                "kategori" to kategori,
                                                "lokasi" to lokasi,
                                                "sku" to sku,
                                                "gambarUri" to (gambarUri?.toString() ?: ""),
                                                "kondisi" to kondisi
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
                            }
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
    if (showKategoriDialog) {
        KategoriInputDialog(
            onDismiss = { showKategoriDialog = false },
            onConfirm = { kategoriBaru ->
                coroutineScope.launch {
                    tambahKategoriBaru(kategoriBaru)
                    kategori = kategoriBaru
                    showKategoriDialog = false
                }
            }
        )
    }
}
