package com.rosaliscagroup.admin.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ProjectRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addProject(
        nama: String,
        lokasi: String
    ) {
        val now = Timestamp.now()
        val data = hashMapOf(
            "name" to nama,
            "address" to lokasi,
            "createdAt" to now
        )
        val projectRef = db.collection("projects").add(data).await()
        // Tambahkan aktivitas ke koleksi activities
        val activity = hashMapOf(
            "type" to "Project Created",
            "createdAt" to now,
            "details" to "Lokasi/Proyek baru: $nama ($lokasi)",
            "projectId" to projectRef.id
        )
        db.collection("activities").add(activity).await()
    }
}
