package com.rosaliscagroup.admin.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
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
        val equipmentRef = db.collection("equipments").add(data).await()
        // Tambahkan aktivitas ke koleksi activities
        val activity = hashMapOf(
            "type" to "Equipment Received",
            "createdAt" to now,
            "details" to "$nama ($sku) - $kategori",
            "equipmentId" to equipmentRef.id,
            "locationId" to lokasiId,
            "projectId" to ""
        )
        db.collection("activities").add(activity).await()
        // Tambahkan log/Toast untuk debug
        android.util.Log.d("EquipmentRepository", "Activity berhasil ditambahkan ke /activities")
    }

    suspend fun addEquipmentWithImageUrl(
        context: android.content.Context,
        nama: String,
        deskripsi: String,
        kategori: String,
        lokasiId: String,
        gambarUri: Uri,
        sku: String,
        onProgress: (Float) -> Unit
    ) {
        // Upload gambar ke Firebase Storage
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
        val fileName = "equipments/${System.currentTimeMillis()}_${gambarUri.lastPathSegment}"
        val imageRef = storageRef.child(fileName)
        val uploadTask = imageRef.putFile(gambarUri)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
            onProgress(progress * 0.8f) // 80% progress for upload
        }.await()
        val downloadUrl = imageRef.downloadUrl.await().toString()
        onProgress(0.9f)
        // Simpan data equipment dengan URL gambar dari Storage
        addEquipment(nama, deskripsi, kategori, lokasiId, Uri.parse(downloadUrl), sku)
        onProgress(1.0f)
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

    fun getLatestEquipmentFlow(): Flow<Equipment?> = callbackFlow {
        val listener = db.collection("equipments")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val doc = snapshot?.documents?.firstOrNull()
                val data = doc?.data
                val equipment = if (doc != null && data != null) Equipment(
                    id = doc.id,
                    nama = data["nama"] as? String ?: "",
                    deskripsi = data["deskripsi"] as? String ?: "",
                    kategori = data["kategori"] as? String ?: "",
                    lokasiId = data["lokasiId"] as? String ?: "",
                    sku = data["sku"] as? String ?: "",
                    gambarUri = data["gambarUri"] as? String ?: "",
                    createdAt = data["createdAt"] as? com.google.firebase.Timestamp,
                    updatedAt = data["updatedAt"] as? com.google.firebase.Timestamp
                ) else null
                trySend(equipment)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateEquipmentLocation(equipmentId: String, lokasiBaru: String) {
        val now = com.google.firebase.Timestamp.now()
        db.collection("equipments").document(equipmentId)
            .update(mapOf(
                "lokasiId" to lokasiBaru,
                "updatedAt" to now
            )).await()
    }

    fun getEquipmentsRealtime(): Flow<List<Equipment>> = callbackFlow {
        val listener = db.collection("equipments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val equipments = snapshot?.documents?.mapNotNull { doc ->
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
                } ?: emptyList()
                trySend(equipments)
            }
        awaitClose { listener.remove() }
    }
}
