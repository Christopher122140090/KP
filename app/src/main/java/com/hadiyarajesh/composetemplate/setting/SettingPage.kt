package com.hadiyarajesh.composetemplate.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import com.hadiyarajesh.composetemplate.ui.profile.ProfileScreen
import com.hadiyarajesh.composetemplate.ui.profile.ProfileData
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.firebase.auth.FirebaseAuth

interface LogoutNavController {
    val onLogout: (() -> Unit)?
}

@Composable
fun SettingPage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showProfile by remember { mutableStateOf(false) }
    val contextasd = LocalContext.current

    if (showProfile) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: ""
        // Ambil nama sebelum '@', ganti angka di akhir dengan spasi dan angka
        val usernameRaw = email.substringBefore("@")
        val username = usernameRaw.replace(Regex("(\\D)(\\d+)$"), "$1 $2").replaceFirstChar { it.uppercase() }
        // Ambil tanggal pembuatan akun dari metadata Firebase
        val createdTimestamp = user?.metadata?.creationTimestamp ?: 0L
        val joinDate = if (createdTimestamp > 0) {
            java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(createdTimestamp))
        } else {
            "-"
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
        ) {
            ProfileScreen(
                profile = ProfileData(
                    name = username,
                    email = email,
                    status = "Aktif",
                    role = "User",
                    joinDate = joinDate
                )
            )
            IconButton(
                onClick = { showProfile = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showProfile = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profil", tint = Color(0xFF1976D2))
                    Spacer(Modifier.width(16.dp))
                    Text("Profil", style = MaterialTheme.typography.titleMedium)
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // Sign out dari FirebaseAuth
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            // Clear navigation stack dan kembali ke login
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color(0xFFD32F2F))
                    Spacer(Modifier.width(16.dp))
                    Text("Logout", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
