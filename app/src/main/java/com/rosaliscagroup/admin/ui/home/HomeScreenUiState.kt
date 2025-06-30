package com.rosaliscagroup.admin.ui.home

import com.rosaliscagroup.admin.data.entity.Activity
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.data.entity.Project

/**
 * Sealed class to represent UI states in [HomeScreen]
 */
sealed interface HomeScreenUiState {
    data object Initial : HomeScreenUiState
    data object Loading : HomeScreenUiState
    data class Success(
        val kondisiStat: Map<String, Int>,
        val totalActivities: Int,
        val totalEquipments: Int,
        val totalLocations: Int,
        val totalProjects: Int,
        val totalUsers: Int,
        val recentActivities: List<Activity>,
        val projects: List<Project>, // Tambahan: daftar project
        val locations: List<Location>, // Tambahan: daftar lokasi
        val newEquipmentsThisWeek: Int // Tambahan: jumlah peralatan baru minggu ini
    ) : HomeScreenUiState
    data class Error(val msg: String) : HomeScreenUiState
}
