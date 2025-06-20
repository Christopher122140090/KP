package com.rosaliscagroup.admin.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileFormScreen(
    onProfileSaved: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: "-"
    val email = user?.email ?: "-"
    val lastLogin = user?.metadata?.lastSignInTimestamp?.let {
        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(it))
    } ?: "-"
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lengkapi Data Profil", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Role/Jabatan") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                isLoading = true
                error = null
                scope.launch {
                    try {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(userId).set(
                            mapOf(
                                "email" to email,
                                "last_login" to lastLogin,
                                "name" to name,
                                "role" to role,
                                "user_id" to userId
                            )
                        ).await()
                        onProfileSaved()
                    } catch (e: Exception) {
                        error = e.localizedMessage
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = name.isNotBlank() && role.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Menyimpan..." else "Simpan")
        }
    }
}

