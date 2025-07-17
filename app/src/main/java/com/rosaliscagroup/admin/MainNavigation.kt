package com.rosaliscagroup.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
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
import com.rosaliscagroup.admin.setting.SettingPage
import com.rosaliscagroup.admin.ui.home.HomeRoute
import com.rosaliscagroup.admin.ui.profile.ProfileScreen
import com.rosaliscagroup.admin.ui.login.LoginScreen
import com.rosaliscagroup.admin.ui.item.CekBarangScreen
import com.rosaliscagroup.admin.ui.item.TambahItem
import com.rosaliscagroup.admin.ui.login.UserProfileFormScreen
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.ViewInAr
import com.rosaliscagroup.admin.setting.ChangeNameScreen
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.rosaliscagroup.admin.ui.transfer.TransferItem
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rosaliscagroup.admin.ui.proyek.CekBarangScreenTransfer
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.rosaliscagroup.admin.ui.proyek.EditProyekScreen
import com.rosaliscagroup.admin.ui.item.EditItemScreen
import com.rosaliscagroup.admin.ui.item.ItemHistoryScreen

// Data class untuk state login
data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Bottom navigation item definition
sealed class BottomNavItem(val route: String, val icon: ImageVector, val contentDescription: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Cek : BottomNavItem("cek", Icons.Filled.ViewInAr, "Cek")
    object Tambah : BottomNavItem("NavigationAdd", Icons.Filled.Add, "Tambah")
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
    userPhotoUrl: String? = null, // Tambahkan parameter untuk foto profil
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
                    // Avatar bulat dengan foto profil atau inisial user
                    if (!userPhotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
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
            actions = {
                if (title == "Profile") {
                    IconButton(onClick = { navController.navigate("setting") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF1976D2)
                        )
                    }
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

// Komponen untuk konten drawer
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
                BottomNavItem.Cek,
                BottomNavItem.Tambah,
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
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    var showNavbar by remember { mutableStateOf(true) } // Tambah state untuk kontrol navbar

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email, password ->
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                isLoggedIn = true
                                userEmail = email
                                userName = (doc.getString("name") ?: "").split(" ").firstOrNull() ?: "User"
                                userPhotoUrl = user.photoUrl?.toString()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.navigate("user_profile_form") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }.addOnFailureListener {
                            navController.navigate("user_profile_form") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
        composable("user_profile_form") {
            UserProfileFormScreen(
                onProfileSaved = {
                    isLoggedIn = true
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    userEmail = user?.email ?: ""
                    val userId = user?.uid
                    if (userId != null) {
                        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
                            userName = (doc.getString("name") ?: "").split(" ").firstOrNull() ?: "User"
                            userPhotoUrl = user?.photoUrl?.toString()
                            navController.navigate("home") {
                                popUpTo("user_profile_form") { inclusive = true }
                            }
                        }.addOnFailureListener {
                            userName = "User"
                            userPhotoUrl = user?.photoUrl?.toString()
                            navController.navigate("home") {
                                popUpTo("user_profile_form") { inclusive = true }
                            }
                        }
                    } else {
                        userName = "User"
                        userPhotoUrl = user?.photoUrl?.toString()
                        navController.navigate("home") {
                            popUpTo("user_profile_form") { inclusive = true }
                        }
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
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = {
                    HomeRoute(navController = navController)
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("cek") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Cek",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    CheckNav(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }

        composable("NavigationAdd") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Tambah",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    AddNav(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("profile") {
            Scaffold(
                // Hapus topBar agar AppBar tidak muncul di menu profile
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
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("TambahItemPage") {
            var showSuccess by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Tambah Item",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    Box(Modifier.padding(padding)) {
                        TambahItem(
                            onSimpan = { _, _, _, _, _, _ -> showSuccess = true },
                            onCancel = { navController.popBackStack() },
                            onShowNavbarChange = { show -> showNavbar = show }
                        )
                        if (showSuccess) {
                            LaunchedEffect(Unit) {
                                showSuccess = false
                                snackbarHostState.showSnackbar("Berhasil menyimpan data!")
                                navController.popBackStack()
                            }
                        }
                    }
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("TambahProyekPage") {
            var showSuccess by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val userUid = user?.uid ?: ""
            val userFullName = user?.displayName ?: userName
            var showDialog by remember { mutableStateOf(false) }
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Tambah Proyek",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    com.rosaliscagroup.admin.ui.proyek.TambahProyek(
                        userUid = userUid,
                        userName = userFullName,
                        onSimpan = {
                            showDialog = true
                            showSuccess = true
                        },
                        onCancel = { navController.popBackStack() }
                    )
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    navController.popBackStack()
                                }) {
                                    Text("OK")
                                }
                            },
                            title = { Text("Berhasil") },
                            text = { Text("Data berhasil disimpan!") }
                        )
                    }
                    if (showSuccess) {
                        LaunchedEffect(Unit) {
                            showSuccess = false
                            snackbarHostState.showSnackbar("Berhasil menyimpan data!")
                        }
                    }
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("ViewProyekPage") { backStackEntry ->
            // Ambil parentEntry agar ViewModel tetap sama instance dengan HomeScreen
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: com.rosaliscagroup.admin.ui.home.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel(parentEntry)
            val uiState = homeViewModel.uiState.collectAsState().value
            val locations = homeViewModel.locationsRealtime.collectAsState().value
            com.rosaliscagroup.admin.ui.location.AllLocationsScreen(
                locations = locations,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("cek_barang") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Cek Item",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    CekBarangScreen(
                        onTransfer = { equipmentUi ->
                            val equipmentJson = java.net.URLEncoder.encode(Json.encodeToString(equipmentUi), "UTF-8")
                            navController.navigate("transfer?equipment=$equipmentJson")
                        },
                        onEdit = { equipmentUi ->
                            val itemId = equipmentUi.id.ifBlank { "unknown" }
                            val initialDescription = equipmentUi.deskripsi.ifBlank { "No description available" }
                            navController.navigate("editItem/$itemId/$initialDescription")
                        },
                        history = { equipmentUi ->
                            val itemId = equipmentUi.id.ifBlank { "unknown" }
                            navController.navigate("itemHistory/$itemId")
                        }
                    )
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("setting") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Setting",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    SettingPage(navController = navController)
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable("change_name") {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Ubah Nama Profil",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    ChangeNameScreen(navController = navController)
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable(
            "transfer?equipment={equipment}",
            arguments = listOf(
                navArgument("equipment") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            androidx.compose.material3.Scaffold(
                topBar = {
                    AppBar(
                        title = "Transfer",
                        navController = navController,
                        drawerState = rememberDrawerState(DrawerValue.Closed),
                        scope = scope,
                        showMenuIcon = false,
                        userName = userName,
                        userPhotoUrl = userPhotoUrl
                    )
                },
                content = { padding ->
                    Box(Modifier.padding(padding)) {
                        val equipmentJson = backStackEntry.arguments?.getString("equipment")
                        val equipment = try {
                            if (equipmentJson != null) {
                                Json.decodeFromString<com.rosaliscagroup.admin.ui.item.EquipmentUi>(
                                    java.net.URLDecoder.decode(equipmentJson, "UTF-8")
                                )
                            } else null
                        } catch (_: Exception) { null }
                        if (equipment != null) {
                            val viewModel: com.rosaliscagroup.admin.ui.home.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                            TransferItem(
                                equipment = equipment,
                                onSimpan = { id, lokasiBaru, pengirim, penerima, tanggalKirim ->
                                    scope.launch {
                                        try {
                                            com.rosaliscagroup.admin.repository.EquipmentRepository.updateEquipmentLocation(id, lokasiBaru)
                                            android.widget.Toast.makeText(context, "Lokasi berhasil diupdate oleh $pengirim ke $penerima pada tanggal $tanggalKirim", android.widget.Toast.LENGTH_SHORT).show()
                                            navController.navigateUp()
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Gagal update lokasi: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onCancel = { navController.navigateUp() },
                                userName = userName, // pastikan userName sudah tersedia di scope ini
                                viewModel = viewModel
                            )
                        } else {
                            Text("Data item tidak ditemukan")
                        }
                    }
                },
                bottomBar = {
                    if (isLoggedIn && showNavbar) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            )
        }
        composable(
            "itemListPage?lokasi={lokasi}&kategori={kategori}",
            arguments = listOf(
                navArgument("lokasi") { type = NavType.StringType; defaultValue = "" },
                navArgument("kategori") { type = NavType.StringType; defaultValue = "Semua" }
            )
        ) { backStackEntry ->
            val lokasi = backStackEntry.arguments?.getString("lokasi") ?: ""
            CekBarangScreenTransfer(lokasiId = lokasi)
        }
        composable(
            "proyekItemListPage?lokasi={lokasi}&kategori={kategori}",
            arguments = listOf(
                navArgument("lokasi") { type = NavType.StringType; defaultValue = "" },
                navArgument("kategori") { type = NavType.StringType; defaultValue = "Semua" }
            )
        ) { backStackEntry ->
            val lokasi = backStackEntry.arguments?.getString("lokasi") ?: ""
            com.rosaliscagroup.admin.ui.proyek.CekBarangScreenTransfer(lokasiId = lokasi)
        }
        composable("ViewActivitiesPage") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: com.rosaliscagroup.admin.ui.home.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel(parentEntry)
            com.rosaliscagroup.admin.ui.activities.ViewActivitiesPage(
                homeViewModel = homeViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("editProyekScreen?locationId={locationId}")  {
            val locationId = it.arguments?.getString("locationId") ?: ""
            EditProyekScreen(locationId = locationId, navController = navController, onBack = { navController.popBackStack() })
        }
        composable("editItem/{itemId}/{initialDescription}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val initialDescription = backStackEntry.arguments?.getString("initialDescription") ?: ""
            EditItemScreen(
                itemId = itemId,
                initialDescription = initialDescription,
                onSave = { description ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable("itemHistory/{itemId}") { backStackEntry ->
            val equipmentId = backStackEntry.arguments?.getString("itemId") ?: "unknown"
            ItemHistoryScreen(equipmentId = equipmentId)
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
