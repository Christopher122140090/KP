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

    // region: Add Equipment
    suspend fun addEquipment(
        nama: String,
        deskripsi: String,
        kategori: String,
        lokasiId: String,
        gambarUri: Uri,
        sku: String,
        namaUser: String, // Tambahkan parameter namaUser
        kondisi: String // Tambahkan parameter kondisi
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
            "updatedAt" to now,
            "kondisi" to kondisi // Simpan kondisi ke Firestore
        )
        val equipmentRef = db.collection("equipments").add(data).await()
        // Tambahkan aktivitas ke koleksi activities
        val activity = hashMapOf(
            "type" to "Equipment Received",
            "createdAt" to now,
            "details" to "Nama: $nama\nDari: $namaUser",
            "equipmentId" to equipmentRef.id,
            "locationId" to lokasiId,
            "projectId" to ""
        )
        db.collection("activities").add(activity).await()
        // Tambahkan log/Toast untuk debug
        android.util.Log.d("EquipmentRepository", "Activity berhasil ditambahkan ke /activities")
    }
    // endregion

    suspend fun addEquipmentWithImageUrl(
        context: android.content.Context,
        nama: String,
        deskripsi: String,
        kategori: String,
        lokasiId: String,
        gambarUri: Uri,
        sku: String,
        onProgress: (Float) -> Unit,
        namaUser: String, // Tambahkan parameter namaUser
        kondisi: String // Tambahkan parameter kondisi
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
        addEquipment(nama, deskripsi, kategori, lokasiId, Uri.parse(downloadUrl), sku, namaUser, kondisi)
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
        val createdAt: Timestamp? = null,
        val updatedAt: Timestamp? = null,
        val kondisi: String = "" // Tambahkan field kondisi
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
                createdAt = data["createdAt"] as? Timestamp,
                updatedAt = data["updatedAt"] as? Timestamp,
                kondisi = data["kondisi"] as? String ?: "" // Ambil kondisi dari Firestore
            )
        }
    }

    suspend fun getAllCategories(): List<String> {
        val snapshot = db.collection("categories").get().await()
        val categories = snapshot.documents.mapNotNull { it.getString("name") }
        return categories.distinct().sorted()
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
                    createdAt = data["createdAt"] as? Timestamp,
                    updatedAt = data["updatedAt"] as? Timestamp
                ) else null
                trySend(equipment)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateEquipmentLocation(equipmentId: String, lokasiBaru: String) {
        val now = Timestamp.now()
        db.collection("equipments").document(equipmentId)
            .update(mapOf(
                "lokasiId" to lokasiBaru,
                "updatedAt" to now
            )).await()
    }

    suspend fun updateDescription(itemId: String, newDescription: String) {
        val now = Timestamp.now()
        val updates = mapOf(
            "deskripsi" to newDescription,
            "updatedAt" to now
        )
        db.collection("equipments").document(itemId).update(updates).await()
    }

    // Fungsi baru untuk update seluruh data item kecuali kategori dan SKU
    suspend fun updateEquipment(
        equipmentId: String,
        nama: String,
        deskripsi: String,
        lokasiId: String,
        gambarUri: Uri,
        kondisi: String
    ) {
        val now = Timestamp.now()
        val updates = mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "lokasiId" to lokasiId,
            "gambarUri" to gambarUri.toString(),
            "kondisi" to kondisi,
            "updatedAt" to now
        )
        db.collection("equipments").document(equipmentId).update(updates).await()
    }

    suspend fun updateItem(itemId: String, name: String, description: String, kondisi: String) {
        val now = Timestamp.now()
        val data = mapOf(
            "nama" to name,
            "deskripsi" to description,
            "kondisi" to kondisi, // Updated to match the correct field name
            "updatedAt" to now
        )
        db.collection("equipments").document(itemId).update(data).await()
    }

    suspend fun updateImageUri(itemId: String, newImageUri: String) {
        try {
            db.collection("equipments").document(itemId)
                .update("gambarUri", newImageUri)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("EquipmentRepository", "Failed to update image URI", e)
        }
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
                        createdAt = data["createdAt"] as? Timestamp,
                        updatedAt = data["updatedAt"] as? Timestamp
                    )
                } ?: emptyList()
                trySend(equipments)
            }
        awaitClose { listener.remove() }
    }

    suspend fun logTransferActivity(
        equipmentId: String,
        locationId: String,
        projectId: String = "",
        details: String = ""
    ) {
        val now = Timestamp.now()
        val activity = hashMapOf(
            "type" to "Transfer",
            "createdAt" to now,
            "details" to details,
            "equipmentId" to equipmentId,
            "locationId" to locationId,
            "projectId" to projectId
        )
        db.collection("activities").add(activity).await()
    }

    suspend fun getItemImageUri(itemId: String): String? {
        return try {
            val document = db.collection("equipments").document(itemId).get().await()
            document.getString("gambarUri")
        } catch (e: Exception) {
            android.util.Log.e("EquipmentRepository", "Error fetching image URI", e)
            null
        }
    }
}
