package com.rosaliscagroup.admin.ui.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hadiyarajesh.admin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginState by remember { mutableStateOf(LoginState()) }
    var googleLoading by remember { mutableStateOf(false) }
    var googleError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginState.isLoading) {
        if (loginState.isLoading) {
            if (email.isEmpty() || password.isEmpty()) {
                loginState = loginState.copy(isLoading = false, errorMessage = "Email and password cannot be empty")
            } else {
                // Proses login ke Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login sukses, currentUser akan terisi
                            // Update last_login di Firestore
                            val user = FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid
                            if (userId != null) {
                                val db = FirebaseFirestore.getInstance()
                                val doc = db.collection("users").document(userId)
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val now = sdf.format(Date())
                                doc.update("last_login", now)
                            }
                            loginState = loginState.copy(isLoading = false, errorMessage = null)
                            onLoginSuccess(email, password)
                        } else {
                            loginState = loginState.copy(isLoading = false, errorMessage = task.exception?.localizedMessage ?: "Login gagal")
                        }
                    }
            }
        }
    }

    // START UI
    Box(modifier = Modifier.fillMaxSize()) {
        // Background biru dengan lengkungan bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    color = Color(0xFF276BB4),
                    shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            },
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "PT. ROSA LISCA Logo",
                            modifier = Modifier.size(100.dp)
                        )

                        Text(
                            text = "Rosalisca Inventory",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.Black
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Kelola inventaris dengan mudah",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Masukkan email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon"
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = loginState.errorMessage != null && email.isBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !loginState.isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                errorContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color(0xFF276BB4),
                                unfocusedIndicatorColor = Color(0xFFBDBDBD),
                                errorIndicatorColor = Color(0xFFF44336)
                            )
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Masukkan password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Icon"
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = loginState.errorMessage != null && password.isBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !loginState.isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                errorContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color(0xFF276BB4),
                                unfocusedIndicatorColor = Color(0xFFBDBDBD),
                                errorIndicatorColor = Color(0xFFF44336)
                            )
                        )

                        loginState.errorMessage?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }

                        Button(
                            onClick = {
                                loginState = loginState.copy(isLoading = true)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !loginState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF276BB4),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF276BB4).copy(alpha = 0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (loginState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Masuk",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            text = "Lupa password?",
                            color = Color(0xFF276BB4),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable {
                                    // TODO: Handle forgot password click
                                }
                        )

                        val context = LocalContext.current
                        val activity = context as? Activity
                        val googleSignInClient = remember {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(context.getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()
                            GoogleSignIn.getClient(context, gso)
                        }
                        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                            googleLoading = false
                            if (result.resultCode == Activity.RESULT_OK) {
                                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                                try {
                                    val account = task.getResult(ApiException::class.java)!!
                                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                                    googleLoading = true
                                    FirebaseAuth.getInstance().signInWithCredential(credential)
                                        .addOnCompleteListener(activity!!) { task ->
                                            googleLoading = false
                                            if (task.isSuccessful) {
                                                val user = FirebaseAuth.getInstance().currentUser
                                                // Update last_login di Firestore untuk login Google
                                                val userId = user?.uid
                                                if (userId != null) {
                                                    val db = FirebaseFirestore.getInstance()
                                                    val doc = db.collection("users").document(userId)
                                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                                    val now = sdf.format(Date())
                                                    doc.update("last_login", now)
                                                }
                                                onLoginSuccess(user?.email ?: "", "google")
                                            } else {
                                                googleError = task.exception?.localizedMessage ?: "Login Google gagal"
                                            }
                                        }
                                } catch (e: ApiException) {
                                    googleError = e.localizedMessage ?: "Login Google gagal"
                                }
                            } else {
                                googleError = "Login Google dibatalkan"
                            }
                        }

                        // Tambahkan tombol Google Sign-In di bawah form login
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (googleError != null) {
                                Text(
                                    text = googleError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    googleError = null
                                    googleLoading = true
                                    googleSignInClient.signOut().addOnCompleteListener {
                                        val signInIntent = googleSignInClient.signInIntent
                                        launcher.launch(signInIntent)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                enabled = !googleLoading
                            ) {
                                if (googleLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.Gray,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google_logo),
                                        contentDescription = "Google Sign-In",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp) // Ubah ukuran logo Google di sini
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Login dengan Google", color = Color.Black)
                                }
                            }
                                    // Tombol untuk mengelola akun Google di perangkat
                            TextButton(
                                onClick = {
                                    // Membuka pengaturan akun Google di perangkat
                                    val intent = Intent(android.provider.Settings.ACTION_SYNC_SETTINGS)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text("Manage accounts on this device", color = Color(0xFF276BB4))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = { _, _ -> }
        )
    }
}

