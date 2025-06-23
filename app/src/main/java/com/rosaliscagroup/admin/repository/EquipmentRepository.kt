package com.rosaliscagroup.admin.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object EquipmentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addEquipment(
        nama: String,
        deskripsi: String,
        kategori: String,
        lokasiId: String,
        gambarUri: Uri,
        sku: String
    ) {
        val now = Timestamp.now()
        val data = hashMapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "kategori" to kategori,
            "lokasiId" to lokasiId,
            "sku" to sku,
            "gambarUri" to gambarUri.toString(),
            "createdAt" to now,
            "updatedAt" to now
        )
        db.collection("equipments").add(data).await()
    }

    data class Equipment(
        val id: String = "",
        val nama: String = "",
        val deskripsi: String = "",
        val kategori: String = "",
        val lokasiId: String = "",
        val sku: String = "",
        val gambarUri: String = "",
        val createdAt: com.google.firebase.Timestamp? = null,
        val updatedAt: com.google.firebase.Timestamp? = null
    )

    suspend fun getAllEquipments(): List<Equipment> {
        val snapshot = db.collection("equipments").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            Equipment(
                id = doc.id,
                nama = data["nama"] as? String ?: "",
                deskripsi = data["deskripsi"] as? String ?: "",
                kategori = data["kategori"] as? String ?: "",
                lokasiId = data["lokasiId"] as? String ?: "",
                sku = data["sku"] as? String ?: "",
                gambarUri = data["gambarUri"] as? String ?: "",
                createdAt = data["createdAt"] as? com.google.firebase.Timestamp,
                updatedAt = data["updatedAt"] as? com.google.firebase.Timestamp
            )
        }
    }
}
