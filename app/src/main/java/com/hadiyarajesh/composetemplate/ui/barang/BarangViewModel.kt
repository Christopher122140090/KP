package com.hadiyarajesh.composetemplate.ui.barang

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BarangViewModel : ViewModel() {
    private val _barangList = MutableStateFlow<List<com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab>>(emptyList())
    val barangList: StateFlow<List<com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab>> = _barangList.asStateFlow()

    private val _isTambahBarangLoading = MutableStateFlow(false)
    val isTambahBarangLoading: StateFlow<Boolean> = _isTambahBarangLoading.asStateFlow()

    init {
        listenBarangList()
    }

    private fun listenBarangList() {
        viewModelScope.launch {
            BarangRepository.listenBarangList().collect { list ->
                // Jangan filter ownerUid di sini, biarkan repository yang handle
                val fixedList = list.map { barang ->
                    val kategoriKeywords = listOf("elektronik", "furnitur", "alat", "meja", "kursi", "proyektor", "laptop")
                    val kondisiKeywords = listOf("baik", "rusak", "perlu perbaikan", "bagus", "jelek")
                    val kategoriLower = barang.kategori.trim().lowercase()
                    val kondisiLower = barang.kondisi.trim().lowercase()
                    val kategoriIsKategori = kategoriKeywords.any { kategoriLower == it }
                    val kondisiIsKondisi = kondisiKeywords.any { kondisiLower == it }
                    val kategoriIsKondisi = kondisiKeywords.any { kategoriLower == it }
                    val kondisiIsKategori = kategoriKeywords.any { kondisiLower == it }
                    if (kategoriIsKategori && kondisiIsKondisi) {
                        barang
                    } else if (kategoriIsKondisi && kondisiIsKategori) {
                        barang.copy(kategori = barang.kondisi, kondisi = barang.kategori)
                    } else {
                        barang
                    }
                }
                _barangList.value = fixedList
            }
        }
    }

    fun tambahBarang(barang: com.hadiyarajesh.composetemplate.ui.barang.dummy.BarangLab, onSelesai: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        if (_isTambahBarangLoading.value) return // Cegah double submit
        _isTambahBarangLoading.value = true
        viewModelScope.launch {
            try {
                BarangRepository.tambahBarang(barang)
                onSelesai?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                _isTambahBarangLoading.value = false
            }
        }
    }

    /**
     * Contoh penggunaan di UI Compose:
     *
     * val viewModel: BarangViewModel = hiltViewModel()
     * val isLoading by viewModel.isTambahBarangLoading.collectAsState()
     * Button(
     *     onClick = {
     *         viewModel.tambahBarang(barangBaru,
     *             onSelesai = { /* reset form, tampilkan pesan sukses */ },
     *             onError = { e -> /* tampilkan pesan error */ }
     *         )
     *     },
     *     enabled = !isLoading
     * ) {
     *     if (isLoading) {
     *         CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
     *     } else {
     *         Text("Tambah Barang")
     *     }
     * }
     */
}
