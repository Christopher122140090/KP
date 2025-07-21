package com.rosaliscagroup.admin.ui.item

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

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
            TopAppBar(
                title = { Text("Riwayat Barang") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading.value -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                activities.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada aktivitas barang.", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(activities) { activity ->
                            val type = activity.optString("type")
                            val details = activity.optString("details")
                            val createdAtRaw = activity.opt("createdAt")
                            val time = when (createdAtRaw) {
                                is Timestamp -> {
                                    val date = createdAtRaw.toDate()
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
                                }

                                is String -> {
                                    try {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                                        val date = sdf.parse(createdAtRaw)
                                        if (date != null) SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date) else ""
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }

                                else -> ""
                            }

                            val (icon, iconTint) = when (type) {
                                "Transfer" -> Icons.Default.SwapHoriz to Color(0xFF1976D2)
                                "Equipment Received" -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
                                "Edit Equipment" -> Icons.Default.Edit to Color(0xFFFFA000)
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
                                    details = "Nama: $namaBarang\nDari: $user",
                                    time = time
                                )
                            } else {
                                ActivityItem(
                                    icon = icon,
                                    iconTint = iconTint,
                                    title = type,
                                    details = details,
                                    time = time
                                )
                            }
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
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(top = 4.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = iconTint)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = details, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

fun fetchActivities(
    equipmentId: String,
    activities: MutableList<JSONObject>,
    isLoading: MutableState<Boolean>
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("activities")
        .whereEqualTo("equipmentId", equipmentId)
        .get()
        .addOnSuccessListener { result ->
            val tempList = mutableListOf<JSONObject>()

            for (document in result) {
                val activity = JSONObject(document.data)
                activity.put("createdAt", document.getTimestamp("createdAt"))
                tempList.add(activity)
            }

            // Urutkan dari terbaru ke lama berdasarkan createdAt Timestamp
            tempList.sortByDescending { obj ->
                val timestamp = obj.opt("createdAt")
                when (timestamp) {
                    is Timestamp -> timestamp.toDate()
                    else -> null
                }
            }

            activities.clear()
            activities.addAll(tempList)

            isLoading.value = false
        }
        .addOnFailureListener { exception ->
            println("Error fetching activities: ${exception.message}")
            exception.printStackTrace()
            isLoading.value = false
        }
}
