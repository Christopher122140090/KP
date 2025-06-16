package com.rosaliscagroup.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import com.rosaliscagroup.admin.ui.home.HomeRoute
import com.rosaliscagroup.admin.ui.profile.ProfileScreen
import com.rosaliscagroup.admin.ui.login.LoginScreen
import com.rosaliscagroup.admin.ui.barang.CekBarangScreen
import com.rosaliscagroup.admin.ui.item.TambahItem

// Data class untuk state login
data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Bottom navigation item definition
sealed class BottomNavItem(val route: String, val icon: ImageVector, val contentDescription: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object CekBarang : BottomNavItem("cek_barang", Icons.Filled.Visibility, "Cek Barang")
    object TambahID : BottomNavItem("TambahItemPage", Icons.Filled.Add, "Tambah Item")
    object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile")
}

// Komponen AppBar untuk semua layar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    showMenuIcon: Boolean, // Tambahkan parameter untuk kontrol ikon menu
    userName: String = "User", // Tambahkan parameter nama user
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar bulat dengan inisial user
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFFFFFF),
                titleContentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
    ) {
        NavigationBar(
            containerColor = Color(0xFFFFFFFF)
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            val items = listOf(
                BottomNavItem.Home,
                BottomNavItem.CekBarang,
                BottomNavItem.TambahID,
                BottomNavItem.Profile
            )
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.contentDescription,
                            tint = if (currentRoute == item.route) Color(0xFF2563EB) else Color(0xFF9CA3AF)
                        )
                    },
                    label = {
                        Text(
                            text = if (item.contentDescription.length > 10) item.contentDescription.take(9) + "…" else item.contentDescription,
                            fontSize = 14.sp,
                            modifier = Modifier,
                            color = if (currentRoute == item.route) Color(0xFF2563EB) else Color(0xFF9CA3AF),
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

// Fungsi untuk mengekstrak nama dari email
fun extractNameFromEmail(email: String): String {
    return email.substringBefore("@")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .replace(".", " ")
}

// Navigasi utama dengan Scaffold
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email, password ->
                    isLoggedIn = true
                    userEmail = email
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Home",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = extractNameFromEmail(userEmail)
                    )
                },
                content = {
                    HomeRoute()
                },
                bottomBar = {
                    if (isLoggedIn) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("cek_barang") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Cek Barang",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = extractNameFromEmail(userEmail)
                    )
                },
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        CekBarangScreen()
                    }
                },
                bottomBar = {
                    if (isLoggedIn) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }

        composable("TambahItemPage") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Tambah Item",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = extractNameFromEmail(userEmail)
                    )
                },
                content = { padding ->
                    TambahItem(
                        onSimpan = { nama, deskripsi, kategori, status, gambarUri ->
                            // TODO: Implement save logic
                        }
                    )
                },
                bottomBar = {
                    if (isLoggedIn) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("profile") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Profile",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = extractNameFromEmail(userEmail)
                    )
                },
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        ProfileScreen(
                            navController = navController
                        )
                    }
                },
                bottomBar = {
                    if (isLoggedIn) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        AppBar(
            title = "Preview AppBar",
            navController = navController,
            drawerState = drawerState,
            scope = scope,
            showMenuIcon = true,
            userName = "Preview User"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        BottomNavigationBar(navController = navController)
    }
}
