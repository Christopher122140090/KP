package com.rosaliscagroup.admin.ui.transfer

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransferViewModel : ViewModel() {
    private val _lokasiList = MutableStateFlow<List<LokasiProyek>>(emptyList())
    val lokasiList: StateFlow<List<LokasiProyek>> = _lokasiList
    private val db = FirebaseFirestore.getInstance()

    init {
        fetchLocations()
    }

    private fun fetchLocations() {
        db.collection("locations").get()
            .addOnSuccessListener { result ->
                val lokasi = result.map { doc ->
                    LokasiProyek(
                        id = doc.id,
                        nama = doc.getString("name") ?: "",
                        alamat = doc.getString("address") ?: "",
                        items = (doc.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
                            ItemBarang(
                                id = itemMap["id"] as? String ?: "",
                                nama = itemMap["name"] as? String ?: "",
                                kategori = itemMap["category"] as? String ?: ""
                            )
                        } ?: emptyList()
                    )
                }
                _lokasiList.value = lokasi
            }
    }
}