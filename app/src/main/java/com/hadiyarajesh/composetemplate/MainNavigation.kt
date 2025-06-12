package com.hadiyarajesh.composetemplate

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hadiyarajesh.composetemplate.ui.barang.BarangTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.hadiyarajesh.composetemplate.setting.SettingPage
import com.hadiyarajesh.composetemplate.ui.home.HomeRoute
import com.hadiyarajesh.composetemplate.ui.barang.TambahBarangScreen
import com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab
import com.hadiyarajesh.composetemplate.ui.profile.ProfileData
import com.hadiyarajesh.composetemplate.ui.profile.ProfileScreen
import com.hadiyarajesh.composetemplate.ui.login.LoginScreen
import com.hadiyarajesh.composetemplate.ui.barang.BarangRepository
import com.hadiyarajesh.composetemplate.ui.barang.CekBarangScreen
import kotlinx.coroutines.flow.emptyFlow

// Data class untuk state login
data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Komponen AppBar untuk semua layar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    showMenuIcon: Boolean, // Tambahkan parameter untuk kontrol ikon menu
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = {
            if (showMenuIcon) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, contentDescription = "Toggle drawer", tint = Color.White)
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("preview") }) {
                Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = Color(0xFF90CAF9)) // Biru muda untuk aksen
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF008FFD), // Biru tua untuk top bar
            titleContentColor = Color.White
        ),
        modifier = modifier
    )
}

// Komponen untuk konten drawer
@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.fillMaxHeight(),
        drawerContainerColor = Color(0xFFE3F2FD) // Biru muda sangat terang
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF7276BB4)) // Biru muda sangat terang
        ) {
            Column {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF42A5F5)) },
                    label = {
                        Text(
                            "Home",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        )
                    },
                    selected = navController.currentDestination?.route == "home",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cek barang", tint = Color(0xFF42A5F5)) },
                    label = {
                        Text(
                            "Cek Barang",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        )
                    },
                    selected = navController.currentDestination?.route == "cek_barang",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("cek_barang") {
                            launchSingleTop = true
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Tambah barang", tint = Color(0xFF42A5F5)) },
                    label = {
                        Text(
                            "Tambah Barang",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        )
                    },
                    selected = navController.currentDestination?.route == "tambah_barang",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("tambah_barang") {
                            launchSingleTop = true
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF42A5F5)) },
                    label = {
                        Text(
                            "Settings",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        )
                    },
                    selected = navController.currentDestination?.route == "settings",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

// Navigasi utama dengan Navigation Drawer global
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(false) } // State untuk status login

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isLoggedIn) { // Hanya tampilkan drawer jika sudah login
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        },
        gesturesEnabled = isLoggedIn, // Nonaktifkan gesture swipe jika belum login
        content = {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { email, password ->
                            isLoggedIn = true // Set state login ke true
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true } // Hapus login dari back stack
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
                                drawerState = drawerState,
                                scope = scope,
                                showMenuIcon = isLoggedIn // Hanya tampilkan menu jika sudah login
                            )
                        },
                        content = {
                            HomeRoute()
                        }
                    )
                }
                composable("cek_barang") {
                    Scaffold(
                        topBar = {
                            AppBar(
                                title = "Cek Barang",
                                navController = navController,
                                drawerState = drawerState,
                                scope = scope,
                                showMenuIcon = isLoggedIn
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
                        }
                    )
                }
                composable("tambah_barang") {
                    Scaffold(
                        topBar = {
                            AppBar(
                                title = "Tambah Barang",
                                navController = navController,
                                drawerState = drawerState,
                                scope = scope,
                                showMenuIcon = isLoggedIn
                            )
                        },
                        content = { padding ->
                            TambahBarangScreen(
                                onSimpan = { nama, kategori, kondisi, labtekId, pengelolaId, status, tanggal ->
                                    val barang = BarangLab(nama, kategori, kondisi, labtekId, pengelolaId, status, tanggal)
                                    // Gunakan coroutineScope.launch, bukan LaunchedEffect (LaunchedEffect hanya untuk composable)
                                    scope.launch {
                                        BarangRepository.tambahBarang(barang)
                                    }
                                }
                            )
                        }
                    )
                }
                composable("settings") {
                    Scaffold(
                        topBar = {
                            AppBar(
                                title = "Settings",
                                navController = navController,
                                drawerState = drawerState,
                                scope = scope,
                                showMenuIcon = isLoggedIn
                            )
                        },
                        content = { padding ->
                            SettingPage(
                                navController = navController,
                                modifier = Modifier.padding(padding)
                            )
                        }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = { email, password -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerContentPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Open)
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
        ) {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                scope = scope
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
            showMenuIcon = true
        )
    }
}

