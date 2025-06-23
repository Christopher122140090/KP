package com.rosaliscagroup.admin.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object EquipmentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadImageToStorage(context: Context, imageUri: Uri, onProgress: ((Float) -> Unit)? = null): String {
        return withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "equipments/${UUID.randomUUID()}"
            val imageRef = storageRef.child(fileName)
            val stream = context.contentResolver.openInputStream(imageUri) ?: throw Exception("Gagal membuka gambar")
            val uploadTask = imageRef.putStream(stream)
            if (onProgress != null) {
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
                    onProgress(progress)
                }
            }
            uploadTask.await()
            stream.close()
            imageRef.downloadUrl.await().toString()
        }
    }

    suspend fun addEquipmentWithImageUrl(
        context: Context,
        nama: String,
        deskripsi: String,
        kategori: String,
        lokasiId: String,
        gambarUri: Uri,
        sku: String,
        onProgress: ((Float) -> Unit)? = null
    ) {
        val imageUrl = uploadImageToStorage(context, gambarUri, onProgress)
        val now = Timestamp.now()
        val data = hashMapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "kategori" to kategori,
            "lokasiId" to lokasiId,
            "sku" to sku,
            "gambarUri" to imageUrl,
            "createdAt" to now,
            "updatedAt" to now
        )
        db.collection("equipments").add(data).await()
    }

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
