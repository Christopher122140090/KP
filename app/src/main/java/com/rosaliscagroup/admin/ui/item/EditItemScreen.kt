package com.rosaliscagroup.admin.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.rosaliscagroup.admin.repository.EquipmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.compose.foundation.clickable
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.focus.onFocusChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: String,
    name: String,
    description: String,
    status: String,
    onSave: (String, String, String) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(URLDecoder.decode(name, "UTF-8")) }
    var description by remember { mutableStateOf(URLDecoder.decode(description, "UTF-8")) }
    var kondisi by remember { mutableStateOf(URLDecoder.decode(status, "UTF-8")) } // Change status to kondisi
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<String?>(null) }
    val newImageUri = remember { mutableStateOf<Uri?>(null) }
    val isUploading = remember { mutableStateOf(false) }
    val initialName = remember { mutableStateOf(URLDecoder.decode(name, "UTF-8")) } // Fetch initial value on start
    val initialDescription = remember { mutableStateOf(URLDecoder.decode(description, "UTF-8")) } // Fetch initial value on start
    val initialStatus = remember { mutableStateOf(URLDecoder.decode(status, "UTF-8")) } // Fetch initial value on start

    val kondisiOptions = listOf("Baik", "Rusak", "Maintenance")

    // Fetch image URI from repository
    LaunchedEffect(itemId) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = EquipmentRepository.getItemImageUri(itemId)
            withContext(Dispatchers.Main) {
                imageUri.value = uri
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "equipments/${System.currentTimeMillis()}_${it.lastPathSegment}"
            val imageRef = storageRef.child(fileName)

            isUploading.value = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    imageRef.putFile(it).await()
                    val downloadUrl = imageRef.downloadUrl.await().toString()
                    withContext(Dispatchers.Main) {
                        newImageUri.value = downloadUrl.toUri() // Use KTX extension function
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isUploading.value = false // Ensure upload state is reset in case of error
                    }
                }
            }
        }
    }

    fun deleteOldImage(oldUri: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldUri)
        CoroutineScope(Dispatchers.IO).launch {
            storageRef.delete().await()
        }
    }

    if (isUploading.value) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Item saved successfully!", Toast.LENGTH_SHORT).show()
            showToast = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Item",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clickable {
                imagePickerLauncher.launch("image/*")
            }) {
            if (newImageUri.value != null) {
                Image(
                    painter = rememberAsyncImagePainter(newImageUri.value),
                    contentDescription = "New Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop
                )
            } else if (imageUri.value != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri.value),
                    contentDescription = "Existing Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = "*Ketuk gambar untuk mengubahnya",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E88E5),
                cursorColor = Color(0xFF9CA3AF),
                unfocusedBorderColor = Color(0xFF757575),
                errorBorderColor = Color(0xFFF44336),
                focusedContainerColor = Color(0x1E88E5FF),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E88E5),
                cursorColor = Color(0xFF9CA3AF),
                unfocusedBorderColor = Color(0xFF757575),
                errorBorderColor = Color(0xFFF44336),
                focusedContainerColor = Color(0x1E88E5FF),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        var kondisiError by remember { mutableStateOf(false) }
        var kondisiExpanded by remember { mutableStateOf(false) }
        var isLokasiFocused by remember { mutableStateOf(false) }

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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    newImageUri.value?.let {
                        deleteOldImage(imageUri.value!!)
                        EquipmentRepository.updateImageUri(itemId, it.toString())
                        imageUri.value = it.toString()
                    }
                    EquipmentRepository.updateItem(itemId, name, description, kondisi) // Update kondisi instead of status

                    // Log activity in /activities
                    val changes = mutableMapOf<String, String>()
                    changes["Name"] = "${initialName.value} -> ${name}" // Example: oldName -> newName
                    changes["Description"] = "${initialDescription.value} -> ${description}"
                    changes["Kondisi"] = "${initialStatus.value} -> ${kondisi}" // Example: inactive -> active
                    newImageUri.value?.let { changes["Image"] = it.toString() }

                    logEditActivity(context, itemId, "locationId_placeholder", changes)

                    withContext(Dispatchers.Main) {
                        onSave(name, description, kondisi)
                        showToast = true
                    }
                }
            }) {
                Text("Save")
            }

            onCancel?.let {
                Button(onClick = {
                    newImageUri.value?.let { uri ->
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                storageRef.delete().await()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Failed to delete uploaded image: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    newImageUri.value = null // Reset newImageUri
                    it()
                }) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun logEditActivity(context: android.content.Context, itemId: String, locationId: String, changes: Map<String, String>) {
    val activity = hashMapOf(
        "type" to "Edit Equipment",
        "createdAt" to com.google.firebase.Timestamp.now(),
        "equipmentId" to itemId,
        "locationId" to locationId,
        "details" to changes.entries.joinToString("\n") { entry ->
            if (entry.key == "Image") "Image changed" else "${entry.key}: ${entry.value}"
        }
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("activities")
                .add(activity)
                .await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Failed to log activity: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditItemScreen() {
    EditItemScreen(
        itemId = "123",
        name = "Sample Name",
        description = "Sample Description",
        status = "Active",
        onSave = { name, description, status ->
            println("Saved: $name, $description, $status")
        },
        onCancel = { println("Cancelled") }
    )
}

