package com.rosaliscagroup.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosaliscagroup.admin.data.entity.Activity
import com.rosaliscagroup.admin.data.entity.Location
import com.rosaliscagroup.admin.repository.EquipmentRepository
import com.rosaliscagroup.admin.repository.EquipmentRepository.Equipment
import com.rosaliscagroup.admin.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Initial)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private var isCacheValid = false

    // Tambahkan StateFlow untuk recent activities realtime
    val recentActivities: StateFlow<List<Activity>> =
        combine(
            homeRepository.getRecentActivitiesRealtime(4),
            EquipmentRepository.getLatestEquipmentFlow()
        ) { activities, latestEquipment ->
            if (latestEquipment == null) return@combine activities
            val equipmentReceivedActivity = Activity(
                id = latestEquipment.id,
                createdAt = latestEquipment.createdAt?.toDate()?.time ?: 0L,
                details = "${latestEquipment.nama} (${latestEquipment.sku}) - ${latestEquipment.kategori}",
                equipmentId = latestEquipment.id,
                locationId = latestEquipment.lokasiId,
                projectId = "",
                type = "Equipment Received"
            )
            // Replace jika sudah ada Equipment Received dengan id yang sama, else add di depan
            val idx = activities.indexOfFirst { it.type == "Equipment Received" && it.equipmentId == latestEquipment.id }
            val updated = activities.toMutableList()
            if (idx >= 0) {
                updated[idx] = equipmentReceivedActivity
            } else {
                updated.add(0, equipmentReceivedActivity)
            }
            updated
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // StateFlow untuk seluruh aktivitas (untuk View All)
    val allActivities: StateFlow<List<Activity>> =
        homeRepository.getRecentActivitiesRealtime(1000)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expose StateFlow untuk equipments dan locations realtime
    val equipmentsRealtime: StateFlow<List<Equipment>> =
        EquipmentRepository.getEquipmentsRealtime()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locationsRealtime: StateFlow<List<Location>> =
        homeRepository.getLocationsRealtime()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadData(forceRefresh: Boolean = false) {
        if (isCacheValid && !forceRefresh && _uiState.value !is HomeScreenUiState.Initial) return
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            try {
                val totalActivities = homeRepository.getActivitiesCount()
                val totalEquipments = equipmentsRealtime.value.size
                val totalLocations = homeRepository.getLocationsCount()
                val totalProjects = homeRepository.getProjectsCount()
                val totalUsers = homeRepository.getUsersCount()
                val kondisiStat = homeRepository.getKondisiStat()
                val projects = homeRepository.getProjects()
                val locations = locationsRealtime.value
                val newEquipmentsThisWeek = homeRepository.getNewEquipmentsThisWeek()
                val recentActivitiesList = recentActivities.value
                _uiState.value = HomeScreenUiState.Success(
                    kondisiStat = kondisiStat,
                    totalActivities = totalActivities,
                    totalEquipments = totalEquipments,
                    totalLocations = totalLocations,
                    totalProjects = totalProjects,
                    totalUsers = totalUsers,
                    recentActivities = recentActivitiesList,
                    projects = projects,
                    locations = locations,
                    newEquipmentsThisWeek = newEquipmentsThisWeek
                )
                isCacheValid = true
            } catch (e: Exception) {
                _uiState.value = HomeScreenUiState.Error(msg = e.message ?: "Something went wrong")
            }
        }
    }

    fun invalidateCache() {
        isCacheValid = false
    }
}
