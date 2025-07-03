package com.rosaliscagroup.admin.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
    ) {
        // Header custom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
                .align(Alignment.TopCenter),
        ) {
            IconButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF222B45))
            }
            Text(
                text = "Profil Saya",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF222B45)
            )
            IconButton(
                onClick = { /* TODO: Edit profile */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF222B45))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                .verticalScroll(rememberScrollState()), // Membuat konten bisa discroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.size(120.dp)) {
                if (user?.photoUrl != null) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF90CAF9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profileData["name"]?.take(1) ?: "-").uppercase(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = profileData["name"] ?: "-",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFF222B45),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Badge role
            if ((profileData["role"] ?: "-") != "-") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profileData["role"] ?: "-",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Info Cards
            ProfileInfoCard(
                icon = Icons.Default.Email,
                label = "Email",
                value = profileData["email"] ?: "-",
                iconColor = Color(0xFF43A047)
            )
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInfoCard(
                icon = Icons.Default.AccessTime,
                label = "Terakhir Login",
                value = profileData["last_login"] ?: "-",
                iconColor = Color(0xFF7C4DFF)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Menu List
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                label = "Pengaturan",
                onClick = { navController?.navigate("setting") },
                iconColor = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Tombol keluar
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
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoCard(icon: ImageVector, label: String, value: String, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = Color(0xFF757575), fontSize = 14.sp)
                Text(value, color = Color(0xFF222B45), fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = Color(0xFF222B45), fontWeight = FontWeight.Medium, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 180f))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
