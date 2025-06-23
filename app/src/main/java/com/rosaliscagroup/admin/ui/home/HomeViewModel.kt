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

    fun loadData(context: android.content.Context? = null) {
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
                val projects = homeRepository.getProjects()
                val locations = homeRepository.getLocations()
                if (context != null) {
                    android.widget.Toast.makeText(context, "Locations: ${locations.size}", android.widget.Toast.LENGTH_SHORT).show()
                }
                _uiState.value = HomeScreenUiState.Success(
                    kondisiStat = kondisiStat,
                    totalActivities = totalActivities,
                    totalEquipments = totalEquipments,
                    totalLocations = totalLocations,
                    totalProjects = totalProjects,
                    totalUsers = totalUsers,
                    recentActivities = recentActivities,
                    projects = projects,
                    locations = locations
                )
            } catch (e: Exception) {
                if (context != null) {
                    android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
                _uiState.value = HomeScreenUiState.Error(msg = e.message ?: "Something went wrong")
            }
        }
    }
}
