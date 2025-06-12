package com.hadiyarajesh.composetemplate.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hadiyarajesh.composetemplate.R
import com.hadiyarajesh.composetemplate.data.entity.Image
import com.hadiyarajesh.composetemplate.ui.components.ErrorItem
import com.hadiyarajesh.composetemplate.ui.components.LoadingIndicator
import com.hadiyarajesh.composetemplate.utility.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        uiState = uiState,
        loadData = { viewModel.loadData() }
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeScreenUiState,
    loadData: () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        loadData()
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    Scaffold(
        topBar = {},
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Latar belakang biru melengkung dan logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0xF7276BB4),
                            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White, shape = RoundedCornerShape(60.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Deskripsi aksesibilitas",
                        modifier = Modifier.size(90.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selamat Datang di Aplikasi ERH",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Grafik di tengah
                if (uiState is HomeScreenUiState.Success) {
                    DashboardPieChart(kondisiStat = uiState.kondisiStat)
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (uiState is HomeScreenUiState.Loading) {
                    LoadingIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (uiState is HomeScreenUiState.Error) {
                    ErrorItem(
                        modifier = Modifier
                            .padding(40.dp)
                            .fillMaxWidth(),
                        text = (uiState as? HomeScreenUiState.Error)?.msg ?: ""
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                val context = LocalContext.current
                Button(
                    onClick = {
                        Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Log Out", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun DashboardPieChart(kondisiStat: Map<String, Int>) {
    val data = kondisiStat.entries.toList()
    val total = data.sumOf { it.value }.toFloat()
    val pieColors = listOf(Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFFBC02D), Color(0xFFD32F2F), Color(0xFF7B1FA2))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Pemantauan Berdasarkan Kondisi Barang", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            data.forEachIndexed { index, entry ->
                val sweep = if (total == 0f) 0f else (entry.value / total) * 360f
                drawArc(
                    color = pieColors[index % pieColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Keterangan warna
        data.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(pieColors[index % pieColors.size], shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(entry.key + " (" + entry.value + ")", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section: Image and Welcome Text
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 40.dp) // Turunkan agar rapi di bawah box biru
        ) {

        }

    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeScreenUiState.Success(
                data = Image(
                    description = stringResource(id = R.string.welcome_message),
                    altText = stringResource(id = R.string.image),
                    url = Constants.IMAGE_URL
                ),
                kondisiStat = mapOf("Baik" to 10, "Rusak" to 2)
            ),
            loadData = {}
        )
    }
}
