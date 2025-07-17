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
    var name by remember { mutableStateOf(name) }
    var description by remember { mutableStateOf(description) }
    var status by remember { mutableStateOf(status) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }

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

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    EquipmentRepository.updateItem(itemId, name, description, status)
                    withContext(Dispatchers.Main) {
                        onSave(name, description, status)
                        showToast = true
                    }
                }
            }) {
                Text("Save")
            }

            onCancel?.let {
                Button(onClick = it) {
                    Text("Cancel")
                }
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

