package com.rosaliscagroup.admin.ui.item

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.compose.foundation.border
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahItem(
    navController: NavController,
    onSimpan: (
        param1: Any?,
        param2: Any?,
        param3: Any?,
        param4: Any?,
        param5: Any?,
        param6: Any?
    ) -> Unit = { _, _, _, _, _, _ -> },
    onCancel: () -> Unit = {},
    onShowNavbarChange: (Boolean) -> Unit = {},
    onBack: () -> Unit = {},
    onTambahBarang: () -> Unit = {},
    onTambahLokasi: () -> Unit = {}
) {
    var showTambahBarang by remember { mutableStateOf(false) }
    if (showTambahBarang) {
        TambahBarangPage(
            onSimpan = { showTambahBarang = false },
            onCancel = { showTambahBarang = false }
        )
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tambah Item", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "Pilih Jenis Item",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pilih jenis item yang ingin Anda tambahkan ke dalam proyek konstruksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Kartu Tambahkan Barang
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { showTambahBarang = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Inventory,
                            contentDescription = null,
                            tint = Color(0xFFFF7043),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tambahkan Barang",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tambahkan material, alat, atau peralatan konstruksi ke dalam inventori proyek",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ItemChip(text = "Material")
                            ItemChip(text = "Alat")
                            ItemChip(text = "Equipment")
                        }
                    }
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Kartu Tambahkan Lokasi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate("TambahLokasiPage") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE3F0FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tambahkan Lokasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF23272E))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tambahkan lokasi baru seperti gudang, area kerja, atau titik distribusi material",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ItemChip(text = "Gudang")
                            ItemChip(text = "Area Kerja")
                            ItemChip(text = "Site Office")
                        }
                    }
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = Color(0xFF23272E))
    }
}

@Preview(showBackground = true)
@Composable
fun TambahItemPreview() {
    val navController = rememberNavController()
    TambahItem(navController = navController, onBack = {}, onTambahBarang = {}, onTambahLokasi = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahBarangPage(
    onSimpan: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current
    var nama by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val kategoriOptions = listOf("Alat Personel", "Alat Berat", "Material", "Lainnya")
    val lokasiOptions = listOf("Gudang Utama - Jakarta", "Gudang 2", "Site Office")
    var kategoriExpanded by remember { mutableStateOf(false) }
    var lokasiExpanded by remember { mutableStateOf(false) }
    val now = remember { java.time.LocalDateTime.now() }
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    val waktu = now.format(formatter)

    val firestore = FirebaseFirestore.getInstance()
    var isSaving by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoUri = uri
            isUploading = true
            val storageRef = FirebaseStorage.getInstance().reference.child("item_images/${UUID.randomUUID()}")
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        fotoUrl = downloadUri.toString()
                        isUploading = false
                        Toast.makeText(context, "Foto berhasil diupload", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    isUploading = false
                    Toast.makeText(context, "Gagal upload foto", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Barang", style = MaterialTheme.typography.titleLarge.copy(fontWeight     = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp) // padding ekstra agar tombol tidak tertutup navbar
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Foto Barang", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .clickable { if (!isUploading) launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(fotoUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentScale = ContentScale.Crop
                    )
                    if (isUploading) {
                        Box(
                            Modifier.fillMaxSize().background(Color(0xAAFFFFFF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap untuk menambah foto", color = Color(0xFF6B7280))
                        Text("JPG, PNG maksimal 5MB", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Nama Barang
            Text("Nama Barang *", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                placeholder = { Text("Bor", color = Color(0xFF9CA3AF)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Kategori
            Text("Kategori *", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            ExposedDropdownMenuBox(
                expanded = kategoriExpanded,
                onExpandedChange = { kategoriExpanded = !kategoriExpanded }
            ) {
                OutlinedTextField(
                    value = kategori,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Alat Personel", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kategoriExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = kategoriExpanded,
                    onDismissRequest = { kategoriExpanded = false }
                ) {
                    kategoriOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                kategori = it
                                kategoriExpanded = false
                                // Generate SKU otomatis berdasarkan kategori
                                sku = when (it) {
                                    "Alat Personel" -> "PRS-004"
                                    "Alat Berat" -> "ALT-001"
                                    "Material" -> "MAT-001"
                                    else -> "OTH-001"
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // SKU
            Text("SKU *", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            OutlinedTextField(
                value = sku,
                onValueChange = {},
                placeholder = { Text("PRS-004", color = Color(0xFF9CA3AF)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Deskripsi
            Text("Deskripsi", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = { Text("bor", color = Color(0xFF9CA3AF)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Lokasi
            Text("Lokasi *", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            ExposedDropdownMenuBox(
                expanded = lokasiExpanded,
                onExpandedChange = { lokasiExpanded = !lokasiExpanded }
            ) {
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Gudang Utama - Jakarta", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lokasiExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = lokasiExpanded,
                    onDismissRequest = { lokasiExpanded = false }
                ) {
                    lokasiOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                lokasi = it
                                lokasiExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Informasi Sistem
            Text("Informasi Sistem", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Dibuat", color = Color(0xFF6B7280), fontSize = 14.sp)
                    Text(waktu, color = Color(0xFF23272E), fontSize = 14.sp)
                }
                Column {
                    Text("Diperbarui", color = Color(0xFF6B7280), fontSize = 14.sp)
                    Text(waktu, color = Color(0xFF23272E), fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal", color = Color(0xFF23272E), style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = {
                        if (isUploading) {
                            Toast.makeText(context, "Tunggu upload foto selesai", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (nama.isBlank() || kategori.isBlank() || lokasi.isBlank() || sku.isBlank()) {
                            Toast.makeText(context, "Lengkapi semua field wajib", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSaving = true
                        val data = hashMapOf(
                            "name" to nama,
                            "sku" to sku,
                            "category" to kategori,
                            "description" to deskripsi,
                            "location" to lokasi,
                            "photoUrl" to (fotoUrl ?: ""),
                            "createdAt" to waktu,
                            "updatedAt" to waktu
                        )
                        firestore.collection("equipments")
                            .add(data)
                            .addOnSuccessListener {
                                isSaving = false
                                Toast.makeText(context, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                onSimpan()
                            }
                            .addOnFailureListener {
                                isSaving = false
                                Toast.makeText(context, "Gagal menambah barang", Toast.LENGTH_SHORT).show()
                            }
                    },
                    enabled = !isSaving && !isUploading,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    else Text("Simpan Barang", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
