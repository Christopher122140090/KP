package com.rosaliscagroup.admin.ui.home

import com.rosaliscagroup.admin.data.entity.Activity

/**
 * Sealed class to represent UI states in [HomeScreen]
 */
internal sealed interface HomeScreenUiState {
    data object Initial : HomeScreenUiState
    data object Loading : HomeScreenUiState
    data class Success(
        val kondisiStat: Map<String, Int>,
        val totalActivities: Int,
        val totalEquipments: Int,
        val totalLocations: Int,
        val totalProjects: Int,
        val totalUsers: Int,
        val recentActivities: List<Activity>
    ) : HomeScreenUiState
    data class Error(val msg: String) : HomeScreenUiState
}
