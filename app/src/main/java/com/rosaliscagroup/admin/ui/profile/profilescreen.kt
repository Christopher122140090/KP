package com.rosaliscagroup.admin.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("SimpleDateFormat")
@Composable
fun ProfileScreen(
    name: String,
    email: String,
    status: String = "Aktif",
    role: String = "User",
    joinDate: String = "-",
    navController: androidx.navigation.NavController? = null,
    onLogout: (() -> Unit)? = null
) {
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
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        color = Color(0xFF276BB4) // Biru tua, sama dengan top bar, fixed ARGB
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
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color(0xFF000000),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = email,
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
                        ProfileInfoRow(label = "Status", value = status)
                        ProfileInfoRow(label = "Role", value = role)
                        ProfileInfoRow(label = "Join Date", value = joinDate)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        onLogout?.invoke()
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

// Helper to get user data for runtime usage
fun getProfileScreenUserData(): ProfileScreenUserData {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "-"
    val name = email.substringBefore("@")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .replace(".", " ")
    val createdTimestamp = user?.metadata?.creationTimestamp ?: 0L
    val joinDate = if (createdTimestamp > 0) {
        java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(createdTimestamp))
    } else {
        "-"
    }
    return ProfileScreenUserData(name, email, "Aktif", "User", joinDate)
}

data class ProfileScreenUserData(
    val name: String,
    val email: String,
    val status: String,
    val role: String,
    val joinDate: String
)

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
    ProfileScreen(
        name = "John Doe",
        email = "john.doe@example.com",
        status = "Aktif",
        role = "User",
        joinDate = "01 Jan 2024"
    )
}
