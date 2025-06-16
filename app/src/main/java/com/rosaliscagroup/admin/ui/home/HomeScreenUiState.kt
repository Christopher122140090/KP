package com.rosaliscagroup.admin.ui.home

import com.rosaliscagroup.admin.data.entity.Image

/**
 * Sealed class to represent UI states in [HomeScreen]
 */
internal sealed interface HomeScreenUiState {
    data object Initial : HomeScreenUiState
    data object Loading : HomeScreenUiState
    data class Success(val data: Image, val kondisiStat: Map<String, Int>) : HomeScreenUiState
    data class Error(val msg: String) : HomeScreenUiState
}
