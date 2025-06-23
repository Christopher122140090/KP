package com.rosaliscagroup.admin.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNameScreen(
    navController: NavController,
    onSave: (String, String) -> Unit = { _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var roleError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
                    text = "Ganti Nama & Role",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Nama Lengkap", color = Color(0xFF9CA3AF)) },
                    isError = nameError,
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
                if (nameError) {
                    Text("Nama wajib diisi", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = role,
                    onValueChange = {
                        role = it
                        roleError = false
                    },
                    label = { Text("Role/Jabatan", color = Color(0xFF9CA3AF)) },
                    isError = roleError,
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
                if (roleError) {
                    Text("Role wajib diisi", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        nameError = name.isBlank()
                        roleError = role.isBlank()
                        if (!nameError && !roleError) {
                            isLoading = true
                            error = null
                            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid
                            if (userId != null) {
                                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                db.collection("users").document(userId)
                                    .update(mapOf(
                                        "name" to name,
                                        "role" to role
                                    ))
                                    .addOnSuccessListener {
                                        onSave(name, role)
                                        isLoading = false
                                        Toast.makeText(context, "Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                                        navController.navigate("profile") {
                                            popUpTo("profile") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        error = e.localizedMessage
                                        isLoading = false
                                    }
                            } else {
                                error = "User tidak ditemukan."
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text(if (isLoading) "Menyimpan..." else "Simpan", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeNameScreenPreview() {
    // Preview function updated to include a dummy NavController
    ChangeNameScreen(navController = NavController(LocalContext.current)) { _, _ -> }
}
