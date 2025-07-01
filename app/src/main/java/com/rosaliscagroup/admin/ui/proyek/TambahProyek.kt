package com.rosaliscagroup.admin.ui.proyek

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahProyek(
    userUid: String = "",
    userName: String = "",
    onSimpan: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var namaProyek by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var tipeLokasi by remember { mutableStateOf("") }
    var namaProyekError by remember { mutableStateOf(false) }
    var lokasiError by remember { mutableStateOf(false) }
    var tipeLokasiError by remember { mutableStateOf(false) }
    val tipeOptions = listOf("Gudang", "Proyek")
    var tipeLokasiExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isSaving by remember { mutableStateOf(false) }
    var saveProgress by remember { mutableStateOf(0) }
    var saveResult by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA)),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp,
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
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = null
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tambah Lokasi/Proyek",
                        fontSize = 24.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    // Nama Proyek
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nama Proyek/Lokasi",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = namaProyek,
                            onValueChange = {
                                namaProyek = it
                                namaProyekError = false
                            },
                            label = null,
                            isError = namaProyekError,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                cursorColor = Color(0xFF9CA3AF),
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336),
                                focusedContainerColor = Color(0x1E88E5FF),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                    }
                    if (namaProyekError) {
                        Text("Nama proyek/lokasi wajib diisi", color = Color.Red, fontSize = 12.sp)
                    }
                    // Lokasi
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Alamat/Detail Lokasi",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = lokasi,
                            onValueChange = {
                                lokasi = it
                                lokasiError = false
                            },
                            label = null,
                            isError = lokasiError,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                cursorColor = Color(0xFF9CA3AF),
                                unfocusedBorderColor = Color(0xFF757575),
                                errorBorderColor = Color(0xFFF44336),
                                focusedContainerColor = Color(0x1E88E5FF),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                    }
                    if (lokasiError) {
                        Text("Alamat/detail lokasi wajib diisi", color = Color.Red, fontSize = 12.sp)
                    }
                    // Tipe Lokasi
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tipe Lokasi",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = tipeLokasiExpanded,
                            onExpandedChange = { tipeLokasiExpanded = !tipeLokasiExpanded }
                        ) {
                            OutlinedTextField(
                                value = tipeLokasi,
                                onValueChange = {},
                                readOnly = true,
                                label = null,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipeLokasiExpanded)
                                },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                isError = tipeLokasiError,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1E88E5),
                                    cursorColor = Color(0xFF9CA3AF),
                                    unfocusedBorderColor = Color(0xFF757575),
                                    errorBorderColor = Color(0xFFF44336),
                                    focusedContainerColor = Color(0x1E88E5FF),
                                    unfocusedContainerColor = Color(0xFFF5F5F5)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = tipeLokasiExpanded,
                                onDismissRequest = { tipeLokasiExpanded = false },
                                modifier = Modifier.background(Color(0xFFF5F5F5))
                            ) {
                                tipeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = Color(0xFF757575)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            tipeLokasi = option
                                            tipeLokasiExpanded = false
                                            tipeLokasiError = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (tipeLokasiError) {
                        Text("Tipe lokasi wajib dipilih", color = Color.Red, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onCancel?.invoke() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2563EB)))
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                namaProyekError = namaProyek.isBlank()
                                lokasiError = lokasi.isBlank()
                                tipeLokasiError = tipeLokasi.isBlank()
                                if (!namaProyekError && !lokasiError && !tipeLokasiError) {
                                    coroutineScope.launch {
                                        isSaving = true
                                        saveProgress = 0
                                        try {
                                            val db = FirebaseFirestore.getInstance()
                                            val data = hashMapOf(
                                                "name" to namaProyek,
                                                "address" to lokasi,
                                                "type" to tipeLokasi,
                                                "createdAt" to com.google.firebase.Timestamp.now(),
                                                "updatedAt" to com.google.firebase.Timestamp.now(),
                                                "createdByUid" to userUid,
                                                "createdByName" to userName
                                            )
                                            db.collection("locations")
                                                .add(data)
                                                .addOnSuccessListener {
                                                    saveResult = "Berhasil disimpan"
                                                    isSaving = false
                                                    onSimpan?.invoke()
                                                }
                                                .addOnFailureListener { e ->
                                                    saveResult = "Gagal menyimpan: ${e.localizedMessage}"
                                                    isSaving = false
                                                }
                                        } catch (e: Exception) {
                                            saveResult = "Gagal menyimpan: ${e.localizedMessage}"
                                            isSaving = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Simpan Lokasi/Proyek")
                        }
                    }
                }
            }
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
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
    LaunchedEffect(saveResult) {
        saveResult?.let {
            snackbarHostState.showSnackbar(it)
            saveResult = null
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TambahProyekPreview() {
    TambahProyek()
}
