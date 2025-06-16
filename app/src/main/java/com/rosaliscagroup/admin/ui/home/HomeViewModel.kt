package com.rosaliscagroup.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosaliscagroup.admin.data.entity.Image
import com.rosaliscagroup.admin.repository.HomeRepository
import com.rosaliscagroup.admin.ui.barang.BarangRepository
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
                // Ambil data image (jika masih ingin ditampilkan)
                var image: Image? = null
                launch {
                    homeRepository.loadData().collect { img ->
                        image = img
                    }
                }
                // Ambil data barang dari Firebase
                BarangRepository.listenBarangList().collect { barangList ->
                    // Hitung jumlah barang per kondisi
                    val kondisiStat = barangList.groupingBy { it.kondisi }.eachCount()
                    _uiState.value = HomeScreenUiState.Success(
                        data = image ?: Image(0, "", "", ""),
                        kondisiStat = kondisiStat
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenUiState.Error(msg = e.message ?: "Something went wrong")
            }
        }
    }
}
