package com.rosaliscagroup.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosaliscagroup.admin.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Initial)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            try {
                val totalActivities = homeRepository.getActivitiesCount()
                val totalEquipments = homeRepository.getEquipmentsCount()
                val totalLocations = homeRepository.getLocationsCount()
                val totalProjects = homeRepository.getProjectsCount()
                val totalUsers = homeRepository.getUsersCount()
                val recentActivities = homeRepository.getRecentActivities(5)
                val kondisiStat = homeRepository.getKondisiStat()
                _uiState.value = HomeScreenUiState.Success(
                    kondisiStat = kondisiStat,
                    totalActivities = totalActivities,
                    totalEquipments = totalEquipments,
                    totalLocations = totalLocations,
                    totalProjects = totalProjects,
                    totalUsers = totalUsers,
                    recentActivities = recentActivities
                )
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                        "Akses ke database ditolak. Silakan cek koneksi dan izin Firestore Anda."
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                    else ->
                        e.message ?: "Terjadi kesalahan tak terduga."
                }
                _uiState.value = HomeScreenUiState.Error(msg = errorMsg)
            }
        }
    }
}
