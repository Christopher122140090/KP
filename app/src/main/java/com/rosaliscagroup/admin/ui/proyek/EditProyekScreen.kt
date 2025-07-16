package com.rosaliscagroup.admin.ui.proyek

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProyekScreen(locationId: String?, navController: NavController, onBack: () -> Unit) {
    if (locationId.isNullOrBlank()) {
        // Handle invalid locationId
        Text("Invalid location ID", color = Color.Red, modifier = Modifier.padding(16.dp))
        return
    }

    val firestore = FirebaseFirestore.getInstance()
    val name = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val isDataFetched = remember { mutableStateOf(false) }

    if (!isDataFetched.value) {
        firestore.collection("locations").document(locationId).get()
            .addOnSuccessListener { document ->
                name.value = document.getString("name") ?: ""
                address.value = document.getString("address") ?: ""
                description.value = document.getString("description") ?: ""
                isDataFetched.value = true
            }
            .addOnFailureListener {
                name.value = "Error fetching data"
                address.value = "Error fetching data"
                description.value = "Error fetching data"
                isDataFetched.value = true
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Edit Proyek", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name.value,
            onValueChange = { newValue ->
                name.value = newValue
            },
            label = { Text("Nama") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address.value,
            onValueChange = { newValue ->
                address.value = newValue
            },
            label = { Text("Alamat") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description.value,
            onValueChange = { newValue ->
                description.value = newValue
            },
            label = { Text("Deskripsi") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedData = mapOf(
                    "name" to name.value,
                    "address" to address.value,
                    "description" to description.value
                )
                firestore.collection("locations").document(locationId).update(updatedData).addOnSuccessListener {
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Simpan")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Kembali")
        }
    }
}

