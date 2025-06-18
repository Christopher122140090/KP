package com.rosaliscagroup.admin.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: androidx.navigation.NavController? = null) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: "Belum login"
    var profileData by remember { mutableStateOf(
        mapOf(
            "email" to "-",
            "last_login" to "-",
            "name" to "-",
            "role" to "-",
            "user_id" to userId
        )
    ) }
    LaunchedEffect(userId) {
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(userId)
            val snapshot = doc.get().await()
            if (snapshot.exists()) {
                profileData = mapOf(
                    "email" to (snapshot.getString("email") ?: "-"),
                    "last_login" to (snapshot.getString("last_login") ?: "-"),
                    "name" to (snapshot.getString("name") ?: "-"),
                    "role" to (snapshot.getString("role") ?: "-"),
                    "user_id" to (snapshot.getString("user_id") ?: userId)
                )
            }
        }
    }
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
        ) {
            // Blue background curve agar serasi dengan top bar biru tua
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = Color(0xF7276BB4), // Biru tua, sama dengan top bar
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 56.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile picture placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF90CAF9)), // Lighter blue
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (profileData["name"]?.take(1) ?: "-").uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profileData["name"] ?: "-",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color(0xFF000000),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profileData["email"] ?: "-",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF000000)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        ProfileInfoRow(label = "Role", value = profileData["role"] ?: "-")
                        ProfileInfoRow(label = "User ID", value = profileData["user_id"] ?: "-")
                        ProfileInfoRow(label = "Last Login", value = profileData["last_login"] ?: "-")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        navController?.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Logout", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF424242)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
