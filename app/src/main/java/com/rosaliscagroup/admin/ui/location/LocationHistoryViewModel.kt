package com.rosaliscagroup.admin.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosaliscagroup.admin.data.entity.LocationHistory
import com.rosaliscagroup.admin.repository.LocationHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationHistoryViewModel @Inject constructor(
    private val repository: LocationHistoryRepository
) : ViewModel() {
    private val _recentLocationHistory = MutableStateFlow<List<LocationHistory>>(emptyList())
    val recentLocationHistory: StateFlow<List<LocationHistory>> = _recentLocationHistory.asStateFlow()

    private val _allLocationHistory = MutableStateFlow<List<LocationHistory>>(emptyList())
    val allLocationHistory: StateFlow<List<LocationHistory>> = _allLocationHistory.asStateFlow()

    fun loadRecent(limit: Int = 3) {
        viewModelScope.launch {
            _recentLocationHistory.value = repository.getRecentLocationHistory(limit)
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            _allLocationHistory.value = repository.getAllLocationHistory()
        }
    }
}
