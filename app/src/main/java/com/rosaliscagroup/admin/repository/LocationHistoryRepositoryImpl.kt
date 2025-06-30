package com.rosaliscagroup.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rosaliscagroup.admin.data.entity.LocationHistory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

@Singleton
class LocationHistoryRepositoryImpl @Inject constructor() : LocationHistoryRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    override suspend fun getRecentLocationHistory(limit: Int): List<LocationHistory> {
        val snapshot = db.collection("projects")
            .orderBy("updatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val name = doc.getString("name") ?: return@mapNotNull null
            val address = doc.getString("address") ?: ""
            val updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
            LocationHistory(id = id, name = name, address = address, updatedAt = updatedAt)
        }
    }

    override fun getRecentLocationHistoryRealtime(limit: Int): Flow<List<LocationHistory>> = callbackFlow {
        val listener = db.collection("projects")
            .orderBy("updatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val histories = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val address = doc.getString("address") ?: ""
                    val updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    LocationHistory(id = id, name = name, address = address, updatedAt = updatedAt)
                } ?: emptyList()
                trySend(histories)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getAllLocationHistory(): List<LocationHistory> {
        val snapshot = db.collection("projects")
            .orderBy("updatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val name = doc.getString("name") ?: return@mapNotNull null
            val address = doc.getString("address") ?: ""
            val updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
            LocationHistory(id = id, name = name, address = address, updatedAt = updatedAt)
        }
    }
}
