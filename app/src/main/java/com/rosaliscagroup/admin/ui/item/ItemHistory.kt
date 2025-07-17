package com.rosaliscagroup.admin.ui.item

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemHistory : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val equipmentId = intent.getStringExtra("equipmentId") ?: ""
            ItemHistoryScreen(equipmentId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemHistoryScreen(equipmentId: String) {
    val activities = remember { mutableStateListOf<JSONObject>() }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchActivities(equipmentId, activities, isLoading)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Item History") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else if (activities.isEmpty()) {
                Text("No recent activities.", color = Color.Gray, modifier = Modifier.padding(16.dp))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    activities.forEach { activity ->
                        val type = activity.optString("type")
                        val details = activity.optString("details")

                        val (icon, iconTint) = when (type) {
                            "Transfer" -> Icons.Default.SwapHoriz to Color(0xFF1976D2)
                            "Equipment Received" -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
                            else -> Icons.Default.Info to Color(0xFF757575)
                        }

                        if (type == "Equipment Received") {
                            val detailsLines = details.split("\n")
                            val namaBarang = detailsLines.getOrNull(0)?.removePrefix("Nama: ") ?: "-"
                            val user = detailsLines.find { it.startsWith("Dari: ") }?.removePrefix("Dari: ") ?: "-"
                            ActivityItem(
                                icon = icon,
                                iconTint = iconTint,
                                title = "Tambah Barang",
                                details = "Nama barang: $namaBarang\nDari: $user",
                                time = ""
                            )
                        } else {
                            ActivityItem(

                                icon = icon,
                                iconTint = iconTint,
                                title = type,
                                details = details,
                                time = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    details: String,
    time: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = iconTint)
        Text(text = details, style = MaterialTheme.typography.bodyMedium)
        Text(text = time, style = MaterialTheme.typography.bodySmall)
    }
}

fun fetchActivities(equipmentId: String, activities: MutableList<JSONObject>, isLoading: MutableState<Boolean>) {
    val db = FirebaseFirestore.getInstance()

    db.collection("activities")
        .whereEqualTo("equipmentId", equipmentId)
        .get()
        .addOnSuccessListener { result ->
            if (result.isEmpty) {
                println("No activities found for equipmentId: $equipmentId")
            } else {
                println("Found ${result.size()} activities for equipmentId: $equipmentId")
            }
            for (document in result) {
                val activity = JSONObject(document.data)
                println("Activity data: ${activity.toString()}")
                activities.add(activity)
            }
            isLoading.value = false
        }
        .addOnFailureListener { exception ->
            println("Error fetching activities: ${exception.message}")
            exception.printStackTrace()
            isLoading.value = false
        }
}








